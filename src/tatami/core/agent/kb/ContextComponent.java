/*******************************************************************************
 * Copyright (C) 2015 Andrei Olaru, Marius-Tudor Benea, Nguyen Thi Thuy Nga, Amal El Fallah Seghrouchni, Cedric Herpson.
 * 
 * This file is part of tATAmI-PC.
 * 
 * tATAmI-PC is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * tATAmI-PC is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with tATAmI-PC.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package tatami.core.agent.kb;

import java.nio.channels.ReadPendingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.collections.Transformer;

import tatami.core.agent.kb.simple.SimpleKnowledge;
import net.xqhs.graphs.context.CCMImplementation;
import net.xqhs.graphs.context.ContextGraph;
import net.xqhs.graphs.context.ContextPattern;
import net.xqhs.graphs.context.ContinuousContextMatchingPlatform;
import net.xqhs.graphs.context.ContinuousMatchingProcess.MatchNotificationReceiver;
import net.xqhs.graphs.context.Instant;
import net.xqhs.graphs.context.Instant.Offset;
import net.xqhs.graphs.context.Instant.TickReceiver;
import net.xqhs.graphs.context.Instant.TimeKeeper;
import net.xqhs.graphs.graph.Edge;
import net.xqhs.graphs.graph.Graph;
import net.xqhs.graphs.graph.GraphComponent;
import net.xqhs.graphs.graph.SimpleGraph;
import net.xqhs.graphs.matcher.GraphMatcherQuick;
import net.xqhs.graphs.matcher.GraphMatchingProcess;
import net.xqhs.graphs.matcher.Match;
import net.xqhs.graphs.matcher.MonitorPack;
import net.xqhs.graphs.pattern.GraphPattern;
import net.xqhs.graphs.representation.text.TextGraphRepresentation;
import net.xqhs.graphs.util.ContentHolder;

public class ContextComponent extends CognitiveComponent implements KnowledgeBase { // TODO extend Cognitive Component

	/**
	 * The serial UID.
	 */
	private static final long						serialVersionUID		= -7541956453166819418L;
	
	protected Graph knowledgeGraph;
	protected ContinuousContextMatchingPlatform continuousMatching;
	
	/**
	 * Constructs a new instance of context component.
	 */
	public ContextComponent() 
	{
		super();
	
		// make ticker
		TimeKeeper ticker = new IntTimeKeeper();
						
		// prepare CCM
		MonitorPack monitor = new MonitorPack();
		continuousMatching = new CCMImplementation(ticker, monitor);
		
		knowledgeGraph = new ContextGraph();
		continuousMatching.setContextGraph((ContextGraph) knowledgeGraph);
		continuousMatching.startContinuousMatching();
	}
	
	/**
	 * Constructs a new instance of context component
	 * 
	 * @param initialKnowledge
	 */
	public ContextComponent(HashSet<Entry<String, String>> initialKnowledge) {
		this();
				
		String initial = initialKnowledge.iterator().next().getValue();
		Graph g = new TextGraphRepresentation(new SimpleGraph()).readRepresentation(new ContentHolder<String>(initial));
		add(g);
	}
	
	/**
	 * Adds new knowledge to knowledgeGraph
	 * 
	 * @param newKnowledge
	 */
	public void add(Graph newKnowledge)
	{	
		for (GraphComponent component : newKnowledge.getComponents())
			if (!knowledgeGraph.contains(component))
				knowledgeGraph.add(component);	
	}
	
	/**
	 * Removes from knowledge graph the information that it is not needed
	 * */
	public void remove(String deleteKnowledge) {
		GraphPattern CP = new GraphPattern();
		TextGraphRepresentation repr = new TextGraphRepresentation(CP);
		repr.readRepresentation(new ContentHolder<String>(deleteKnowledge));
		
		Match m = read(CP);
		// there is no information to remove
		if (m == null) 
			return;
		
		for (Edge e : CP.getEdges()) {
			knowledgeGraph.removeEdge(m.getMatchedGraphEdges(e).get(0));
		}
		
	}
	
	public Match read(GraphPattern pattern)
	{
		GraphMatchingProcess GMQ = GraphMatcherQuick.getMatcher(knowledgeGraph, pattern, new MonitorPack());
		GMQ.resetIterator(0);
		
		Match m = GMQ.getNextMatch();
		return m;
	}
	
	public List<Match> readAll(GraphPattern pattern)
	{
		ArrayList<Match> matches = new ArrayList<Match>();
		GraphMatchingProcess GMQ = GraphMatcherQuick.getMatcher(knowledgeGraph, pattern, new MonitorPack());
		GMQ.resetIterator(0);
		
		Match m = GMQ.getNextMatch();
		while (m != null) {
			matches.add(m);
			m = GMQ.getNextMatch();
		}
		
		return GMQ.getAllMatches(0);
		//return GMQ.getAllCompleteMatches();
		//return matches;
	}
	
	/**
	 * 
	 * @return the graph that contains the knowledge
	 * */
	public Graph getKnowledge() 
	{
		return knowledgeGraph;
	}
	
	/**
	 * 
	 * @return continuous matching platform
	 * */
	public ContinuousContextMatchingPlatform getContinuousMatching() 
	{
		return continuousMatching;
	}
	
	/**
	 * Adds new pattern to continuousMatching
	 *
	 * @param pattern
	 */
	public void addPattern(GraphPattern pattern) 
	{
		continuousMatching.addContextPattern((ContextPattern) pattern);
	}
	
	/**
	 * Starts continuous matching 
	 */
	public void startContinuousMatching() 
	{
		continuousMatching.startContinuousMatching();
	}
	
	/**
	 * Adds a notification target for matches of the specified pattern.
	 * 
	 * @param pattern
	 * @param receiver
	 */
	public void registerMatchNotificationTarget(ContextPattern pattern, MatchNotificationReceiver receiver) 
	{
		continuousMatching.addMatchNotificationTarget(pattern, receiver);
	}
	
	public void registerMatchNotificationTarget(int thresholdK, MatchNotificationReceiver receiver)
	{
		continuousMatching.addMatchNotificationTarget(thresholdK, receiver);
	}
	
	public void registerMatchNotificationTarget(MatchNotificationReceiver receiver)
	{
		continuousMatching.addMatchNotificationTarget(receiver);
	}
	
	/** TODO 
	 * maybe move in net.xqhs.Graphs
	 * copied from testing.testing.ContextGraphsTest.java 
	 **/
	public static class IntTimeKeeper implements TimeKeeper
	{
		/**
		 * Tick receivers.
		 */
		Set<TickReceiver>	receivers	= new HashSet<TickReceiver>();
		/**
		 * Time coordinate.
		 */
		long				now			= 0;
		
		@Override
		public void registerTickReceiver(TickReceiver receiver, Offset tickLength)
		{
			receivers.add(receiver);
		}
		
		@Override
		public Instant now()
		{
			return new Instant(now);
		}
		
		/**
		 * Increments the time and notifies receivers (regardless of their tick length preference. //FIXME
		 */
		public void tickUp()
		{
			now++;
			for(TickReceiver rcv : receivers)
				rcv.tick(this, new Instant(now));
		}
	}
	
	private void initFromDescription(SimpleGraph graph, KnowledgeDescription description) {
		// append all elements together and hope it looks like a graph.
		if (!(description instanceof SimpleKnowledge)) {
			// TODO(catalinb): This should be enforced with proper argument typing.
			throw new IllegalArgumentException("Unsupported knowledge description.");
		}
		SimpleKnowledge knowledge = (SimpleKnowledge) description;
		String knowledgeText = "";
		for (String component : knowledge.getSimpleKnowledge()) {
			knowledgeText  = knowledgeText + component;
		}
		TextGraphRepresentation textRepr = new TextGraphRepresentation(graph);
		textRepr.readRepresentation(knowledgeText);
	}

	@Override
	public boolean add(KnowledgeDescription piece) {
		SimpleGraph graph = new SimpleGraph();
		initFromDescription(graph, piece);
		
		add(graph);
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends KnowledgeDescription> piece) {
		for (KnowledgeDescription description : piece) {
			add(description);
		}
		return true;
	}

	@Override
	public KnowledgeDescription getFirst(KnowledgeDescription pattern) {
		GraphPattern graphPattern = new GraphPattern();
		initFromDescription(graphPattern, pattern);
		
		Match match = read(graphPattern);
		return new SimpleKnowledge(match);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<KnowledgeDescription> getAll(KnowledgeDescription pattern) {
		GraphPattern graphPattern = new GraphPattern();
		initFromDescription(graphPattern, pattern);
		List<Match> matches = readAll(graphPattern);
		
		return ListUtils.transformedList(matches, new Transformer() {
			@Override
			public Object transform(Object obj) {
				Match match = (Match) obj;
				return new SimpleKnowledge(match);
			}
		});
	}

	@Override
	public boolean remove(KnowledgeDescription pattern) {
		GraphPattern graphPattern = new GraphPattern();
		initFromDescription(graphPattern, pattern);
		
		Match match = read(graphPattern);
		if (match == null) {
			return false;
		}
		
		for (GraphComponent component : match.getMatchedGraph().getComponents()) {
			knowledgeGraph.remove(component);
		}

		return true;
	}

	@Override
	public boolean removeAll(KnowledgeDescription pattern) {
		GraphPattern graphPattern = new GraphPattern();
		initFromDescription(graphPattern, pattern);
		
		List<Match> matches = readAll(graphPattern);
		if (matches.size() == 0) {
			return false;
		}
		
		for (Match match : matches) {
			for (GraphComponent component : match.getMatchedGraph().getComponents()) {
				knowledgeGraph.remove(component);
			}
		}
		
		return true;
	}
}
