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
package tatami.core.agent.parametric;

import java.util.Collection;
import java.util.Map;

import tatami.core.agent.AgentComponent;

/**
 * This basic component of the agent handles agent parameters.
 * <p>
 * It contains the list of agent's parameters and implements methods for retrieving parameters.
 * <p>
 * The class is underpinned by an {@link AgentParameters} instance.
 * <p>
 * Access to 'registered' parameters (specified in {@link AgentParameterName}) can be only be done per-name.
 * 'Unregistered' parameters can also be retrieved in bulk.
 * 
 * @author Andrei Olaru
 */
public class ParametricComponent extends AgentComponent
{
	/**
	 * Class UID
	 */
	private static final long	serialVersionUID	= -409355822786197494L;
	
	/**
	 * The agent's parameters. They are served to other components by means of specialized methods.
	 */
	private AgentParameters		parameters			= null;
	
	/**
	 * Constructs a new instance of parametric component.
	 * 
	 * @param agentParameters
	 *            - the initial parameters of the agent.
	 */
	public ParametricComponent(AgentParameters agentParameters)
	{
		super(AgentComponentName.PARAMETRIC_COMPONENT);
		
		parameters = agentParameters;
	}
	
	/**
	 * Retrieves a value associated with the name (it may not be the only one). An exception may be thrown if the first
	 * found value is not a {@link String} instance.
	 * 
	 * @param name
	 *            - the name of the searched entry.
	 * @return the first found value associated with the name.
	 */
	public final String parVal(AgentParameterName name)
	{
		return parameters.getValue(name.toString());
	}
	
	/**
	 * Retrieves all value associated with the name. An exception may be thrown if any value is not a {@link String}
	 * instance.
	 * 
	 * @param name
	 *            - the name of the searched entry(ies).
	 * @return a {@link Collection} of values associated with the name.
	 */
	public final Collection<String> parVals(AgentParameterName name)
	{
		return parameters.getValues(name.toString());
	}
	
	/**
	 * Retrieves a value associated with the name (it may not be the only one).
	 * 
	 * @param name
	 *            - the name of the searched entry.
	 * @return the first found value associated with the name, as an {@link Object} instance.
	 */
	public final Object parObj(AgentParameterName name)
	{
		return parameters.getObject(name.toString());
	}
	
	/**
	 * Indicates whether a parameter with the specified name has been set.
	 * 
	 * @param name
	 *            - the name of the searched entry.
	 * @return <code>true</code> if any entry exists with the specified name.
	 */
	public final boolean hasPar(AgentParameterName name)
	{
		return parameters.isSet(name.toString());
	}
	
	/**
	 * Retrieves a {@link Map} with the 'unregistered' parameters - those parameters whose name does not correspond to a
	 * {@link AgentParameterName} instance.
	 * 
	 * @return the map of entries. Multiple entries with the same name will be ignored.
	 */
	public Map<String, Object> getUnregisteredParameters()
	{
		return parameters.getUnregisteredParameters();
	}
}
