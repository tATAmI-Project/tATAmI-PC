package tatami.communication;


import java.net.URI;
import java.net.UnknownHostException;
import java.util.HashMap;

import main.java.org.java_websocket.WebSocketImpl;
import main.java.org.java_websocket.drafts.Draft;
import main.java.org.java_websocket.drafts.Draft_17;
import net.xqhs.util.XML.XMLTree.XMLNode;
import tatami.core.agent.AgentComponent.AgentComponentName;
import tatami.simulation.AgentManager;
import tatami.simulation.BootSettingsManager;
import tatami.simulation.PlatformLoader;
import tatami.simulation.PlatformLoader.PlatformLink;

/**
 *
 */
public class WebSocketMessagingPlatform implements PlatformLoader, PlatformLink {

	/**
	 * 
	 */
	public static final int NONE = 0x0;

	/**
	 * 
	 */
	public static final int SERVER = 0x1;

	/**
	 * 
	 */
	public static final int CLIENT = 0x2;

	/**
	 * 
	 */
	public int componentType = NONE;

	/**
	 * 
	 */
	public int port = 9002;
	
	
	/**
	 * 
	 */
	public AutobahnServer server;
	
	/**
	 * 
	 */
	AutobahnClient client;
	
	/**
	 * 
	 */
	Thread clientThread;
	
	/**
	 * 
	 */
	HashMap<String, WebSocketMessagingComponent> agents;
	

	/**
	 * 
	 */
	public WebSocketMessagingPlatform() {
	}

	@Override
	public String getName() {
		return StandardPlatformType.GENERAL.toString();
	}

	@Override
	public WebSocketMessagingPlatform setConfig(XMLNode configuration,
			BootSettingsManager settings) {

		agents = new HashMap<String, WebSocketMessagingComponent>();

		String tmpComponentType = settings.getMainHost();

		String tmpPort = settings.getLocalPort();

		if (tmpComponentType.toLowerCase().indexOf("server") > -1) {
			componentType |= SERVER;
		}

		if (tmpComponentType.toLowerCase().indexOf("client") > -1) {
			componentType |= CLIENT;
		}

		port = Integer.parseInt(tmpPort);
		System.out.println("WebSocketMessagingPlatform configured");

		return this;
	}

	@Override
	public boolean start() {
		if (componentType == NONE) {
			System.out.println("Component type = none");
			return false;
		}

		if ((componentType & SERVER) == SERVER) {
			WebSocketImpl.DEBUG = false;
			try {
				System.out.println("Server started on port: " + port);
				server = new AutobahnServer(port, new Draft_17());
				server.start();
				
			} catch (UnknownHostException e) {
				System.out.println("Unknown host exception");
				e.printStackTrace();
			}
			System.out.println("Server started");
		}

		if ((componentType & CLIENT) == CLIENT) {
			
			System.out.println("Also a client");
			Draft d = new Draft_17();
			String clientname = "tootallnate/websocket";

			String protocol = "ws";
			String host = "localhost";

			String serverlocation = protocol + "://" + host + ":" + port;
			URI uri = null;
			uri = URI.create( serverlocation + "/agent=" + clientname );
			
			client = new AutobahnClient( d, uri );
			clientThread = new Thread( client );
			clientThread.start();
		}

		return true;
	}

	@Override
	public boolean stop() {
		System.out.println("Platform stopped");
		try {
			clientThread.join();

		} catch ( InterruptedException e ) {
			e.printStackTrace();
			return false;
		} finally {
			client.close();
		}
		return true;
	}

	@Override
	public boolean addContainer(String containerName) {
		return true;
	}
	
	/**
	 * 
	 * @param target
	 * @param source
	 * @param message
	 */
	public void onMessage(String source, String target, String message){
		agents.get(target).onMessage(source, target, message);
	}
	
	/**
	 * 
	 * @param agentName
	 * @param messagingComponent
	 */
	public void register(String agentName, WebSocketMessagingComponent messagingComponent){
		agents.put(agentName, messagingComponent);
	}

	@Override
	public boolean loadAgent(String containerName, AgentManager agentManager) {
		agentManager.setPlatformLink(this);
		client.registerPlatform(this, agentManager.getAgentName());
		client.newAgentNotification(agentManager.getAgentName());
		return true;
	}

	@Override
	public String getRecommendedComponentClass(AgentComponentName componentName) {
		switch(componentName)
		{
		case MESSAGING_COMPONENT:
			return WebSocketMessagingComponent.class.getName();
		default:
			break;
		}
		return null;
	}

}
