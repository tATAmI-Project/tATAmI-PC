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
