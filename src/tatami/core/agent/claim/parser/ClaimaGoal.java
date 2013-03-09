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
package tatami.core.agent.claim.parser;

import java.util.Random;
import java.util.Vector;

import testing.planning.MaintainGoalState;

public class ClaimaGoal extends ClaimGoal
{
	/**
	 * The behavior to which this construct belongs.
	 */
	private ClaimBehaviorDefinition myBehavior;
	private String name;
	
	public ClaimaGoal(ClaimConstructType type, String name, Vector<ClaimConstruct> arguments, int priority)
	{
		super(type, arguments);
		this.name = name;
		//this.priority = priority;
		Random random = new Random();
		this.priority = random.nextInt(10);
		System.out.println(this);
	}
	
	public void setMyBehavior(ClaimBehaviorDefinition myBehavior) {
		this.myBehavior = myBehavior;
	}

	public ClaimBehaviorDefinition getMyBehavior() {
		return myBehavior;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String n)
	{
		name = n;
	}
	
	public String toString()
	{
		return "ClaimaGoal object \nname: " + name + "\narguments: " + dropConditions + "\npriority: " + priority;

	}
}
