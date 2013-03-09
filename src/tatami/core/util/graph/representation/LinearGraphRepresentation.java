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

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import tatami.core.util.graph.Graph;
import tatami.core.util.graph.GraphPattern.NodeP;
import tatami.core.util.graph.Node;

/**
 * Class that allows the representation of a {@link Graph} structure. Also can be written as GrapheR
 * 
 * <p>
 * An instance remains associated with the same {@link Graph} instance for all of its lifecycle.
 * 
 * @author Andrei Olaru
 * 
 */
public abstract class LinearGraphRepresentation extends GraphRepresentation
{
	/**
	 * Compares two {@link Node} structures. First criterion: node with lower in-degree is first; second criterion is
	 * lexical order.
	 * 
	 * @author Andrei Olaru
	 * 
	 */
	static class NodeInAlphaComparator implements Comparator<Node>
	{
		@Override
		public int compare(Node n0, Node n1)
		{
			if(n0.getInEdges().size() != n1.getInEdges().size())
				return (int) Math.signum(n0.getInEdges().size() - n1.getInEdges().size());
			if(!n0.getLabel().equals(n1.getLabel()))
				return n0.getLabel().compareTo(n1.getLabel());
			if((n0 == n1) || !(n0 instanceof NodeP) || !(n1 instanceof NodeP)) // something may be weird here...
				return 0;
			return ((NodeP) n0).genericIndex() - ((NodeP) n1).genericIndex();
		}
	}
	
	static class PathComparator implements Comparator<PathElement>
	{
		@Override
		public int compare(PathElement el1, PathElement el2)
		{
			if(el1.pathlength != el2.pathlength)
				return -(el1.pathlength - el2.pathlength); // longest path first
			return new NodeInAlphaComparator().compare(el1.node, el2.node);
			// return (el1.distance - el2.distance); // longest path first // old alternative
		}
	}
	
	public class PathElement
	{
		// FIXME: fix access and remove public qualifiers
		public Node			  node		  = null;
		int					  distance	  = 0;							// distance from firstNode / subgraph
																				// root
		PathElement			  parent		= null;
		int					  treeOrder	 = -1;						   // order of the sub-tree containing
																				// this
																				// element
		public List<PathElement> children	  = new LinkedList<PathElement>();
		public List<PathElement> otherChildren = new LinkedList<PathElement>();
		int					  pathlength	= -1;						   // distance to farthest leaf
																				
		public PathElement(Node _node, int _distance, PathElement _parent)
		{
			this.node = _node;
			this.distance = _distance;
			this.parent = _parent;
		}
		
		public boolean pathContains(PathElement el1)
		{
			PathElement el = this;
			while(el.parent != null)
			{
				if(el == el1)
					return true;
				el = el.parent;
			}
			return (el == el1);
		}
		
		@Override
		public String toString()
		{
			if(node == null)
				return "<corrupt>";
			return node.toString() + "(" + distance + ":" + (parent != null ? parent.node.toString() : "-")
					+ (!children.isEmpty() ? "/" + children.size() : ".")
					+ (otherChildren.isEmpty() ? "" : "+" + otherChildren.size()) + "/" + pathlength + ")";
		}
	}
	
	public static class GraphConfig extends GraphRepresentation.GraphConfig
	{
		boolean isBackwards = false;
		
		public GraphConfig(Graph thegraph)
		{
			super(thegraph);
		}
		
		public GraphConfig setBackwards()
		{
			isBackwards = true;
			return this;
		}
		
		public GraphConfig setBackwards(boolean back)
		{
			isBackwards = back;
			return this;
		}
		
		protected boolean isBackwards()
		{
			return isBackwards;
		}
		
		@Override
		protected String setDefaultName(String name)
		{
			return super.setDefaultName(name) + "Lin";
		}
	}
	
	protected List<Node>		sortedNodes = null;
	protected List<PathElement> paths	   = null;
	
	/**
	 * Builds a new {@link LinearGraphRepresentation} for the specified graph.
	 */
	public LinearGraphRepresentation(GraphConfig conf)
	{
		super(conf);
	}
	
	@Override
	protected void processGraph()
	{
		sortedNodes = new LinkedList<Node>(theGraph.getNodes());
		Collections.sort(sortedNodes, new NodeInAlphaComparator());
		
		buildPaths();
	}
	
