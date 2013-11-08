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

import jade.gui.GuiAgent;

import java.util.Collection;
import java.util.Map;

import tatami.core.agent.AgentComponent;
import tatami.core.agent.CompositeAgent;

/**
 * The base layer of the agent. It handles the interface with Jade, agent parameters, and knowledge.
 * <p>
 * It is extended on top of and works directly with Jade's {@link GuiAgent}.
 * <p>
 * It implements {@link ParametrizedAgent}, contains the list of agent's parameters and implements
 * methods for working with parameters.
 * 
 * @author Andrei Olaru
 */
public class ParametricComponent extends AgentComponent
{
	/**
	 * The agent's parameters.
	 * <p>
	 * They can only be accessed by this level of the agent, which serves them to the layers above
	 * through specialized functions.
	 */
	private AgentParameters	parameters	= null;
	
	public ParametricComponent(CompositeAgent parent, AgentParameters agentParameters)
	{
		super(parent, AgentComponentName.PARAMETRIC_COMPONENT);
		
		parameters = agentParameters;
	}
	
	public final String parVal(AgentParameterName name)
	{
		return parameters.getValue(name.toString());
	}
	
	public final Collection<String> parVals(AgentParameterName name)
	{
		return parameters.getValues(name.toString());
	}
	
	public final Object parObj(AgentParameterName name)
	{
		return parameters.getObject(name.toString());
	}
	
	public final boolean hasPar(AgentParameterName name)
	{
		return parameters.isSet(name.toString());
	}
	
	public Map<String, Object> getUnregisteredParameters()
	{
		return parameters.getUnregisteredParameters();
	}
}
