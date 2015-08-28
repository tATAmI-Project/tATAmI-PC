package testing.system_testing.default_components;

import java.util.Arrays;

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

/**
 * This test / example class show how messages can be sent between agents and what methods are available.
 * <p>
 * There are two agents. At the start, one ({@value #NAME_A}) sends to the other ({@value #NAME_B}) a "hello" message. A
 * reply is sent with the content "hello back" (this happens between endpoints "/testing") and then a new message from
 * the second agent to the first agent is sent to to the endpoint "testing/final" to signal the end of the execution.
 * Both agents then exit.
 * <p>
 * Here one can see examples of methods offered by {@link AgentComponent}: <code>registerMessageReceiver</code>,
 * <code>sendMessage</code>, <code>sendMessageToEndpoint</code>, <code>sendReply</code> and
 * <code>getComponentEndpoint</code>. Also it is shown how to process the elements in an endpoint, with help from
 * methods in the messaging component.
 * 
 * @author Andrei Olaru
 */
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
				protected void postAgentEvent(AgentEvent event)
				{
					super.postAgentEvent(event);
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
							
							// here's how to do some endpoint processing.
							MessagingComponent msg = (MessagingComponent) getAgentComponent(
									AgentComponentName.MESSAGING_COMPONENT);
							String targetAgent = msg.extractAgentAddress(
									(String) event.getParameter(MessagingComponent.SOURCE_PARAMETER));
							String[] targetElements = msg.extractInternalAddressElements(
									(String) event.getParameter(MessagingComponent.SOURCE_PARAMETER));
							String[] targetElementsNew = Arrays.copyOf(targetElements, targetElements.length + 1);
							targetElementsNew[targetElements.length] = "final";
							String target = msg.makePath(targetAgent, targetElementsNew);
							
							sendMessageToEndpoint("ending",
									(String) event.getParameter(MessagingComponent.DESTINATION_PARAMETER), target);
							postAgentEvent(new AgentEvent(AgentEventType.AGENT_STOP));
						}
					}, "testing");
				}
				
				@Override
				protected boolean sendReply(String content, AgentEvent replyTo)
				{
					return super.sendReply(content, replyTo);
				}
				
				@Override
				protected boolean sendMessageToEndpoint(String content, String sourceEndpoint, String targetEndpoint)
				{
					return super.sendMessageToEndpoint(content, sourceEndpoint, targetEndpoint);
				}
				
				@Override
				protected AgentComponent getAgentComponent(AgentComponentName name)
				{
					return super.getAgentComponent(name);
				}
				
				@Override
				protected void postAgentEvent(AgentEvent event)
				{
					super.postAgentEvent(event);
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
	
	/**
	 * Main method.
	 * 
	 * @param args
	 *            - not used.
	 */
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
		try
		{
			Thread.sleep(1500);
		} catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		p.stop();
	}
}
