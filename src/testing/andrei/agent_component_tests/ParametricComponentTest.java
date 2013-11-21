package testing.andrei.agent_component_tests;

import net.xqhs.util.logging.Unit;
import tatami.core.agent.AgentComponent;
import tatami.core.agent.AgentComponent.AgentComponentName;
import tatami.core.agent.AgentEvent;
import tatami.core.agent.AgentEvent.AgentEventHandler;
import tatami.core.agent.AgentEvent.AgentEventType;
import tatami.core.agent.CompositeAgent;
import tatami.core.agent.parametric.AgentParameterName;
import tatami.core.agent.parametric.AgentParameters;
import tatami.core.agent.parametric.ParametricComponent;

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
		
		agent.addComponent(new AgentComponent(AgentComponentName.TESTING_COMPONENT) {
			@Override
			protected void componentInitializer()
			{
				super.componentInitializer();
				registerHandler(AgentEventType.AGENT_START, new AgentEventHandler() {
					@Override
					public void handleEvent(AgentEvent event)
					{
						System.out.println("tracked here");
					}
				});
			}
		});
		
		agent.start();
		
		li("done.");
		doExit();
	}
	
	public static void main(String args[])
	{
		new ParametricComponentTest();
	}
}
