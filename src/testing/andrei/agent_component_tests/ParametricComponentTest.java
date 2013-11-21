package testing.andrei.agent_component_tests;

import tatami.core.agent.CompositeAgent;
import tatami.core.agent.parametric.AgentParameterName;
import tatami.core.agent.parametric.AgentParameters;
import tatami.core.agent.parametric.ParametricComponent;
import net.xqhs.util.logging.Unit;

public class ParametricComponentTest extends Unit
{
	public ParametricComponentTest()
	{
		setUnitName("parametric component tester");
		li("starting...");
		
		CompositeAgent agent = new CompositeAgent();
		AgentParameters agentParameters = new AgentParameters().add(AgentParameterName.AGENT_NAME,
				"test parametric agent");
		agent.addComponent(new ParametricComponent(agentParameters));
		
		li("done.");
		doExit();
	}
	
	public static void main(String args[])
	{
		new ParametricComponentTest();
	}
}
