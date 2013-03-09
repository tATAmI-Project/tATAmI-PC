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

public class ClaimmGoal extends ClaimGoal
{
	private String name;
	private Vector<ClaimConstruct> maintainstate;
	private MaintainGoalState maintaingoalstate = MaintainGoalState.IDLE;
	
	public ClaimmGoal(ClaimConstructType mgoal, String name, Vector<ClaimConstruct> maintainCondition, Vector<ClaimConstruct> targetCondition, int priority)
	{
		super(ClaimConstructType.MGOAL, targetCondition, maintainCondition);
		this.name = name;
		this.maintainstate = maintainCondition;
		//this.priority = priority;
		Random random = new Random();
		this.priority = random.nextInt(10);
		//System.out.println(this);
	}
	
	public String toString()
	{
		return "ClaimmGoal object \nname: " + name + "\nfirst arguments: " + maintainstate + "\nsecond arguments: " + dropConditions + "\npriority: " + priority;
	}
	
	public MaintainGoalState getMaintainGoalState()
	{
		return maintaingoalstate;
	}
	
	public void setMaintainGoalState(MaintainGoalState state)
	{
		maintaingoalstate = state;
	}
	
	public Vector<String> getStringRepresentationOfMaintainCondition()
	{
		Vector<String> maintainConditionStrings = new Vector<String>();
		for(ClaimConstruct condition : maintainstate)
		{
			maintainConditionStrings.add(((ClaimValue) condition).toString());
		}
		return maintainConditionStrings;
	}
}
