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
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import net.xqhs.windowLayout.WindowLayout;
import net.xqhs.windowLayout.WindowParameters;
import tatami.HMI.src.PC.AgentGui;
import tatami.HMI.src.PC.AgentGuiConfig;

/**
 * Default agent GUI for the PC platform. It stores components as a map of {@link Component} instances associated with
 * lower case {@link String} names,
 * <p>
 * This implementation has the following features:
 * <ul>
 * <li>It offers the required functionality (output and passive/active input), by means of methods such as
 * {@link #doOutput(String, Vector)}, {@link #getInput(String)} and {@link #connectInput(String, InputListener)},
 * respectively.
 * <li>For easier use, this implementation treats component names in a case insensitive manner. It is <i>strongly
 * recommended</i> that extending classes follow the same rule.
 * <li>The class offers to extending classes methods for managing components ({@link #addComponent}} and {
 * {@link #getComponent}} the already enforce the rule above. It is <i>strongly recommended</i> that extending classes
 * use these methods instead of directly accessing the components map. For custom operations the map of components can
 * be retrieved by means of <code>getComponentMap()</code>.
 * <li>When the default listener is (re-)set, only those inputs that have not been already explicitly connected to an
 * input will be connected to the default listener, even if some inputs were connected by means of {@link #connectInput}
 * to the same listener. In order to achieve this, an internal, hidden, listener is used that redirects to the listener
 * that is set as default.
 * <li>As a consequence, at the moment, an input cannot be disconnected from a listener (i.e. revert to the default
 * listener. TODO
 * <li>By default, this implementation includes in the agent GUI the two default components --
 * {@link tatami.HMI.src.PC.AgentGui.DefaultComponent#AGENT_NAME} and
 * {@link tatami.HMI.src.PC.AgentGui.DefaultComponent#AGENT_LOG}.
 * <li>The window is automatically placed according to the current (static) {@link WindowLayout} instance.
 * <li>Some default functionality is already implemented, as follows:
 * <ul>
 * <li>For instances of {@link JButton}, output with a String argument changes the label, output with a Boolean argument
 * enables / disables the button, output with a <code>null</code> argument disables the button, and connecting an input
 * automatically connects pressing the button to calling the listener.
 * <li>For instances of {@link JTextArea}, output with a String argument changes the contents, and if the second
 * argument is a {@link Boolean} set to <code>true</code> or the String value "1", the contents is also scrolled down.
 * <li>For instances of {@link JLabel}, output with a String argument changes the label.
 * </ul>
 * </ul>
 * 
 * @author Andrei Olaru
 */
public class PCDefaultAgentGui implements AgentGui
{
	/**
	 * The configuration for the GUI, containing parameters such as window name and type.
	 */
	protected AgentGuiConfig config = null;
	
	/**
	 * Parameters for the window, as returned by the {@link WindowLayout}.
	 */
	protected WindowParameters				params				= null;
	/**
	 * The {@link JFrame} containing the GUI.
	 */
	protected JFrame						window				= null;
	/**
	 * The components in the GUI, identified by their name. The keys will always be lower case.
	 */
	private Map<String, Component>			components			= null;
	/**
	 * Connections between component names and {@link InputListener} instances responding to that input. The keys will
	 * always be lower case.
	 */
	protected Map<String, InputListener>	inputConnections	= null;
	
	/**
	 * The default input listener, as set by means of {@link #setDefaultListener(InputListener)}. This should not be
	 * registered directly as a receiver for component input as it might not be possible to differentiate anymore if an
	 * input is connected to a listener that is default or not, especially since the default listener may be used for
	 * other inputs, as an intended (not default) listener. When a different default input listener is registered, it
	 * must be clear which components where using the default listener. This is what {@link #defaultInputListener} is
	 * used for.
	 */
	protected InputListener externalDefaultInputListener = null;
	
	/**
	 * The listener to be used as default. All calls will be relayed to {@link #externalDefaultInputListener}, if any.
	 */
	protected InputListener defaultInputListener;
	
