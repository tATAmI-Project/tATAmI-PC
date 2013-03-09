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
