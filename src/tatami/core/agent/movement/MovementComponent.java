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
package tatami.core.agent.movement;

import tatami.core.agent.AgentComponent;
import tatami.core.agent.CompositeAgent;

public class MovementComponent extends AgentComponent
{
	protected MovementComponent(CompositeAgent parent)
	{
		super(parent, AgentComponentName.MOVEMENT_COMPONENT);
		// TODO Auto-generated constructor stub
	}
	
	public String extractDestination(Object eventData)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
}
