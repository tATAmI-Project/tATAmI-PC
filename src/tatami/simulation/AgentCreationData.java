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

import tatami.core.interfaces.ParametrizedAgent;
import tatami.core.interfaces.ParametrizedAgent.AgentParameters;

/**
 * Class containing the data for the creation of an agent.
 * 
 * @author Andrei Olaru
 * 
 */
public class AgentCreationData
{
	boolean			isRemote;
	String			agentName;
	String			classpath;
	ParametrizedAgent.AgentParameters	parameters;
	String			destinationContainer;
	
	public AgentCreationData(String name, String path, ParametrizedAgent.AgentParameters agentParameters, String destination, boolean remote)
	{
		agentName = name;
		classpath = path;
		parameters = agentParameters;
		destinationContainer = destination;
		isRemote = remote;
	}
	
	@Deprecated
	public AgentCreationData(String name, String path, @SuppressWarnings("unused") Object[] params, String destination, boolean remote)
	{
		agentName = name;
		classpath = path;
		destinationContainer = destination;
		isRemote = remote;
	}
	
}
