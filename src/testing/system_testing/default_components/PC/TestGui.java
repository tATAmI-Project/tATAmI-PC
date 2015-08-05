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
package testing.system_testing.default_components.PC;

import java.awt.TextArea;

import javax.swing.JButton;

import tatami.core.agent.visualization.AgentGuiConfig;
import tatami.pc.agent.visualization.PCDefaultAgentGui;

/**
 * Simple GUI with one button.
 * 
 * @author Andrei Olaru
 */
public class TestGui extends PCDefaultAgentGui
{
	/**
	 * Name for the button component.
	 */
	public static final String	BUTTON_NAME	= "thebutton";
	
	/**
	 * creates a new GUI.
	 * 
	 * @param configuration
	 *            - the configuration
	 */
	public TestGui(AgentGuiConfig configuration)
	{
		super(configuration);
	}
	
	@Override
	protected void buildGUI()
	{
		JButton theButton = new JButton("Press to exit");
		components.put(BUTTON_NAME, theButton);
		window.add(theButton);
		
		components.put(DefaultComponent.AGENT_LOG.toString(), new TextArea()); // will not be used
	}
}
