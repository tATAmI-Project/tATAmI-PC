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
package tatami.core.util.graph;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;



import tatami.core.interfaces.Logger;
import tatami.core.util.graph.GraphPattern.NodeP;
import tatami.core.util.graph.representation.LinearGraphRepresentation;
import tatami.core.util.logging.Unit;

/**
 * <p>
 * Represents a directed graph. Subclasses: {@link Node} and {@link Edge}.
 * 
 * <p>
 * Functions that modify the graph return the graph itself, so that chained calls are possible.
 * 
 * <p>
 * This class should only be used as a data structure. Visualization should happen elsewhere (for instance, in {@link LinearGraphRepresentation}.
 * 
 * <p>
 * Currently only supports adding of new nodes and edges.
 * 
 * <p>
 * Warning: if a graph contains the edge, id does not necessarily contain any of the nodes of the edge. It may be that the nodes have not been added to the
 * graph. This is because this graph may be a subgraph of a larger graph.
 * 
 * @author Andrei Olaru
 * 
 */
public class Graph extends Unit
{
	protected Set<Node>	nodes	= null;
	protected Set<Edge>	edges	= null;
	
	/**
	 * Generates an empty graph.
	 */
	public Graph()
	{
		this(null);
	}
	
	public Graph(UnitConfigData unitConfig)
	{
		super(unitConfig);
		nodes = new HashSet<Node>();
		edges = new HashSet<Edge>();
	}
	
	public Graph addNode(Node node)
	{
		if(node == null)
			throw new IllegalArgumentException("null nodes not allowed");
		if(!nodes.add(node))
			log.warn("node [" + node.toString() + "] already present.");
		return this;
	}
	
	/**
	 * Warning: the function will not add the nodes to the graph, only the edge between them. Nodes must be added separately.
	 * 
	 * @param edge
	 *            : the edge to add
	 * @return the updated graph
	 */
	public Graph addEdge(Edge edge)
	{
		if(edge == null)
			throw new IllegalArgumentException("null edges not allowed");
		if(!edges.add(edge))
			log.warn("edge [" + edge.toString() + "] already present.");
		return this;
	}
	
	public Graph removeNode(Node node)
	{
		if(!nodes.remove(node))
			log.warn("node[" + node + "] not contained");
		return this;
	}
	
	public Graph removeEdge(Edge edge)
	{
		if(!edges.remove(edge))
			log.warn("edge [" + edge + "] not contained");
		return this;
	}
	
	public int n()
	{
		return nodes.size();
	}
	
	public int m()
	{
		return edges.size();
	}
	
	public int size()
	{
		return n();
	}
	
	public boolean contains(Edge e)
	{
		return edges.contains(e);
	}
	
	public boolean contains(Node node)
	{
		return nodes.contains(node);
	}
	
	public Collection<Node> getNodesNamed(String name)
	{
		Collection<Node> ret = new HashSet<Node>();
		for(Node node : nodes)
			if(node.label.equals(name))
				ret.add(node);
		return ret;
	}
	
	public Collection<Node> getNodes()
	{
		return nodes;
	}
	
	public Collection<Edge> getEdges()
	{
		return edges;
	}
	
	/**
	 * Checks if the inEdges and outEdges lists of the nodes are coherent with the edges list of the graph
	 * 
	 * @return true if the graph is coherent with respect to the above principle.
	 */
	@SuppressWarnings("static-method")
	public boolean isCoherent()
	{
		// TODO
		return true;
	}
	
	protected Map<Node, Integer> computeDistancesFromUndirected(Node node)
	{
		if(!nodes.contains(node))
			throw new IllegalArgumentException("node " + node + " is not in graph");
		Map<Node, Integer> dists = new HashMap<Node, Integer>();
		Queue<Node> grayNodes = new LinkedList<Node>();
		Set<Node> blackNodes = new HashSet<Node>();
		grayNodes.add(node);
		dists.put(node, new Integer(0));
		
		while(!grayNodes.isEmpty())
		{
			Node cNode = grayNodes.poll();
			int dist = dists.get(cNode).intValue();
			blackNodes.add(cNode);
			
			for(Edge e : cNode.outEdges)
				if(!blackNodes.contains(e.to))
				{
					if(!grayNodes.contains(e.to))
						grayNodes.add(e.to);
					if(!dists.containsKey(e.to) || (dists.get(e.to).intValue() > (dist + 1)))
						dists.put(e.to, new Integer(dist + 1));
				}
			for(Edge e : cNode.inEdges)
				if(!blackNodes.contains(e.from))
				{
					if(!grayNodes.contains(e.from))
						grayNodes.add(e.from);
					if(!dists.containsKey(e.from) || (dists.get(e.from).intValue() > (dist + 1)))
						dists.put(e.from, new Integer(dist + 1));
				}
		}
		
		return dists;
	}

