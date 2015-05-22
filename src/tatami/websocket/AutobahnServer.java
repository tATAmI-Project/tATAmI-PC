package tatami.websocket;

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
	 * Number of connected clients.
	 */
	int mCounter = 0;
	
	/**
	 * The registry where connected clients references are stored
	 */
	private HashMap<String, WebSocket> registry;
	

	/**
	 * 
	 * @param port The port on which the server will listen
	 * @param d - Base class for everything of a websocket 
	 * specification which is not common such as the way the 
	 * handshake is read or frames are transfered.
	 * @throws UnknownHostException The exception that will be 
	 * thrown when the initialization fails
	 */
	public AutobahnServer(int port , Draft d ) throws UnknownHostException {
		super( new InetSocketAddress( port ), Collections.singletonList( d ) );
		registry = new HashMap<String, WebSocket>();
	}
	
	/**
	 * 
	 * @param address The address that will be used to initialize the server
	 * @param d - Base class for everything of a websocket 
	 * specification which is not common such as the way the 
	 * handshake is read or frames are transfered.
	 * @throws UnknownHostException The exception that will be 
	 * thrown when the initialization fails
	 */
	public AutobahnServer(InetSocketAddress address, Draft d) throws UnknownHostException {
		super( address, Collections.singletonList( d ) );
		registry = new HashMap<String, WebSocket>();
	}

	/**
	 * Method called when the client connects successfully to this server
	 */
	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		mCounter++;
	}

	/**
	 * Method called when the connection with the server was closed
	 */
	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		// nothing to do
	}

	/**
	 * Method called when a message is received
	 */
	@Override
	public void onMessage(WebSocket conn, String message) {
		//Now the message needs to be routed
		if(message.indexOf("::internal") > -1){
			String agentName = message.substring(message.lastIndexOf("::") + 2, message.length());
			registry.put(agentName, conn);
		}
		else{
			String target = message.split("::")[1];
			String currentTarget = (target.indexOf("/") > 0) ? target.substring(0, target.indexOf("/")) : target;
			registry.get(currentTarget).send(message);
		}
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		ex.printStackTrace();
	}

}
