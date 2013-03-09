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
package testing.planning;

import jade.core.AID;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Vector;

import tatami.core.agent.claim.ClaimAgent;
import tatami.core.agent.claim.parser.ClaimConstruct;
import tatami.core.agent.claim.parser.ClaimGoal;
import tatami.core.agent.claim.parser.ClaimValue;
import tatami.core.agent.claim.parser.ClaimaGoal;
import tatami.core.agent.claim.parser.ClaimmGoal;
import tatami.core.agent.claim.parser.ClaimpGoal;
import tatami.core.agent.hierarchical.HierarchyOntology;
import tatami.core.agent.hierarchical.HierarchyOntology.Vocabulary;
import testing.planning.graphplan.*;
import jade.core.behaviours.CyclicBehaviour;
/**
 * 
 * @author Gerard Simons, Alex Garella
 *
 *	This class denotes a GoalAgent, it extends the ClaimAgent and is a proactive goaldriven agent.
 */
import jade.core.behaviours.CyclicBehaviour;

public class GoalAgent extends ClaimAgent
{
	private static final long serialVersionUID = -5601603215229059640L;
	
	private Vector<ClaimConstruct> statements;
	protected EnvironmentAgent environmentAgent;
	private Vector<String> latestEnvironmentState;
	private Vector<ClaimGoal> goalbase;
	private GraphPlan graphplan;
	
	
	/**
	 * Sets up this agent, the statements are given to it through JADE as part of the AgentCreationData. These statements constitute Goal, and are cast to ClaimGoals
	 * the 5th argument constitutes the EnvironmentAgent, which is to be shared among groups of GoalAgents.
	 */
	public void setup()
	{
		super.setup();
		goalbase = new Vector<ClaimGoal>();
		for(ClaimConstruct construct : statements)
		{
			goalbase.add((ClaimGoal) construct);
		}
		Object[] args = getArguments();
		
		if(args.length > 5 && args[5] != null)
		{
			environmentAgent = (EnvironmentAgent) args[5];
		}
		graphplan = new GraphPlan();
		//Register this agent with the environment agent and receive the initial environment state
		environmentAgent.registerGoalAgent(this);
		initGoalStates();
		
		//The GoalAgent has one cyclic behaviour, it is the assesment of goals and the environment, generating plans and executing the actions involved.
		addBehaviour(new CyclicBehaviour() {
			private static final long	serialVersionUID	= 1L;

			@Override
			public void action()
			{
				updateGoalStates();
				execute();
			}
		});
	}
	
	
	public Vector<Vector<Conjunction>> generateGoalSets()
	{
		Vector<Conjunction> activeGoals = new Vector<Conjunction>();
		for(ClaimGoal goal : goalbase)
		{
			if(goal.isActive())
			{
				activeGoals.add(goal.getDropConditionsConjunction());
			}
		}
		return powerSet(activeGoals);
	}
	
	public Vector<Vector<Conjunction>> powerSet(Vector<Conjunction> originalSet) 
	{
	    Vector<Vector<Conjunction>> sets = new Vector<Vector<Conjunction>>();
	    if (originalSet.isEmpty()) 
	    {
	        sets.add(originalSet);
	        return sets;
	    }
	    Vector<Conjunction> list = new Vector<Conjunction>(originalSet);
	    Conjunction head = list.get(0);
	    Vector<Conjunction> rest = new Vector<Conjunction>(list.subList(1, list.size())); 
	    for (Vector<Conjunction> set : powerSet(rest)) 
	    {
	        Vector<Conjunction> newSet = new Vector<Conjunction>();
	        newSet.add(head);
	        newSet.addAll(set);
	        sets.add(newSet);
	        sets.add(set);
	    }           
	    return sets;
	}
	
    /**
     * Generates a plan
     * @param goal the ClaimGoal for which the plan has to be computed 
     * @return the plan, which is a vector of actions
     */
	public Vector<Action> computePlan(ClaimGoal goal)
	{
		Vector<Action> res = null;
		System.out.println("GoalAgent computing plan.");
		if(goalbase != null && environmentAgent != null)
		{
			latestEnvironmentState = environmentAgent.getEnvironmentState();
			
			if(goal instanceof ClaimGoal)
			{
				ClaimGoal claimGoal = (ClaimGoal) goal;
				Vector<String> goalStrings = new Vector<String>();
				for(ClaimConstruct prop : claimGoal.getDropCondition())
				{
					goalStrings.add(((ClaimValue) prop).toString());
				}
				if(claimGoal instanceof ClaimpGoal) //Special treatment required for performGoal as it defines an action instead of a proposition.
				{
					res = graphplan.computePlanForActions(goalStrings, environmentAgent.getEnvironmentState(), environmentAgent.getObjects(), environmentAgent.getOperators());
				}
				else
				{
					res = graphplan.computePlan(goalStrings, environmentAgent.getEnvironmentState(), environmentAgent.getObjects(), environmentAgent.getOperators());
				}
			}
		}
		return res;
	}
	
