/*******************************************************************************
 * Copyright (C) 2015 Andrei Olaru, Marius-Tudor Benea, Nguyen Thi Thuy Nga, Amal El Fallah Seghrouchni, Cedric Herpson.
 * 
 * This file is part of tATAmI-PC.
 * 
 * tATAmI-PC is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * tATAmI-PC is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with tATAmI-PC.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package tatami.websocket;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import main.java.org.java_websocket.WebSocketImpl;
import main.java.org.java_websocket.drafts.Draft;
import main.java.org.java_websocket.drafts.Draft_17;
import net.xqhs.util.XML.XMLTree.XMLNode;
import net.xqhs.util.logging.LoggerSimple.Level;
import net.xqhs.util.logging.UnitComponentExt;
import tatami.core.agent.AgentComponent.AgentComponentName;
import tatami.core.agent.AgentEvent;
import tatami.core.agent.CompositeAgent;
import tatami.core.agent.mobility.MobilityComponent;
import tatami.simulation.AgentManager;
import tatami.simulation.BootSettingsManager;
import tatami.simulation.PlatformLoader;
import tatami.simulation.PlatformLoader.PlatformLink;

/**
 * Implements a {@link PlatformLoader} instance that allows centralized communication via WebSockets.
 */
public class WebSocketMessagingPlatform implements PlatformLoader, PlatformLink
{
	
	/**
	 * When the componentType is NONE, this class is neither server or client
	 */
	public static final int							NONE				= 0x0;
																		
	/**
	 * When the SERVER bit is configured, this class is initialized as a server
	 */
	public static final int							SERVER				= 0x1;
																		
	/**
	 * When the CLIENT bit is configured, this class is initialized as a client
	 */
	public static final int							CLIENT				= 0x2;
																		
	/**
	 * In this parameter will be stored the info about the class profile: client, server or both
	 */
	public int										componentType		= NONE;
																		
	/**
	 * The port on which the server will be started or on which the client will connect to the server
	 */
	public int										mPort				= 9002;
																		
	/**
	 * The object representing the server if this platform will have the server role
	 */
	public AutobahnServer							mServer;
													
	/**
	 * The object representing the client if this platform will have the client role
	 */
	AutobahnClient									mClient;
													
	/**
	 * The thread on which the client will run
	 */
	Thread											mClientThread;
													
	/**
	 * The host name on which the websocket will be launched
	 */
	String											mClientHost;
													
	/**
	 * The register where all agents and their connections are registered
	 */
	HashMap<String, WebSocketMessagingComponent>	mAgents;
													
	/** the logger */
	UnitComponentExt								log;
	
	
	ArrayList<OutputComplexMessageTokenizer> mAgentsbuffer;
	
	Set<String> mContainers = null;
													
	/**
	 * Name of the unit for logging purposes
	 */
	protected final static String					COMMUNICATION_UNIT	= "websock";
																		
	@Override
	public String getName()
	{
		return StandardPlatformType.WEBSOCKET.toString();
	}
	
	/**
	 * Configure the current platform
	 * 
	 * @param configuration
	 *            The configuration node in the scenario file.
	 * @param settings
	 *            The settings provided by the boot component
	 */
	@Override
	public WebSocketMessagingPlatform setConfig(XMLNode configuration, BootSettingsManager settings)
	{
		mAgentsbuffer = new ArrayList<OutputComplexMessageTokenizer>();
		mAgentsbuffer.add(new OutputComplexMessageTokenizer());
		
		log = (UnitComponentExt) new UnitComponentExt().setUnitName(COMMUNICATION_UNIT).setLogLevel(Level.ALL);
		
		mAgents = new HashMap<String, WebSocketMessagingComponent>();
		
		String tmpComponentType = settings.getMainHost();
		
		String tmpPort = settings.getLocalPort();
		
		mClientHost = settings.getLocalHost();
		
		if(tmpComponentType.toLowerCase().indexOf("server") > -1)
		{
			componentType |= SERVER;
		}
		
		if(tmpComponentType.toLowerCase().indexOf("client") > -1)
		{
			componentType |= CLIENT;
		}
		
		mPort = Integer.parseInt(tmpPort);
		return this;
	}
	
	@Override
	public boolean start()
	{
		if(componentType == NONE)
		{
			return false;
		}
		
		if((componentType & SERVER) == SERVER)
		{
			WebSocketImpl.DEBUG = false;
			try
			{
				mServer = new AutobahnServer(mPort, new Draft_17());
				mServer.start();
				log.info("Communication server started");
			} catch(UnknownHostException e)
			{
				log.error("Communication server could not be started");
				e.printStackTrace();
			}
			
		}
		
		

		if((componentType & CLIENT) == CLIENT)
		{
			Draft d = new Draft_17();
			String clientname = "tootallnate/websocket";

			String protocol = "ws";

			String serverlocation = protocol + "://" + mClientHost + ":" + mPort;
			URI uri = null;
			uri = URI.create(serverlocation + "/agent=" + clientname);

			mClient = new AutobahnClient(d, uri);
			mClientThread = new Thread(mClient);
			mClientThread.start();
			
			boolean serverStarted = false;
			while (!serverStarted) {
				try {

					
					mClient.send("::handshake::");

					log.info("Communication client started");
					serverStarted = true;
				} catch (Exception e) {
					try {
						Thread.sleep(10);
					} catch (Exception ex) {

					}
				}
			}
		}
		return true;
	}
	
