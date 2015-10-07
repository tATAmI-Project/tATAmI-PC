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
package scenario.s2012.debateScenario_android.PC;

import java.awt.Component;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.swing.JTextArea;

import tatami.core.agent.visualization.AgentGui;
import tatami.core.agent.visualization.AgentGuiConfig;
import tatami.pc.util.windowLayout.WindowLayout;
import tatami.pc.util.windowLayout.WindowParameters;

public class PDAAgentGui implements AgentGui{

	protected AgentGuiConfig				config				= null;
	protected WindowParameters				params				= null;
	protected JFramePDA						window				= null;
	protected Map<String, Component>		components			= null;
	
	enum PDAComponents {
		AGENT_LOG, AGENT_NAME, CLEAR, PROOPINION, CONOPINION, JOIN, ADD, DELETE;
	}
	
	public PDAAgentGui(AgentGuiConfig configuration) {

		config = configuration;
		components = new Hashtable<String, Component>();
		window = new JFramePDA();
		
		window.jLabel4.setText(config.getWindowName());

		components.put(PDAComponents.JOIN.toString(), window.jTextField1);
		components.put(PDAComponents.ADD.toString(), window.jTextField2);
		components.put(PDAComponents.DELETE.toString(), window.jTextField3);
		components.put(PDAComponents.PROOPINION.toString(), window.jTextArea1);
		components.put(PDAComponents.CONOPINION.toString(), window.jTextArea2);
		components.put(PDAComponents.AGENT_NAME.toString(), window.jLabel4);
		
		window.setVisible(true);
	}
	
	@Override
	public void doOutput(String componentName, Vector<Object> arguments)
	{
		String compName = componentName.toUpperCase();
		Component component;
		
		if(compName.compareTo(PDAComponents.CLEAR.toString()) == 0){
			
			component = components.get(PDAComponents.PROOPINION.toString());
			if(component != null && component instanceof JTextArea){
				JTextArea ta = (JTextArea)component;
				ta.setText(null);
			}
			component = components.get(PDAComponents.CONOPINION.toString());
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
					ta.append((String)arguments.get(0) + "\n");
					ta.repaint();
				}
			}
		}
	}

	@Override
	public void connectInput(String componentName, InputListener input) {
		
		final InputListener listener = input;
		
		if((PDAComponents.JOIN.toString().toLowerCase()).equals(componentName.toLowerCase()))
		{
			window.joinListener = listener;
			System.out.println("connectInput join");
		}
		else if((PDAComponents.ADD.toString().toLowerCase()).equals(componentName.toLowerCase()))
		{
			window.addListener = listener;
			System.out.println("connectInput add");
		}
		else if((PDAComponents.DELETE.toString().toLowerCase()).equals(componentName.toLowerCase()))
		{
			window.deleteListener = listener;
			System.out.println("connectInput delete");
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
	public void background(AgentGuiBackgroundTask agentGuiBackgroundTask, Object argument,
			ResultNotificationListener resultListener)
	{
		// TODO Auto-generated method stub
		
	} 
}
