package tatami.communication;

import java.net.URI;
import java.net.UnknownHostException;

import main.java.org.java_websocket.WebSocketImpl;
import main.java.org.java_websocket.drafts.Draft;
import main.java.org.java_websocket.drafts.Draft_17;
import net.xqhs.util.XML.XMLTree.XMLNode;
import tatami.core.agent.AgentComponent.AgentComponentName;
import tatami.simulation.AgentManager;
import tatami.simulation.BootSettingsManager;
import tatami.simulation.PlatformLoader;
import tatami.simulation.PlatformLoader.PlatformLink;

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

	public int port = -1;

	/**
	 * 
	 */
	public WebSocketMessagingPlatform() {
		System.out.println("WebSocketMessagingPlatform constructor");
	}

	@Override
	public String getName() {
		return StandardPlatformType.GENERAL.toString();
	}

	@Override
	public WebSocketMessagingPlatform setConfig(XMLNode configuration,
			BootSettingsManager settings) {

		System.out.println("Config entered " + settings.getMainHost());

		String tmpComponentType = settings.getMainHost();

		String tmpPort = settings.getLocalPort();

		if (tmpComponentType.toLowerCase().indexOf("server") > -1) {
			componentType = SERVER;
		}

		if (tmpComponentType.toLowerCase().indexOf("client") > -1) {
			componentType = CLIENT;
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
				new AutobahnServer(port, new Draft_17()).start();
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
			int port = 9001;

			String serverlocation = protocol + "://" + host + ":" + port;
			URI uri = null;
			uri = URI.create( serverlocation + "/agent=" + clientname );
			
			System.out.println( "//////////////////////Exec: " + uri.getQuery() );
			AutobahnClient e = new AutobahnClient( d, uri );
			Thread t = new Thread( e );
			t.start();
			try {
				t.join();

			} catch ( InterruptedException e1 ) {
				e1.printStackTrace();
			} finally {
				e.close();
			}
			
			
		}

		return true;
	}

	@Override
	public boolean stop() {
		return true;
	}

	@Override
	public boolean addContainer(String containerName) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean loadAgent(String containerName, AgentManager agentManager) {
		// TODO Auto-generated method stub
		agentManager.setPlatformLink(this);
		return true;
	}

	@Override
	public String getRecommendedComponentClass(AgentComponentName componentName) {
		// TODO Auto-generated method stub
		return null;
	}

}
