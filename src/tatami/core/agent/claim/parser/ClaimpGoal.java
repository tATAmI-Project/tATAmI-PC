/*******************************************************************************
 * Copyright (C) 2013 Andrei Olaru, Marius-Tudor Benea, Nguyen Thi Thuy Nga, Amal El Fallah Seghrouchni, Cedric Herpson.
 * 
 * This file is part of tATAmI-PC.
 * 
 * tATAmI-PC is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * tATAmI-PC is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with tATAmI-PC.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package tatami.core.agent.claim.parser;

import java.util.Vector;


public class ClaimpGoal extends ClaimGoal
{
	private String name;
	private ClaimConstruct action;
	
	public ClaimpGoal(ClaimConstructType pgoal, String name, ClaimConstruct action, int priority)
	{
		super(ClaimConstructType.PGOAL, action);
		this.name = name;
		this.action = action;
		this.priority = priority;
		System.out.println(this);
	}
	
	public String toString()
	{
		return "ClaimpGoal object \nname: " + name + "\naction: " + action + "\npriority: " + priority;
	}
}
