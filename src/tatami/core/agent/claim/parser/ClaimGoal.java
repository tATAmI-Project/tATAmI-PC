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

import java.util.Collection;
import java.util.Vector;

import testing.planning.GoalState;
import testing.planning.graphplan.Conjunction;


public class ClaimGoal extends ClaimConstruct {

	protected GoalState goalstate = GoalState.OPTION;
	
	protected Vector<ClaimConstruct> dropConditions;
	
	protected int priority;
	
	protected ClaimGoal(ClaimConstructType type, Vector<ClaimConstruct> dropCondition) 
	{
		super(type);
		this.dropConditions = dropCondition;
	}
	
	protected ClaimGoal(ClaimConstructType type)
	{
		super(type);
		dropConditions = new Vector<ClaimConstruct>();
	}

	public ClaimGoal(ClaimConstructType type, Vector<ClaimConstruct> targetCondition, Vector<ClaimConstruct> maintainCondition)
	{
		super(type);
		if(targetCondition != null) dropConditions = targetCondition;
		else dropConditions = maintainCondition;
	}

	public ClaimGoal(ClaimConstructType type, ClaimConstruct action)
	{
		super(type);
		dropConditions = new Vector<ClaimConstruct>();
		dropConditions.add(action);
	}

	public boolean isActive()
	{
		return goalstate == GoalState.ACTIVE;
	}

	public Vector<ClaimConstruct> getDropCondition()
	{
		return dropConditions;
	}
	
	
	public Vector<String> getStringRepresentationOfDropCondition()
	{
		Vector<String> dropConditionStrings = new Vector<String>();
		for(ClaimConstruct condition : dropConditions)
		{
			if(condition.getType()==ClaimConstructType.VALUE)
				dropConditionStrings.add(((ClaimValue) condition).toString());
		}
		return dropConditionStrings;
	}

	public Conjunction getDropConditionsConjunction()
	{
		Conjunction dropConditionsConj = new Conjunction();
		for(ClaimConstruct condition : dropConditions)
		{
			if(condition != null)
			{
				if(condition.getType()==ClaimConstructType.VALUE)
					dropConditionsConj.addLiteral(((ClaimValue) condition).toString());
			}
		}
		return dropConditionsConj;
	}
	
	public GoalState getGoalState()
	{
		return goalstate;
	}
	
	public void setGoalState(GoalState state)
	{
		goalstate = state;
	}

	public int getPriority() {
		return priority;
	}
	
	public void setPriority(int p)
	{
		priority = p;
	}
}
