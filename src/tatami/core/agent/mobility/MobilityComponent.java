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
package tatami.core.agent.mobility;

import tatami.core.agent.AgentComponent;
import tatami.core.agent.AgentEvent;
import tatami.core.agent.AgentEvent.AgentEventType;
import tatami.core.agent.CompositeAgent;

public class MobilityComponent extends AgentComponent
{
	protected MobilityComponent()
	{
		super(AgentComponentName.MOBILITY_COMPONENT);
	}
	
	public String extractDestination(Object eventData)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public boolean move(String nodeName)
	{
		postAgentEvent(new AgentEvent(AgentEventType.AGENT_STOP));
		
		// ...
		
		return true; // if it is most likely that the agent will move
	}
	
	@Override
	protected void atAgentStop(AgentEvent event)
	{
		super.atAgentStop(event);
		
		CompositeAgent agent = getParent(); // the agent to pack
		
		// ...
	}
	
}
