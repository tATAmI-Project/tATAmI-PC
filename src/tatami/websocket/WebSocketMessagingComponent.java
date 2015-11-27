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

import java.util.Timer;

import tatami.core.agent.AgentComponent;
import tatami.core.agent.AgentEvent;
import tatami.core.agent.messaging.MessagingComponent;
import tatami.core.agent.messaging.NameBasedMessagingComponent;

/**
 * Agent component that allows communication via WebSockets, using the services provided by
 * {@link WebSocketMessagingPlatform}.
 */
public class WebSocketMessagingComponent extends NameBasedMessagingComponent
{
	/**
	 * the serial UID.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Reference to the platform
	 */
	WebSocketMessagingPlatform platform;
	
	@Override
	protected void atAgentStart(AgentEvent event)
	{
		super.atAgentStart(event);
		
		if(!(getPlatformLink() instanceof WebSocketMessagingPlatform))
			throw new IllegalStateException("Platform Link is not of expected type");
		platform = (WebSocketMessagingPlatform) getPlatformLink();
		platform.register(getAgentName(), this);
		mActive = true;
	}
	
	/**
	 * Obtain a link to the platform
	 */
	@Override
	protected Object getPlatformLink()
	{
		return super.getPlatformLink();
	}
	
	/**
	 * Pass the message to the implementation in {@link MessagingComponent}, via {@link AgentComponent}.
	 * 
	 * @param source
	 *            - source endpoint
	 * @param target
	 *            - target endpoint
	 * @param message
	 *            - message content
	 */
	public void onMessage(String source, String target, String message)
	{
		receiveMessage(source, target, message);
	}
	
	/**
	 * Method called from the agent when it needs to send a message.
	 */
	@Override
	public boolean sendMessage(String target, String source, String content)
	{
		try
		{
			getAgentLog().dbg(MessagingDebug.DEBUG_MESSAGING,
					"Seding message: " + source + "::" + target + "::" + content);
			platform.mClient.send(source + "::" + target + "::" + content);
			return true;
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return false;
	}
	
	
	@Override
	protected void atAgentStop(AgentEvent event) {
		//AgentEvent event = new AgentEvent(AgentEventType.BEFORE_MOVE);
		super.atAgentStop(event);

	}
	
	@Override
	protected void atBeforeAgentMove(AgentEvent event)
	{
		mActive = false;
	}
	
	@Override
	protected void atAfterAgentMove(AgentEvent event)
	{
	}
	
	
	
	
}