	@Override
	public String toString()
	{
		String ret = "";
		ret += "G[" + n() + ", " + m() + "] ";
		List<Node> list = new ArrayList<Node>(nodes);
		Collections.sort(list, new Node.NodeAlphaComparator());
		int first = 0;
		ret += "[";
		for(Node node : list)
			if(node instanceof NodeP && ((NodeP)node).isGeneric())
				ret += (first++ > 0 ? ", " : "") + node.toString() + "(generic)";
			else
				ret += (first++ > 0 ? ", " : "") + node.toString();
		ret += "]";
//		for(Node node : list)
//			ret += "\n" + node.getLabel() + ": " + node.inEdges + "  :  " + node.outEdges;
		for(Edge e : edges)
			ret += "\n" + e.toString();
		return ret;
	}
	
	public String toDot()
	{
		String ret = "digraph G {\n";
		for(Edge edge : edges)
		{
			String fromNode = edge.getFrom().toString();
			String toNode = edge.getTo().toString();
//			if(fromNode.contains(" "))
//				fromNode = fromNode.replace(' ', '_');
//			if(toNode.contains(" "))
//				toNode = toNode.replace(' ', '_');
			ret += "\t";
			ret += "\"" + fromNode + "\"";
			ret += " -> ";
			ret += "\"" + toNode + "\"";
			if(edge.getLabel() != null)
				ret += " [" + "label=\"" + edge.getLabel() + "\"]";
			ret += ";\n";
		}
		for(Node node : nodes)
		{
			if(node instanceof NodeP && ((NodeP)node).isGeneric())	// fairly redundant as all NodeP instances are generic
				ret += "\t\"" + node.toString() + "\" [label=\"" + node.getLabel() + "\"];\n";
//			if(node.getLabel().contains(" "))
//				ret += "\t" + node.getLabel().replace(' ', '_') + " [label=\"" + node.getLabel() + "\"];\n";
		}
		ret += "}";
		return ret;
	}

	public static Graph readFrom(InputStream input)
	{
		return readFrom(input, null);
	}
	
	public static Graph readFrom(InputStream input, UnitConfigData unitConfig)
	{
		Graph g = new Graph(unitConfig);
		Logger log = g.log;
		Scanner scan = new Scanner(input);
		while(scan.hasNextLine())
		{
			String line = scan.nextLine();
			String edgeReads[] = line.split(";");
			for(String edgeRead : edgeReads)
			{
				log.trace("new edge: " + edgeRead);
				
				String[] parts1 = edgeRead.split("-", 2);
				if(parts1.length < 2)
				{
					log.error("input corrupted");
					continue;
				}
				String node1name = parts1[0].trim();
				String node2name = null;
				String edgeName = null;
				boolean bidirectional = false;
				String[] parts2 = parts1[1].split(">");
				if((parts2.length < 1) || (parts2.length > 2))
				{
					log.error("input corrupted");
					continue;
				}
				
				Node node1 = null;
				Node node2 = null;
				
				if(parts2.length == 2)
				{
					edgeName = parts2[0].trim();
					node2name = parts2[1].trim();
				}
				else
				{
					bidirectional = true;
					parts2 = parts1[1].split("-");
					if(parts2.length == 2)
					{
						edgeName = parts2[0].trim();
						node2name = parts2[1].trim();
					}
					else
						node2name = parts2[0].trim();
				}
				if((edgeName != null) && (edgeName.length() == 0))
					edgeName = null;
				// log.trace("[" + parts1.toString() + "] [" + parts2.toString() + "]");
				log.trace("[" + node1name + "] [" + node2name + "] [" + edgeName + "]");
				
				if(g.getNodesNamed(node1name).isEmpty())
				{
					node1 = new Node(node1name);
					g.addNode(node1);
				}
				else
					node1 = g.getNodesNamed(node1name).iterator().next();
				
				if(g.getNodesNamed(node2name).isEmpty())
				{
					node2 = new Node(node2name);
					g.addNode(node2);
				}
				else
					node2 = g.getNodesNamed(node2name).iterator().next();
				
				g.addEdge(new Edge(node1, node2, edgeName));
				if(bidirectional)
					g.addEdge(new Edge(node2, node1, edgeName));
			}
		}
		
		return g;
	}
}