	/**
	 * Creates and configures the GUI.
	 * 
	 * @param configuration
	 *            - the {@link AgentGuiConfig} specifying parameters such as window name and type.
	 */
	public PCDefaultAgentGui(AgentGuiConfig configuration)
	{
		defaultInputListener = new InputListener() {
			@Override
			public void receiveInput(String componentName, Vector<Object> arguments)
			{
				if(externalDefaultInputListener != null)
					externalDefaultInputListener.receiveInput(componentName, arguments);
			}
		};
		
		config = configuration;
		
		components = new Hashtable<String, Component>();
		inputConnections = new HashMap<String, AgentGui.InputListener>();
		window = new JFrame();
		
		buildGUI();
		
		placeWindow();
		
		window.setVisible(true);
	}
	
	/**
	 * Builds the default GUI by adding 2 grid elements: a label for the agent name and a text area for the log.
	 */
	protected void buildGUI()
	{
		window.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = c.weighty = 1;
		
		c.gridx = 0;
		c.gridy = 0;
		Component pic = new JLabel(config.getWindowName()); // future: should be a picture
		pic.setPreferredSize(new Dimension(100, 100));
		window.add(pic, c);
		addComponent(DefaultComponent.AGENT_NAME.toString(), pic);
		
		TextArea ta = new TextArea();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 1;
		c.gridwidth = 2;
		window.add(ta, c);
		ta.setMinimumSize(new Dimension(100, 100));
		addComponent(DefaultComponent.AGENT_LOG.toString(), ta);
	}
	
	/**
	 * Places the GUI window according to the {@link WindowLayout}.
	 */
	protected void placeWindow()
	{
		params = (WindowLayout.staticLayout != null)
				? WindowLayout.staticLayout.getWindow(config.getWindowType(), config.getWindowName(), null)
				: WindowParameters.defaultParameters();
		params.setWindow(window, true);
	}
	
	/**
	 * The method adds a component to the map of components, ensuring to transform the component name to lower case.
	 * 
	 * @param name
	 *            - the name of the component (will be transformed to lower case).
	 * @param component
	 *            - the component.
	 * @return the old value associated to the key, if any.
	 */
	protected Component addComponent(String name, Component component)
	{
		return components.put(name.toLowerCase(), component);
	}
	
	/**
	 * The methods retrieves a component, ensuring to transform the component name to lower case.
	 * 
	 * @param name
	 *            - the name of the component (will be transformed to lower case).
	 * @return the {@link Component} associated with the name.
	 */
	protected Component getComponent(String name)
	{
		return components.get(name.toLowerCase());
	}
	
	/**
	 * <b>WARNING:</b> This method should only be used with caution.
	 * 
	 * @return the mapping of names to components.
	 */
	protected Map<String, Component> getComponentMap()
	{
		return components;
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
		if(getComponent(componentName) == null)
		{
			System.err.println("component [" + componentName + "] not found."); // FIXME: get a log from somewhere
			return;
		}
		
		Component component = getComponent(componentName);
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
			if((arg1 != null) && (((arg1 instanceof Boolean) && ((Boolean) arg1).booleanValue())
					|| ((arg1 instanceof String) && (Integer.parseInt((String) arg1) == 1))))
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
			if((arg0 == null) || ((arg0 instanceof Boolean) && !((Boolean) arg0).booleanValue()))
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
		if(getComponent(componentName) == null)
		{
			System.err.println("component [" + componentName + "] not found."); // FIXME: get a log from somewhere
			return;
		}
		inputConnections.put(componentName.toLowerCase(), listener);
		Component component = getComponent(componentName);
		if(component instanceof JButton)
			((JButton) component).addActionListener(new ActionListener() {
				String comp = null;
				InputListener list = null;
				
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
	public void setDefaultListener(InputListener listener)
	{
		reconnectDefault();
		externalDefaultInputListener = listener;
	}
	
	/**
	 * Reconnects all components not associated with a listener to the default listener.
	 */
	protected void reconnectDefault()
	{
		for(String component : components.keySet())
			if(!inputConnections.containsKey(component))
				connectInput(component, defaultInputListener);
	}
	
	@Override
	public Vector<Object> getInput(String componentName)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
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
