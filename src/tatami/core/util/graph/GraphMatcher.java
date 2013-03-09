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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;



import tatami.core.interfaces.Logger.Level;
import tatami.core.util.graph.GraphPattern.EdgeP;
import tatami.core.util.graph.GraphPattern.NodeP;
import tatami.core.util.graph.representation.TextGraphRepresentation;
import tatami.core.util.logging.Unit;

public class GraphMatcher extends Unit
{
	public static class Match
	{
		Graph					targetGraphLink;		// G
		GraphPattern			patternLink;			// GP
		// int matchIndex;
		Graph					matchedGraph;			// G'
		GraphPattern			solvedPart;			// GmP
		GraphPattern			unsolvedPart;			// GxP
		int						k;						// k (missing edges)
														
		Map<NodeP, Node>		nodeFunction;			// : VmP -> V'
		Map<EdgeP, List<Edge>>	edgeFunction;			// : EmP -> E'
		Map<NodeP, Integer>		frontier		= null; // nodes are subset of VmP
		Set<Match>				matchCandidates	= null;
		
		String					id				= "-";
		
		/**
		 * Create a new empty match; some parts may be uninitialized / undefined (like frontier, or matchCandidates)
		 * 
		 * <p>
		 * This constructor is meant just to construct matches that will later be completely initialized as a result of merging two existing matches.
		 */
		public Match(Graph g, GraphPattern p)
		{
			targetGraphLink = g;
			patternLink = p;
		}
		
		/**
		 * Create a match,using an initial matching edge
		 * 
		 * @param g
		 *            : the graph
		 * @param p
		 *            : the pattern
		 * @param e
		 *            : the matching edge in the graph
		 * @param eP
		 *            : the matching edge in the pattern
		 * @param id
		 *            : the matching graph edge's id
		 */
		public Match(Graph g, GraphPattern p, Edge e, EdgeP eP, String id)
		{
			targetGraphLink = g;
			patternLink = p;
			
			matchedGraph = new Graph().addNode(e.from).addNode(e.to).addEdge(e);
			solvedPart = (GraphPattern)new GraphPattern().addNode((NodeP)eP.from, false).addNode((NodeP)eP.to, false).addEdge(eP);
			nodeFunction = new HashMap<NodeP, Node>();
			nodeFunction.put((NodeP)eP.from, e.from);
			nodeFunction.put((NodeP)eP.to, e.to);
			frontier = new HashMap<GraphPattern.NodeP, Integer>();
			frontier.put((NodeP)eP.from, new Integer(eP.from.inEdges.size() + eP.from.outEdges.size() - 1));
			frontier.put((NodeP)eP.to, new Integer(eP.to.inEdges.size() + eP.to.outEdges.size() - 1));
			edgeFunction = new HashMap<EdgeP, List<Edge>>();
			List<Edge> eL = new Vector<Edge>(1, 0);
			eL.add(e);
			edgeFunction.put(eP, eL);
			
			unsolvedPart = new GraphPattern();
			for(Node vP : p.nodes)
				if((vP != eP.from) && (vP != eP.to))
					unsolvedPart.addNode((NodeP)vP, false);
			for(Edge ePi : p.edges)
				if(ePi != eP)
					unsolvedPart.addEdge(ePi);
			k = unsolvedPart.edges.size();
			
			matchCandidates = new HashSet<Match>();
			
			this.id = id;
		}
		
		@Override
		public String toString()
		{
			String ret = "match [" + id + "] (k=" + k + "): ";
			ret += new TextGraphRepresentation(new TextGraphRepresentation.GraphConfig(matchedGraph).setLayout("", " ", 2)) + " : ";
			ret += new TextGraphRepresentation(new TextGraphRepresentation.GraphConfig(solvedPart).setLayout("", " ", 2)) + "\n\t";
			ret += "Gx: " + new TextGraphRepresentation(new TextGraphRepresentation.GraphConfig(unsolvedPart).setLayout("", " ", 2)) + "\n\t";
			ret += "frontier: " + frontier + "; ";
			ret += "mCs: [";
			for(Match mi : matchCandidates)
				ret += mi.id + ", ";
			ret += "] \n\t";
			ret += "fv: " + nodeFunction + "\n\t";
			// ret += "fe: " + edgeFunction + "\n\t";
			return ret;
		}
		
