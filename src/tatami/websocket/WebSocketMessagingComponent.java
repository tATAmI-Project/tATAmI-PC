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

import tatami.core.agent.AgentEvent;
import tatami.core.agent.messaging.MessagingComponent;

/**
 * 
 */
public class WebSocketMessagingComponent extends MessagingComponent{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Reference to the platform
	 */
	WebSocketMessagingPlatform platform;
	
	/**
	 * 
	 */
	public WebSocketMessagingComponent(){
	}
	
	/**
	 * Method called when an agent starts
	 */
	@Override
	protected void atAgentStart(AgentEvent event)
	{
		super.atAgentStart(event);
		
		if(!(getPlatformLink() instanceof WebSocketMessagingPlatform))
			throw new IllegalStateException("Platform Link is not of expected type");
		platform = (WebSocketMessagingPlatform)getPlatformLink();
		platform.register(getAgentName(), this);

	}
	
	/**
	 * Obtain a link to the platform
	 */
	@Override
	protected Object getPlatformLink() {
		return super.getPlatformLink();
	}

	/**
	 * Returns the agent name
	 */
	@Override
	public String getAgentAddress(String agentName, String containerName) {
		return agentName;
	}
	
	/**
	 * Returns the agent's address
	 */
	@Override
	public String getAgentAddress(String agentName){
		return getAgentAddress(agentName, null);
	}
	
	/**
	 * Pass the message to the agent implementation
	 * @param source  -Source
	 * @param target - Target
	 * @param message - Content
	 */
	public void onMessage(String source, String target, String message){
		receiveMessage(source, target, message);
	}

	/**
	 * Method called from the agent when it needs to send a message
	 */
	@Override
	public boolean sendMessage(String target, String source, String content) {
		try{
			getAgentLog().dbg(MessagingDebug.DEBUG_MESSAGING, "Seding message: " + source + "::" + target + "::" + content);
			platform.mClient.send(source + "::" + target + "::" + content);
			return true;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return false;
	}

}
