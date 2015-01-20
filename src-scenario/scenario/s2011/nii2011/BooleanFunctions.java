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
package s2011.nii2011;

import java.util.Vector;

import tatami.core.agent.claim.ClaimFunctionLibrary;
import tatami.core.agent.claim.parser.ClaimValue;


public class BooleanFunctions implements ClaimFunctionLibrary
{
	
	public static boolean not(Vector<ClaimValue> arguments)
	{
		if(((String)arguments.get(0).getValue()).equals("true"))
			return false;
		else
			return true;
	}
	
	public static boolean isDifferent(Vector<ClaimValue> arguments)
	{
		String s1 = (String)arguments.get(0).getValue();
		String s2 = (String)arguments.get(1).getValue();
		return !s1.equals(s2);
	}
	
}
