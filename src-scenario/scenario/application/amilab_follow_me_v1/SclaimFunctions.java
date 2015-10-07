/*******************************************************************************
 * Copyright (C) 2015 Andrei Olaru, Marius-Tudor Benea, Nguyen Thi Thuy Nga, Amal El Fallah Seghrouchni, Cedric Herpson.
 * 
 * This file is part of tATAmI-PC.
 * 
 * tATAmI-PC is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * tATAmI-PC is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with tATAmI-PC.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package scenario.application.amilab_follow_me_v1;

import java.util.Vector;

import tatami.core.agent.claim.ClaimFunctionLibrary;
import tatami.sclaim.constructs.basic.ClaimValue;

/**
 * Functions used by the S-CLAIM components.
 * 
 * @author Claudiu-Mihai Toma
 *
 */
public class SclaimFunctions implements ClaimFunctionLibrary
{

	/**
	 * Determines the closest client and puts the result over the second client.
	 * 
	 * @param arguments
	 *            - list of arguments given in this order: client1, proximity1, client2, proximity2
	 * @return usually {@code true}
	 */
	public static boolean bestClient(Vector<ClaimValue> arguments)
	{
		// Extract proximities.
		long proximity = Long.parseLong((String) arguments.get(1).getValue());
		long smallestProximity = Long.parseLong((String) arguments.get(3).getValue());

		// Check if the smallest proximity is still the smallest.
		if (proximity < smallestProximity)
		{
			// Extract the name of the new best client.
			String client = (String) arguments.get(0).getValue();

			// Update the values for the best client.
			arguments.set(2, new ClaimValue(client));
			arguments.set(3, new ClaimValue(new Long(proximity).toString()));
		}

		return true;
	}
}
