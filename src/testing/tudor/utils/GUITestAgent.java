package testing.tudor.utils;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import core.mobile.MobileAgent;

public class GUITestAgent extends MobileAgent {

	private static final long serialVersionUID = -1235559183895438099L;

	protected AgentView gui;
	
	@SuppressWarnings("serial")
	protected void setup()
	{
		gui = new AgentView(this);
		addBehaviour(new CyclicBehaviour(){

			public void action() {
				ACLMessage msg = myAgent.receive();
				
				//if a message is available and a listener is available
				if (msg != null){
					gui.appendScreen(msg.getSender().getLocalName()+": "+msg.getContent());
				} else {
					block();
				}
			}
		});
	}
	
	protected void takeDown()
	{
		gui.dispose();
	}
	
	public void printToScreen(String str) {
		gui.setScreen(str);
	}

	public void appendToScreen(String str) {
		gui.appendScreen(str);
	}
}
