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
import java.util.LinkedList;
import java.util.List;

import tatami.core.util.graph.Edge;
import tatami.core.util.graph.GraphComponent;
import tatami.core.util.graph.Node;
import tatami.core.util.graph.representation.GraphRepresentation;
import tatami.core.util.graph.representation.RepresentationElement;
import tatami.pc.util.graphical.GConnector;
import tatami.pc.util.graphical.GContainer;
import tatami.pc.util.graphical.GElement;
import tatami.pc.util.graphical.GElement.ReferenceType;
import tatami.pc.util.graphical.GLabel;

public class GraphicalRepresentationElement extends RepresentationElement
{
	public static class GraphicalRepElementConfig extends RepElementConfig
	{
		public GraphicalRepElementConfig(GraphRepresentation root, GraphComponent component)
		{
			super(root, component);
		}
	}
	
	enum Type {
		NODE, EDGE, ELEMENT_CONTAINER
	}
	
	enum EdgeType {
		CHILDLINK, SIDELINK, EXTLINK, FORELINK, BACKLINK
	}
	
	Type								 type;
	
	GElement							 gelement	 = null;
	GLabel							   glabel	   = null;
	Point								gridPos	  = null; // not applicable for edges
	Point								subSize	  = null; // not applicable for edges
	String							   label		= "";
	float								widthFactor  = 1;
	float								heightFactor = 1;
	
	// GraphComponent peer = null;
	List<GraphicalRepresentationElement> connected	= null;
	EdgeType							 edgeType	 = null;
	
	// FIXME: the type should be put in config
	public GraphicalRepresentationElement(GraphicalRepElementConfig conf, Type elementType)
	{
		super(conf);
		
		this.type = elementType;
		connected = new LinkedList<GraphicalRepresentationElement>();
		
		switch(elementType)
		{
		case ELEMENT_CONTAINER:
			gelement = new GContainer().setReferenceType(ReferenceType.TOPLEFT);
			break;
		case EDGE:
			gelement = new GConnector();
			glabel = new GLabel().setText(((Edge) this.representedComponent()).getLabel()).setParent(gelement);
			break;
		case NODE:
			gelement = new GElement();
			glabel = new GLabel().setText(((Node) this.representedComponent()).getLabel()).setParent(gelement);
			break;
		default:
			gelement = new GElement();
			break;
		}
		gelement.setRepresented(representedComponent());
	}
	
	public GraphicalRepresentationElement setEdge(EdgeType type, GraphicalRepresentationElement from,
			GraphicalRepresentationElement to)
	{
		if(this.type != Type.EDGE)
			throw new IllegalArgumentException("function is only available for edges");
		this.connected.add(from);
		this.connected.add(to);
		this.edgeType = type;
		return this;
	}
	
	public GraphicalRepresentationElement setSize(Point size)
	{
		this.subSize = size;
		return this;
	}
	
	public GraphicalRepresentationElement positionInGrid(Point position, float widthFactor, float heightFactor)
	{
		this.gridPos = new Point(position);
		this.widthFactor = widthFactor;
		this.heightFactor = heightFactor;
		
		gelement.setMoveTo(new Point2D.Float(gridPos.x * widthFactor, gridPos.y * heightFactor));
		return this;
	}
}
