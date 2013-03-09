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
package tatami.core.agent.claim.behavior;

import tatami.core.agent.claim.ClaimOntology;
import tatami.core.agent.hierarchical.HierarchicalAgent;
import tatami.core.agent.visualization.VisualizableAgent;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Result;
import jade.core.AID;
import jade.core.Location;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.FIPANames;
import jade.domain.JADEAgentManagement.WhereIsAgentAction;
import jade.domain.mobility.MobilityOntology;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

@SuppressWarnings("serial")
public class AskForLocation extends SimpleBehaviour
{
	
	private final MessageTemplate template = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
												   MessageTemplate.MatchSender(myAgent.getAMS()));
	
	private final String		  agentName;
	private boolean			   finished = false;
	
	public AskForLocation(HierarchicalAgent a, String agentName)
	{
		super(a);
		this.agentName = agentName;
		reset();
	}
	
	@Override
	public void action()
	{
		ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
		// fills all parameters of the request ACLMessage
		request.addReceiver(myAgent.getAMS());
		request.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
		request.setOntology(MobilityOntology.NAME);
		request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
		
		myAgent.getContentManager().registerLanguage(new SLCodec(), FIPANames.ContentLanguage.FIPA_SL0);
		myAgent.getContentManager().registerOntology(MobilityOntology.getInstance());
		
		// creates the content of the ACLMessage
		try
		{
			
			AID requestedAgent = new AID(agentName, AID.ISLOCALNAME);
			Action action = new Action();
			action.setActor(myAgent.getAMS());
			WhereIsAgentAction whereIsAgent = new WhereIsAgentAction();
			whereIsAgent.setAgentIdentifier(requestedAgent);
			action.setAction(whereIsAgent);
			myAgent.getContentManager().fillContent(request, action);
			myAgent.send(request);
			
			// receive answer from AMS
			ACLMessage msg = myAgent.receive(template);
			if(msg != null)
			{
				try
				{
					Result result = (Result) myAgent.getContentManager().extractContent(msg);
					getDataStore().put(ClaimOntology.LOC_RTN, result.getValue());
					((VisualizableAgent) myAgent).getLog().info(
							"container found " + ((Location) result.getValue()).getName());
					finished = true;
				} catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				block();
			}
		} catch(Exception fe)
		{
			fe.printStackTrace();
		}
	}
	
	@Override
	public boolean done()
	{
		return finished;
	}
	
}
