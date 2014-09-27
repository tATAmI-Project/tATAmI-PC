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

import sclaim.constructs.basic.ClaimValue;
import sclaim.constructs.basic.ClaimVariable;
import tatami.core.agent.claim.ClaimAgent;
import tatami.core.agent.claim.ClaimMessage;
import tatami.core.agent.hierarchical.HierarchyOntology;
import tatami.core.agent.hierarchical.HierarchyOntology.Vocabulary;
import tatami.core.agent.visualization.VisualizableAgent;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * Agent A asks agent B for its parent
 * then waits for agent B's answer
 * @author Marius-Tudor Benea
 * @version 9/27/14
 */
@SuppressWarnings("serial")
public class AskForGrandparentBehaviour extends SimpleBehaviour{
	private boolean finish = false;
	
	private String parent = null;
	
	public AskForGrandparentBehaviour(ClaimAgent agent){
		super(agent);
		
		ClaimValue parentValue = agent.getSt().get(new ClaimVariable("parent", true));
		
		if(parentValue!=null)
			this.parent = parentValue.getValue().toString();
		else {
			((VisualizableAgent)myAgent).getLog().info(myAgent.getLocalName() + " is already the in the root of the hierarchy. Out with no action.");
			finish = true;
		}
	}
	
	@Override
	public void action() {
		if(parent==null) {
			finish = true;
			return;
		}
		
		ACLMessage msg = HierarchyOntology.setupMessage(Vocabulary.GRANDPARENT);
		
		msg.addReceiver(new AID(this.parent, AID.ISLOCALNAME));
		myAgent.send(msg);
		((VisualizableAgent)myAgent).getLog().info(myAgent.getLocalName() + " demands " + this.parent + " for its parent");
		
		//waits for parent's answer
		ACLMessage msgAnswer = myAgent.receive(HierarchyOntology.template(Vocabulary.MYPARENT));
		if(msgAnswer != null){
			//parent informs:
			if(msgAnswer.getPerformative() == ACLMessage.INFORM){
				((VisualizableAgent)myAgent).getLog().info(this.parent + " sent " + myAgent.getLocalName() + " the name of its parent.");
				//stores the name of its gransparent
				if(msgAnswer.getContent().equals("null")){
					((ClaimAgent)myAgent).getSt().remove(new ClaimVariable("parent", true));
				} else {
				ClaimValue value = new ClaimValue(msgAnswer.getContent());
				
				//changes the parent of the ClaimAgent
				((ClaimAgent)myAgent).getSt().put(new ClaimVariable("parent", true), value);
				}
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
