package testing.tudor.loader_android;

import testing.tudor.utils.AgentView;
import testing.tudor.utils.GUITestAgent;
import jade.core.behaviours.LoaderBehaviour;
import jade.wrapper.ControllerException;

public class LoaderBehavAg extends GUITestAgent {

	private static final long serialVersionUID = 3935321587434848446L;

	protected void setup() {
		gui = new AgentView(this);
		try {
			appendToScreen("Here I am! So this is my birth place: "
					+ getContainerController().getContainerName()
					+ ". Hmmm! Nice!");
		} catch (ControllerException e) {
			e.printStackTrace();
		}

		addBehaviour(new LoaderBehaviour());
	}
}