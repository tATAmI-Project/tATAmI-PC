package tatami.communication;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

import main.java.org.java_websocket.client.WebSocketClient;
import main.java.org.java_websocket.drafts.Draft;
import main.java.org.java_websocket.handshake.ServerHandshake;

public class AutobahnClient extends WebSocketClient {
	

	HashMap<String, WebSocketMessagingPlatform> pltformRouting;

	/**
	 * 
	 * @param d
	 * @param uri
	 */
	public AutobahnClient( Draft d , URI uri ) {
		super( uri, d );
		pltformRouting = new HashMap<String, WebSocketMessagingPlatform>();
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
	}
	
	public void registerPlatform(WebSocketMessagingPlatform platform, String agentName){
		pltformRouting.put(agentName, platform);
	}
	
	public void newAgentNotification(String agentName){
		String message = "::" + "internal" + "::" + agentName;
		send( message );
	}

	@Override
	public void onMessage(String message) {
		System.out.println("Message received client: " + message);
		String[] messageComponents = message.split("::");
		pltformRouting.get(messageComponents[0]).onMessage( messageComponents[2], messageComponents[0], messageComponents[1]);
		
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		System.out.println( "Closed: " + code + " " + reason );
	}

	@Override
	public void onError(Exception ex) {
		System.out.println( "Error: " );
		ex.printStackTrace();
		
	}

}
