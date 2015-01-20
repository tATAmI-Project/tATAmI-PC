package tatami.communication;

import tatami.core.agent.messaging.MessagingComponent;

public class WebSocketMessagingComponent extends MessagingComponent{
	
	WebSocketMessagingPlatform platform;

	public WebSocketMessagingComponent(WebSocketMessagingPlatform platform){
		this.platform = platform;
		System.out.println("Constructor");
	}
	
	@Override
	public String getAgentAddress(String agentName, String containerName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean sendMessage(String target, String source, String content) {
		// TODO Auto-generated method stub
		return false;
	}

}
