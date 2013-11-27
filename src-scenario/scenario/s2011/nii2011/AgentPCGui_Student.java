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
package s2011.nii2011;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.TextArea;

import javax.swing.JLabel;
import javax.swing.JPanel;

import tatami.pc.agent.visualization.PCDefaultAgentGui;



public class AgentPCGui_Student extends PCDefaultAgentGui
{
	public AgentPCGui_Student(AgentGuiConfig config)
	{
		super(config);
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 3;
		c.gridheight = 1;
		c.weightx = c.weighty = 1;
		TextArea ita = new TextArea();
		ita.setMinimumSize(new Dimension(100, 100));
		
		JPanel panel = new JPanel(new FlowLayout());
		JLabel output = new JLabel("");
		panel.add(output);
		components.put("outputField", output);
		JLabel output2 = new JLabel("");
		panel.add(output2);
		components.put("outputField2", output2);
		panel.add(new JLabel(" | opinion:"));
		panel.add(ita);
		
		window.add(panel, c);
		components.put("inputTextArea", ita);
		
		window.setVisible(true);
	}
}
