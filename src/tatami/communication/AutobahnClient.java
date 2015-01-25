package tatami.communication;

import java.net.URI;

import main.java.org.java_websocket.client.WebSocketClient;
import main.java.org.java_websocket.drafts.Draft;
import main.java.org.java_websocket.handshake.ServerHandshake;

public class AutobahnClient extends WebSocketClient {

	/**
	 * 
	 * @param d
	 * @param uri
	 */
	public AutobahnClient( Draft d , URI uri ) {
		super( uri, d );
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
	}
	
	public void newAgentNotification(String agentName){
		String message = "::" + "internal" + "::" + agentName;
		send( message );
	}

	@Override
	public void onMessage(String message) {
		System.out.println("Message received " + message);
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
