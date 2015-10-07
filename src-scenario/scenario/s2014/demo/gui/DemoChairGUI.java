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
package scenario.s2014.demo.gui;

import java.awt.Component;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.swing.JTextArea;

import scenario.s2014.demo.gui.Demo.PDAComponents;
import tatami.core.agent.visualization.AgentGui;
import tatami.core.agent.visualization.AgentGuiConfig;
import tatami.pc.util.windowLayout.WindowLayout;
import tatami.pc.util.windowLayout.WindowParameters;
import tatami.simulation.BootSettingsManager;

public class DemoChairGUI implements AgentGui {
 
	protected AgentGuiConfig				config				= null;
	protected WindowParameters				params				= null;
	protected DemoChair					window				= null;
	public Map<String, Component>		components			= null;
	
	public DemoChairGUI(AgentGuiConfig configuration) {
		
		config = configuration;
		components = new Hashtable<String, Component>();
		window = new DemoChair();
		window.setVisible(true);
	}
	
	@Override
	public void doOutput(String componentName, Vector<Object> arguments)
	{
		// do nothing
	}

	@Override
	public void connectInput(String componentName, InputListener input) {
		
		final InputListener listener = input;
		
		if((Demo.PDAComponents.JOIN.toString().toLowerCase()).equals(componentName.toLowerCase()))
		{
			window.joinListener = listener;
			System.out.println("connectInput " + componentName);
		}
		else
		{
			System.err.println("component [" + componentName + "] not found."); // FIXME: get a log from somewhere
		}
		
	}

	@Override
	public Vector<Object> getInput(String componentName)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close()
	{
		if(WindowLayout.staticLayout != null)
			WindowLayout.staticLayout.dropWindow(config.getWindowType(), config.getWindowName());
		window.dispose();
		window = null;
	}
	
	@Override
	public void background(AgentGuiBackgroundTask agentGuiBackgroundTask,
			Object argument, ResultNotificationListener resultListener) {
		// TODO Auto-generated method stub
		
	} 
}
