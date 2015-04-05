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
package agent_packages.example.follow_me.PC;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;

import tatami.pc.agent.visualization.PCDefaultAgentGui;

public class HostAgentGui extends PCDefaultAgentGui
{
	public static final String	BUTTON_NAME		= "call";
	
	InputListener				inputListener	= null;
	
	public HostAgentGui(AgentGuiConfig configuration)
	{
		super(configuration);
		
		JButton theButton = new JButton("Follow me here");
		components.put(BUTTON_NAME, theButton);
		window.add(theButton);
		
		components.put(DefaultComponent.AGENT_LOG.toString(), new TextArea()); // will not be used
		
		theButton.addActionListener(new ActionListener() {
			@SuppressWarnings("synthetic-access")
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				Vector<Object> args = new Vector<Object>();
				args.add("unused");
				inputConnections.get(BUTTON_NAME).receiveInput(BUTTON_NAME, args);
			}
		});
	}
}
