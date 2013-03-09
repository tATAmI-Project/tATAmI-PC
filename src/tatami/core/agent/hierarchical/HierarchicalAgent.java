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
package tatami.core.agent.hierarchical;

import jade.content.lang.sl.SLCodec;
import jade.core.AID;
import jade.core.Location;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.FIPANames;
import jade.domain.mobility.MobilityOntology;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.wrapper.ControllerException;
import tatami.core.agent.hierarchical.HierarchyOntology.Vocabulary;
import tatami.core.agent.visualization.VisualizableAgent;
import tatami.core.agent.webServices.WSAgent;
import tatami.core.interfaces.ParametrizedAgent.AgentParameterName;

/**
 * Abstract class that extends class GuiAgent. Agent has a hierarchical relationship and a structure of knowledge
 * 
 * @author Nguyen Thi Thuy Nga
 * @author Andrei Olaru
 * 
 */
public class HierarchicalAgent extends WSAgent
{
	private static final long		serialVersionUID	= -7984498848330956862L;
	
	private final static int		CONTAINER_MOVE_TIME	= 2000;
	private final static int		HELLO_DAD_TIME		= 200;							// wait for all agents to be
																						// created, then notify parent
																						// of its creation
																						
	protected HierarchicalRelation	hierRelation		= new HierarchicalRelation();
	protected boolean				fixedAgent			= false;
	
	/**
	 * setup
	 */
	@Override
	public void setup()
	{
		super.setup();
		
		// TODO: are the statements below (until parameters) really necessary?
		
		/**
		 * Register the SL0 content language and the mobility ontology
		 */
		getContentManager().registerLanguage(new SLCodec(), FIPANames.ContentLanguage.FIPA_SL0);
		getContentManager().registerOntology(MobilityOntology.getInstance());
		
		/**************************************
		 * INITIAL HIERARCHICAL RELATIONSHIP AND KNOWLEDGE
		 */
		// Accept objects through the object-to-agent communication
		setEnabledO2ACommunication(true, 10);
		
		// Notify blocked threads that the agent is ready and that
		// object-to-agent communication is enabled
		
		if("true".equals(parVal(AgentParameterName.FIXED)))
		{
			fixedAgent = true;
			getLog().info("agent is fixed");
		}
		
		// parameters
		if(hasPar(AgentParameterName.HIERARCHICAL_PARENT))
			this.hierRelation = new HierarchicalRelation(parVal(AgentParameterName.HIERARCHICAL_PARENT));
		
		if(hasPar(AgentParameterName.INITIAL_LOCATION))
		{
			final Location destination = (Location) parObj(AgentParameterName.INITIAL_LOCATION);
			log.info("moving to destination container [" + destination + "]");
			addBehaviour(new WakerBehaviour(this, CONTAINER_MOVE_TIME) {
				private static final long	serialVersionUID	= 1L;
				
				@Override
				public void onWake()
				{
					((VisualizableAgent) myAgent).getLog()
							.info("moving to destination container [" + destination + "]");
					doMove(destination);
				}
			});
		}
		
		/*****************************************
		 * BEHAVIORS
		 */
		// send message to inform agent's parent of its creation
		addBehaviour(new WakerBehaviour(this, HELLO_DAD_TIME) {
			private static final long	serialVersionUID	= 1L;
			
			@Override
			public void onWake()
			{
				// send message to "parent"
				String parentAgent = hierRelation.getParent();
				if(parentAgent != null)
				{
					ACLMessage helloDad = HierarchyOntology.setupMessage(Vocabulary.HELLODAD);
					helloDad.addReceiver(new AID(hierRelation.getParent(), AID.ISLOCALNAME));
					reportAddParent(hierRelation.getParent());
					send(helloDad);
				}
			}
		});
		
		// add child to children list when received inform from child
		addBehaviour(new CyclicBehaviour() {
			private static final long	serialVersionUID	= 1L;
			
			@Override
			public void action()
			{
				ACLMessage msg = myAgent.receive(HierarchyOntology.template(Vocabulary.HELLODAD));
				if(msg != null)
				{
					String newChild = msg.getSender().getLocalName();
					hierRelation.addChild(newChild);
					// FIXME add log entry
					((HierarchicalAgent) this.myAgent).getLog().info(
							this.myAgent.getLocalName() + " has new child " + newChild);
				}
				else
				{
					block();
				}
			}
		});
		
		// remove agent from the children list
		addBehaviour(new CyclicBehaviour() {
			private static final long	serialVersionUID	= 1L;
			
			@Override
			public void action()
			{
				ACLMessage msg = myAgent.receive(HierarchyOntology.template(Vocabulary.REMOVEME));
				if(msg != null)
				{
					String child = msg.getSender().getLocalName();
					hierRelation.getChildren().remove(child);
					getLog().info(
							myAgent.getLocalName() + " has removed " + msg.getSender().getLocalName()
									+ " from the children list");
				}
				else
				{
					block();
				}
			}
		});
		
		// answer the request of being child from an agent
		addBehaviour(new CyclicBehaviour() {
			private static final long	serialVersionUID	= 1L;
			
			@Override
			public void action()
			{
				ACLMessage msg = myAgent.receive(HierarchyOntology.template(Vocabulary.DEMANDPARENT));
				if(msg != null)
				{
					getLog().info("received request of being child from " + msg.getSender().getLocalName());
					// For testing only: always agree
					ACLMessage msgAnswer = HierarchyOntology.setupMessage(Vocabulary.AGREEPARENT);
					msgAnswer.addReceiver(msg.getSender());
					myAgent.send(msgAnswer);
					// add agent into its children list
					((HierarchicalAgent) myAgent).getHierRelation().getChildren().add(msg.getSender().getLocalName());
				}
				else
				{
					block();
				}
			}
		});
		
		addBehaviour(new CyclicBehaviour() {
			private static final long	serialVersionUID	= 1L;
			
			@Override
			public void action()
			{
				// FIXME: why is this here?
			}
			
		});
		
		// add load event behavior
		// addBehaviour(new LoadEventBehaviour(this));
		// add follow parent behavior
		addBehaviour(new FollowParentBehaviour(this));
	}
	
