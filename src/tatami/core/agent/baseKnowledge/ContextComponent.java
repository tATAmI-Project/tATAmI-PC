package tatami.core.agent.baseKnowledge;

import java.util.HashMap;
import java.util.Map;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import com.sun.corba.se.impl.orbutil.graph.Node;

import net.xqhs.graphs.graph.ConnectedNode;
import net.xqhs.graphs.graph.Edge;
import net.xqhs.graphs.graph.Graph;
import net.xqhs.graphs.matcher.GraphMatcherQuick;
import net.xqhs.graphs.matcher.GraphMatchingProcess;
import net.xqhs.graphs.matcher.Match;
import net.xqhs.graphs.matcher.MonitorPack;
import net.xqhs.graphs.pattern.GraphPattern;
import net.xqhs.util.logging.Logger.Level;
import net.xqhs.util.logging.LoggerSimple;
import net.xqhs.util.logging.UnitComponent;
import tatami.core.agent.AgentComponent;
import tatami.core.agent.CompositeAgent;
import tatami.core.agent.AgentComponent.AgentComponentName;
import tatami.core.agent.kb.CognitiveComponent;

public class ContextComponent extends AgentComponent {

	/**
	 * The serial UID.
	 */
	private static final long						serialVersionUID		= -7541956453166819418L;
	
	private GraphPattern knowledgeGraph;
	
	/**
	 * Constructs a new instance of context component.
	 */
	public ContextComponent() {
		super(AgentComponentName.COGNITIVE_COMPONENT);
	
		knowledgeGraph = new GraphPattern();
	}
	
	/**
	 * Constructs a new instance of context component.
	 * 
	 * @param file
	 * 				- the initial graph
	 * 				- conference information
	 * 				- maybe the information provided by the participants
	 * @throws FileNotFoundException 
	 */
	public ContextComponent(File file) throws FileNotFoundException {
		super(AgentComponentName.COGNITIVE_COMPONENT);
		knowledgeGraph = (GraphPattern) (((GraphPattern) new GraphPattern().setUnitName("graph"))
				.readFrom(new FileInputStream(file)));
	}
	
	public boolean add(GraphPattern newKnowledge) {
		
		/* TODO in net.xqhs.Graphs or here 
		knowledgeGraph.merge(newKnowledge);
		*/
		
		return true;
	}
	
	public boolean remove(GraphPattern deleteKnowledge) {
		
		for (Edge edge : deleteKnowledge.getEdges()) {
			knowledgeGraph.removeEdge(edge);
		}
		
		// MAYBE do not delete the nodes that are important (like rooms etc)
		for (net.xqhs.graphs.graph.Node node : deleteKnowledge.getNodes()) {
			if (((ConnectedNode)node).getInEdges().isEmpty() &&
				((ConnectedNode)node).getOutEdges().isEmpty())
			knowledgeGraph.removeNode(node);
		}
		
		return true;
	}
	
	public GraphPattern getKnowledge() {
		return knowledgeGraph;
	}
	
	/**
	 * 
	 * @param pattern
	 * 
	 * @return all the matches if not specified otherwise
	 */
	public ArrayList<Match> addPattern(GraphPattern pattern) {
		
		ArrayList<Match> matches = new ArrayList<Match>();
		MonitorPack monitoring = new MonitorPack()
				.setLog((LoggerSimple) new UnitComponent().setUnitName(
						"matcher").setLogLevel(Level.INFO));
		GraphMatcherQuick GMQ = GraphMatcherQuick.getMatcher(knowledgeGraph, pattern,
				monitoring);
		
		while (true) {
			Match m = GMQ.getNextMatch();
			if (m == null)
				break;
			matches.add(m);
		}
		
		return matches;
	}
	
	/**
	 * 
	 * 
	 * @param pattern
	 * @return
	 */
	public boolean registerMatchNotificationTarget(GraphPattern pattern, AgentNotificationHandler handler) {
		return true;
	}

}
