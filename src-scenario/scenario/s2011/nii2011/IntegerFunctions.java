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

import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import tatami.core.agent.claim.ClaimFunctionLibrary;
import tatami.core.agent.claim.parser.ClaimValue;


public class IntegerFunctions implements ClaimFunctionLibrary
{
	public static boolean plus(Vector<ClaimValue> arguments)
	{
		arguments.set(2, new ClaimValue(new Integer(Integer.parseInt((String)arguments.get(0).getValue()) + Integer.parseInt((String)arguments.get(1).getValue())).toString()));
		return true;
	}
	
	public static boolean percentOf(Vector<ClaimValue> arguments)
	{
		arguments.set(2, new ClaimValue(new String(Math.round(Integer.parseInt((String)arguments.get(0).getValue()) / 100.0 * Integer.parseInt((String)arguments.get(1).getValue())) + "")));
		return true;
	}
	
	public static boolean count(Vector<ClaimValue> arguments)
	{
		arguments.set(1, new ClaimValue(new Integer(((Set<?>)arguments.get(0).getValue()).size())));
		return true;
	}
	
	public static boolean gteq(Vector<ClaimValue> arguments)
	{
		Vector<Float> args = new Vector<Float>();
		for(ClaimValue arg : arguments)
			if(arg.getValue() instanceof Float)
				args.add((Float)arg.getValue());
			else if(arg.getValue() instanceof Integer)
				args.add(new Float(((Integer)arg.getValue()).intValue()));
			else
				args.add(new Float(Float.parseFloat((String)arg.getValue())));
		return args.get(0).floatValue() >= args.get(1).floatValue();
	}
	
	public static boolean random(Vector<ClaimValue> arguments)
	{
		arguments.set(0, new ClaimValue(new Float(new Random().nextFloat()).toString()));
		
		return true;
	}
	
	public static boolean determineLocation(Vector<ClaimValue> arguments)
	{
		String user = (String)arguments.get(0).getValue();
		@SuppressWarnings("unchecked")
		Map<String, String> info = (Map<String, String>)arguments.get(1).getValue();
		
		if(info.get(user).equals("screen1"))
			arguments.set(2, new ClaimValue("RightSideScreenAgent"));
		if(info.get(user).equals("screen2"))
			arguments.set(2, new ClaimValue("LeftSideScreenAgent"));
		
		return true;
	}
}
