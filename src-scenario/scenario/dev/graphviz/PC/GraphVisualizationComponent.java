package scenario.dev.graphviz.PC;

import java.awt.BorderLayout;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.JFrame;

import amicity.graph.pc.gui.AnimatedJungGraphViewer;
import amicity.graph.pc.gui.CMPViewer;
import amicity.graph.pc.gui.JungGraphViewer;
import amicity.graph.pc.jung.JungGraph;
import amicity.graph.pc.jung.JungGraphMirror;
import net.xqhs.graphs.context.ContextPattern;
import net.xqhs.graphs.graph.Edge;
import net.xqhs.graphs.graph.SimpleEdge;
import net.xqhs.graphs.graph.SimpleNode;
import net.xqhs.graphs.matchingPlatform.TrackingGraph;
import net.xqhs.graphs.pattern.EdgeP;
import net.xqhs.graphs.pattern.GraphPattern;
import net.xqhs.graphs.pattern.NodeP;
import tatami.core.agent.AgentComponent;
import tatami.core.agent.AgentEvent;
import tatami.core.agent.io.AgentActiveIO.InputListener;
import tatami.core.agent.kb.ContextComponent;

@SuppressWarnings("javadoc")
public class GraphVisualizationComponent extends AgentComponent implements InputListener
{
	
	JFrame	window = null;
	Timer	timer	= null;
					
	public GraphVisualizationComponent()
	{
		super(AgentComponentName.TESTING_COMPONENT);
		AgentComponent component = getAgentComponent(AgentComponentName.COGNITIVE_COMPONENT);
		System.out.println("CATALINB:" + component);
	}
	
	private void setMutateGraph()
	{
		timer = new Timer();
		timer.schedule(new TimerTask() {
			int count = 0;
			
			@Override
			public void run()
			{
				System.out.println("Adding node in the graph");
				ContextComponent component = (ContextComponent) getAgentComponent(
						AgentComponentName.COGNITIVE_COMPONENT);
				TrackingGraph trackingGraph = (TrackingGraph) component.getKnowledge();
				for(net.xqhs.graphs.graph.Node node : trackingGraph.getNodes())
				{
					SimpleNode newNode = new SimpleNode("node" + count++);
					trackingGraph.addNode(newNode);
					trackingGraph.addEdge(new SimpleEdge(node, newNode, "label"));
					break;
				}
			}
		}, 0, 2000);
	}
	
	private void buildGUI()
	{
		window = new JFrame();
		window.setLayout(new BorderLayout());
		window.setSize(800, 600);
		window.setVisible(true);
		
		ContextComponent component = (ContextComponent) getAgentComponent(AgentComponentName.COGNITIVE_COMPONENT);
		TrackingGraph trackingGraph = (TrackingGraph) component.getKnowledge();
		System.out.println(trackingGraph);
		CMPViewer matchViewer = new CMPViewer();
		
		// TODO: How to choose threshold here?
		component.registerMatchNotificationTarget(matchViewer);
		
		JungGraph graph = new JungGraphMirror(trackingGraph);
		final JungGraphViewer editor = new AnimatedJungGraphViewer(graph);
		trackingGraph.addNode(new SimpleNode("start"));
		
		// Add a pattern to match stuff.
		NodeP nodeFrom = new NodeP("start");
		NodeP nodeTo = new NodeP();
		Edge edge = new EdgeP(nodeFrom, nodeTo, "label");
		GraphPattern pattern = new ContextPattern();
		pattern.add(nodeFrom);
		pattern.add(nodeTo);
		pattern.add(edge);
		
		component.addPattern(pattern);
		
		window.add(editor, BorderLayout.CENTER);
		window.add(matchViewer, BorderLayout.EAST);
	}
	
	@Override
	protected void atAgentStart(AgentEvent event)
	{
		super.atAgentStart(event);
		
		buildGUI();
	}
	
	@Override
	protected void atAgentStop(AgentEvent event)
	{
		super.atAgentStop(event);
		
		if(timer != null)
		{
			timer.cancel();
			timer = null;
		}
		
		if(window != null)
		{
			window.dispose();
			window = null;
		}
	}

	@Override
	protected void atSimulationStart(AgentEvent event)
	{
		super.atSimulationStart(event);
		
		setMutateGraph();
	}
	
	/**
	 * Class UID.
	 */
	private static final long serialVersionUID = -1277342393025315369L;
	
	@Override
	public void receiveInput(String portName, Vector<Object> arguments)
	{
		System.out.println("CATALINB: ");
	}
	
}
