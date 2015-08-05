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
import tatami.core.agent.messaging.MessagingComponent;
import tatami.core.util.platformUtils.PlatformUtils;

/**
 * Implements messaging functionality, using the features offered by Jade.
 * 
 * @author Andrei Olaru
 */
public class JadeMessagingComponent extends MessagingComponent
{
	/**
	 * The serial UID.
	 */
	private static final long	serialVersionUID	= 6948064285049451164L;
	
	@Override
	protected void atAgentStart(AgentEvent event)
	{
		super.atAgentStart(event);
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
	
	@Override
	protected void atAgentStop(AgentEvent event)
	{
		super.atAgentStop(event);
		getWrapper().doExit();
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
		return agentName;
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
		try
		{
			getAgentLog().dbg(MessagingDebug.DEBUG_MESSAGING,
					"Sending message to [" + target + "] with content [" + content + "].");
		} catch(NullPointerException e1)
		{
			// it's ok
		}
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
			try
			{
				getAgentLog().error("Platform link is not a jade agent wrapper:" + PlatformUtils.printException(e));
			} catch(NullPointerException e1)
			{
				// it's ok
			}
			throw new IllegalStateException("Platform link is not a jade agent wrapper:"
					+ PlatformUtils.printException(e));
		}
		if(wrapper == null)
		{
			try
			{
				getAgentLog().error("Platform link is null.");
			} catch(NullPointerException e1)
			{
				// it's ok
			}
			throw new IllegalStateException("Platform link is null.");
		}
		return wrapper;
	}
}
