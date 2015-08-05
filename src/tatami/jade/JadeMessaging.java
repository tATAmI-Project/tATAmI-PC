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
package tatami.jade;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import tatami.core.agent.AgentEvent;
import tatami.core.agent.AgentEvent.AgentEventHandler;
import tatami.core.agent.AgentEvent.AgentEventType;
import tatami.core.agent.CompositeAgent;
import tatami.core.agent.messaging.MessagingComponent;
import tatami.core.agent.visualization.VisualizableComponent;
import tatami.core.util.platformUtils.PlatformUtils;

/**
 * Implements messaging functionality, using the features offered by Jade.
 * 
 * @author Andrei Olaru
 */
public class JadeMessaging extends MessagingComponent
{
	/**
	 * The serial UID.
	 */
	private static final long	serialVersionUID	= 6948064285049451164L;
	
	@Override
	protected void parentChangeNotifier(CompositeAgent oldParent)
	{
		super.parentChangeNotifier(oldParent);
		
		
		
		registerHandler(AgentEventType.AGENT_START, new AgentEventHandler() {
			@Override
			public void handleEvent(AgentEvent event)
			{
				initialize(); // TODO: should this be done on parent change instead of agent start?
			}
		});
	}
	
	/**
	 * Performs initialization: registers a behavior to receive messages received by the Jade agent.
	 */
	protected void initialize()
	{
		getWrapper().addBehaviour(new CyclicBehaviour() {
			private static final long	serialVersionUID	= 7120577948083587188L;
			
			@Override
			public void action()
			{
				ACLMessage message = myAgent.receive();
				if(message != null)
				{
					String receiver = myAgent.getLocalName();
					String source = message.getSender().getLocalName();
					String ontology = message.getOntology();
					String protocol = message.getProtocol();
					String conversation = message.getConversationId();
					String content = message.getContent();
					String destination = MessagingComponent.makePathHelper(receiver, ontology, protocol, conversation);
					
					receiveMessage(source, destination, content);
				}
				else
					block();
			}
		});
		
	}
	
	/**
	 * Relay for the overridden method to avoid warning.
	 */
	@Override
	protected void receiveMessage(String source, String destination, String content)
	{
		super.receiveMessage(source, destination, content);
	}
	
	@Override
	public String getAgentAddress(String agentName, String containerName)
	{
		return agentName;
	}
	
	@Override
	public String getAgentAddress(String agentName)
	{
		return getAgentAddress(agentName, null);
	}

	@Override
	public boolean sendMessage(String target, String source, String content)
	{
		String[] targetElements = target.split(ADDRESS_SEPARATOR, 4);
		int nElements = targetElements.length;
		if(nElements == 0)
			return false;
		ACLMessage message = new ACLMessage(ACLMessage.INFORM);
		message.addReceiver(new AID(targetElements[0], AID.ISLOCALNAME));
		if(nElements > 1)
			message.setOntology(targetElements[1]);
		if(nElements > 2)
			message.setProtocol(targetElements[2]);
		if(nElements > 3)
			message.setConversationId(targetElements[3]);
		message.setContent(content);
		if(getVisualizable() != null && getVisualizable().getLog() != null)
			getVisualizable().getLog().dbg(MessagingDebug.DEBUG_MESSAGING,
					"Sending message to [" + target + "] with content [" + content + "].");
		getWrapper().send(message);
		return true;
	}
	
	/**
	 * Retrieves the wrapping Jade agent.
	 * 
	 * @return the wrapper agent.
	 */
	protected JadeAgentWrapper getWrapper()
	{
		JadeAgentWrapper wrapper;
		try
		{
			wrapper = (JadeAgentWrapper) getPlatformLink();
		} catch(ClassCastException e)
		{
			if(getVisualizable() != null)
				getVisualizable().getLog().error(
						"Platform link is not a jade agent wrapper:" + PlatformUtils.printException(e));
			throw new IllegalStateException("Platform link is not a jade agent wrapper:"
					+ PlatformUtils.printException(e));
		}
		if(wrapper == null)
		{
			if(getVisualizable() != null && getVisualizable().getLog() != null)
				getVisualizable().getLog().error("Platform link is null.");
			throw new IllegalStateException("Platform link is null.");
		}
		return wrapper;
	}
	
	public VisualizableComponent getVisualizable()
	{
		return (VisualizableComponent) getAgentComponent(AgentComponentName.VISUALIZABLE_COMPONENT);
	}
	
}
