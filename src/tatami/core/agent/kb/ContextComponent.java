package tatami.core.agent.kb;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

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
import tatami.core.agent.AgentComponent;

@SuppressWarnings("javadoc")
public class ContextComponent extends AgentComponent {

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
		super(AgentComponentName.COGNITIVE_COMPONENT);
	
		// make ticker
		TimeKeeper ticker = new IntTimeKeeper();
						
		// prepare CCM
		MonitorPack monitor = new MonitorPack();
		continuousMatching = new CCMImplementation(ticker, monitor);
		
		knowledgeGraph = new ContextGraph((CCMImplementation) continuousMatching);
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
	public void remove(Graph deleteKnowledge) 
	{	
		for (Edge edge : deleteKnowledge.getEdges()) {
			knowledgeGraph.removeEdge(edge);
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
		return matches;
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