		public String toStringLong()
		{
			String ret = "match: \n\t";
			ret += "G': " + new TextGraphRepresentation(new TextGraphRepresentation.GraphConfig(matchedGraph).setLayout("", " ", 2)) + "\n\t";
			ret += "Gm: " + new TextGraphRepresentation(new TextGraphRepresentation.GraphConfig(solvedPart).setLayout("", " ", 2)) + "\n\t";
			ret += "Gx: " + new TextGraphRepresentation(new TextGraphRepresentation.GraphConfig(unsolvedPart).setLayout("", " ", 2)) + "\n\t";
			ret += "fv: " + nodeFunction + "\n\t";
			ret += "fe: " + edgeFunction + "\n\t";
			ret += "k=" + k;
			
			return ret;
		}
	}
	
	Graph			graph;
	GraphPattern	pattern;
	
	public GraphMatcher(Graph graph, GraphPattern pattern)
	{
		super(new UnitConfigData().setName(Unit.DEFAULT_UNIT_NAME).setLevel(Level.ALL));
		this.graph = graph;
		this.pattern = pattern;
	}
	
	/**
	 * 
	 * @return number of matches found
	 */
	public int doMatching()
	{
		SortedSet<Node> vertexSet = new TreeSet<Node>(new Comparator<Node>() {
			@Override
			public int compare(Node n1, Node n2)
			{
				int out1 = n1.outEdges.size() - n1.inEdges.size();
				int out2 = n2.outEdges.size() - n2.inEdges.size();
				if(out1 != out2)
					return -(out1 - out2);
				else
					return n1.label.compareTo(n2.label);
			}
		});
		vertexSet.addAll(pattern.getNodes());
		log.trace("sorted vertex set: " + vertexSet);
		Node vMP = vertexSet.first();
		log.trace("start vertex: " + vMP);
		final Map<Node, Integer> distances = pattern.computeDistancesFromUndirected(vMP);
		log.trace("vertex distances: " + distances);
		
		// match sorter queue
		PriorityQueue<Match> matchQueue = new PriorityQueue<Match>(1, new Comparator<Match>() {
			@Override
			public int compare(Match m1, Match m2)
			{
				if((m1.solvedPart.m() == 1) && (m2.solvedPart.m() == 1))
				{
					Edge e1 = m1.solvedPart.edges.iterator().next();
					Edge e2 = m2.solvedPart.edges.iterator().next();
					int result = Math.min(distances.get(e1.from).intValue(), distances.get(e1.to).intValue()) - Math.min(distances.get(e2.from).intValue(), distances.get(e2.to).intValue());
					// log.trace("compare (" + result + ") " + e1 + " : " + e2);
					if(result != 0)
						return result;
					else
						return m1.id.compareTo(m2.id);
				}
				else
					return m1.unsolvedPart.size() - m2.unsolvedPart.size();
			}
		});
		
		// /////// build initial matches
		
		int edgeId = 0;
		SortedSet<Edge> sortedEdges = new TreeSet<Edge>(new Comparator<Edge>() {
			@Override
			public int compare(Edge e1, Edge e2)
			{
				return e1.label.compareTo(e2.label);
			}
			
		});
		sortedEdges.addAll(pattern.edges);
		for(Edge ePi : sortedEdges)
		{
			EdgeP eP = (EdgeP)ePi;
			if(!eP.generic)
			{
				int matchId = 0;
				for(Edge e : graph.edges)
				{
					// log.trace("trying edges: " + eP + " : " + e);
					if(isMatch(eP, e))
					{
						Match m = new Match(graph, pattern, e, eP, edgeId + ":" + matchId);
						addMatchToQueue(m, matchQueue);
						log.info("new initial match: " + m.solvedPart.edges.iterator().next() + " : " + m.matchedGraph.edges.iterator().next());
						matchId++;
					}
				}
				log.trace("edge " + eP + " has id [" + edgeId + "]");
				edgeId++;
			}
		}
		
		log.trace("initial matches (" + matchQueue.size() + "): " + matchQueue + "-------------------------");
		
		while(!matchQueue.isEmpty())
		{
			Match m = matchQueue.poll();
			for(Iterator<Match> itm = m.matchCandidates.iterator(); itm.hasNext();)
			{
				Match mc = itm.next();
				itm.remove();
				mc.matchCandidates.remove(m);
				log.trace("merging " + m + " and " + mc);
				Match mr = merge(m, mc);
				if(mr != null)
					addMatchToQueue(mr, matchQueue);
			}
		}
		
		return 0; // FIXME
	}
	
