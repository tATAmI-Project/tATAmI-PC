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
import java.util.Map.Entry;
import java.util.Vector;

import javax.swing.JTextArea;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor.SetterOnlyReflection;

import scenario.s2014.demo.gui.Demo.PDAComponents;
import tatami.core.agent.visualization.AgentGui;
import tatami.core.agent.visualization.AgentGuiConfig;
import tatami.core.agent.visualization.AgentGui.InputListener;
import tatami.pc.util.windowLayout.WindowLayout;
import tatami.pc.util.windowLayout.WindowParameters;
import tatami.simulation.BootSettingsManager;

public class DemoAgentOutputInputGUI implements AgentGui {
 
	protected AgentGuiConfig				config				= null;
	protected WindowParameters				params				= null;
	protected DemoAgentOutputInput				window				= null;
	public Map<String, Component>		components			= null;
	private static String outputDisplay = "ATTENDS";
	
	public DemoAgentOutputInputGUI(AgentGuiConfig configuration) {
		
		config = configuration;
		components = new Hashtable<String, Component>();
		window = new DemoAgentOutputInput();
		
		components.put(outputDisplay, window.jTextArea1);
		window.setVisible(true);
	}
	
	@Override
	public void doOutput(String componentName, Vector<Object> arguments)
	{
		String compName = componentName.toUpperCase();
		Component component;
		
		if(compName.compareTo(Demo.PDAComponents.CLEAR.toString()) == 0){
			
			component = components.get(outputDisplay);
			if(component != null && component instanceof JTextArea){
				JTextArea ta = (JTextArea)component;
				ta.setText(null);
			}
			component = components.get(Demo.PDAComponents.CONOPINION.toString());
			if(component != null && component instanceof JTextArea){
				JTextArea ta = (JTextArea)component;
				ta.setText(null);
			}
		}
		else {
			if(!components.containsKey(compName))
			{
				System.err.println("component [" + componentName + "] not found."); // FIXME: get a log from somewhere
				return;
			}
			
			component = components.get(compName);
			if(component instanceof JTextArea)
			{
				if(arguments.size() > 0)
				{
					JTextArea ta = (JTextArea)component;
					for (Object arg : arguments)
						ta.append((String)arg + " ");
					ta.append("\n");
					ta.repaint();
				}
			}
		}
	}

	@Override
	public void connectInput(String componentName, InputListener input) {
		final InputListener listener = input;
		if("attend".equals(componentName.toLowerCase()))
		{
			window.joinListener = listener;
			System.out.println("connectInput attend");
		}
		else
		{
			System.err.println("component [" + componentName + "] not found."); // FIXME: get a log from somewhere
		}
	}

	@Override
	public Vector<Object> getinput(String componentName)
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
