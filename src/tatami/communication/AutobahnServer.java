package tatami.communication;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;

import main.java.org.java_websocket.WebSocket;
import main.java.org.java_websocket.drafts.Draft;
import main.java.org.java_websocket.handshake.ClientHandshake;
import main.java.org.java_websocket.server.WebSocketServer;

/**
 *
 */
public class AutobahnServer extends WebSocketServer {
	
	/**
	 * How many clients are connected to this server
	 */
	private static int counter = 0;
	
	/**
	 * The registry where connected clients references are stored
	 */
	private HashMap<String, WebSocket> registry;
	

	/**
	 * 
	 * @param port
	 * @param d
	 * @throws UnknownHostException
	 */
	public AutobahnServer(int port , Draft d ) throws UnknownHostException {
		super( new InetSocketAddress( port ), Collections.singletonList( d ) );
		registry = new HashMap<String, WebSocket>();
	}
	
	/**
	 * 
	 * @param address 
	 * @param d
	 * @throws UnknownHostException
	 */
	public AutobahnServer(InetSocketAddress address, Draft d) throws UnknownHostException {
		super( address, Collections.singletonList( d ) );
		registry = new HashMap<String, WebSocket>();
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		counter++;
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		System.out.println( "closed" );
		
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		//Now it needs to be routed
		if(message.indexOf("::internal") > -1){
			String agentName = message.substring(message.lastIndexOf("::") + 2, message.length());
			registry.put(agentName, conn);
		}
		else{
			String target = message.substring(message.indexOf("::") + 2, message.lastIndexOf("::"));
			registry.get(target).send(message);
		}
		
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		ex.printStackTrace();
	}

}
