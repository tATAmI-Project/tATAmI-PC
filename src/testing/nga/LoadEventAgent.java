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