	@Override
	public boolean stop()
	{
		try
		{
			mClient.close();
			mClientThread.join();
			log.doExit();
			log.error("Communication client out: ");
			return true;
		} catch(InterruptedException e)
		{
			log.error("Communication client could not be stopped: ", e);
			log.doExit();
			return false;
		}
	}
	
	public String getIP() {
		String ip = "";

		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {

		}
		return ip;
	}
	
	@Override
	public boolean addContainer(String containerName)
	{
		if(mContainers == null){
			mContainers = new HashSet<String>();
		}
		mContainers.add(containerName);
		mClient.newContainerNotification(containerName);
		return true;
	}
	
	/**
	 * 
	 * @param target
	 *            The target of the message
	 * @param source
	 *            The source of the message
	 * @param message
	 *            The content of the message
	 */
	public void onMessage(String source, String target, String message)
	{
		String currentTarget = (target.indexOf("/") > 0) ? target.substring(0, target.indexOf("/")) : target;
		mAgents.get(currentTarget).onMessage(source, target, message);
	}
	
	public void onMobilityPackReceived(String content){
		
		mAgentsbuffer.get(0).addNewMessage(content);
		if(mAgentsbuffer.get(0).allMessagereceived()){
			System.out.println("All messages received?!?!?!?!?!?!");
			
			byte[] x = mAgentsbuffer.get(0).returnObj();
			System.out.println("x");
			ByteArrayInputStream bis = new ByteArrayInputStream(x);
			System.out.println("y");
			ObjectInput in = null;
			AgentManager agent = null;
			
			try {
				in = new ObjectInputStream(bis);

				Object o = in.readObject();

				agent = (AgentManager) o;
				
				

				bis.close();
				in.close();
			} catch (Exception e) {
				System.out.println("Agent deserialization failed");
			}
			
			System.out.println("Before loading");
			
			if(loadAgent("Container", agent)){
				System.out.println("Agent loaded successfully" + agent.getAgentName());
				
				
				CompositeAgent tmpAgent = (CompositeAgent) agent;
				tmpAgent.resume();
				
				
				System.out.println();
			}
		}
		else{
			//System.out.println("All messages not received yet?!?!?!?!?!?!");
		}
	}
	/**
	 * Method called from inside the agents, when agents are loaded on the platform.
	 * 
	 * @param agentName
	 *            The name of the agent
	 * @param messagingComponent
	 *            The component to call when messages are received.
	 */
	public void register(String agentName, WebSocketMessagingComponent messagingComponent)
	{
		mAgents.put(agentName, messagingComponent);
	}
	
	/**
	 * Method called to load an agent.
	 * 
	 * @param containerName
	 *            The name of the container in which to load the agent.
	 * @param agentManager
	 *            The agent, represented by means of the {@link AgentManager} interface.
	 */
	@Override
	public boolean loadAgent(String containerName, AgentManager agentManager)
	{
		StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		for(StackTraceElement st: ste){
			System.out.println(st);
		}
		
		
		System.out.println("^^^^^^^^^^^^^^ " + containerName);
		agentManager.setPlatformLink(this);
		mClient.registerPlatform(this, agentManager.getAgentName());
		mClient.newAgentNotification(agentManager.getAgentName());
		return true;
	}
	
	@Override
	public String getRecommendedComponentClass(AgentComponentName componentName)
	{
		switch(componentName)
		{
		case MESSAGING_COMPONENT:
			return WebSocketMessagingComponent.class.getName();
		default:
			break;
		}
		return null;
	}
	
	@Override
	public void onAgentStateChanged(AgentEvent event, AgentManager agent)
	{
		//event.getValue(MobilityComponent.DESTINATION_PARAMETER)
		if(agent.isStopped())
		{ // TODO and an indication of future movement exists from mobility component (with destination container name)
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput out = null;
			try
			{
				out = new ObjectOutputStream(bos);
				out.writeObject(agent);
				byte[] yourBytes = bos.toByteArray();
				
				InputComplexMessageTokenizer tok = new InputComplexMessageTokenizer(yourBytes);
				
				while(tok.hasMorePackages())
				{
					mClient.mobilityPackage(event.getValue(MobilityComponent.DESTINATION_PARAMETER), tok.getNextPackage());
				}
				
				System.out.println("!!!!!!!!!!!!Length: " + yourBytes.length);
				
			} catch(Exception e)
			{
				System.out.println(e.getMessage());
				e.printStackTrace();
				System.out.println("Exception occured");
			}
		}
		
	}

	@Override
	public void onAgentStateChanged(AgentManager agent) {
		// TODO Auto-generated method stub
		
	}
	
}
