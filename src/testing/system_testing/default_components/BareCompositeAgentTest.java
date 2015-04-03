package testing.system_testing.default_components;

import net.xqhs.util.logging.LoggerSimple.Level;
import net.xqhs.util.logging.Unit;
import net.xqhs.util.logging.UnitComponent;
import net.xqhs.util.logging.logging.Logging;
import tatami.core.agent.AgentComponent;
import tatami.core.agent.AgentComponent.AgentComponentName;
import tatami.core.agent.AgentEvent;
import tatami.core.agent.AgentEvent.AgentEventHandler;
import tatami.core.agent.AgentEvent.AgentEventType;
import tatami.core.agent.CompositeAgent;

/**
 * Creates a bare composite agent (without a platform), and adds a test component to it. The test component intercepts agent events and prints them.
 * The agent is asked to exit soon after creation.
 * <p>
 * Expected output:
 * <ul>
 * <li> start successful
 * <li> interception of AGENT_START
 * <li> delay
 * <li> interception of AGENT_EXIT
 * <li> system exit
 * </ul>
 * 
 * @author Andrei Olaru
 */
public class BareCompositeAgentTest extends Unit
{
	/**
	 * General level for logs.
	 */
	static final Level	generalLevel	= Level.ALL;
	
	/**
	 * Main testing method.
	 */
	public BareCompositeAgentTest()
	{
		setUnitName("parametric component tester").setLogLevel(generalLevel);
		li("starting...");
		
		CompositeAgent agent = new CompositeAgent();
		agent.addComponent(new AgentComponent(AgentComponentName.TESTING_COMPONENT) {
			private static final long	serialVersionUID	= 1L;
			UnitComponent	locallog;
			
			@Override
			protected void componentInitializer()
			{
				super.componentInitializer();
				
				locallog = (UnitComponent) new UnitComponent().setUnitName("monitoring").setLogLevel(generalLevel);
				
				AgentEventHandler allEventHandler = new AgentEventHandler() {
					@Override
					public void handleEvent(AgentEvent event)
					{
						if(locallog == null)
							System.out.println("local log is null");
						else
							locallog.li("event: [" + event.getType().toString() + "]");
						if(event.getType() == AgentEventType.AGENT_STOP)
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
			Thread.sleep(2000);
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
	
	/**
	 * main
	 * 
	 * @param args - not used
	 */
	@SuppressWarnings("unused")
	public static void main(String args[])
	{
		Logging.getMasterLogging().setLogLevel(generalLevel);
		new BareCompositeAgentTest();
	}
}
