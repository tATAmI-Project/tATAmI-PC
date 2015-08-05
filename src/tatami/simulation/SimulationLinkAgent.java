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
package tatami.simulation;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.xqhs.util.logging.Logger;
import tatami.core.agent.AgentComponent.AgentComponentName;
import tatami.core.agent.AgentEvent.AgentEventType;
import tatami.core.agent.CompositeAgent;
import tatami.core.agent.messaging.MessagingComponent;
import tatami.core.agent.visualization.VisualizableComponent;
import tatami.core.agent.visualization.VisualizableComponent.Vocabulary;

/**
 * In order to be able to communicate with other agents in a platform, the {@link SimulationManager} keeps an agent on
 * each of the available platforms, in order to relay events and control messages to the agents.
 * 
 * @author Andrei Olaru
 */
public class SimulationLinkAgent extends CompositeAgent
{
	/**
	 * The serial UID.
	 */
	private static final long	serialVersionUID	= 9020113598000072111L;
	
	/**
	 * The name of this agent, as set by the owning {@link SimulationManager}.
	 */
	String						name				= null;
	
	/**
	 * A {@link Map} of agent names to agent addresses (as provided by the messaging component).
	 */
	Set<String>					agents				= new HashSet<String>();
	
	/**
	 * Creates a new instance, with the specified name. The instance will have two components: a
	 * {@link VisualizableComponent} and a {@link MessagingComponent}.
	 * 
	 * @param agentName
	 *            - the name of the agent.
	 * @param messaging
	 *            - the messaging component to use, if any. If <code>null</code>, no messaging component will be added.
	 */
	public SimulationLinkAgent(String agentName, MessagingComponent messaging)
	{
		name = agentName;
		addComponent(new VisualizableComponent());
		if(messaging != null)
			addComponent(messaging);
	}
	
	@Override
	public String getAgentName()
	{
		return name;
	}
	
	/**
	 * Registers the agent specified by the {@link AgentCreationData} instance in the argument as being visualized by
	 * this {@link SimulationLinkAgent}. The agent's name and address are put in a local map, and the agent is informed
	 * of the new visualization monitor.
	 * 
	 * @param agentData
	 *            - information about the agent to be enrolled.
	 * @return <code>true</code> if the operation has succeeded; <code>false</code> otherwise.
	 */
	public boolean enrol(AgentCreationData agentData)
	{
		Logger log = ((VisualizableComponent) getComponent(AgentComponentName.VISUALIZABLE_COMPONENT)).getLog();
		MessagingComponent msgComp = (MessagingComponent) getComponent(AgentComponentName.MESSAGING_COMPONENT);
		if(msgComp == null)
		{
			log.error("Messaging component not present");
			return false;
		}
		String target = msgComp.makePath(agentData.getAgentName(), Vocabulary.VISUALIZATION.toString(),
				Vocabulary.VISUALIZATION_MONITOR.toString());
		log.trace("Sending enrollment message to [].", target);
		if(!msgComp.sendMessage(target, msgComp.getAgentAddress(), msgComp.getAgentAddress()))
		{
			log.error("Sending of enrollment message to agent [" + agentData.getAgentName() + "] failed");
			return false;
		}
		agents.add(agentData.getAgentName());
		return true;
	}
	
	/**
	 * Sends an message to all agents that are monitored by this agent, indicating an agent event to be posted.
	 * 
	 * @param event
	 *            - the event to post to the agent's event queue, as {@link AgentEventType}.
	 * @return <code>true</code> if the operation has succeeded; <code>false</code> if any message (possibly all) was
	 *         not successfully sent.
	 */
	public boolean broadcast(AgentEventType event)
	{
		Logger log = ((VisualizableComponent) getComponent(AgentComponentName.VISUALIZABLE_COMPONENT)).getLog();
		MessagingComponent msgComp = (MessagingComponent) getComponent(AgentComponentName.MESSAGING_COMPONENT);
		if(msgComp == null)
		{
			log.error("Messaging component not present");
			return false;
		}
		
		boolean ret = true;
		for(String agent : agents)
		{
			String target = msgComp.makePath(agent, Vocabulary.VISUALIZATION.toString(), Vocabulary.CONTROL.toString());
			if(!msgComp.sendMessage(target, msgComp.getAgentAddress(), event.toString()))
			{
				log.error("Sending of exit message to [" + target + "] failed");
				ret = false;
			}
		}
		return ret;
	}
}
