package tatami.simulation;

import java.util.HashMap;
import java.util.Map;

import tatami.core.agent.AgentComponent.AgentComponentName;
import tatami.core.agent.AgentEvent.AgentEventHandler;
import tatami.core.agent.AgentEvent.AgentEventType;
import tatami.core.agent.AgentEvent;
import tatami.core.agent.CompositeAgent;
import tatami.core.agent.messaging.MessagingComponent;
import tatami.core.agent.messaging.MessagingComponent.MessagingDebug;
import tatami.core.agent.visualization.VisualizableComponent;
import tatami.simulation.PlatformLoader.PlatformLink;

public class LocalDeploymentPlatform extends DefaultPlatform implements PlatformLink
{
	/**
	 * Simple implementation of {@link MessagingComponent}, that uses agents' names as their addresses.
	 * 
	 * @author Andrei Olaru
	 */
	public static class SimpleLocalMessaging extends MessagingComponent
	{
		/**
		 * The serial UID.
		 */
		private static final long	serialVersionUID	= 1L;
		
		@Override
		public String getAgentAddress(String agentName, String containerName)
		{
			return agentName;
		}
		
		@Override
		public boolean sendMessage(String target, String source, String content)
		{
			// FIXME do checks
			if(!(getPlatformLink() instanceof LocalDeploymentPlatform))
				throw new IllegalStateException("Platform Link is not of expected type");
			String[] targetElements = target.split(ADDRESS_SEPARATOR, 2);
			SimpleLocalMessaging targetComponent = ((LocalDeploymentPlatform) getPlatformLink()).registry
					.get(targetElements[0]);
			if(targetComponent != null)
				targetComponent.receiveMessage(source, target, content);
			// FIXME else error
			return true;
		}
		
		/**
		 * @return this. Used to avoid errors in AGENT_START event handler.
		 */
		protected SimpleLocalMessaging getComponent()
		{
			return this;
		}
		
		@Override
		protected Object getPlatformLink()
		{
			return super.getPlatformLink();
		}
		
		@Override
		protected String getAgentName()
		{
			return super.getAgentName();
		}
		
		@Override
		protected VisualizableComponent getVisualizable()
		{
			return super.getVisualizable();
		}
		
		@Override
		protected void parentChangeNotifier(CompositeAgent oldParent)
		{
			super.parentChangeNotifier(oldParent);
			registerHandler(AgentEventType.AGENT_START, new AgentEventHandler() {
				@Override
				public void handleEvent(AgentEvent event)
				{
					if(!(getPlatformLink() instanceof LocalDeploymentPlatform))
						throw new IllegalStateException("Platform Link is not of expected type");
					if(getVisualizable() != null && getVisualizable().getLog() != null)
						getVisualizable().getLog().dbg(MessagingDebug.DEBUG_MESSAGING, "Registered with platform.");
					((LocalDeploymentPlatform) getPlatformLink()).registry.put(getAgentName(), getComponent());
				}
			});
		}
		
	}
	
	/**
	 * The registry of agents that can receive messages, specifying the {@link MessagingComponent} receiving the
	 * message.
	 *
	 */
	protected Map<String, SimpleLocalMessaging>	registry	= new HashMap<String, SimpleLocalMessaging>();
	
	@Override
	public boolean loadAgent(String containerName, AgentManager agentManager)
	{
		return agentManager.setPlatformLink(this) && super.loadAgent(containerName, agentManager);
	}
	
	@Override
	public String getRecommendedComponentClass(AgentComponentName componentName)
	{
		if(componentName == AgentComponentName.MESSAGING_COMPONENT)
			return SimpleLocalMessaging.class.getName();
		return super.getRecommendedComponentClass(componentName);
	}
	
	@Override
	public String getName()
	{
		return StandardPlatformType.LOCAL.toString();
	}
}
