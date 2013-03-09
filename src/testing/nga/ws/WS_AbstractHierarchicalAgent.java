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
package testing.nga.ws;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;


import tatami.core.agent.claim.parser.ClaimAgentDefinition;
import tatami.core.agent.claim.parser.ClaimValue;
import tatami.core.agent.claim.parser.ClaimVariable;
import tatami.core.agent.hierarchical.FollowParentBehaviour;
import tatami.core.agent.hierarchical.HierarchicalRelation;
import tatami.core.agent.hierarchical.HierarchyOntology;
import tatami.core.agent.hierarchical.HierarchyOntology.Vocabulary;
import tatami.core.agent.kb.Knowledge;
import tatami.core.agent.kb.simple.SimpleKnowledge;
import tatami.core.agent.visualization.VisualizableAgent;

/**
 * Abstract class that extends class GuiAgent. Agent has a hierarchical relationship and a structure of knowledge
 * 
 * @author Nguyen Thi Thuy Nga
 * @author Andrei Olaru
 * 
 */
public abstract class WS_AbstractHierarchicalAgent extends VisualizableAgent
{
	private static final long	serialVersionUID	= -7984498848330956862L;

	// FIXME fix naming conventions
	private final static int		helloDadTime			= 1000;								// wait for all agents to be created, then notify parent of its creation

	protected String agentType = "";
	protected ClaimAgentDefinition cad;
	protected HashMap<String,String> parameters;
	protected HierarchicalRelation	hierRelation = new HierarchicalRelation();
	private ArrayList<SimpleKnowledge>	knowledge;
	
	/**
	 * setup
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void setup()
	{
		super.setup();
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
		Object[] args = getArguments();
		if(args.length > 0)
		{
			//claim agent definition
			this.cad = (ClaimAgentDefinition)args[0];
			//agent type
			if(args.length > 1)
				this.agentType = (String)args[1];
			//parameters
			if(args.length > 2){
				this.parameters = (HashMap<String,String>)args[2];
				//first, check parameter "parent" to build hierarchicalRelation
				if(this.parameters.containsKey("parent")){
					this.hierRelation = new HierarchicalRelation(this.parameters.get("parent"));
				}
				
				if(this.cad != null)
				{
					//bind value for the parameters
					Iterator<Entry<String,String>> it = this.parameters.entrySet().iterator();
					while(it.hasNext()){
						Entry<String,String> aParam = (Entry<String,String>)it.next(); 
						String key = (String)aParam.getKey();
						String value = (String)aParam.getValue();
						
						/****************************
						 * register service
						 */
//						if(key.equals("service")){
//							register(this.agentType, value);
//						}
						/****************************/
						this.cad.getSymbolTable().put(new ClaimVariable(key), new ClaimValue(value));
					}
				}
			}
			//knowledge
			if(args.length > 3)
				this.knowledge = (ArrayList<SimpleKnowledge>)args[3];
			
			// container move
			if((args.length > 4) && (args[4] != null))
			{
				log.info("moving to destination container [" + (Location)args[4] + "]");
				doMove((Location)args[4]);
			}
		}
		
		/*****************************************
		 * BEHAVIORS
		 */
		// send message to inform agent's parent of its creation
		addBehaviour(new WakerBehaviour(this, helloDadTime) {
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
					((WS_AbstractHierarchicalAgent)this.myAgent).getLog().info(this.myAgent.getLocalName() + 
							" has new child " + newChild);
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
					String child = (String)msg.getSender().getLocalName();
					hierRelation.getChildren().remove(child);
					getLog().info(myAgent.getLocalName() + " has removed " + msg.getSender().getLocalName() + " from the children list");
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
					((WS_AbstractHierarchicalAgent)myAgent).getHierRelation().getChildren().add(msg.getSender().getLocalName());
				}
				else
				{
					block();
				}
			}
		});
		
		// add load event behavior
		//addBehaviour(new LoadEventBehaviour(this));
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
	
	public ArrayList<SimpleKnowledge> getKnowledge()
	{
		return knowledge;
	}
	
	public void addKnowledge(SimpleKnowledge kl)
	{
		knowledge.add(kl);
	}
	
	public void addListKnowledge(ArrayList<SimpleKnowledge> kl)
	{
		knowledge.addAll(kl);
	}
}