	protected void buildPaths()
	{
		GraphConfig conf = (GraphConfig) this.config;
		
		Queue<PathElement> grayNodes = new LinkedList<PathElement>();
		Queue<PathElement> blackNodes = new LinkedList<PathElement>();
		
		while(blackNodes.size() < sortedNodes.size())
		{
			for(Node node : sortedNodes)
			{
				boolean found = false;
				for(PathElement el : blackNodes)
					if(el.node == node)
						found = true;
				if(!found)
				{
					grayNodes.add(new PathElement(node, 0, null));
					break;
				}
			}
			
			while(!grayNodes.isEmpty())
			{
				PathElement el = grayNodes.poll();
				log.trace("taking element " + el);
				// expand
				for(Node n1 : (conf.isBackwards ? el.node.inList() : el.node.outList()))
				{
					boolean towardsoutside = false;
					if(!conf.isBackwards && !theGraph.contains(el.node.getEdgeTo(n1)))
						continue; // the edge is not in the graph displayed.
					if(conf.isBackwards && !theGraph.contains(el.node.getEdgeFrom(n1)))
						continue; // the edge is not in the graph displayed.
					if(!theGraph.contains(n1))
						// the edge is in the graph, but the other node is not
						towardsoutside = true;
					
					PathElement el1 = null;
					boolean wasinblacknodes = false;
					for(PathElement eli : grayNodes)
						if(eli.node == n1)
							el1 = eli;
					for(PathElement eli : blackNodes)
						if(eli.node == n1)
						{
							el1 = eli;
							wasinblacknodes = true;
							log.trace("(element " + el1 + " was black)");
						}
					if(el1 == null)
					{ // new node, add new PathElement
						el1 = new PathElement(n1, el.distance + 1, el);
						if(!towardsoutside)
						{
							log.trace("new gray node added: " + el1 + " of " + el);
							grayNodes.add(el1);
							el.children.add(el1);
						}
						else
						{
							el.otherChildren.add(el1);
						}
					}
					else if(el.pathContains(el1))
					{ // cycle detected -> not good / no add
						log.trace("cycle detected for " + el1);
						if(!el.otherChildren.contains(el1))
							el.otherChildren.add(el1);
					}
					else if(el.distance + 1 > el1.distance)
					{ // new, longer path detected -> update
						if(el1.parent != null)
						{
							el1.parent.children.remove(el1);
							el1.parent.otherChildren.add(el1);
						}
						el1.distance = el.distance + 1;
						el1.parent = el;
						if(el.otherChildren.contains(el1))
							el.otherChildren.remove(el1);
						el.children.add(el1);
						if(wasinblacknodes)
						{
							blackNodes.remove(el1);
							grayNodes.add(el1);
						}
						log.trace("element reinserted: " + el1);
					}
					else
					{ // new distance would not be longer, leave alone
						log.trace("element not reinserted");
						if(!el.otherChildren.contains(el1))
							el.otherChildren.add(el1);
					}
				}
				blackNodes.add(el);
			}
			log.info("build paths done");
			
			for(PathElement el : blackNodes)
				if(el.children.isEmpty())
				{ // leaf
					el.pathlength = 0;
					PathElement eli = el, elp;
					while(eli.parent != null)
					{
						elp = eli.parent;
						if(elp.pathlength < eli.pathlength + 1)
							// update
							elp.pathlength = eli.pathlength + 1;
						eli = elp;
					}
				}
		}
		log.info("measure paths done");
		log.trace("path_element : [children] / [otherchildren]");
		
		for(PathElement el : blackNodes)
		{
			Set<PathElement> marked = new HashSet<LinearGraphRepresentation.PathElement>();
			for(PathElement oth : el.otherChildren)
				if(!oth.pathContains(el) && (oth.parent != null) && (el.pathlength > oth.parent.pathlength))
				// 1) if the other child is already having the element as ancestor, then it is already on the main path
				// and it is a forward link
				// 2) if the parent of the other child is null, then it is already root of the main path (and it is a
				// backlink)
				// 3) switch if the other child is outside of the main path, and this way it would be closer to the main
				// path
				{ // switch
					log.trace("switching " + oth.toString()
							+ ((oth.parent != null) ? (" from " + oth.parent.toString()) : "") + " to " + el.toString());
					if(oth.parent != null)
					{
						oth.parent.children.remove(oth);
						oth.parent.otherChildren.add(oth);
					}
					oth.parent = el;
					oth.distance = el.distance + 1; // FIXME distance should be propagated
					marked.add(oth);
				}
			for(PathElement oth : marked)
			{
				el.otherChildren.remove(oth);
				el.children.add(oth);
			}
			Collections.sort(el.children, new PathComparator());
			Collections.sort(el.otherChildren, new PathComparator());
			log.trace(el.toString() + ": " + el.children.toString() + " / " + el.otherChildren.toString());
		}
		paths = new LinkedList<PathElement>(blackNodes);
		Collections.sort(paths, new PathComparator());
		
		log.info("sort paths done");
		
		log.info("[node_name ( distance_from_root : parent_or_dash_if_root / number_of_children_or_dot_if_none +number_of_other_children)]"
				+ "/ path_length_from_node)]");
		log.info(paths.toString());
	}
}
