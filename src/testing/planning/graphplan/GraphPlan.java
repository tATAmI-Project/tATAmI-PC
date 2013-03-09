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
//----------------------------------------------------------------------------
// Copyright (C) 2004  Yasser EL-Manzalawy.
// 
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//
// Contact the Author:
// Yasser EL-Manzalawy
// e-mail: ymelmanz@yahoo.com
//----------------------------------------------------------------------------

/*
 * Project: GraphPlan 1.0
 * Class:   graphplan.GraphPlan
 *
 * Date:    2004-03-31
 *
 * Author:  Yasser EL-Manzalawy
 * Email:   ymelmanz@yahoo.com
 */

package testing.planning.graphplan;

import java.util.*;
import java.io.*;
import testing.planning.graphplan.parser.*;

/**
 * Class GraphPlan.
 *  
 * @author  Yasser EL-Manzalawy, modified by Gerard Simons, Alex Garella to fit needs of CLAIM
 */
public class GraphPlan {

    // ------------------------------------------------------------------------
    // --- fields                                                           ---
    // ------------------------------------------------------------------------
    /**
     * The operators.
     */
    private TOperatorSet operators;

    /**
     * The objects.
     */
    private ObjectSet objects;

    /**
     * The initials.
     */
    private Conjunction initials;

    /**
     * The goal.
     */
    private Conjunction goals;
    
    /**
     * Actions which need to be found for perform goals.
     */
    private Conjunction goalActions;

    /**
     * The first prop.
     */
    private PropositionLayer firstProp=null;

    /**
     * The last prop.
     */
    private PropositionLayer lastProp=null;

    /**
     * The valid plan.
     */
    private boolean validPlan=false;

    /**
     * The levels.
     */
    private int levels=0;


    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------
    /**
     * Creates a new instance of GraphPlan.
     */
    public GraphPlan() {
        operators = new TOperatorSet();
        objects = new ObjectSet();
        initials = new Conjunction();
        goals = new Conjunction();
    }
    
    /**
     * Sets up the graphplan according to given parameters and then computes plan.
     * @param actionGoalStrings description of the goals (actions)
     * @param environmentState the initial state of the environment (string description)
     * @param objects the objects in the environment
     * @param operators, description of the kind of operators that can be used, important for creating actions from objects.
     * @return the plan.
     */
    public Vector<Action> computePlan(Vector<String> goalStrings, Vector<String> environmentState, ObjectSet objects, TOperatorSet operators)
    {
    	goals = new Conjunction(goalStrings);
    	initials = new Conjunction(environmentState);
    	this.objects = objects;
    	this.operators = operators;
    	operators.initUnifiers (objects);
    	boolean done = CreateGraph();
        if (done)
        System.out.println ("\nGraph Created Successfully");
        else
        System.out.println ("\nGraph Creation Failed");
        // search for a valid plan
        if (done)
        {
            if (lastProp.getPrevLayer().searchPlan(goals))	// plan is found for goal
            {
                validPlan = true;
            }
            else
            System.out.println ("Error in searching for the plan");
        }
        Vector plan = getActionPlan();
        return plan;
    }
    
    /**
     * Sets up the graphplan according to given parameters and then computes plan.
     * @param actionGoalStrings description of the goals (actions)
     * @param environmentState the initial state of the environment (string description)
     * @param objects the objects in the environment
     * @param operators, description of the kind of operators that can be used, important for creating actions from objects.
     * @return the plan.
     */
    public Vector<Action> computePlanForActions(Vector<String> actionGoalStrings, Vector<String> environmentState, ObjectSet objects, TOperatorSet operators)
    {
    	System.out.println("Computing plan for actions.");
    	goalActions = new Conjunction(actionGoalStrings);
    	goals = new Conjunction();
    	initials = new Conjunction(environmentState);
    	this.objects = objects;
    	this.operators = operators;
    	operators.initUnifiers (objects);
        // search for a valid plan
        if (CreateGraphTillActionsFound())
        {
        	goals = lastProp.getPrevLayer().getPreConditionsOf((actionGoalStrings));
        	System.out.println ("\nGraphPlan Created Successfully");
        	if(lastProp.getPrevLayer().getPrevLayer().getPrevLayer() != null)
        	{		
        		if (lastProp.getPrevLayer().getPrevLayer().getPrevLayer().searchPlan(goals))	// plan is found for goal
        		{
        			validPlan = true;
        		}
        		else
        		System.out.println ("Error in searching for the plan");
        	}
        }
        else System.out.println("GraphPlan Creation Failed.");
        // writing the plan
        //Vector plan = getPlan();
        //System.out.println(plan);
        Vector plan = getActionPlan();
        Vector<Action> originalActions = lastProp.getPrevLayer().getActions(actionGoalStrings);
        plan.addAll(originalActions);
        return plan;
    }
    

