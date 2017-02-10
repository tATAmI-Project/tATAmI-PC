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
package tatami.core.agent;

import tatami.core.agent.AgentComponent.AgentComponentName;
import tatami.core.agent.agent_type.TatamiAgent;
import tatami.simulation.AgentCreationData;
import tatami.simulation.AgentManager;

/**
 * Agent loader for agents based on {@link CompositeAgent}.
 * 
 * @author Andrei Olaru
 */
public class CompositeAgentLoader implements TatamiAgent
{
	/**
	 * Name of XML nodes in the scenario representing components.
	 */
	private static final String	COMPONENT_NODE_NAME			= "component";
	/**
	 * The name of the attribute representing the name of the component in the component node.
	 */
	private static final String	COMPONENT_NAME_ATTRIBUTE	= "name";
	/**
	 * The name of the attribute representing the class of the component in the component node. The class may not be
	 * specified, it the component is standard and its class is specified by the corresponding
	 * {@link AgentComponentName} entry.
	 */
	private static final String	COMPONENT_CLASS_ATTRIBUTE	= "classpath";
	/**
	 * The name of nodes containing component parameters.
	 */
	private static final String	PARAMETER_NODE_NAME			= "parameter";
	/**
	 * The name of the attribute of a parameter node holding the name of the parameter.
	 */
	private static final String	PARAMETER_NAME				= "name";
	/**
	 * The name of the attribute of a parameter node holding the value of the parameter.
	 */
	private static final String	PARAMETER_VALUE				= "value";
	/**
	 * The name of the parameter in the {@link AgentParameters} list that corresponds to a component entry.
	 */
	private static final String	COMPONENT_PARAMETER_NAME	= "agent_component";
	
	/**
	 * The constructor does not do any initializations.
	 */
	public CompositeAgentLoader()
	{
		// nothing to do.
	}
	
	@Override
	public String getName()
	{
		return StandardAgentLoaderType.COMPOSITE.toString();
	}
	
	//@Override
	public AgentManager load(AgentCreationData agentCreationData)
	{
		CompositeAgent agent = new CompositeAgent();
		//for(Object componentObj : agentCreationData.getParameters().getObjects(COMPONENT_PARAMETER_NAME))
		//	agent.addComponent((AgentComponent) componentObj);
		return agent;
	}

    @Override
    public void run() {
        // TODO Auto-generated method stub
        
    }

}