	/**
     * Checks if the environment data is stale
     * 
     * @return true if the environment is out dated compared to the environment of the environment agent
     */
	public boolean environmentDataIsStale()
	{
		return (environmentAgent.getEnvironmentState().equals(latestEnvironmentState));
	}
	
	public void setGoals(Vector<ClaimConstruct> statements)
	{
		this.statements = statements;
	}
	
	/**
     * Update the environment
     * @param environment the latest states of an environment
     */
	public void updateEnvironment(Vector<String> environment)
	{
		latestEnvironmentState = environment;
		
	}
	
	/**
     * Sets the maintain-goals and the achieve-goal or perform-goal with the highest priority to active
     */
	public void initGoalStates(){
		ClaimGoal maxgoal = null;
		int maxpriority = -1;
		for(ClaimGoal goal : goalbase)
		{
			if(goal instanceof ClaimmGoal && !goal.isActive())
			{
				goal.setGoalState(GoalState.ACTIVE);
			}
			else if((goal instanceof ClaimaGoal || goal instanceof ClaimpGoal) && !goal.isActive())
			{
				if(goal.getPriority() > maxpriority)
				{
					maxgoal = goal;
					maxpriority = goal.getPriority();
				}
			}
		}
		if(maxgoal != null)
			maxgoal.setGoalState(GoalState.ACTIVE);
	}
	
	/**
     * Updates the goalstates according to the environment
     */
	public void updateGoalStates(){
		
		for(ClaimGoal goal : goalbase)
		{
			if(goal instanceof ClaimmGoal && goal.isActive())
			{
				ClaimmGoal mgoal = (ClaimmGoal)goal;
				if(mgoal.getMaintainGoalState() == MaintainGoalState.IDLE)
				{
					if(!latestEnvironmentState.containsAll(mgoal.getStringRepresentationOfMaintainCondition()))
						mgoal.setMaintainGoalState(MaintainGoalState.IN_PROCESS);
				}
				else if(mgoal.getMaintainGoalState() == MaintainGoalState.IN_PROCESS)
				{
					if(latestEnvironmentState.containsAll(mgoal.getStringRepresentationOfDropCondition()))
						mgoal.setMaintainGoalState(MaintainGoalState.IDLE);			
				}
			}
			else if(goal instanceof ClaimaGoal && goal.isActive() && goal.getStringRepresentationOfDropCondition() != null)
			{
				if(latestEnvironmentState.containsAll(goal.getStringRepresentationOfDropCondition()))
				{
					goal.setGoalState(GoalState.SUCCESS);
				}
			}
		}
		Vector<ClaimGoal> removegoals = new Vector<ClaimGoal>();
		for(ClaimGoal goal : goalbase)
		{
			if(goal.getGoalState() == GoalState.SUCCESS || goal.getGoalState() == GoalState.FAIL )
				removegoals.add(goal);
		}
		goalbase.removeAll(removegoals);
		initGoalStates();
	}
	
	/**
     * Selects the goal with the highest priority. Generates and applies a goal if possible.
     */
	public void execute()
	{	
		Vector<ClaimGoal> activegoals = new Vector<ClaimGoal>();
		for(ClaimGoal goal : goalbase){
			if(goal.isActive())
			{
				activegoals.add(goal);
			}
		}
		int maxpriority = -1;
		ClaimGoal maxgoal = null;
		for(ClaimGoal goal : activegoals)
		{
			if(goal.getPriority() > maxpriority)
			{
				maxgoal = goal;
				maxpriority = goal.getPriority();
			}
		}
		Vector<Action> plan = null;
		if(maxgoal != null)
		{
			if(maxgoal instanceof ClaimmGoal)
			{
				ClaimmGoal mgoal = (ClaimmGoal) maxgoal;
				if(mgoal.getMaintainGoalState() == MaintainGoalState.IN_PROCESS)
				{
					plan = computePlan(maxgoal);
					System.out.println("Generated plan: " + plan);
				}
				else
				{
					plan = new Vector<Action>();
				}
			}
			else
			{
				plan = computePlan(maxgoal);
				System.out.println("Generated plan: " + plan);
			}
		}
		else
		{
			plan = new Vector<Action>();
		}
		while(!plan.isEmpty()) 
		{
			Action action = plan.firstElement();
			environmentAgent.applyAction(action);
			plan.remove(action);
			System.out.println("Performing action: " + action);
			System.out.println("Environment: " + latestEnvironmentState);
		}
	}
}
