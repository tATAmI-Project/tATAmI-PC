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

import tatami.core.agent.hierarchical.HierarchyOntology.Vocabulary;
import jade.core.Agent;
import jade.core.ContainerID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class FollowParentBehaviour extends CyclicBehaviour
{
	private static final long	serialVersionUID	= -428801933759718030L;

	public FollowParentBehaviour(final Agent myAgent)
	{
		super(myAgent);
	}
	
	@Override
	public void action()
	{
		// try to get the message (if it cross the filter)
		final ACLMessage msg = this.myAgent.receive(HierarchyOntology.template(Vocabulary.FOLLOWME));
		
		if(msg != null && !((HierarchicalAgent)myAgent).fixedAgent)
		{
			((HierarchicalAgent)myAgent).getLog().info(myAgent.getLocalName() + " must migrate to " + msg.getContent());
			this.myAgent.doMove(new ContainerID(msg.getContent(), null));
		}
		else
		{
			block();
		}
		
	}
}
