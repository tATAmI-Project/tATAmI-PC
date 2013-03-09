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
package tatami.core.util.graph.representation;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;



import tatami.core.interfaces.Logger;
import tatami.core.util.ContentHolder;
import tatami.core.util.graph.Edge;
import tatami.core.util.graph.Graph;
import tatami.core.util.graph.Node;
import tatami.core.util.graph.GraphPattern.NodeP;
import tatami.core.util.graph.representation.TextRepresentationElement.Symbol;
import tatami.core.util.graph.representation.TextRepresentationElement.TextRepElementConfig;
import tatami.core.util.graph.representation.TextRepresentationElement.Type;
import tatami.core.util.logging.Unit;

public class TextGraphRepresentation extends LinearGraphRepresentation
{
	public static class GraphConfig extends LinearGraphRepresentation.GraphConfig
	{
		protected String	indent			= "";
		protected String	indentIncrement	= "";
		protected int		incrementLimit	= -1;
		
		public GraphConfig(Graph g)
		{
			super(g);
		}
		
		@Override
		public GraphConfig makeDefaults()
		{
			super.makeDefaults();
			return this;
		}
		
		/**
		 * Configures the presentation of the linear graph representation. More precisely, configures what happens in the output when a branch appears. See
		 * parameter descriptions for more details.
		 * 
		 * @param _separator
		 *            - is added before each branch. Usually it is a newline.
		 * @param _indent
		 *            - <i>d</i> indentations are added after the separator, where <i>d</i> is the depth of the parent node. Usually it is a tabbing string.
		 * @param limit
		 *            - the use of separator and indent are limited to a depth specified by this parameter. Use 0 so that no separation will occur. Use -1 to
		 *            not limit the separation.
		 * @return the {@link LinearGraphRepresentation} instance, for chained calls.
		 */
		public GraphConfig setLayout(String _separator, String _indent, int limit)
		{
			if(_separator != null)
				this.indent = _separator;
			if(_indent != null)
				this.indentIncrement = _indent;
			incrementLimit = limit;
			return this;
		}
		
		@Override
		protected String setDefaultName(String name)
		{
			return super.setDefaultName(name) + "T";
		}
	}
	
	public TextGraphRepresentation(GraphConfig _config)
	{
		super(_config);
	}
	
	@Override
	protected void processGraph()
	{
		super.processGraph();
		
		Set<PathElement> blackNodes = new HashSet<PathElement>(); // contains all 'visited' nodes
		TextRepresentationElement textRepresentation = new TextRepresentationElement(new TextRepElementConfig(this, Type.ELEMENT_CONTAINER));
		
		boolean first = true;
		for(PathElement el : paths)
			// check all paths (subgraphs)
			if(!blackNodes.contains(el))
			{
				// subgraph: contains the representation of the whole subgraph
				TextRepresentationElement nodeRepr = new TextRepresentationElement(new TextRepElementConfig(this, el.node, Type.NODE));
				blackNodes.add(el);
				nodeRepr.addSub(buildTextChildren(el, 1, blackNodes));
				
				TextRepresentationElement repr = new TextRepresentationElement(new TextRepElementConfig(this, Type.SUBGRAPH, first));
				repr.addSub(nodeRepr);
				textRepresentation.addSub(repr);
				first = false;
			}
		this.theRepresentation = textRepresentation;
	}
	
