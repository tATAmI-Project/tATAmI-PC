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
package testing.tudor.mobility_android;

import java.util.Hashtable;

import jade.content.ContentElement;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Result;
import jade.core.Agent;
import jade.core.Location;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.JADEAgentManagement.QueryPlatformLocationsAction;
import jade.domain.mobility.MobilityOntology;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

@SuppressWarnings("serial")
public class ContinuousMovingAgent extends Agent{
//	public String currentContainer;
	
	protected void setup() {
//		currentContainer = "pc";
		addBehaviour(new MovingBehaviour(this,5000, "android"));
		addBehaviour(new MessageEchoerBehaviour());
	}
	
	public void beforeMove()
	{
	}
	
	public void afterMove()
	{
		addBehaviour(new MovingBehaviour(this,5000, "android"));
	}
}

@SuppressWarnings("serial")
class MovingBehaviour extends WakerBehaviour {
	public String destinationContainer;
	
	public MovingBehaviour(Agent a, long timeout, String destContainer) {
		super(a, timeout);
		destinationContainer = destContainer;
	}

	public void onWake()
	{
		moveToContainer(destinationContainer);
	}
	
	public void moveToContainer(String containerName)
	{
		myAgent.getContentManager().registerLanguage(new SLCodec());
		myAgent.getContentManager().registerOntology(MobilityOntology.getInstance());

		try {
			Hashtable<String, Location> locations = new Hashtable<String, Location>();

			// Get available locations with AMS
			sendRequest(new Action(myAgent.getAMS(), new QueryPlatformLocationsAction()));

			//Receive response from AMS
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchSender(myAgent.getAMS()),
					MessageTemplate.MatchPerformative(ACLMessage.INFORM));
			ACLMessage resp = myAgent.blockingReceive(mt);
			ContentElement ce = myAgent.getContentManager().extractContent(resp);
			Result result = (Result) ce;
			jade.util.leap.Iterator it = result.getItems().iterator();
			while (it.hasNext()) {
				Location loc = (Location)it.next();
				locations.put(loc.getName(), loc);
			}

			Location dest = locations.get(containerName);
			myAgent.doMove(dest);
		}
		catch (Exception e) { e.printStackTrace(); }
	}
	
	public void sendRequest(Action action)
	{
		ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
		request.setLanguage(new SLCodec().getName());
		request.setOntology(MobilityOntology.getInstance().getName());
		try {
			myAgent.getContentManager().fillContent(request, action);
			request.addReceiver(action.getActor());
			myAgent.send(request);
		}
		catch (Exception ex) { ex.printStackTrace(); }
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
