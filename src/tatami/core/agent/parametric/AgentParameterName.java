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
package tatami.core.agent.parametric;

import tatami.simulation.AgentLoader;
import tatami.simulation.Boot;
import tatami.simulation.PlatformLoader;


/**
 * Here be all names of 'registered' parameters (see {@link AgentParameters}).
 * <p>
 * Each parameter has an internal name - the constant in the enum - and the name in the scenario file - given in the
 * constructor of the enum constant.
 * <p>
 * It would be advised that these values be used when reading from the XML file.
 * <p>
 * Note about the javadoc: some links were not included in order to avoid imports outside of the <code>core</code>
 * package.
 * 
 * @author Andrei Olaru
 * 
 */
public enum AgentParameterName {
	
	// ///////// Simulation/Boot
	/**
	 * The class of the agent implementation, in case the loader needs it.
	 * 
	 * Used by {@link Boot}.
	 */
	AGENT_CLASS("classpath"),
	
	/**
	 * The {@link AgentLoader} to use for this agent.
	 */
	AGENT_LOADER("loader"),
	
	/**
	 * The {@link PlatformLoader} to use for this agent.
	 */
	AGENT_PLATFORM("platform"),
	
	/**
	 * Agent packages with classes that are relevant to this agent (GUIs, java functions, etc).
	 * 
	 * Used by VisualizableAgent (for the GUI) and ClaimAgent (for java function classes).
	 */
	AGENT_PACKAGE("agentPackage"),
	
	// ///////// basic functionality
	/**
	 * The name of the agent.
	 */
	AGENT_NAME("name"),
	
	;
	
	/**
	 * The name of the parameter, as appearing in the scenario file.
	 */
	String	name	= null;
	
	/**
	 * @param parName
	 *            - the name of the parameter as will appear in the scenario file.
	 */
	private AgentParameterName(String parName)
	{
		name = parName;
	}
	
	@Override
	public String toString()
	{
		return name;
	}
	
	/**
	 * Retrieves the {@link AgentParameterName} instance that corresponds to the specified name.
	 * 
	 * @param name
	 *            - the name to search for (the name with which the instance was created).
	 * @return - the corresponding {@link AgentParameterName} instance.
	 */
	public static AgentParameterName getName(String name)
	{
		for(AgentParameterName parName : AgentParameterName.values())
			if(parName.toString().equals(name))
				return parName;
		return null;
	}
}
