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
package tatami.simulation;

import tatami.core.agent.parametric.AgentParameters;

/**
 * Class containing the data for the creation of an agent.
 * 
 * @author Andrei Olaru
 * 
 */
public class AgentCreationData
{
	/**
	 * <code>true</code> if the agent should be created on the local machine, false if it should be created on a
	 * different machine.
	 */
	boolean			isRemote;
	/**
	 * The name of the agent.
	 */
	String			agentName;
	/**
	 * The class to instantiate at agent creation.
	 */
	String			classpath;
	/**
	 * The parameters to pass to the agent.
	 */
	AgentParameters	parameters;
	/**
	 * If the agent is remote, the name of the destination container.
	 */
	String			destinationContainer;
	
	/**
	 * Creates a new instance of information for the creation of an agent.
	 * 
	 * @param name
	 *            - the name of the agent.
	 * @param path
	 *            - the fully qualified name of the class to instantiate at agent creation.
	 * @param agentParameters
	 *            - the {@link AgentParameters} instance to pass to the agent.
	 * @param destination
	 *            - the container on which the agent should be created.
	 * @param remote
	 *            - <code>true</code> if the container is remote (not on the local machine); <code>false</code>
	 *            otherwise.
	 */
	public AgentCreationData(String name, String path, AgentParameters agentParameters, String destination,
			boolean remote)
	{
		agentName = name;
		classpath = path;
		parameters = agentParameters;
		destinationContainer = destination;
		isRemote = remote;
	}
}
