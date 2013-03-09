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
package testing.tudor;

import jade.core.Agent;
import jade.core.ContainerID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class TestAgent extends Agent {

	protected void setup() {
		
		addBehaviour(new MessageEchoerBehaviour());
		
		doMove(new ContainerID("pc", null));
	}
	
	protected void afterMove(){
		doMove(new ContainerID("pc", null));
	}
	
}

@SuppressWarnings("serial")
class MessageEchoerBehaviour extends CyclicBehaviour{

	public void action() {
		ACLMessage msg = myAgent.receive();
		
		//if a message is available and a listener is available
		if (msg != null){
			ACLMessage reply = msg.createReply();
			reply.setPerformative(ACLMessage.INFORM);
			reply.setContent("received: "+msg.getContent());
			myAgent.send(reply);
		} else {
			block();
		}
	}
}
