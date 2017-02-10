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

import java.util.ArrayList;
import java.util.HashMap;

import net.xqhs.util.XML.XMLTree.XMLNode;
import tatami.core.agent.agent_type.TatamiAgent;
import tatami.core.agent.components.ComponentCreationData;

/**
 * Class containing the data for the creation of an agent. This information is all the information needed by (and
 * provided to) {@link SimulationManager} to create an agent.
 * <p>
 * Some data (agentName and platform) is covered by the enclosed {@link AgentParameters} instance, but it is also added
 * separately so as to guarantee its availability.
 * <p>
 * Most of the information available otherwise is also covered by the enclosed {@link XMLNode} reference. The node may
 * however contain additional information. Loaders not needing (or not handling) this information may never use it.
 * 
 * @author Andrei Olaru
 * 
 */
public class AgentCreationData extends HashMap<String, String>
{
	
	ArrayList<ComponentCreationData> mComponentsData;
	
	
	/**
	 * Creates a new instance of information for the creation of an agent.
	 * 
	 * @param name
	 *            - the name of the agent.
	 * @param agentParameters
	 *            - the {@link AgentParameters} instance to pass to the agent.
	 * @param agentPackages
	 *            - the set of packages where the loader and the components may look for data.
	 * @param destination
	 *            - the container on which the agent should be created.
	 * @param remote
	 *            - <code>true</code> if the container is remote (not on the local machine); <code>false</code>
	 *            otherwise.
	 * @param agentPlatform
	 *            - the platform on which the agent will execute.
	 * @param agentLoader
	 *            - the {@link TatamiAgent} instance to use for loading the agent.
	 * @param scenarioNode
	 *            - the {@link XMLNode} instance corresponding to the agent, as resulted from parsing the scenario file.
	 */
	public AgentCreationData(String name, String destination, String agentType, ArrayList<ComponentCreationData> componentsData)
	{
		if(name == null)
			throw new NullPointerException("Agent name cannot be null");
		
		put("name", name);
		put("container", destination);
		put("type", agentType);
		mComponentsData = componentsData;
	}
	
	/**
	 * @return the name of the agent.
	 */
	public String getAgentName()
	{
		return get("name");
	}
	
	/**
	 * @return the name of the container in which the agent should be created.
	 */
	public String getDestinationContainer()
	{
		return get("container");
	}
	
	public String getType(){
	    return get("type");
	}
	
	public ArrayList<ComponentCreationData> getComponentsData(){
	    return mComponentsData;
	}
	
}
