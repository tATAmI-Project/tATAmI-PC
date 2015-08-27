package tatami.websocket;

import tatami.core.agent.AgentEvent;
import tatami.core.agent.messaging.NameBasedMessagingComponent;

/**
 * 
 */
public class WebSocketMessagingComponent extends NameBasedMessagingComponent
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Reference to the platform
	 */
	WebSocketMessagingPlatform platform;
	
	/**
	 * Method called when an agent starts
	 */
	@Override
	protected void atAgentStart(AgentEvent event)
	{
		super.atAgentStart(event);
		
		if(!(getPlatformLink() instanceof WebSocketMessagingPlatform))
			throw new IllegalStateException("Platform Link is not of expected type");
		platform = (WebSocketMessagingPlatform) getPlatformLink();
		platform.register(getAgentName(), this);
		
	}
	
	/**
	 * Obtain a link to the platform
	 */
	@Override
	protected Object getPlatformLink()
	{
		return super.getPlatformLink();
	}
	
	/**
	 * Pass the message to the agent implementation
	 * 
	 * @param source
	 *            -Source
	 * @param target
	 *            - Target
	 * @param message
	 *            - Content
	 */
	public void onMessage(String source, String target, String message)
	{
		receiveMessage(source, target, message);
	}
	
	/**
	 * Method called from the agent when it needs to send a message
	 */
	@Override
	public boolean sendMessage(String target, String source, String content)
	{
		try
		{
			getAgentLog().dbg(MessagingDebug.DEBUG_MESSAGING,
					"Seding message: " + source + "::" + target + "::" + content);
			platform.mClient.send(source + "::" + target + "::" + content);
			return true;
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return false;
	}
	
}
