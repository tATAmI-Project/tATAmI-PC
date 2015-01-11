package tatami.communication;

import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;

import main.java.org.java_websocket.WebSocketImpl;
import main.java.org.java_websocket.drafts.Draft;
import main.java.org.java_websocket.drafts.Draft_17;
import net.xqhs.util.XML.XMLTree.XMLNode;
import tatami.core.agent.AgentComponent.AgentComponentName;
import tatami.core.util.platformUtils.PlatformUtils;
import tatami.simulation.AgentManager;
import tatami.simulation.BootSettingsManager;
import tatami.simulation.PlatformLoader;

public class WebSocketMessagingPlatform implements PlatformLoader {

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
		return null;
	}

	@Override
	public WebSocketMessagingPlatform setConfig(XMLNode configuration,
			BootSettingsManager settings) {

		String tmpComponentType = PlatformUtils.getParameterValue(
				configuration, "mainHost");

		String tmpPort = PlatformUtils.getParameterValue(configuration, "port");

		if (tmpComponentType.toLowerCase().indexOf("server") > -1) {
			componentType = SERVER;
		}

		if (tmpComponentType.toLowerCase().indexOf("client") > -1) {
			componentType = CLIENT;
		}

		port = Integer.parseInt(tmpPort);

		return this;
	}

	@Override
	public boolean start() {
		if (componentType == NONE) {
			return false;
		}

		if ((componentType & SERVER) == SERVER) {
			WebSocketImpl.DEBUG = false;
			try {
				new AutobahnServer(port, new Draft_17()).start();
				System.out.println("Server started");
			} catch (UnknownHostException e) {
				System.out.println("Unknown host exception");
				e.printStackTrace();
			}
		}

		if ((componentType & CLIENT) == CLIENT) {
			/* First of the thinks a programmer might want to change */
			/*
			Draft d = new Draft_17();
			String clientname = "tootallnate/websocket";

			String protocol = "ws";
			String host = "localhost";
			int cPort = 9001;

			String serverlocation = protocol + "://" + host + ":" + port;
			String line = "";
			AutobahnClient e;
			URI uri = null;
			try {
				uri = URI.create(serverlocation + "/linkCase" + "&agent="
						+ clientname);

				e = new AutobahnClient(d, uri);
				Thread t = new Thread(e);
				t.start();
				try {
					t.join();

				} catch (InterruptedException e1) {
					e1.printStackTrace();
				} finally {
					e.close();
				}
			} catch (ArrayIndexOutOfBoundsException er) {
				System.out.println("Missing server uri");
			} catch (IllegalArgumentException er) {
				er.printStackTrace();
				System.out
						.println("URI should look like ws://localhost:8887 or wss://echo.websocket.org");
			} catch (IOException er) {
				er.printStackTrace();
			}
*/
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
		return false;
	}

	@Override
	public boolean loadAgent(String containerName, AgentManager agentManager) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getRecommendedComponentClass(AgentComponentName componentName) {
		// TODO Auto-generated method stub
		return null;
	}

}