	protected List<TextRepresentationElement> buildTextChildren(PathElement el, int level, Set<PathElement> blackNodes)
	{
		GraphConfig conf = (GraphConfig)this.config;
		
		List<TextRepresentationElement> ret = new LinkedList<TextRepresentationElement>();
		
		int allchildren = el.otherChildren.size() + el.children.size();
		int remainingChildren = allchildren;
		List<PathElement> others = new LinkedList<PathElement>(el.otherChildren);
		
		for(PathElement child : el.children)
		{
			while(!others.isEmpty() && blackNodes.contains(others.get(0)))
			{
				remainingChildren--;
				PathElement other = others.get(0);
				Edge edge = conf.isBackwards ? el.node.getEdgeFrom(other.node) : el.node.getEdgeTo(other.node);
				// backlink
				// ret.add(new TextRepresentationElement(new TextRepElementConfig(this, other.node.toString(), edge.toStringShort(conf.isBackwards),
				// Type.INTERNAL_LINK, level, !(remainingChildren > 0), (allchildren == 1))));
				TextRepresentationElement repr = new TextRepresentationElement(new TextRepElementConfig(this, edge, Type.INTERNAL_LINK, level, !(remainingChildren > 0), (allchildren == 1)));
				repr.addSub(new TextRepresentationElement(new TextRepElementConfig(this, other.node, Type.NODE)));
				ret.add(repr);
				
				others.remove(0);
			}
			
			remainingChildren--;
			Edge edge = conf.isBackwards ? el.node.getEdgeFrom(child.node) : el.node.getEdgeTo(child.node);
			// branch
			TextRepresentationElement reprEdge = new TextRepresentationElement(new TextRepElementConfig(this, edge, Type.BRANCH, level, !(remainingChildren > 0), (allchildren == 1)));
			TextRepresentationElement reprNode = new TextRepresentationElement(new TextRepElementConfig(this, child.node, Type.NODE));
			blackNodes.add(child);
			reprEdge.addSub(reprNode);
			reprNode.addSub(buildTextChildren(child, level + 1, blackNodes));
			ret.add(reprEdge);
		}
		
		while(!others.isEmpty())
		{
			remainingChildren--;
			PathElement other = others.get(0);
			boolean external = !blackNodes.contains(other);
			Edge edge = conf.isBackwards ? el.node.getEdgeFrom(other.node) : el.node.getEdgeTo(other.node);
			// backlinks and external links
			TextRepresentationElement repr = new TextRepresentationElement(new TextRepElementConfig(this, edge, (external ? Type.EXTERNAL_LINK : Type.INTERNAL_LINK), level, !(remainingChildren > 0), (allchildren == 1)));
			repr.addSub(new TextRepresentationElement(new TextRepElementConfig(this, other.node, Type.NODE)));
			ret.add(repr);
			blackNodes.add(other);
			others.remove(0);
		}
		
		return ret;
	}
	
	@Override
	public String toString()
	{
		return displayRepresentation();
	}
	
	@Override
	public RepresentationElement getRepresentation()
	{
		return theRepresentation;
	}
	
	/**
	 * Returns a text representation of the associated graph, on one line.
	 * 
	 * <p>
	 * The representation uses the text-representations of the nodes (written by {@link Node}.toString() ) and edges (written by {@link Edge} .toStringShort()},
	 * and a few special symbols: parentheses for branches (the last branch of a node is not surrounded by parentheses) and "*" to refer nodes that have already
	 * appeared in the representation earlier. Also, "^" for nodes outside the (sub)graph.
	 * 
	 * <p>
	 * Example: a graph that is a triangle ABC with one other edge BD will be represented as (edges are not labeled): <br>
	 * A->B(->D)->C->*A<br>
	 * That is, there is a cycle A-B-C-A, and also there is also a branch from B to D.
	 */
	@Override
	public String displayRepresentation()
	{
		return ((TextRepresentationElement)theRepresentation).toString(((GraphConfig)config).indent, ((GraphConfig)config).indentIncrement, ((GraphConfig)config).incrementLimit);
	}
	
	public static Graph readRepresentation(String rawInput)
	{
		return readRepresentation(rawInput, null, null);
	}
	
