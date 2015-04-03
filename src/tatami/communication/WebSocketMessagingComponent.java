package tatami.communication;

import tatami.core.agent.AgentEvent;
import tatami.core.agent.CompositeAgent;
import tatami.core.agent.AgentEvent.AgentEventHandler;
import tatami.core.agent.AgentEvent.AgentEventType;
import tatami.core.agent.messaging.MessagingComponent;

public class WebSocketMessagingComponent extends MessagingComponent{
	
	WebSocketMessagingPlatform platform;
	
	public WebSocketMessagingComponent(){

	}
	
	@Override
	protected void atAgentStart(AgentEvent event)
	{
		super.atAgentStart(event);
		
		if(!(getPlatformLink() instanceof WebSocketMessagingPlatform))
			throw new IllegalStateException("Platform Link is not of expected type");
		platform = (WebSocketMessagingPlatform)getPlatformLink();
		platform.register(getAgentName(), this);

	}
	
	@Override
	protected Object getPlatformLink() {
		return super.getPlatformLink();
	}

	@Override
	public String getAgentAddress(String agentName, String containerName) {
		System.out.println("Get agent address " + agentName);
		return agentName;
	}
	
	@Override
	public String getAgentAddress(String agentName)
	{
		return getAgentAddress(agentName, null);
	}
	
	public void onMessage(String message){
		System.out.println("Agent received message: " + message);
		receiveMessage(source, destination, content); // TODO
	}

	@Override
	public boolean sendMessage(String target, String source, String content) {
		String tg = (target.indexOf("/") > 0) ? target.substring(0, target.indexOf("/")) : target;
		System.out.println("Send message source: " + source + " taget: " + tg + " content: " +  content);
		try{
			platform.client.send(source + "::" + tg + "::" + content);
			return true;
		}
		catch(Exception e){
			System.out.println("Message could not be sent");
			e.printStackTrace();
		}
		
		return false;
	}

}
