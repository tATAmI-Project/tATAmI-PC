package testing.andrei.agent_component_tests;

import net.xqhs.util.logging.Logger.Level;
import net.xqhs.util.logging.Logging;
import net.xqhs.util.logging.Unit;
import net.xqhs.util.logging.UnitComponent;
import tatami.core.agent.AgentComponent;
import tatami.core.agent.AgentComponent.AgentComponentName;
import tatami.core.agent.AgentEvent;
import tatami.core.agent.AgentEvent.AgentEventHandler;
import tatami.core.agent.AgentEvent.AgentEventType;
import tatami.core.agent.CompositeAgent;

@SuppressWarnings("javadoc")
public class BareCompositeAgentTest extends Unit
{
	static final Level generalLevel = Level.ALL;
	
	@SuppressWarnings("serial")
	public BareCompositeAgentTest()
	{
		setUnitName("parametric component tester").setLogLevel(generalLevel);
		li("starting...");
		
		CompositeAgent agent = new CompositeAgent();
		agent.addComponent(new AgentComponent(AgentComponentName.TESTING_COMPONENT) {
			UnitComponent	locallog	= null;
			
			@Override
			protected void componentInitializer()
			{
				super.componentInitializer();
				
				locallog = (UnitComponent) new UnitComponent().setUnitName("monitoring").setLogLevel(generalLevel);
				AgentEventHandler allEventHandler = new AgentEventHandler() {
					@Override
					public void handleEvent(AgentEvent event)
					{
						locallog.li("event: [" + event.getType().toString() + "]");
						if(event.getType() == AgentEventType.AGENT_EXIT)
							locallog.doExit();
					}
				};
				for(AgentEventType eventType : AgentEventType.values())
					registerHandler(eventType, allEventHandler);
			}
		});
		
		if(agent.start())
			li("start successful");
		else
			le("start failed");
		try
		{
			Thread.sleep(200);
		} catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		boolean done = false;
		while(!done)
		{
			done = agent.exit();
			if(!done)
				le("exit failed");
		}
		li("exit successful");
		li("done.");
		doExit();
	}
	
	@SuppressWarnings("unused")
	public static void main(String args[])
	{
		Logging.getMasterLogging().setLogLevel(generalLevel);
		new BareCompositeAgentTest();
	}
}
