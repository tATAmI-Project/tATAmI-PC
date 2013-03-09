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
package testing.tudor.loader_android;

import util.jadeutil.JadeInterface;


public class StartJadePlatform {

	public static void main(String[] args) {

		// starting the platform:
//		JadeInterface.runJadeWithArguments("-host localhost -platform-id ClaimPlatform -gui -icps jade.imtp.leap.http.HTTPPeer(1099)");
		JadeInterface.runJadeWithArguments("-host localhost -port 1099 -platform-id ClaimPlatform -gui");

	}
}