	protected Match merge(Match m1, Match m2)
	{
		GraphPattern pt = m1.patternLink;
		Match newM = new Match(m1.targetGraphLink, pt);
		
		newM.unsolvedPart = new GraphPattern();
		newM.unsolvedPart.edges.addAll(pt.edges);
		newM.unsolvedPart.nodes.addAll(pt.nodes);
		newM.k = newM.unsolvedPart.edges.size();
		
		Set<Edge> totalMatch = new HashSet<Edge>();
		totalMatch.addAll(m1.solvedPart.edges);
		totalMatch.addAll(m2.solvedPart.edges);
		
		for(Edge e : totalMatch)
		{
			EdgeP eP = (EdgeP)e;
			boolean fitted = false;
			newM.solvedPart.addEdge(eP).addNode(eP.from).addNode(eP.to);
			newM.unsolvedPart.removeEdge(eP).removeNode(eP.from).removeNode(eP.to);
			newM.k--;
			if(newM.frontier == null)
				newM.frontier = new HashMap<GraphPattern.NodeP, Integer>();
			// newM.frontier.put((NodeP)eP.from, new Integer(eP.from.inEdges.size() + eP.from.outEdges.size() - 1)); // FIXME
			// newM.frontier.put((NodeP)eP.to, new Integer(eP.to.inEdges.size() + eP.to.outEdges.size() - 1)); // FIXME
			
			if(m1.solvedPart.contains(eP))
			{
				fitted = true;
				for(Edge em : m1.edgeFunction.get(eP))
					newM.matchedGraph.addEdge(em).addNode(em.from).addNode(em.to);
			}
			if(m2.solvedPart.contains(eP))
			{
				if(fitted)
				{
					log.error("match-intersection pattern edge found: [" + eP + "]");
					throw new IllegalArgumentException("match-intersection edge");
				}
				fitted = true;
			}
		}
		
		return null;
	}
	
	protected void addMatchToQueue(Match m, PriorityQueue<Match> queue)
	{
		for(Match mi : queue)
			if(!new HashSet<Edge>(m.solvedPart.edges).removeAll(mi.solvedPart.edges)) // check that the two matches don't intersect
				if(!new HashSet<Edge>(m.matchedGraph.edges).removeAll(mi.matchedGraph.edges))
					for(Map.Entry<NodeP, Integer> frontierV : mi.frontier.entrySet())
						if(m.frontier.containsKey(frontierV.getKey()) && (m.nodeFunction.get(frontierV.getKey()) == mi.nodeFunction.get(frontierV.getKey())))
						{
							m.matchCandidates.add(mi);
							mi.matchCandidates.add(m);
						}
		queue.add(m);
		
	}
	
	protected boolean isMatch(EdgeP eP, Edge e)
	{
		if(!((NodeP)eP.from).generic && !eP.from.label.equals(e.from.label))
			return false;
		if(!((NodeP)eP.to).generic && !eP.to.label.equals(e.to.label))
			return false;
		
		if(!eP.generic)
		{
			if((eP.label == null) || (eP.label.equals(e.label)))
				return true;
			else if((e.label == null) || (e.label.equals("")))
				return true;
			else
				return false;
		}
		else
			return false; // TODO // FIXME
	}
}