	/**
	 * new do move behavior
	 */
	@Override
	public void doMove(Location dest)
	{
		try
		{
			if(!dest.getName().equals(this.getContainerController().getContainerName()))
			{
				log.info("moving to [" + dest.getName() + "]");
				super.doMove(dest);
			}
		} catch(ControllerException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * after move, send message to all children to request them to follow
	 */
	@Override
	protected void afterMove()
	{
		super.afterMove();
		
		log.info("arrived on new container");
		// Send a message to all is subnetwork to spark off the migration
		// 1° create the message
		
		final ACLMessage msg = HierarchyOntology.setupMessage(Vocabulary.FOLLOWME);
		try
		{
			msg.setContent(this.getContainerController().getContainerName());
		} catch(ControllerException e)
		{
			e.printStackTrace();
		}
		
		// 2° add all the receiver's AID
		for(String child : this.getHierRelation().getChildren())
		{
			msg.addReceiver(new AID(child, AID.ISLOCALNAME));
		}
		
		// 3° send it
		this.send(msg);
		
	}
	
	@Override
	public void takeDown()
	{
		super.takeDown();
		// Disables the object-to-agent communication channel, thus
		// waking up all waiting threads
		setEnabledO2ACommunication(false, 0);
	}
	
	@Override
	protected void onGuiEvent(GuiEvent ev)
	{
		// TODO Auto-generated method stub
		super.onGuiEvent(ev);
	}
	
	/**
	 * Setters and getters
	 */
	public HierarchicalRelation getHierRelation()
	{
		return hierRelation;
	}
}
