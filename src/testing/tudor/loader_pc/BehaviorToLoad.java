package testing.tudor.loader_pc;
import testing.tudor.utils.GUITestAgent;
import jade.core.behaviours.CyclicBehaviour;

public class BehaviorToLoad extends CyclicBehaviour{
	
	private static final long serialVersionUID = 9119332709852269235L;
	
	public void action() {
	((GUITestAgent) myAgent).appendToScreen("Just another tick ...");
	block(10000);
	}
}
