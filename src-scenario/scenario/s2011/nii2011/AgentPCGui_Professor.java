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

import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;

import javax.swing.JPanel;

import tatami.pc.agent.visualization.PCDefaultAgentGui;



public class AgentPCGui_Professor extends PCDefaultAgentGui
{
	public AgentPCGui_Professor(AgentGuiConfig config)
	{
		super(config);
		
		JPanel buttonsPanel = new JPanel(new FlowLayout());
		
		Button lsb = new Button("Last Slide");
		buttonsPanel.add(lsb);
		components.put("lastSlideButton", lsb);
		
		Button ecb = new Button("End Course");
		buttonsPanel.add(ecb);
		components.put("endCourseButton", ecb);
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 3;
		c.gridheight = 2;
		c.weightx = c.weighty = 1;
		window.add(buttonsPanel, c);
		
		window.setVisible(true);
	}
}
