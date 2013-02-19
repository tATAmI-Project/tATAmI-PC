package testing.planning;

import java.util.Collection;
import java.util.Vector;

import tatami.core.agent.claim.parser.ClaimConstruct;
import tatami.core.agent.claim.parser.ClaimConstructType;
import tatami.core.agent.claim.parser.ClaimStructure;
import tatami.core.agent.claim.parser.ClaimValue;
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