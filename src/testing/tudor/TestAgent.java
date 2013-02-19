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