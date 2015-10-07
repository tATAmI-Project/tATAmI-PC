/*******************************************************************************
 * Copyright (C) 2015 Andrei Olaru, Marius-Tudor Benea, Nguyen Thi Thuy Nga, Amal El Fallah Seghrouchni, Cedric Herpson.
 * 
 * This file is part of tATAmI-PC.
 * 
 * tATAmI-PC is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * tATAmI-PC is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with tATAmI-PC.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package scenario.application.amilab_follow_me_v1.PC;

import java.awt.Label;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import tatami.core.agent.visualization.AgentGuiConfig;
import tatami.pc.agent.visualization.PCDefaultAgentGui;

/**
 * Gui used for the AmILab application.
 * 
 * @author Claudiu-Mihai Toma
 *
 */
public class AmILabGui extends PCDefaultAgentGui
{
	/**
	 * Label that will display images.
	 */
	private JLabel label;

	/**
	 * Name of the main label.
	 */
	public static final String MAIN_LABEL = "main_label";

	/**
	 * The type of the window, to be given to WindowLayout.
	 */
	public static final String WINDOW_TYPE = "amilab-gui";

	/**
	 * Request message.
	 */
	public static final String REQUEST = "request";

	/**
	 * Creates and configures the GUI.
	 * 
	 * @param configuration
	 *            - the {@link AgentGuiConfig} specifying parameters such as window name and type.
	 */
	public AmILabGui(AgentGuiConfig configuration)
	{
		super(configuration);
		addComponent(REQUEST, new Label());
	}

	@Override
	protected void buildGUI()
	{
		label = new JLabel("", SwingConstants.CENTER);

		window.add(label);
		addComponent(MAIN_LABEL, label);
	}

	/**
	 * Getter for the main label of this GUI.
	 * 
	 * @return the label
	 */
	public JLabel getLabel()
	{
		return label;
	}

	/**
	 * Sends requests to all the clients.
	 */
	public void sendRequests()
	{
		inputConnections.get(REQUEST).receiveInput(REQUEST, new Vector<Object>());
	}
}
