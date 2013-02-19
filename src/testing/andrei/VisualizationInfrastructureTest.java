package testing.andrei;



import tatami.core.agent.visualization.VisualizableAgent;
import tatami.core.interfaces.Logger;
import tatami.core.interfaces.JadeInterface.JadeConfig;
import tatami.core.util.logging.Log;
import tatami.pc.agent.visualization.VisualizationAgent;
import tatami.pc.util.jade.PCJadeInterface;
import tatami.pc.util.windowLayout.LayoutIndications;
import tatami.pc.util.windowLayout.WindowLayout;
import tatami.pc.util.windowLayout.LayoutIndications.BarPosition;

/**
 * 
 * @author Andrei Olaru
 * 
 */
public class VisualizationInfrastructureTest
{
	private static String	unitName		= "visTestMain";
	protected Logger		log				= Log.getLogger(unitName);
	
	public static void main(String args[])
	{
		new VisualizationInfrastructureTest();
	}
	
	public VisualizationInfrastructureTest()
	{
		log.trace("Hello Visualizable World");
		
		WindowLayout.staticLayout = new WindowLayout(1600, 1000, new LayoutIndications(8, 8)
												.indicateBar(BarPosition.LEFT, 70, 0)
												.indicateWindowType("agent", 2, 2)
												.indicateWindowType("system", 4, 4)
												, null);
		
		PCJadeInterface jade = new PCJadeInterface();
		String containerName = "test1";
		String[] agentNames = {"traced1", "traced2"};
		String visName = "visualizer1";
		jade.startContainer(containerName);
		for(String name : agentNames)
			jade.addAgentToContainer(containerName, name, VisualizableAgent.class.getCanonicalName(), null);
		jade.addAgentToContainer(containerName, visName, VisualizationAgent.class.getCanonicalName(), new Object[] {agentNames});
		
	}
	
}