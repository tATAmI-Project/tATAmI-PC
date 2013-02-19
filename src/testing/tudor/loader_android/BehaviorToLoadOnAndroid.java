package testing.tudor.loader_android;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class BehaviorToLoadOnAndroid extends CyclicBehaviour{
	
	private static final long serialVersionUID = 9119332709852269235L;
	
	public void action() {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.addReceiver(new AID("agent1", AID.ISLOCALNAME));
		msg.setContent("Although small, this device is quite nice. Greetings from it!");
		myAgent.send(msg);
		
		block(10000);
	}
}
