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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.TextArea;
import java.util.Vector;

import javax.swing.JLabel;

import tatami.core.agent.visualization.AgentGuiConfig;
import tatami.pc.agent.visualization.PCDefaultAgentGui;

public class GroupCoordonatorAgentGui2 extends PCDefaultAgentGui{
	
	enum DebateGroupComponents {
		PROS, CONS, CLEAR, PROOPINION, CONOPINION;
	}
	
	public GroupCoordonatorAgentGui2(AgentGuiConfig configuration) {
		
		super(configuration);

		GridBagConstraints c = new GridBagConstraints();

		window.setMinimumSize(new Dimension(800, 300));

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		
		Component pro_name = new JLabel(DebateGroupComponents.PROS.toString()); 
		pro_name.setMinimumSize(new Dimension(400, 25));
		window.add(pro_name, c);
		
		c.gridx = 2;
		c.gridy = 1;
		
		Component con_name = new JLabel(DebateGroupComponents.CONS.toString()); 
		con_name.setMinimumSize(new Dimension(400, 25));
		window.add(con_name, c);
		
		c.gridx = 0;
		c.gridy = 2;
		
		TextArea pro_ta = new TextArea();
		pro_ta.setEnabled(false);
		pro_ta.setEditable(false);
		pro_ta.setMinimumSize(new Dimension(400, 125));
		window.add(pro_ta, c);
		
		c.gridy = 2;
		c.gridx = 2;
		
		TextArea con_ta = new TextArea();
		con_ta.setEnabled(false);
		con_ta.setEditable(false);
		con_ta.setMinimumSize(new Dimension(400, 125));
		window.add(con_ta, c);
		
		addComponent(DebateGroupComponents.PROOPINION.toString(), pro_ta);
		addComponent(DebateGroupComponents.CONOPINION.toString(), con_ta);
		addComponent(DebateGroupComponents.PROS.toString(), pro_name);
		addComponent(DebateGroupComponents.CONS.toString(), pro_name);
	}
	
	@Override
	public void doOutput(String componentName, Vector<Object> arguments)
	{
		String compName = componentName.toUpperCase();
		Component component;
		
		if(compName.compareTo(DebateGroupComponents.CLEAR.toString()) == 0){
			
			component = getComponent(DebateGroupComponents.PROOPINION.toString());
			if(component != null && component instanceof TextArea){
				TextArea ta = (TextArea)component;
				ta.setText(null);
			}
			component = getComponent(DebateGroupComponents.CONOPINION.toString());
			if(component != null && component instanceof TextArea){
				TextArea ta = (TextArea)component;
				ta.setText(null);
			}
		}
		else {
			if(getComponent(compName) == null)
			{
				System.err.println("component [" + componentName + "] not found."); // FIXME: get a log from somewhere
				return;
			}
			
			component = getComponent(compName);
			if(component instanceof TextArea)
			{
				if(arguments.size() > 0)
				{
					TextArea ta = (TextArea)component;
					ta.append((String)arguments.get(0) + "\n");
					ta.repaint();
				}
			}
		}
	}

}
