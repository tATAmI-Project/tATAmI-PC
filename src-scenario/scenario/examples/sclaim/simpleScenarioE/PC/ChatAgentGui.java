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
package scenario.examples.sclaim.simpleScenarioE.PC;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;

import tatami.core.agent.visualization.AgentGuiConfig;
import tatami.pc.agent.visualization.PCDefaultAgentGui;


public class ChatAgentGui extends PCDefaultAgentGui
{
	enum ChatComponents {
		SEND, CHATTEXT, CHATLOG, MESSAGEINPUT
	}
	
	InputListener	inputListener	= null;
	
	public ChatAgentGui(AgentGuiConfig configuration)
	{
		super(configuration);
		
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = c.weighty = 1;
		
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		TextField tf = new TextField();
		window.add(tf, c);
		tf.setMinimumSize(new Dimension(150, 30));
		addComponent(ChatComponents.CHATTEXT.toString(), tf);
		
		c.gridx = 1;
		TextArea ta = new TextArea();
		ta.setEnabled(false);
		window.add(ta, c);
		ta.setMinimumSize(new Dimension(150, 100));
		addComponent(ChatComponents.CHATLOG.toString(), ta);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 2;
		JButton sendButton = new JButton(ChatComponents.SEND.toString());
		sendButton.addActionListener(new ActionListener() {
			@SuppressWarnings("synthetic-access")
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				if(inputListener != null)
				{
					Vector<Object> args = new Vector<Object>(1);
					args.add(((TextField)getComponent(ChatComponents.CHATTEXT.toString())).getText());
					inputListener.receiveInput(ChatComponents.MESSAGEINPUT.toString().toLowerCase(), args);
					((TextField)getComponent(ChatComponents.CHATTEXT.toString())).setText("");
				}
				else
					System.out.println("nobody to receive the input");
				// FIXME else, a log should pick up an error
			}
		});
		window.add(sendButton, c);
		addComponent(DefaultComponent.AGENT_NAME.toString(), sendButton);
	}
	
	@Override
	public void connectInput(String componentName, InputListener listener)
	{
		System.out.println("connecting input [" + componentName + "]..");
		if(componentName.equals(ChatComponents.MESSAGEINPUT.toString().toLowerCase()))
		{
			inputListener = listener;
			System.out.println("...done");
		}
		else
			super.connectInput(componentName, listener);
	}
	
	@Override
	public void doOutput(String componentName, Vector<Object> arguments)
	{
		if(componentName.compareToIgnoreCase(ChatComponents.CHATLOG.toString()) == 0)
			super.doOutput(ChatComponents.CHATLOG.toString(), arguments);	// FIXME: artificial
		else
			super.doOutput(componentName, arguments);
	}
}
