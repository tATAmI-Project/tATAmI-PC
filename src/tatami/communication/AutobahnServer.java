package tatami.communication;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collections;

import main.java.org.java_websocket.WebSocket;
import main.java.org.java_websocket.drafts.Draft;
import main.java.org.java_websocket.handshake.ClientHandshake;
import main.java.org.java_websocket.server.WebSocketServer;

public class AutobahnServer extends WebSocketServer {
	
	private static int counter = 0;

	public AutobahnServer(int port , Draft d ) throws UnknownHostException {
		super( new InetSocketAddress( port ), Collections.singletonList( d ) );
	}
	
	public AutobahnServer(InetSocketAddress address, Draft d) throws UnknownHostException {
		super( address, Collections.singletonList( d ) );
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
		conn.send( message );
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		System.out.println( "Error:" );
		ex.printStackTrace();
		
	}

}
