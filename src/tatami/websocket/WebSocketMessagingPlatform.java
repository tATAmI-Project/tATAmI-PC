package tatami.websocket;


import java.net.URI;
import java.net.UnknownHostException;
import java.util.HashMap;

import main.java.org.java_websocket.WebSocketImpl;
import main.java.org.java_websocket.drafts.Draft;
import main.java.org.java_websocket.drafts.Draft_17;
import net.xqhs.util.XML.XMLTree.XMLNode;
import net.xqhs.util.logging.LoggerSimple.Level;
import net.xqhs.util.logging.UnitComponentExt;
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
	 * When the componentType is NONE, 
	 * this class is neither server or client
	 */
	public static final int NONE = 0x0;

	/**
	 * When the SERVER bit is configured, this class is 
	 * initialized as a server
	 */
	public static final int SERVER = 0x1;

	/**
	 * When the CLIENT bit is configured, this class is
	 * initialized as a client
	 */
	public static final int CLIENT = 0x2;

	/**
	 * In this parameter will be stored the info about the class
	 * profile: client, server or both
	 */
	public int componentType = NONE;

	/**
	 * The port on which the server will be started or
	 * on which the client will connect to the server
	 */
	public int mPort = 9002;
	
	
	/**
	 * The object representing the server if this platform will
	 * have the server role
	 */
	public AutobahnServer mServer;
	
	/**
	 * The object representing the client if this platform will
	 * have the client role
	 */
	AutobahnClient mClient;
	
	/**
	 * The thread on which the client will run
	 */
	Thread mClientThread;
	
	/**
	 * The host name on which the websocket will be launched
	 */
	String mClientHost;
	
	/**
	 * The register where all agents and their connections are registered
	 */
	HashMap<String, WebSocketMessagingComponent> mAgents;
	
	
	/** the logger */
	UnitComponentExt log;
	
	/**
	 * Name of the unit for logging purposes
	 */
	protected final static String COMMUNICATION_UNIT="websock";
	

	/**
	 * 
	 */
	public WebSocketMessagingPlatform() {
	}

	/**
	 * 
	 * @return Returns the name of the current platform
	 */
	@Override
	public String getName() {
		return StandardPlatformType.WEBSOCKET.toString();
	}

	/**
	 * Configure the current platform
	 * @param configuration The configuration node in the scenario file.
	 * @param settings The settings provided by the boot component
	 */
	@Override
	public WebSocketMessagingPlatform setConfig(XMLNode configuration,
			BootSettingsManager settings) {
		
		log = (UnitComponentExt) new UnitComponentExt().setUnitName(COMMUNICATION_UNIT).setLogLevel(Level.ALL);

		mAgents = new HashMap<String, WebSocketMessagingComponent>();

		String tmpComponentType = settings.getMainHost();

		String tmpPort = settings.getLocalPort();

		mClientHost = settings.getLocalHost();

		if (tmpComponentType.toLowerCase().indexOf("server") > -1) {
			componentType |= SERVER;
		}

		if (tmpComponentType.toLowerCase().indexOf("client") > -1) {
			componentType |= CLIENT;
		}

		mPort = Integer.parseInt(tmpPort);
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
				mServer = new AutobahnServer(mPort, new Draft_17());
				mServer.start();
				
			} catch (UnknownHostException e) {
				log.error("Communication server could not be started");
				e.printStackTrace();
			}
			
			log.info("Communication server started");
		}
		// FIXME
		try
		{
			Thread.sleep(1500);
		} catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		

		if ((componentType & CLIENT) == CLIENT) {
			
			Draft d = new Draft_17();
			String clientname = "tootallnate/websocket";

			String protocol = "ws";
			
			String serverlocation = protocol + "://" + mClientHost + ":" + mPort;
			URI uri = null;
			uri = URI.create( serverlocation + "/agent=" + clientname );
			
			mClient = new AutobahnClient( d, uri );
			mClientThread = new Thread( mClient );
			mClientThread.start();
			
			log.info("Communication client started");
		}
		return true;
	}

	/**
	 * 
	 * @return Returns true if the platform successfully stopped
	 */
	@Override
	public boolean stop() {
		try {
			mClient.close();
			mClientThread.join();
			log.doExit();
			return true;
		} catch ( InterruptedException e ) {
			log.error("Communication client could not be stopped: ", e);
			log.doExit();
			return false;
		}
	}

	@Override
	public boolean addContainer(String containerName) {
		return true;
	}
	
	/**
	 * 
	 * @param target The target of the message
	 * @param source The source of the message
	 * @param message The content of the message
	 */
	public void onMessage(String source, String target, String message){
		String currentTarget = (target.indexOf("/") > 0) ? target.substring(0, target.indexOf("/")) : target;
		mAgents.get(currentTarget).onMessage(source, target, message);
	}
	
	/**
	 * Method called from inside the agents, when agents are loaded on the platform.
	 * 
	 * @param agentName The name of the agent
	 * @param messagingComponent The component to call when messages are received.
	 */
	public void register(String agentName, WebSocketMessagingComponent messagingComponent){
		mAgents.put(agentName, messagingComponent);
	}

	/**
	 * Method called to load an agent.
	 * 
	 * @param containerName The name of the container in which to load the agent. 
	 * @param agentManager The agent, represented by means of the {@link AgentManager} interface.
	 */
	@Override
	public boolean loadAgent(String containerName, AgentManager agentManager) {
		agentManager.setPlatformLink(this);
		mClient.registerPlatform(this, agentManager.getAgentName());
		mClient.newAgentNotification(agentManager.getAgentName());
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