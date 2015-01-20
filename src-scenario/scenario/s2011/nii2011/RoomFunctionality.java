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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import tatami.core.agent.claim.ClaimFunctionLibrary;
import tatami.core.agent.claim.parser.ClaimValue;


public class RoomFunctionality implements ClaimFunctionLibrary
{
	public static boolean makeXACRequest(Vector<ClaimValue> arguments)
	{
		String request = null;
		String phase = (String)arguments.get(1).getValue();
		if(phase.equals("init"))
		{
			request = "POST /WSNManager/UserPresenceTask HTTP/1.1\nHost: wsnManagerHost\nContent-type: application/xml\n";
			request += "<serviceHost>serviceHost</serviceHost>";
		}
		else if(phase.equals("check"))
		{
			request = "GET /LectureService/UsersInTheRoom HTTP/1.1\nHost: wsnManagerHost\nContent-type: application/xml\n";
		}
		else
		{
			request = "GET /LectureService/UserLocation HTTP/1.1\nHost: wsnManagerHost\nContent-type: application/xml\n";
		}
		arguments.set(0, new ClaimValue(request));
		return true;
	}
	
	public static boolean makeRoomRequest(Vector<ClaimValue> arguments)
	{
		String request = null;
		String config = (String)arguments.get(1).getValue();
		request = "PUT /RoomManager/RoomConfiguration HTTP/1.1\nHost: wsnManagerHost\nContent-type: application/xml\n";
		request += "<configurationId>" + config + "</configurationId>";
		arguments.set(0, new ClaimValue(request));
		return true;
	}
	
	public static boolean processUserList(Vector<ClaimValue> arguments)
	{
		Set<String> list = new HashSet<String>();
		
		String text = (String)arguments.get(0).getValue();
		// FIXME should be done with the XML parser
		StringTokenizer tokker = new StringTokenizer(text, " \n\t<>/");
		while(tokker.hasMoreElements())
		{
			String tok = tokker.nextToken();
			if(!tok.equals("users") && !tok.equals("user"))
				list.add(tok);
		}
		arguments.set(1, new ClaimValue(list));
		return true;
	}
	
	public static boolean processStudentsInLocations(Vector<ClaimValue> arguments)
	{
		Map<String, String> rez = new HashMap<String, String>();
		
		String text = (String)arguments.get(0).getValue();
		// FIXME should be done with the XML parser
		StringTokenizer tokker = new StringTokenizer(text, " \n\t<>/");
		String user = null;
		while(tokker.hasMoreElements())
		{
			String tok = tokker.nextToken();
			if(!tok.equals("userlocations") && !tok.equals("userlocation") && !tok.equals("location") && !tok.equals("user"))
				if(user != null)
				{
					rez.put(user, tok);
					user = null;
				}
				else
					user = tok;
		}
		arguments.set(1, new ClaimValue(rez));
		return true;
	}
	
	public static boolean count(Vector<ClaimValue> arguments)
	{
		arguments.set(1, new ClaimValue(new Integer(((Set<?>)arguments.get(0).getValue()).size())));
		return true;
	}
	
	public static boolean gteq(Vector<ClaimValue> arguments)
	{
		Vector<Integer> args = new Vector<Integer>();
		for(ClaimValue arg : arguments)
			if(arg.getValue() instanceof Integer)
				args.add((Integer)arg.getValue());
			else
				args.add(new Integer(Integer.parseInt((String)arg.getValue())));
		return args.get(0).intValue() >= args.get(1).intValue();
	}
	
	public static boolean mainScreen(Vector<ClaimValue> arguments)
	{
		if(((String)arguments.get(0).getValue()).equals("true"))
			return true;
		else
			return false;
	}
}
