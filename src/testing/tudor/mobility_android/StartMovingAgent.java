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
package testing.tudor.mobility_android;

import util.jadeutil.JadeInterface;


public class StartMovingAgent {

	public static void main(String[] args) {

		// starting the agent... It should be started after the platform was launched
		// and also the jade-android application, so that the android agent has already joined
		// the platform.
//		JadeInterface.runJadeWithArguments("-host localhost -gui");
//		JadeInterface.runJadeWithArguments("-container -container-name android -port 1099 -local-host localhost");
		
//		long startTime = System.currentTimeMillis();
//		while((System.currentTimeMillis()-startTime)-3000<0);

//		JadeInterface.runJadeWithArguments("-container -container-name pc -host 192.168.1.2 -port 1099 -local-host 192.168.1.2 -local-port 1099 -icps jade.imtp.leap.http.HTTPPeer -proto http "+
//		"agent1:testing.tudor.mobility_android.ContinuousMovingAgent();guiAg:testing.tudor.utils.GUITestAgent()");

		JadeInterface.runJadeWithArguments("-container -container-name pc -host localhost -port 1099 -local-host 132.227.203.43 -local-port 1097 "+
		"agent1:testing.tudor.mobility_android.ContinuousMovingAgent();guiAg:testing.tudor.utils.GUITestAgent()");
}
}
