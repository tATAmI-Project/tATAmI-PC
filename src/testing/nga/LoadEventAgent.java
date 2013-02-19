package testing.nga;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * This class defines agent who loads events from the scenario for the simulation 
 * @author Nguyen Thi Thuy Nga
 * @version 3/6/11
 */

@SuppressWarnings("serial")
public class LoadEventAgent extends Agent {
	
	public void setup() {
		// Accept objects through the object-to-agent communication
		// channel, with a maximum size of 20 queued objects
		setEnabledO2ACommunication(true, 20);
		// waker behavior wait for 5s until all the agents of platform is created
		//send event messages to the agents relevant
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		addBehaviour(new CyclicBehaviour() {
			@Override
			public void action() {
				// TODO Auto-generated method stub
				// Retrieve the first object in the queue 
				// assume that all objects are the type ACLMessage
				Object obj = getO2AObject();
				if(obj != null) {
					ACLMessage eventMess = (ACLMessage)obj;
					send(eventMess);
				}
				else 
					block();
			}

		});
	}
}
