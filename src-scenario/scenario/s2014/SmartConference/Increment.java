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
package scenario.s2014.SmartConference;

import java.util.Vector;

import tatami.core.agent.claim.ClaimFunctionLibrary;
import tatami.sclaim.constructs.basic.ClaimValue;


public class Increment implements ClaimFunctionLibrary
{
	public static boolean justPrintArguments(Vector<ClaimValue> arguments)
	{
		System.out.println(" ---- arguments are ----- ");
		for (int i = 0; i < arguments.size(); ++i) {
			System.out.println(arguments.get(i));
		}
		return true;
	}
	
	public static boolean increment(Vector<ClaimValue> arguments)
	{
		arguments.set(1, new ClaimValue(new Integer(Integer.parseInt((String)arguments.get(0).getValue()) + 1).toString()));
		return true;
	}
	
	public static boolean func(Vector<ClaimValue> arguments)
	{
		//arguments.set(0, new ClaimValue(new Boolean(true)));
		return true;
	}

}
