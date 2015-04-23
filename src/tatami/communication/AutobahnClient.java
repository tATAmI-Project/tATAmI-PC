package tatami.communication;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

import main.java.org.java_websocket.client.WebSocketClient;
import main.java.org.java_websocket.drafts.Draft;
import main.java.org.java_websocket.handshake.ServerHandshake;


/**
 *
 */
public class AutobahnClient extends WebSocketClient {
	

	/**
	 * A map between the agent names and their corresponding server references
	 */
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
	
	/**
	 * Register a platform associated with an agent name
	 * @param platform
	 * @param agentName
	 */
	public void registerPlatform(WebSocketMessagingPlatform platform, String agentName){
		pltformRouting.put(agentName, platform);
	}
	
	/**
	 * 
	 * Sends a notification to the server when a new agent starts
	 * @param agentName The agent that will be registered
	 */
	public void newAgentNotification(String agentName){
		String message = "::" + "internal" + "::" + agentName;
		send( message );
	}

	@Override
	public void onMessage(String message) {
		/*Forward the message to the platform*/
		String[] messageComponents = message.split("::");
		String currentTarget = (messageComponents[1].indexOf("/") > 0) ? messageComponents[1].substring(0, messageComponents[1].indexOf("/")) : messageComponents[1];
		pltformRouting.get(currentTarget).onMessage( messageComponents[0], messageComponents[1], messageComponents[2]);
		
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
