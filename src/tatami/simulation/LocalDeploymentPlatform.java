package tatami.simulation;

import java.util.HashMap;
import java.util.Map;

import tatami.core.agent.AgentComponent.AgentComponentName;
import tatami.core.agent.AgentEvent;
import tatami.core.agent.messaging.MessagingComponent;
import tatami.simulation.PlatformLoader.PlatformLink;

/**
 * Simple platform that allows agents to send messages locally (inside the same JVM) based simply on agent name.
 * 
 * @author Andrei Olaru
 */
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
		public String getAgentAddress(String agentName)
		{
			return getAgentAddress(agentName, null);
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
		
		@Override
		protected void atAgentStart(AgentEvent event)
		{
			super.atAgentStart(event);
			if(!(getPlatformLink() instanceof LocalDeploymentPlatform))
				throw new IllegalStateException("Platform Link is not of expected type");
			getAgentLog().dbg(MessagingDebug.DEBUG_MESSAGING, "Registered with platform.");
			((LocalDeploymentPlatform) getPlatformLink()).registry.put(getAgentName(), this);
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
