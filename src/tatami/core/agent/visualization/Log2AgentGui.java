/*******************************************************************************
 * Copyright (C) 2013 Andrei Olaru, Marius-Tudor Benea, Nguyen Thi Thuy Nga, Amal El Fallah Seghrouchni, Cedric Herpson.
 * 
 * This file is part of tATAmI-PC.
 * 
 * tATAmI-PC is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * tATAmI-PC is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with tATAmI-PC.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package tatami.core.agent.visualization;

import java.util.Vector;


import tatami.core.interfaces.AgentGui;
import tatami.core.util.logging.Log.DisplayEntity;

public class Log2AgentGui implements DisplayEntity
{
	AgentGui		gui			= null;
	String			component	= null;
	Vector<Object>	args		= null;
	
	public Log2AgentGui(AgentGui agentGui, String componentName)
	{
		gui = agentGui;
		component = componentName;
		args = new Vector<Object>(2);
		args.add(new String());
		args.add(new Boolean(true));	// presume the second argument is for "scroll to the end"
	}
	
	@Override
	public void output(String string)
	{
		args.set(0, string);
		gui.doOutput(component, args);
	}
	
}
