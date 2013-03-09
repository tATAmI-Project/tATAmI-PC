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

import java.awt.GridBagConstraints;
import java.awt.Panel;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class PCSimulationGui extends PCDefaultAgentGui
{
	public enum SimulationComponent {
		CREATE, START, TIME, PAUSE, CLEAR, EXIT
	}
	
	public PCSimulationGui(AgentGuiConfig configuration)
	{
		super(configuration);
		
		Panel box = new Panel();
		box.setLayout(new BoxLayout(box, BoxLayout.LINE_AXIS));
		JButton create = new JButton("Create agents");
		box.add(create);
		components.put(SimulationComponent.CREATE.toString(), create);
		
		JButton start = new JButton("and Start");
		box.add(start);
		components.put(SimulationComponent.START.toString(), start);
		
		JLabel displayedTime = new JLabel();
		displayedTime.setText("--:--.-");
		box.add(displayedTime);
		components.put(SimulationComponent.TIME.toString(), displayedTime);
		
		JButton pause = new JButton("Pause");
		box.add(pause);
		components.put(SimulationComponent.PAUSE.toString(), pause);
		
		JButton clear = new JButton("Clear agents");
		box.add(clear);
		components.put(SimulationComponent.CLEAR.toString(), clear);
		
		JButton stop = new JButton("Exit");
		box.add(stop);
		components.put(SimulationComponent.EXIT.toString(), stop);
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 3;
		window.add(box, c);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
	}
}
