package testing.andrei.agent_component_tests;

import net.xqhs.util.logging.LoggerSimple.Level;
import net.xqhs.util.logging.Unit;
import net.xqhs.util.logging.UnitComponent;
import tatami.core.agent.AgentComponent;
import tatami.core.agent.AgentComponent.AgentComponentName;
import tatami.core.agent.AgentEvent;
import tatami.core.agent.AgentEvent.AgentEventHandler;
import tatami.core.agent.AgentEvent.AgentEventType;
import tatami.core.agent.CompositeAgent;
import tatami.core.agent.parametric.AgentParameterName;
import tatami.core.agent.parametric.AgentParameters;
import tatami.core.agent.parametric.ParametricComponent;

@SuppressWarnings("javadoc")
public class ParametricComponentTest extends Unit
{
	@SuppressWarnings("serial")
	public ParametricComponentTest()
	{
		setUnitName("parametric component tester");
		li("starting...");
		
		CompositeAgent agent = new CompositeAgent();
		AgentParameters agentParameters = new AgentParameters().add(AgentParameterName.AGENT_NAME,
				"test parametric agent");
		agent.addComponent(new ParametricComponent(agentParameters));
		
		agent.addComponent(new AgentComponent(AgentComponentName.TESTING_COMPONENT) {
			UnitComponent	locallog;
			
			@Override
			protected ParametricComponent getParametric()
			{
				return super.getParametric();
			}
			
			@Override
			protected void componentInitializer()
			{
				super.componentInitializer();
				
				locallog = (UnitComponent) new UnitComponent().setUnitName("monitoring").setLogLevel(Level.ALL);
				AgentEventHandler allEventHandler = new AgentEventHandler() {
					@Override
					public void handleEvent(AgentEvent event)
					{
						locallog.li("event: [" + event.getType().toString() + "]");
						ParametricComponent parametric = getParametric();
						if(parametric == null)
							locallog.li("\t parametric component is currently null");
						else
							locallog.li("\t parameter value: [" + parametric.parVal(AgentParameterName.AGENT_NAME)
									+ "]");
						if(event.getType() == AgentEventType.AGENT_EXIT)
							locallog.doExit();
					}
				};
				for(AgentEventType eventType : AgentEventType.values())
					registerHandler(eventType, allEventHandler);
			}
		});
		
		agent.start();
		try
		{
			Thread.sleep(200);
		} catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		agent.exit();
		li("done.");
		doExit();
	}
	
	@SuppressWarnings("unused")
	public static void main(String args[])
	{
		new ParametricComponentTest();
	}
}
