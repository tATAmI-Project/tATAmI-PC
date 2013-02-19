package testing.tudor.mobility;

import testing.tudor.utils.AgentView;
import core.mobile.MobileAgent;

import jade.core.behaviours.*;
import jade.wrapper.ControllerException;

public class MovingTestAgent extends MobileAgent {

	private static final long serialVersionUID = 3935321587434848446L;
	private AgentView gui;

	protected void setup() {
		gui = new AgentView(this);
		try {
			appendToScreen("Here I am! So this is my birth place: "
					+ getContainerController().getContainerName()
					+ ". Hmmm! Nice!");
		} catch (ControllerException e) {
			e.printStackTrace();
		}

		addBehaviour(new Behaviour(this) {

			private static final long serialVersionUID = 2961482086480776913L;

			@Override
			public void action() {
				block();
			}

			@Override
			public boolean done() {

				return false;
			}

		});
	}

	protected void beforeMove() {
		gui.setVisible(false);
	}

	protected void afterMove() {
		gui.setVisible(true);
		try {
			appendToScreen("I\'m happy to be in this new location: "
					+ getContainerController().getContainerName() + "!");
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		gui.addListeners();
	}

	protected void afterClone() {
		gui = new AgentView(this);
	}

	public void printToScreen(String str) {
		gui.setScreen(str);
	}

	public void appendToScreen(String str) {
		gui.appendScreen(str);
	}
}