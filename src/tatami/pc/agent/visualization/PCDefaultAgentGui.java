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
package tatami.pc.agent.visualization;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.TextArea;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;



import tatami.core.interfaces.AgentGui;
import tatami.pc.util.windowLayout.WindowLayout;
import tatami.pc.util.windowLayout.WindowParameters;

public class PCDefaultAgentGui implements AgentGui
{
	protected AgentGuiConfig				config				= null;
	
	protected WindowParameters				params				= null;
	protected JFrame						window				= null;
	protected Map<String, Component>		components			= null;
	protected Map<String, InputListener>	inputConnections	= null;
	
	public PCDefaultAgentGui(AgentGuiConfig configuration)
	{
		config = configuration;
		
		components = new Hashtable<String, Component>();
		inputConnections = new HashMap<String, AgentGui.InputListener>();
		window = new JFrame();
		window.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = c.weighty = 1;
		
		c.gridx = 0;
		c.gridy = 0;
		Component pic = new JLabel(config.windowName); // future: should be a picture
		pic.setPreferredSize(new Dimension(100, 100));
		window.add(pic, c);
		components.put(DefaultComponent.AGENT_NAME.toString(), pic);
		
		TextArea ta = new TextArea();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 1;
		c.gridwidth = 2;
		window.add(ta, c);
		ta.setMinimumSize(new Dimension(100, 100));
		components.put(DefaultComponent.AGENT_LOG.toString(), ta);
		
		params = (WindowLayout.staticLayout != null) ? WindowLayout.staticLayout.getWindow(config.windowType, config.windowName, null) : WindowParameters.defaultParameters();
		params.setWindow(window, true);
		// window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		window.setVisible(true);
	}
	
	@Override
	public void close()
	{
		if(WindowLayout.staticLayout != null)
			WindowLayout.staticLayout.dropWindow(config.windowType, config.windowName);
		window.dispose();
		window = null;
	}
	
	@Deprecated
	public Component getComponent(String componentName)
	{
		return components.get(componentName);
	}
	
	@Override
	public void doOutput(String componentName, Vector<Object> arguments)
	{
		if(!components.containsKey(componentName))
		{
			System.err.println("component [" + componentName + "] not found."); // FIXME: get a log from somewhere
			return;
		}
		
		Component component = components.get(componentName);
		if(component instanceof TextArea)
		{
			if(arguments.size() > 0)
			{
				TextArea ta = (TextArea)component;
				ta.setText((String)arguments.get(0));
				if(arguments.size() > 1 && ((Boolean)arguments.get(1)).booleanValue())
					ta.append(".");
				ta.repaint();
			}
		}
	}
	
	@Override
	public void connectInput(String componentName, InputListener listener)
	{
		if(!components.containsKey(componentName))
		{
			System.err.println("component [" + componentName + "] not found."); // FIXME: get a log from somewhere
			return;
		}
		inputConnections.put(componentName, listener);
	}

	@Override
	public Vector<Object> getinput(String componentName)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
