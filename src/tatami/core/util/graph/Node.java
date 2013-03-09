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

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import tatami.core.util.graph.GraphPattern.NodeP;

public class Node extends GraphComponent
{
	public static class NodeAlphaComparator implements Comparator<Node>
	{
		@Override
		public int compare(Node n0, Node n1)
		{
			if(n0 instanceof NodeP && ((NodeP)n0).isGeneric() && n0 instanceof NodeP && ((NodeP)n0).isGeneric() && n0.label.equals(n1.label))
				return ((NodeP)n0).genericIndex() - ((NodeP)n1).genericIndex();
			return n0.label.compareTo(n1.label);
		}
	}
	
	protected String	label		= null;
	protected Set<Edge>	outEdges	= null;
	protected Set<Edge>	inEdges		= null;
	
	public Node(String nodeLabel)
	{
		this.label = nodeLabel;
		this.outEdges = new HashSet<Edge>();
		this.inEdges = new HashSet<Edge>();
	}
	
	public Set<Node> outList()
	{
		Set<Node> ret = new HashSet<Node>();
		for(Edge e : outEdges)
			ret.add(e.to);
		return ret;
	}
	
	public Set<Node> inList()
	{
		Set<Node> ret = new HashSet<Node>();
		for(Edge e : inEdges)
			ret.add(e.from);
		return ret;
	}
	
	public String getLabel()
	{
		return label;
	}
	
	public Set<Edge> getOutEdges()
	{
		return outEdges;
	}
	
	public Set<Edge> getInEdges()
	{
		return inEdges;
	}
	
	public Edge getEdgeTo(Node outNode)
	{
		for(Edge e : outEdges)
			if(e.to == outNode)
				return e;
		return null;
	}
	
	public Edge getEdgeFrom(Node inNode)
	{
		for(Edge e : inEdges)
			if(e.from == inNode)
				return e;
		return null;
	}
	
	@Override
	public String toString()
	{
		return this.label;
	}
}
