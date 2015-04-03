package tatami.communication;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;

import main.java.org.java_websocket.WebSocket;
import main.java.org.java_websocket.drafts.Draft;
import main.java.org.java_websocket.handshake.ClientHandshake;
import main.java.org.java_websocket.server.WebSocketServer;

public class AutobahnServer extends WebSocketServer {
	
	private static int counter = 0;
	
	private HashMap<String, WebSocket> registry;
	

	public AutobahnServer(int port , Draft d ) throws UnknownHostException {
		super( new InetSocketAddress( port ), Collections.singletonList( d ) );
		registry = new HashMap<String, WebSocket>();
	}
	
	public AutobahnServer(InetSocketAddress address, Draft d) throws UnknownHostException {
		super( address, Collections.singletonList( d ) );
		registry = new HashMap<String, WebSocket>();
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		counter++;
		System.out.println( "///////////Opened connection number" + counter );
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		System.out.println( "closed" );
		
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		
		System.out.println("Message received " + message);
		//Now it needs to be routed
		if(message.indexOf("::internal") > -1){
			String agentName = message.substring(message.lastIndexOf("::") + 2, message.length());
			System.out.println("New agent registered: " + agentName);
			registry.put(agentName, conn);
		}
		else{
			String target = message.substring(message.indexOf("::") + 2, message.lastIndexOf("::"));
			String content = target + "::" + message.substring(message.lastIndexOf("::") + 2);
			System.out.println("Message to be sent " + target + " -- " + content);
			registry.get(target).send(content);
		}
		
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		System.out.println( "Error:" );
		ex.printStackTrace();
		
	}

}
