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

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

import main.java.org.java_websocket.client.WebSocketClient;
import main.java.org.java_websocket.drafts.Draft;
import main.java.org.java_websocket.handshake.ServerHandshake;

/**
 * The AutobahnClient class is the interface with the client part of the java websocket project
 */
public class AutobahnClient extends WebSocketClient
{
	
	/**
	 * The map between the agent names and their corresponding server references
	 */
	HashMap<String, WebSocketMessagingPlatform> pltformRouting;
	
	
	/**
	 * This will not connect automatically the client to the server, connect method must be used
	 * 
	 * @param d
	 *            - Base class for everything of a websocket specification which is not common such as the way the
	 *            handshake is read or frames are transfered.
	 * @param uriAddress
	 *            The URI that will be used
	 */
	public AutobahnClient(Draft d, URI uriAddress)
	{
		super(uriAddress, d);
		pltformRouting = new HashMap<String, WebSocketMessagingPlatform>();
	}
	
	/**
	 * Method called when the client connects successfully to the server
	 */
	@Override
	public void onOpen(ServerHandshake handshakedata)
	{
		// nothing to do
	}
	
	/**
	 * Register a platform associated with an agent name
	 * 
	 * @param platform
	 *            The platform on which this messaging component exists
	 * @param agentName
	 *            the name of the agent that will be registered
	 */
	public void registerPlatform(WebSocketMessagingPlatform platform, String agentName)
	{
		pltformRouting.put(agentName, platform);
	}
	
	/**
	 * Sends a notification to the server when a new agent starts
	 * 
	 * @param agentName
	 *            The agent name that will be registered
	 */
	public void newAgentNotification(String agentName)
	{
		String message = "::" + "internal" + "::" + agentName;
		send(message);
	}
	
	public void newContainerNotification(String container){
		String containerMessage = "::" + "container" + "::" + container;
		
		try{
			send(containerMessage);
		}
		catch(Exception e){
			System.out.println("sssssssssssssssss " + e.getMessage());
		}
		
	}
	
	public void mobilityPackage(String destination, String pack){
		String message = "::" + "mobility" + "::" + destination + "::" + pack;
		send(message);
	}
	
	/**
	 * Method called when a message is received
	 */
	@Override
	public void onMessage(String message)
	{
		
		if(message.indexOf("::mobility") > -1){

			String destination = message.substring(12, message.lastIndexOf("::"));
			
//			/System.out.println("Mobility message received, detination: " + destination);
			
			String content = message.substring(message.lastIndexOf("::") + 2);
			
			for(WebSocketMessagingPlatform platform: pltformRouting.values()){
				platform.onMobilityPackReceived(content);
			}
			
			
			return;
		}
		/* Forward the message to the platform */
		String[] messageComponents = message.split("::");
		String currentTarget = (messageComponents[1].indexOf("/") > 0)
				? messageComponents[1].substring(0, messageComponents[1].indexOf("/")) : messageComponents[1];
		pltformRouting.get(currentTarget).onMessage(messageComponents[0], messageComponents[1], messageComponents[2]);
		
	}
	
	/**
	 * Method called when the connection with the server was closed
	 */
	@Override
	public void onClose(int code, String reason, boolean remote)
	{
		// nothing to do
	}
	
	@Override
	public void onError(Exception ex)
	{
		ex.printStackTrace();
	}
	
}
