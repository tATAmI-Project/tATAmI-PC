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

import tatami.core.agent.claim.ClaimMessage;
import tatami.core.agent.hierarchical.HierarchicalAgent;
import tatami.core.agent.hierarchical.HierarchyOntology;
import tatami.core.agent.hierarchical.HierarchyOntology.Vocabulary;
import tatami.core.agent.visualization.VisualizableAgent;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * Agent A ask agent B for being one of its child
 * then waits for agent B's answer
 * @author Nguyen Thi Thuy Nga
 * @version 7/6/11
 */
@SuppressWarnings("serial")
public class AskForBeingChildBehaviour extends SimpleBehaviour{
	private boolean finish = false;
	
	private String parent = null;
	
	public AskForBeingChildBehaviour(HierarchicalAgent agent, String parent){
		super(agent);
		this.parent = parent;
	}
	
	@Override
	public void action() {
		ACLMessage msg = HierarchyOntology.setupMessage(Vocabulary.DEMANDPARENT);
		
		msg.addReceiver(new AID(this.parent, AID.ISLOCALNAME));
		myAgent.send(msg);
		((VisualizableAgent)myAgent).getLog().info(myAgent.getLocalName() + " demands " + this.parent + " to be its parent");
		
		//waits for parent's answer
		ACLMessage msgAnswer = myAgent.receive(HierarchyOntology.template(Vocabulary.AGREEPARENT));
		if(msgAnswer != null){
			//parent agree
			if(msgAnswer.getPerformative() == ACLMessage.AGREE){
				((VisualizableAgent)myAgent).getLog().info(this.parent + " agrees to be parent of " + myAgent.getLocalName());
				//demands old parent to remove it from the list of children
				((VisualizableAgent)myAgent).reportAddParent(this.parent);
				String oldParent = ((HierarchicalAgent)myAgent).getHierRelation().getParent();
				
				if(oldParent != null){
					ACLMessage msgRemoveMe = HierarchyOntology.setupMessage(Vocabulary.REMOVEME);
					msgRemoveMe.addReceiver(new AID(oldParent, AID.ISLOCALNAME));
					myAgent.send(msgRemoveMe);
					((VisualizableAgent)myAgent).getLog().info(ClaimMessage.printMessage(msgRemoveMe));
					((VisualizableAgent)myAgent).reportRemoveParent(oldParent);
				}
				
				//changes knowledge about hierarchical relation
				((HierarchicalAgent)myAgent).getHierRelation().setParent(parent);
			}
			finish = true;
		}
		else{
			block();
		}
	}

	@Override
	public boolean done() {
		return finish;
	}

}
