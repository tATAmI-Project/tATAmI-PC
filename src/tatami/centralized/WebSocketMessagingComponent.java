package tatami.centralized;

import tatami.core.agent.messaging.MessagingComponent;

public class WebSocketMessagingComponent extends MessagingComponent{
	
	public WebSocketMessagingComponent(){
		
		System.out.println("WebSocketMessagingComponent initialzied");
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
