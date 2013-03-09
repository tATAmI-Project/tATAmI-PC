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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.TextArea;

public class PCVisualizationGui extends PCDefaultAgentGui
{
	public enum VisualizationComponent {
		CENTRAL_LOG, AGENT_GRAPH
	}
	
	public PCVisualizationGui(AgentGuiConfig configuration)
	{
		super(configuration);
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1;
		
		TextArea centralLogDisplay = new TextArea();
		centralLogDisplay.setMinimumSize(new Dimension(100, 100));
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		window.add(centralLogDisplay, c);
		components.put(VisualizationComponent.CENTRAL_LOG.toString(), centralLogDisplay);
		
		TextArea graphDisplay = new TextArea();
		graphDisplay.setMinimumSize(new Dimension(100, 100));
		c.gridx = 2;
		c.gridy = 1;
		c.gridwidth = 1;
		window.add(graphDisplay, c);
		components.put(VisualizationComponent.AGENT_GRAPH.toString(), graphDisplay);
		
		window.setVisible(true);
	}
}
