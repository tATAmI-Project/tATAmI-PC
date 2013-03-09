/*******************************************************************************
 * Copyright (C) 2013 Andrei Olaru, Marius-Tudor Benea, Nguyen Thi Thuy Nga, Amal El Fallah Seghrouchni, Cedric Herpson.
 * 
 * This file is part of tATAmI-PC.
 * 
 * tATAmI-PC is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * tATAmI-PC is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with tATAmI-PC.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package tatami.pc.util.graph.representation;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import tatami.core.util.graph.Edge;
import tatami.core.util.graph.Graph;
import tatami.core.util.graph.representation.LinearGraphRepresentation;
import tatami.pc.util.graph.representation.GraphicalRepresentationElement.EdgeType;
import tatami.pc.util.graph.representation.GraphicalRepresentationElement.GraphicalRepElementConfig;
import tatami.pc.util.graph.representation.GraphicalRepresentationElement.Type;
import tatami.pc.util.graphical.GCanvas;
import tatami.pc.util.graphical.GConnector;
import tatami.pc.util.graphical.GContainer;

public class GraphicalGraphRepresentation extends LinearGraphRepresentation
{
	public static class GraphConfig extends LinearGraphRepresentation.GraphConfig
	{
		GCanvas canvas;
		Point2D topleft;
		Point2D bottomright;
		
		public GraphConfig(Graph g)
		{
			super(g);
		}
		
		@Override
		public GraphConfig makeDefaults()
		{
			super.makeDefaults();
			setCanvas(new GCanvas());
			topleft = new Point2D.Float(-100, -100);
			bottomright = new Point2D.Float(100, 100);
			return this;
		}
		
		public GraphConfig setCanvas(GCanvas canvas)
		{
			this.canvas = canvas;
			return this;
		}
		
		/**
		 * The origin of the rectangle for this representation, on the GCanvas
		 */
		public GraphConfig setOrigin(Point2D origin)
		{
			this.topleft = origin;
			return this;
		}
		
		public GraphConfig setBottomRight(Point2D bottomRight)
		{
			this.bottomright = bottomRight;
			return this;
		}
		
		@Override
		protected String setDefaultName(String name)
		{
			return super.setDefaultName(name) + "X";
		}
		
		@Override
		protected boolean isBackwards()
		{
			return super.isBackwards();
		}
	}
	
	public GraphicalGraphRepresentation(GraphConfig config)
	{
		super(config);
	}
	
	@Override
	protected void processGraph()
	{
		super.processGraph(); // calculates paths
		
		Set<PathElement> blackNodes = new HashSet<PathElement>();
		GraphicalRepresentationElement representation = new GraphicalRepresentationElement(
				new GraphicalRepElementConfig(this, null), Type.ELEMENT_CONTAINER);
		
		for(PathElement el : paths)
			if(!blackNodes.contains(el))
				representation.connected.add(representChildren(el, blackNodes));
		
		this.theRepresentation = representation;
		
		doLayout();
		log.trace("get ready for magnetics");
		((GraphConfig) this.config).canvas.startMagnetics();
	}
	
	protected GraphicalRepresentationElement representChildren(PathElement el, Set<PathElement> blackNodes)
	{
		GraphConfig config = (GraphConfig) this.config;
		
		GraphicalRepresentationElement repr = new GraphicalRepresentationElement(new GraphicalRepElementConfig(this,
				el.node), Type.NODE);
		blackNodes.add(el);
		
		List<PathElement> others = new LinkedList<PathElement>(el.otherChildren);
		
		representOthers(others, blackNodes, EdgeType.BACKLINK, el, repr);
		
		boolean first = true;
		for(PathElement child : el.children)
		{
			Edge edge = config.isBackwards() ? el.node.getEdgeFrom(child.node) : el.node.getEdgeTo(child.node);
			GraphicalRepresentationElement childRepr = representChildren(child, blackNodes);
			GraphicalRepresentationElement edgeRepr = new GraphicalRepresentationElement(new GraphicalRepElementConfig(
					this, edge), Type.EDGE);
			edgeRepr.setEdge(EdgeType.CHILDLINK, repr, childRepr);
			
			representOthers(others, blackNodes, first ? EdgeType.FORELINK : EdgeType.SIDELINK, el, repr);
			
			repr.connected.add(edgeRepr);
			child.node.addRepresentation(childRepr);
			edge.addRepresentation(edgeRepr);
			first = false;
		}
		representOthers(others, blackNodes, EdgeType.EXTLINK, el, repr);
		
		return repr;
	}
	
	protected void representOthers(List<PathElement> others, Set<PathElement> blackNodes, EdgeType edgeType,
			PathElement parent, GraphicalRepresentationElement parentRepr)
	{
		GraphConfig config = (GraphConfig) this.config;
		
		while(!others.isEmpty() && ((edgeType == EdgeType.EXTLINK) || blackNodes.contains(others.get(0))))
		{
			PathElement other = others.get(0);
			EdgeType actualEdgeType = edgeType;
			GraphicalRepresentationElement otherRepr = (GraphicalRepresentationElement) other.node
					.getFirstRepresentationForPlatform(parentRepr.getRootRepresentation());
			if(otherRepr == null)
			{
				others.remove(other);
				continue;
				// FIXME this may happen for some SIDELINKS. decide.
			}
			
			if((actualEdgeType == EdgeType.BACKLINK) && !parent.pathContains(other))
				actualEdgeType = EdgeType.SIDELINK;
			Edge edge = config.isBackwards() ? parent.node.getEdgeFrom(other.node) : parent.node.getEdgeTo(other.node);
			GraphicalRepresentationElement edgeRepr = new GraphicalRepresentationElement(new GraphicalRepElementConfig(
					this, edge), Type.EDGE);
			edgeRepr.setEdge(actualEdgeType, parentRepr, otherRepr);
			
			parentRepr.connected.add(edgeRepr);
			edge.addRepresentation(edgeRepr);
			others.remove(other);
		}
	}
	
	protected void doLayout()
	{
		GraphConfig config = (GraphConfig) this.config;
		
		float w = (float) (config.bottomright.getX() - config.topleft.getX());
		float h = (float) (config.bottomright.getY() - config.topleft.getY());
		
		Point measurement = measureLayout((GraphicalRepresentationElement) theRepresentation);
		int wc = measurement.x;
		int hc = measurement.y;
		
		GraphicalRepresentationElement repr = (GraphicalRepresentationElement) theRepresentation;
		((GContainer) repr.gelement).setSize(w, h);
		doLayout((GraphicalRepresentationElement) theRepresentation, new Point(1, 0), w / (wc + 2), h / (hc + 1), repr);
		
	}
	
	protected void doLayout(GraphicalRepresentationElement repr, Point cPos, float wFactor, float hFactor,
			GraphicalRepresentationElement container)
	{
		repr.gelement.setCanvas(((GraphConfig) this.config).canvas);
		if(repr.glabel != null)
			repr.glabel.setCanvas(((GraphConfig) this.config).canvas);
		if(repr.type == Type.NODE)
			((GContainer) container.gelement).addReferencingElement(repr.gelement);
		
		log.info("layout for: [" + repr.label + "] at [" + cPos + "] having size [" + repr.subSize + "] : "
				+ (repr.edgeType == null ? "-" : repr.edgeType));
		switch(repr.type)
		{
		case EDGE:
			if(repr.edgeType == EdgeType.CHILDLINK)
				doLayout(repr.connected.get(1), new Point(cPos), wFactor, hFactor, container);
			((GConnector) repr.gelement).setFrom(repr.connected.get(0).gelement).setTo(repr.connected.get(1).gelement);
			break;
		case NODE:
			repr.positionInGrid(new Point(cPos), hFactor, hFactor);
			cPos.setLocation(cPos.x, cPos.y + 1);
			for(GraphicalRepresentationElement sub : repr.connected)
			{
				doLayout(sub, new Point(cPos), wFactor, hFactor, container);
				if(sub.edgeType == EdgeType.CHILDLINK)
					cPos.setLocation(cPos.x + sub.subSize.x, cPos.y);
			}
			break;
		case ELEMENT_CONTAINER:
			cPos.setLocation(cPos.x, cPos.y + 1);
			for(GraphicalRepresentationElement sub : repr.connected)
			{
				doLayout(sub, new Point(cPos), wFactor, hFactor, container);
				cPos.setLocation(cPos.x + sub.subSize.x, cPos.y);
			}
		}
	}
	
	protected Point measureLayout(GraphicalRepresentationElement repr)
	{
		int height = 0;
		int width = 0;
		switch(repr.type)
		{
		case ELEMENT_CONTAINER:
			for(GraphicalRepresentationElement sub : repr.connected)
			{
				Point result = measureLayout(sub);
				if(result.y > height)
					height = result.y;
				width += result.x;
			}
			repr.setSize(new Point(width, height));
			return new Point(width, height);
		case EDGE:
			return repr.setSize(measureLayout(repr.connected.get(1))).subSize;
		case NODE:
			for(GraphicalRepresentationElement sub : repr.connected)
			{
				if(sub.edgeType != EdgeType.CHILDLINK)
					continue;
				Point result = measureLayout(sub);
				if(result.y > height)
					height = result.y;
				width += result.x;
			}
			repr.setSize(new Point((width == 0) ? 1 : width, height + 1));
			return repr.subSize;
		default:
			return new Point(width, height);
		}
	}
	
	@Override
	public GraphicalRepresentationElement getRepresentation()
	{
		return (GraphicalRepresentationElement) theRepresentation;
	}
	
	@Override
	public GCanvas displayRepresentation()
	{
		GraphConfig config = (GraphConfig) this.config;
		
		// ((GraphicalRepresentationElement)theRepresentation).gelement.setMoveTo(new Point2D.Float(20, 10));
		
		return config.canvas;
	}
}
