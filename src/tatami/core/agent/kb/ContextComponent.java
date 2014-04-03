package tatami.core.agent.kb;

import java.util.HashSet;
import java.util.Set;

import net.xqhs.graphs.context.CCMImplementation;
import net.xqhs.graphs.context.ContextGraph;
import net.xqhs.graphs.context.ContextPattern;
import net.xqhs.graphs.context.Instant;
import net.xqhs.graphs.context.ContinuousContextMatchingPlatform.MatchNotificationReceiver;
import net.xqhs.graphs.context.Instant.Offset;
import net.xqhs.graphs.context.Instant.TickReceiver;
import net.xqhs.graphs.context.Instant.TimeKeeper;
import net.xqhs.graphs.graph.Edge;
import net.xqhs.graphs.graph.Graph;
import net.xqhs.graphs.matcher.GraphMatcherQuick;
import net.xqhs.graphs.matcher.GraphMatchingProcess;
import net.xqhs.graphs.matcher.Match;
import net.xqhs.graphs.matcher.MonitorPack;
import net.xqhs.graphs.pattern.GraphPattern;
import tatami.core.agent.AgentComponent;

@SuppressWarnings("javadoc")
public class ContextComponent extends AgentComponent {

	/**
	 * The serial UID.
	 */
	private static final long						serialVersionUID		= -7541956453166819418L;
	
	private Graph knowledgeGraph;
	private CCMImplementation continuousMatching;
	
	/**
	 * Constructs a new instance of context component.
	 */
	public ContextComponent() {
		super(AgentComponentName.COGNITIVE_COMPONENT);
	
		// make ticker
		IntTimeKeeper ticker = new IntTimeKeeper();
						
		// prepare CCM
		MonitorPack monitor = new MonitorPack();
		continuousMatching = new CCMImplementation(ticker, monitor);
		knowledgeGraph = new ContextGraph(continuousMatching);
		continuousMatching.setContextGraph((ContextGraph) knowledgeGraph);
		continuousMatching.startContinuousMatching();
	}
	
	/**
	 * Adds new knowledge to knowledgeGraph
	 * First finds the commune part, then adds the new part
	 * 
	 * @param newKnowledge
	 * @return
	 */
	public void add(Graph newKnowledge) {
		
		/* TODO
		merge(newKnowledge, knowledgeGraph);
		*/
		
		GraphMatchingProcess GMQ = GraphMatcherQuick.getMatcher(knowledgeGraph, 
										(GraphPattern)newKnowledge, new MonitorPack());
		Match match;
		int k = newKnowledge.getNodes().size();
		do {
			GMQ.resetIterator(k);
			match = GMQ.getNextMatch();
		}
		while (match == null);
		
		/* getUnsolvedPart() method should be implemented in net.xqhs.graphs.matcher.Matcher
		 * for (Node node : match.getUnsolvedPart().getNodes()) {
			knowledgeGraph.addNode(node);
		}
		for (Edge edge : match.getUnsolvedPart().getEdges()) {
			knowledgeGraph.addEdge(edge);
		}*/
		
	}
	
	public boolean remove(Graph deleteKnowledge) {
		
		for (Edge edge : deleteKnowledge.getEdges()) {
			knowledgeGraph.removeEdge(edge);
		}
		
		return true;
	}
	
	public Graph getKnowledge() {
		return knowledgeGraph;
	}
	
	/**
	 *
	 * @param pattern
	 */
	public void addPattern(GraphPattern pattern) {
		continuousMatching.addContextPattern((ContextPattern) pattern);
	}
	
	/**
	 * 
	 * 
	 * @param pattern
	 * @return
	 */
	public void registerMatchNotificationTarget(GraphPattern pattern, MatchNotificationReceiver receiver) {
		continuousMatching.setMatchNotificationTarget(pattern, receiver);
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
}
