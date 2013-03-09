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
package testing.tudor.jade_interface;

import util.jadeutil.JadeInterface;

public class JadeInterfaceTest {

	public static void main(String[] args) {
		JadeInterface ji;
		
//		JadeInterface.runJadeWithArguments("-host localhost -port 9999 -agents ag1:testing.tudor.utils.GUITestAgent()");
//		JadeInterface.runJadeWithArguments("-host localhost -port 9999 -gui -agents ag1:testing.tudor.utils.GUITestAgent()");
//		JadeInterface.runJadeWithArguments("-host localhost -port 9999 -gui -platform-id Claim -agents ag1:testing.tudor.utils.GUITestAgent()");
		
		ji = new JadeInterface();
		
		ji.startPlatform("ClaimPlatform","myMain");
		
		ji.addAgentToContainer("myMain", "claimAgent1", "testing.tudor.utils.GUITestAgent", null);
		
		ji.startContainer(null);
		ji.startContainer("secondContainer");
		ji.startContainer(null);
		ji.startContainer("fourthContainer");
		ji.startContainerWithArguments("-local-host localhost");
		ji.startContainerWithArguments("-container -local-host localhost");
		ji.startContainerWithArguments("-container -local-host localhost -container-name seventhContainer");
		
		ji.addAgentToContainer("seventhContainer", "7thCAg1", "testing.tudor.utils.GUITestAgent", null);
		
		System.out.println(ji.getMainContainerName());
	}

}