    /**
     *     
     * Creates the graph by incrementally adding an action layer generated from the last proposition layer, the proposition layer after that is created from this action layer.
     * It stops when the graph starts to level off, see levelOff() or when the goals can be found or the max levels is reached.
     *  
     * @return true when succes, false otherwise.
     */
    public boolean CreateGraph() {
        firstProp = new PropositionLayer();
        firstProp.setInitLayer (initials);
        lastProp = firstProp;
        levels = 0;
        for (int i = 0; i < G.MaxLevel; i++)
        {
            levels++;
            // create Ai and Pi+1
            System.out.println(lastProp.getConjunction());
            Vector apps = operators.generateActions (lastProp.getConjunction());
            if (apps.size() == 0)
            {
                // TODO: throw an exception
                System.out.println ("No applicable actions at level: " + levels);
                return false;
            }
            ActionLayer aLayer = new ActionLayer (lastProp, apps );
            lastProp.setNextLayer (aLayer);
            // point to Pi+1
            lastProp = aLayer.getNextLayer();
            // test that all goals are reachable
            if (ReachableGoals())
            return true;
            // ToDo: test that graph levels off (Done)
            if (levelOff())
            {
                System.out.println ("Graph Levels Off at level" + levels);
                return false;
            }
        } // end for
        System.out.println ("Graph Creation Error: Max Level reached" + G.MaxLevel);
        return false;
    }
    
    /**
     * Creates the graph by incrementally adding an action layer generated from the last proposition layer, the proposition layer after that is created from this action layer.
     * It stops when the graph starts to level off, see levelOff() or when the goals can be found or the max levels is reached.
     * 
     * Different from CreateGraph() in that it looks for actions as opposed to looking for propositions! Small but essential difference, this one is used for performgoals.
     *  
     * @return  true when creation was a succes, false otherwise.
     */
    public boolean CreateGraphTillActionsFound() {
        firstProp = new PropositionLayer();
        firstProp.setInitLayer (initials);
        lastProp = firstProp;
        levels = 0;
        for (int i = 0; i < G.MaxLevel; i++)
        {

            levels++;
            // create Ai and Pi+1
            Vector apps = operators.generateActions (lastProp.getConjunction());
            if (apps.size() == 0)
            {
                // TODO: throw an exception
                System.out.println ("No applicable actions at level: " + levels);
                return false;
            }
            ActionLayer aLayer = new ActionLayer (lastProp, apps );
            lastProp.setNextLayer (aLayer);
            lastProp = aLayer.getNextLayer();
        	if (ReachableActions(aLayer))
            	return true;
            
            // point to Pi+1
            
            // test that all goals are reachable
            if (levelOff())
            {
                return false;
            }
        } // end for
        System.out.println ("Graph Creation Error: Max Level reached" + G.MaxLevel);
        return false;
    }

    /**
     * String representation of the GraphPlan object.
     *  
     * @return  The string representation
     */
    public String toString() {
        PropositionLayer pPointer = firstProp;
        ActionLayer aPointer = null;
        String s = new String();
        while (pPointer != null)
        {
            s += pPointer.toString();
            aPointer = pPointer.getNextLayer();
            if (aPointer != null)
            {
                s += aPointer.toString();
                pPointer = aPointer.getNextLayer();
            }
            else
            pPointer = null;
        }
        return s;
    }


    /**
     * Creates the plan from all the layers' applicable actions. 
     * @return
     */
    public Vector getActionPlan() {
        Vector plan = new Vector();
        if (validPlan)
        {
            ActionLayer act = firstProp.getNextLayer();
            while (act != null)
            {
                plan.addAll (act.getApplicableActionActions());
                act = act.getNextLayer().getNextLayer();
            }
        }
        return plan;
    }

    /**
     * Checks whether the normal goals can be reached. 
     *  
     * @return  The boolean.
     */
    private boolean ReachableGoals() 
    {
        int len = goals.size();
        // all goals are reachbale
        for (int i = 0; i < len; i++)
        {
            if (lastProp.getConjunction().contains (goals.getLiteral(i))== false)
            return false;
        }
        // no mutex relation
        for (int i = 0; i < len; i++)
        {
            Proposition p1 = lastProp.getProposition (goals.getLiteral(i));
            for (int j = i + 1; j < len; j++)
            {
                Proposition p2 = lastProp.getProposition (goals.getLiteral(j));
                if (p1.isMutex(p2))
                return false;
            }
        }
        return true;
    }
    
    /**
     * Checks whether the given ActionLayer contains the actions it wants to do (goalActions) and the goalactions are not in mutex with eachother
     * @param aLayer, the action layer to be checked
     * @return true if the actions are all reachable and not in mutex
     */
    private boolean ReachableActions(ActionLayer aLayer)
    {
    	for(String action : goalActions.getLiterals())
    	{
    		if(!aLayer.contains(action)) return false;
    	}
    	for(int i = 0 ; i < goalActions.size() ; i++)
    	{
    		Action a1 = aLayer.getAction(goalActions.getLiteral(i));
    		for(int j = i + 1 ; j < goalActions.size() ; j++)
    		{
    			Action a2 = aLayer.getAction(goalActions.getLiteral(j));
    			if(a1.isMutex(a2)) return false;
    		}
    	}
    	return true;
    }

    /**
     * returns true when the last 2 proposition/action layers are identical
     *  
     * @return  The boolean.
     */
    private boolean levelOff() {
        if (levels < 2)
        return false;
        PropositionLayer p = lastProp;
        ActionLayer act = lastProp.getPrevLayer();
        if ( p.equal (act.getPrevLayer()) == false)
        return false;
        else
        {
            p = act.getPrevLayer();
            if (act.equal (p.getPrevLayer()) == false)
            return false;
        }
        return true;
    }

    /**
     * Prints the plan, consisting of each consecutive action layer.
     */
    private void printPlan() {
        ActionLayer act = firstProp.getNextLayer();
        System.out.println ("Printing the Plan");
        while (act != null)
        {
            act.printApplicable();
            act = act.getNextLayer().getNextLayer();
        }
    }
} // end GraphPlan
