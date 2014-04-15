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

import net.xqhs.util.logging.logging.Logging.DisplayEntity;

/**
 * The class makes the link between a log (needing a {@link DisplayEntity} instance) and the appropriate component in an
 * agent GUI.
 * <p>
 * Whenever the log posts something, by calling the <code>output()</code> method, the logging message is relayed to the
 * GUI component by means of the <code>doOutput()</code> method in {@link AgentGui}.
 * 
 * @author Andrei Olaru
 */
public class Log2AgentGui implements DisplayEntity
{
	/**
	 * The GUI.
	 */
	AgentGui		gui			= null;
	/**
	 * The name of the component in the GUI.
	 */
	String			component	= null;
	/**
	 * The arguments that are sent to the component (part of them remain constant throughout the lifetime of the
	 * instance.
	 */
	Vector<Object>	args		= null;
	
	/**
	 * Creates a new instance, configuring the GUI and the name of the GUI component.
	 * 
	 * @param agentGui
	 *            - the {@link AgentGui} instance.
	 * @param componentName
	 *            - the name of the component in the GUI.
	 */
	public Log2AgentGui(AgentGui agentGui, String componentName)
	{
		gui = agentGui;
		component = componentName;
		args = new Vector<Object>(2);
		args.add(new String());
		args.add(new Boolean(true)); // presume the second argument is for "scroll to the end"
	}
	
	@Override
	public void output(String string)
	{
		args.set(0, string);
		gui.doOutput(component, args);
	}
	
}
