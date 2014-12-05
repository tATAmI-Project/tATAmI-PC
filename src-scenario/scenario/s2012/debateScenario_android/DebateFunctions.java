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
package scenario.s2012.debateScenario_android;

import java.util.Vector;

import tatami.sclaim.constructs.basic.ClaimValue;


public class DebateFunctions {

	public static boolean increment(Vector<ClaimValue> arguments)
	{
		arguments.set(1, new ClaimValue(new Integer(Integer.parseInt((String)arguments.get(0).getValue()) + 1).toString()));
		return true;
	}
	
	public static boolean assembleOutput(Vector<ClaimValue> arguments)
	{
		String name = (String)arguments.get(0).getValue();
		String id = (String)arguments.get(1).getValue();
		String msg = (String)arguments.get(2).getValue();
		arguments.set(3, new ClaimValue(name + ":" + id + ":" + msg));
		return true;
	}
	
	public static boolean equalString(Vector<ClaimValue> arguments)
	{
		String s1 = (String)arguments.get(0).getValue();
		String s2 = (String)arguments.get(1).getValue();
		return s1.equals(s2);
	}

	public static boolean initOp(Vector<ClaimValue> arguments)
	{
		arguments.set(0, new ClaimValue(new String("")));
		return true;
	}
	
	public static boolean appendOp(Vector<ClaimValue> arguments)
	{
		String allOp = (String)arguments.get(0).getValue();
		String newOp = (String)arguments.get(1).getValue();
		arguments.set(0, new ClaimValue(allOp + "\n" + newOp));
		return true;
	}
	
	public static boolean deleteOpinionFunction(Vector<ClaimValue> arguments)
	{
		String allOp = (String)arguments.get(0).getValue();
		String id = (String)arguments.get(1).getValue();
		
		boolean ok = false;
		  String[] temp;
		  String newAllOp = "";
		  String delimiter = "\n";
		  temp = allOp.split(delimiter);
		  
		  for(int i =0; i < temp.length ; i++)
			  if(temp[i].indexOf(":") != -1)
			  {
				  int i1 = temp[i].indexOf(":");
				  int i2 = temp[i].indexOf(":", i1+1);
				  String id2 = temp[i].substring(i1+1,i2);
				  if(ok || (!id.equals(id2))){
					  newAllOp += temp[i]+"\n";
				  }
				  else {
					  ok = true;
				  }
			  }
		  
		arguments.set(0, new ClaimValue(newAllOp));
		return true;
	}
}
