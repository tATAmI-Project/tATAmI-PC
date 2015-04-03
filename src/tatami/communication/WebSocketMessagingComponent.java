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
	protected void parentChangeNotifier(CompositeAgent oldParent) {
		// TODO Auto-generated method stub
		super.parentChangeNotifier(oldParent);
		
		// vezi JadeMessaging
		registerHandler(AgentEventType.AGENT_START, new AgentEventHandler() {
			String agentName;
			
			WebSocketMessagingComponent messagingComponent;
			
			public AgentEventHandler init(String agentName, WebSocketMessagingComponent messagingComponent){
				this.agentName = agentName;
				this.messagingComponent = messagingComponent;
				return this;
			}
			
			@Override
			public void handleEvent(AgentEvent event){
				if(!(getPlatformLink() instanceof WebSocketMessagingPlatform))
					throw new IllegalStateException("Platform Link is not of expected type");
				platform = (WebSocketMessagingPlatform)getPlatformLink();
				platform.register(agentName, messagingComponent);
				
				// TODO register with platform that this component receives messages for this agent - getAgentName()
				// TODO when receiving a message, call 9from this class): receiveMessage(source, destination, content);
			}
		}.init(getAgentName(), this));
	}
	
	@Override
	protected Object getPlatformLink() {
		return super.getPlatformLink();
	}

	public WebSocketMessagingComponent(WebSocketMessagingPlatform platform){
		this.platform = platform;
		System.out.println("Constructor");
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