	public static Graph readRepresentation(String rawInput, UnitConfigData graphUnitConfig, UnitConfigData thisUnitConfig)
	{
		Unit thisUnit = new Unit(thisUnitConfig);
		Logger log = thisUnit.getLog();
		
		log.info("reading graph");
		ContentHolder<String> input = new ContentHolder<String>(rawInput);
		Graph g = new Graph(graphUnitConfig);
		
		boolean isBackwards = input.get().indexOf(Symbol.EDGE_ENDING_BACKWARD.toString()) >= 0;
		TextGraphRepresentation repr = new TextGraphRepresentation((GraphConfig)new TextGraphRepresentation.GraphConfig(g).setBackwards(isBackwards));
		repr.theRepresentation = TextRepresentationElement.readRepresentation(input, repr, log);
		log.trace("result:" + repr.theRepresentation.toString() + "\n====================================");
		
		// start building the graph
		Stack<Queue<TextRepresentationElement>> tree = new Stack<Queue<TextRepresentationElement>>();
		Queue<TextRepresentationElement> cLevel = new LinkedList<TextRepresentationElement>(), nLevel = null;
		cLevel.add((TextRepresentationElement)repr.theRepresentation);
		tree.add(cLevel);
		
		// very easy to build: any internal links are to nodes that have been already defined
		while(!tree.isEmpty())
		{
			cLevel = tree.peek();
			log.trace("inspecting tree at level [" + tree.size() + "]");
			if(cLevel.isEmpty())
				tree.pop();
			else
			{
				log.trace("[" + cLevel.size() + "] elements left to inspect");
				
				TextRepresentationElement element = cLevel.poll();
				Type type = ((TextRepElementConfig)element.config).linkType;
				switch(type)
				{
				case ELEMENT_CONTAINER:
				case SUBGRAPH:
					log.trace("inspecting [" + type + "]");
					nLevel = new LinkedList<TextRepresentationElement>();
					nLevel.addAll(element.content);
					tree.push(nLevel);
					break;
				case NODE:
				{
					Node node = (Node)element.config.representedComponent;
					log.trace("inspecting [" + type + "]: [" + node + "]");
					log.info("adding to graph node [" + node + "]");
					g.addNode(node);
					
					nLevel = new LinkedList<TextRepresentationElement>();
					tree.push(nLevel);
					
					// add edges
					for(TextRepresentationElement edgeEl : element.content)
					{
						Edge edge = (Edge)edgeEl.config.representedComponent;
						if(!isBackwards)
							edge.setFrom(node);
						else
							edge.setTo(node);
						log.trace("adding to queue [" + ((TextRepElementConfig)edgeEl.config).linkType + "]: [" + edge + "]");
						nLevel.add(edgeEl);
					}
					break;
				}
				case BRANCH:
				case INTERNAL_LINK:
				case EXTERNAL_LINK:
				{
					Edge edge = (Edge)element.config.representedComponent;
					log.trace("inspecting [" + type + "]: [" + edge + "]");
					
					nLevel = new LinkedList<TextRepresentationElement>();
					tree.push(nLevel);
					Node targetNode = null;
					TextRepresentationElement targetNodeEl = element.content.iterator().next();
					if(((TextRepElementConfig)element.config).linkType == Type.INTERNAL_LINK)
					{
						Node dummyTargetNode = (Node)targetNodeEl.config.representedComponent;
						if(dummyTargetNode instanceof NodeP && ((NodeP)dummyTargetNode).isGeneric())
						{
							log.trace("searching pattern target node [" + dummyTargetNode + "]");
							for(Node candidateNode : g.getNodesNamed(dummyTargetNode.getLabel()))
								if(candidateNode instanceof NodeP && ((NodeP)dummyTargetNode).genericIndex() == ((NodeP)candidateNode).genericIndex())
									targetNode = candidateNode;
							log.trace("found old target pattern node [" + targetNode + "]");
						}
						else
						{
							log.trace("searching target node [" + dummyTargetNode + "]");
							targetNode = g.getNodesNamed(dummyTargetNode.getLabel()).iterator().next();
							log.trace("found old target node [" + targetNode + "]");
						}
					}
					else
					{ // no external links should actually appear here, i think? TODO: is it?
						// actual new node
						targetNode = (Node)targetNodeEl.config.representedComponent;
						nLevel.add(targetNodeEl);
						log.trace("target node (added to queue) [" + targetNode + "]");
					}
					if(!isBackwards)
						edge.setTo(targetNode);
					else
						edge.setFrom(targetNode);
					log.info("adding to graph edge [" + edge + "]");
					g.addEdge(edge);
				}
				}
			}
		}
		thisUnit.exit();
		return g;
	}
}
