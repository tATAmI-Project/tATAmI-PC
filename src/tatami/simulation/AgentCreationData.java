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
import tatami.pc.util.XML.XMLTree.XMLNode;

/**
 * Class containing the data for the creation of an agent.
 * <p>
 * Some data (agentName and platform) is covered by the enclosed {@link AgentParameters} instance, but it is also added
 * separately so as to guarantee is availability.
 * <p>
 * Most of the information available otherwise is also covered by the enclosed {@link XMLNode} reference. The node may
 * however contain additional information. Loaders not needing (or not handling) this information may never use it.
 * 
 * @author Andrei Olaru
 * 
 */
public class AgentCreationData
{
	/**
	 * The name of the agent. It cannot be <code>null</code>.
	 */
	String			agentName;
	/**
	 * The parameters to pass to the agent. The reference cannot be <code>null</code>.
	 */
	AgentParameters	parameters;
	/**
	 * <code>true</code> if the agent should be created on the local machine, <code>false</code> if it should be created
	 * on a different machine.
	 */
	boolean			isRemote;
	/**
	 * The name in which the container should be created.
	 */
	String			destinationContainer	= null;
	
	/**
	 * A reference to the actual node resulted from parsing the scenario file. It cannot be <code>null</code>.
	 */
	XMLNode			node;
	
	/**
	 * Creates a new instance of information for the creation of an agent.
	 * 
	 * @param name
	 *            - the name of the agent.
	 * @param agentParameters
	 *            - the {@link AgentParameters} instance to pass to the agent.
	 * @param destination
	 *            - the container on which the agent should be created.
	 * @param remote
	 *            - <code>true</code> if the container is remote (not on the local machine); <code>false</code>
	 *            otherwise.
	 * @param scenarioNode
	 *            - the {@link XMLNode} instance corresponding to the agent, as resulted from parsing the scenario file.
	 */
	public AgentCreationData(String name, AgentParameters agentParameters, String destination, boolean remote,
			XMLNode scenarioNode)
	{
		if(name == null)
			throw new NullPointerException("Agent name cannot be null");
		if(agentParameters == null)
			throw new NullPointerException("Agent parameters cannot be null");
		if(scenarioNode == null)
			throw new NullPointerException("XML node cannot be null");
		
		agentName = name;
		parameters = agentParameters;
		destinationContainer = destination;
		isRemote = remote;
		node = scenarioNode;
	}
	
	/**
	 * @return the name of the agent.
	 */
	public String getAgentName()
	{
		return agentName;
	}
	
	/**
	 * @return the parameters to pass to the agent.
	 */
	public AgentParameters getParameters()
	{
		return parameters;
	}
	
	/**
	 * @return <code>true</code> if the agent should be created on the local machine, <code>false</code> if it should be
	 *         created on a different machine.
	 */
	public boolean isRemote()
	{
		return isRemote;
	}
	
	/**
	 * @return the name of the container in which the agent should be created.
	 */
	public String getDestinationContainer()
	{
		return destinationContainer;
	}
	
	/**
	 * @return a reference to the actual node resulted from parsing the scenario file.
	 */
	public XMLNode getNode()
	{
		return node;
	}
}
