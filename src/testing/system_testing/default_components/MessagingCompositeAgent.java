package testing.system_testing.default_components;

import net.xqhs.util.logging.Logger;
import tatami.core.agent.AgentComponent;
import tatami.core.agent.AgentEvent;
import tatami.core.agent.AgentComponent.AgentComponentName;
import tatami.core.agent.AgentEvent.AgentEventHandler;
import tatami.core.agent.AgentEvent.AgentEventType;
import tatami.core.agent.CompositeAgent;
import tatami.core.agent.messaging.MessagingComponent;
import tatami.core.agent.visualization.VisualizableComponent;
import tatami.simulation.AgentManager;
import tatami.simulation.LocalDeploymentPlatform;
import tatami.simulation.PlatformLoader;

public class MessagingCompositeAgent
{
	/**
	 * Name of first agent.
	 */
	public final static String	NAME_A	= "TestAgentA";
	/**
	 * Name of second agent.
	 */
	public final static String	NAME_B	= "TestAgentB";
	
	@SuppressWarnings({ "serial", "javadoc" })
	static class TestAgentA extends CompositeAgent
	{
		public TestAgentA()
		{
			addComponent(new VisualizableComponent());
			addComponent(new LocalDeploymentPlatform.SimpleLocalMessaging());
			addComponent(new AgentComponent(AgentComponentName.TESTING_COMPONENT) {
				@Override
				protected void parentChangeNotifier(CompositeAgent oldParent)
				{
					super.parentChangeNotifier(oldParent);
					registerMessageReceiver(new AgentEventHandler() {
						@Override
						public void handleEvent(AgentEvent event)
						{
							getAgentLog().info("final message received from []",
									event.getParameter(MessagingComponent.SOURCE_PARAMETER));
//							try
//							{
//								Thread.sleep(500);
//							} catch(InterruptedException e)
//							{
//								e.printStackTrace();
//							}
							postAgentEvent(new AgentEvent(AgentEventType.AGENT_STOP));
						}
					}, "testing", "final");
					registerMessageReceiver(new AgentEventHandler() {
						@Override
						public void handleEvent(AgentEvent event)
						{
							getAgentLog().info("message received []",
									event.getParameter(MessagingComponent.CONTENT_PARAMETER));
						}
					}, "testing");
				}
				
				@Override
				protected void atAgentStart(AgentEvent event)
				{
					super.atAgentStart(event);
					getAgentLog().info("start");
					sendMessage("hello", thisComponentEndpoint(), NAME_B, "testing");
				}
				
				protected String thisComponentEndpoint()
				{
					return getComponentEndpoint("testing");
				}
				
				@Override
				protected Logger getAgentLog()
				{
					return super.getAgentLog();
				}
			});
			
		}
		
		@Override
		public String getAgentName()
		{
			return NAME_A;
		}
	}
	
	@SuppressWarnings({ "javadoc", "serial" })
	static class TestAgentB extends CompositeAgent
	{
		public TestAgentB()
		{
			addComponent(new VisualizableComponent());
			addComponent(new LocalDeploymentPlatform.SimpleLocalMessaging());
			addComponent(new AgentComponent(AgentComponentName.TESTING_COMPONENT) {
				@Override
				protected void parentChangeNotifier(CompositeAgent oldParent)
				{
					super.parentChangeNotifier(oldParent);
					registerMessageReceiver(new AgentEventHandler() {
						@Override
						public void handleEvent(AgentEvent event)
						{
							getAgentLog().info("message with content [] received",
									event.getParameter(MessagingComponent.CONTENT_PARAMETER));
							sendReply("hello back", event);
							try
							{
								Thread.sleep(500);
							} catch(InterruptedException e)
							{
								e.printStackTrace();
							}
							sendMessageToEndpoint("ending",
									(String) event.getParameter(MessagingComponent.DESTINATION_PARAMETER),
									(String) event.getParameter(MessagingComponent.SOURCE_PARAMETER) + "/final");
//							try
//							{
//								Thread.sleep(500);
//							} catch(InterruptedException e)
//							{
//								e.printStackTrace();
//							}
							postAgentEvent(new AgentEvent(AgentEventType.AGENT_STOP));
						}
					}, "testing");
				}
				
				@Override
				protected void atAgentStart(AgentEvent event)
				{
					super.atAgentStart(event);
					sendMessage("hello", getComponentEndpoint("testing"), NAME_A, "testing");
				}
				
				@Override
				protected Logger getAgentLog()
				{
					return super.getAgentLog();
				}
			});
		}
		
		@Override
		public String getAgentName()
		{
			return NAME_B;
		}
	}
	
	public static void main(String[] args)
	{
		AgentManager A = new TestAgentA();
		AgentManager B = new TestAgentB();
		
		PlatformLoader p = new LocalDeploymentPlatform();
		p.start();
		p.loadAgent(null, A);
		p.loadAgent(null, B);
		
		B.start();
		try
		{
			Thread.sleep(500);
		} catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		A.start();
	}
}
