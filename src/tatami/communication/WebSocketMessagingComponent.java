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
			@Override
			public void handleEvent(AgentEvent event)
			{
				if(!(getPlatformLink() instanceof WebSocketMessagingPlatform))
					throw new IllegalStateException("Platform Link is not of expected type");
				platform = (WebSocketMessagingPlatform)getPlatformLink();
			}
		});
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
		System.out.println("Get agent address");
		//platform.
		
		return null;
	}

	@Override
	public boolean sendMessage(String target, String source, String content) {
		System.out.println("Send message");
		return false;
	}

}
