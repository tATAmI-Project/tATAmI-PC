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
package testing.tudor.loader_pc;

import util.jadeutil.JadeInterface;


public class StartLoaderTest {

	public static void main(String[] args) {

		// starting the platform:
		JadeInterface ji = new JadeInterface();

		ji.startPlatform(
				"ClaimPlatform", null);
		
		// Starting the container:
		ji.startContainer("PCContainer");

		// Starting the agents:
		ji.addAgentToContainer("PCContainer", "agent1",
				"testing.tudor.loader_pc.SenderBehavAg", null);
		ji.addAgentToContainer("PCContainer", "agent2",
				"testing.tudor.loader_pc.LoaderBehavAg", null);
		
/*		long startTime = System.currentTimeMillis();
		while(System.currentTimeMillis()<startTime+10000);
		System.out.println("Shuting down...");
		ji.shutDownPlatform();
*/	}

}
