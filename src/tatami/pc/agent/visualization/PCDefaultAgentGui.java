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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingWorker;

import tatami.core.agent.visualization.AgentGui;
import tatami.core.agent.visualization.AgentGuiConfig;
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
		Component pic = new JLabel(config.getWindowName()); // future: should be a picture
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
		
		params = (WindowLayout.staticLayout != null) ? WindowLayout.staticLayout.getWindow(config.getWindowType(),
				config.getWindowName(), null) : WindowParameters.defaultParameters();
		params.setWindow(window, true);
		// window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		window.setVisible(true);
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
	public void doOutput(String componentName, Vector<Object> arguments)
	{
		if(!components.containsKey(componentName))
		{
			System.err.println("component [" + componentName + "] not found."); // FIXME: get a log from somewhere
			return;
		}
		
		Component component = components.get(componentName);
		if(arguments.isEmpty())
			return;
		Object arg0 = arguments.get(0);
		Object arg1 = null;
		if(arguments.size() > 1)
			arg1 = arguments.get(1);
		if((component instanceof TextArea) && (arg0 instanceof String))
		{
			TextArea ta = (TextArea) component;
			ta.setText((String) arg0);
			if((arg1 != null) && (arg1 instanceof Boolean) && ((Boolean) arg1).booleanValue())
				ta.append(".");
			ta.repaint();
		}
		if((component instanceof JLabel) && (arg0 != null) && (arg0 instanceof String))
		{
			JLabel label = (JLabel) component;
			label.setText((String) arg0);
			label.repaint();
		}
		if(component instanceof JButton)
		{
			JButton button = (JButton) component;
			if((arg0 == null) || ((arg0 instanceof Boolean) && (((Boolean) arg0).booleanValue())))
			{
				button.setEnabled(false);
			}
			if(arguments.get(0) instanceof String)
			{
				button.setEnabled(true);
				button.setText((String) arguments.get(0));
				button.repaint();
			}
			if((arg0 instanceof Boolean) && (((Boolean) arg0).booleanValue()))
			{
				button.setEnabled(true);
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
		Component component = components.get(componentName);
		if(component instanceof JButton)
			((JButton) component).addActionListener(new ActionListener() {
				String			comp	= null;
				InputListener	list	= null;
				
				public ActionListener init(String compName, InputListener inputList)
				{
					comp = compName;
					list = inputList;
					return this;
				}
				
				@Override
				public void actionPerformed(ActionEvent e)
				{
					list.receiveInput(comp, new Vector<Object>(0));
				}
			}.init(componentName, listener));
	}
	
	@Override
	public Vector<Object> getinput(String componentName)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@SuppressWarnings("unused")
	@Override
	public void background(final AgentGuiBackgroundTask task, final Object argument,
			final ResultNotificationListener resultListener)
	{
		new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception
			{
				task.execute(argument, resultListener);
				return null;
			}
		}.execute();
	}
}
