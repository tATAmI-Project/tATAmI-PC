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
 * Class:   graphplan.ActionLayer
 *
 * Date:    2004-03-31
 *
 * Author:  Yasser EL-Manzalawy
 * Email:   ymelmanz@yahoo.com
 */

package testing.planning.graphplan;

import java.io.*;
import java.util.*;

/**
 * Implements an action layer in the planning graph
 *  
 * @author  Yasser EL-Manzalawy
 */
public class ActionLayer {

    // ------------------------------------------------------------------------
    // --- fields                                                           ---
    // ------------------------------------------------------------------------
    /**
     * The pos.
     */
    int pos;

    /**
     * previous and next proposition layers
     */
    private PropositionLayer prev=null;

    /**
     * The next.
     */
    private PropositionLayer next=null;

    /**
     * hashtable for all propositions in that layer
     */
    private Hashtable<String,Action> actions;

    /**
     * The actions_vector.
     */
    private Vector<Action> actions_vector;

    /**
     * The conjunction.
     */
    private Conjunction conjunction;

    /**
     * The applicable.
     */
    private Vector applicable=null;

    /**
     * The my goals.
     */
    private Vector myGoals=null;


    // ------------------------------------------------------------------------
    // --- constructors                                                     ---
    // ------------------------------------------------------------------------
    /**
     * Creates a new instance of ActionLayer.
     */
    public ActionLayer() {
        actions = new Hashtable<String,Action>();
        conjunction = new Conjunction();
    }

    /**
     * Creates a new instance of ActionLayer.
     *  
     * @param thePrev The the prev.
     * @param appActions The app actions.
     */
    public ActionLayer(PropositionLayer thePrev, Vector appActions) {
        prev = thePrev;
        next = new PropositionLayer (this);
        // then init actions and conjunction
        actions = new Hashtable<String,Action>();
        conjunction = new Conjunction();
        int len = appActions.size();
        for (int i = 0; i < len; i++)
        {
            Action act = (Action) appActions.elementAt(i);
            actions.put (act.getHeader(), act);
            conjunction.addLiteral (act.getHeader());
            // establish precondition edges
            ConnectPreEdges (act);
            //create and establish add / del edges
            ConnectADEdges (act);
        }
        // add no-ops
        addNoops();
        // calculate mutex
        calculateMutex();
        // in this action layer
        next.calculateMutex();
    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------
    /**
     * Returns the conjunction.
     *  
     * @return  The conjunction.
     */
    public Conjunction getConjunction() {
        return conjunction;
    }

    /**
     * ...
     *  
     * @return  The int.
     */
    public int size() {
        return actions.size();
    }

    /**
     * ...
     *  
     * @param theLayer The the layer.
     * @return  The boolean.
     */
    public boolean equal(ActionLayer theLayer) {
        return conjunction.equal (theLayer.getConjunction());
    }

    /**
     * ...
     */
    public void calculateMutex() {
        createActionVector();
        int len = actions.size();
        for (int act = 0; act < len; act++)		// for each action
        {
            Action op1 = getAction (act);
            for (int act2 = act + 1; act2 < len; act2++) 	// for each 2nd action
            {
                Action op2 = getAction (act2);
                if ( checkEffects (op1, op2))
                continue;
                else if ( checkPre (op1, op2))
                continue;
                else if ( checkPreMutex (op1, op2))
                continue;
            }
        }
    }

    /**
     * ...
     *  
     * @return  The string.
     */
    public String toString() {
        String s = new String ();
        s += "\n Action Layer";
       
        for (int i = 0; i < actions_vector.size(); i++)
        s += "\n" + getAction(i).toString();

        return s;
    }

    /**
     * Returns the prev layer.
     *  
     * @return  The prev layer.
     */
    public PropositionLayer getPrevLayer() {
        return prev;
    }

    /**
     * Returns the next layer.
     *  
     * @return  The next layer.
     */
    public PropositionLayer getNextLayer() {
        return next;
    }

    /**
     * Returns the action.
     *  
     * @param key The key.
     * @return  The action.
     */
    public Action getAction(String key) {
        return actions.get (key);
    }

    /**
     * Returns the action.
     *  
     * @param index The index.
     * @return  The action.
     */
    public Action getAction(int index) {
        return actions_vector.elementAt (index);
    }

    /**
     * ...
     *  
     * @param theGoals The the goals.
     * @return  The boolean.
     */
    public boolean searchTheLevel(Conjunction theGoals) {
        // TODO: test for memoized goals. if found return -1
        if (myGoals == null)
        {
            applicable = new Vector();
            myGoals = new Vector();
            pos = 0;
            int len = theGoals.size();
            for (int i = 0; i < len; i++)
            myGoals.addElement( new Goal (theGoals.getLiteral(i), next) );
        }
        int len = myGoals.size();
        for (int i = pos; i < len; i++)		// for each remaining goal
        {
            Goal g = (Goal) myGoals.elementAt (i);
            boolean achieved = false;
            g.init();
            for (int j = 0; j < g.length(); j++)	// for each action supports that goal
            {
                Action op = g.nextAction();
                if ( addApplicableAction(op))
                {
                    achieved = true;
                    pos++;
                    if (searchTheLevel (theGoals))
                    return true;
                    else 		// try another action supports that goal
                    {
                        pos--;
                        achieved = false;
                        removeApplicableAction();
                    }
                } // end if
            }	// end j loop
            return false;
        }	// end i loop
        return true;
    }

    /**
     * ...
     */
    public void printApplicable() {
        if (applicable == null)
        {
            System.out.println("No applicables");
            return;
        }
        int len = applicable.size();
        System.out.println ("APPLICABLE ACTIONS ARE:" + len);
        for(int i =0; i < len; i++)
        {
            Action op = (Action) applicable.elementAt(i);
            System.out.println (op.getHeader());
        }
    }

    /**
     * returns a vector of applicable actions at this layer
     * and remove any duplicated actions
     * before calling it you must be sure you get a valid plan
     *  
     * @return  The applicable actions.
     */
    public Vector getApplicableActions() {
        Vector rVal = new Vector();
        int len = applicable.size();
        for(int i =0; i < len; i++)
        {
            Action op = (Action) applicable.elementAt(i);
            String item = op.getHeader();
            if (item.indexOf("noop") < 0)
            rVal.addElement (item);
        }
        // now remove any dupliacted entries
        for (int i = 0; i < rVal.size() ; i++)
        {
            String s1 = (String) rVal.elementAt(i);
            for (int j = i + 1; j < rVal.size(); j++)
            {
                String s2 = (String) rVal.elementAt(j);
                if (s1.equals(s2))
                rVal.removeElementAt(j);
            }
        }
        return rVal;
    }
    
    public Vector<Action> getApplicableActionActions() {
        Vector<Action> rVal = new Vector();
        int len = applicable.size();
        for(int i =0; i < len; i++)
        {
            Action op = (Action) applicable.elementAt(i);
            String item = op.getHeader();
            if (item.indexOf("noop") < 0)
            rVal.addElement (op);
        }
        // now remove any dupliacted entries
        for (int i = 0; i < rVal.size() ; i++)
        {
            Action s1 = rVal.elementAt(i);
            for (int j = i + 1; j < rVal.size(); j++)
            {
                Action s2 = (Action) rVal.elementAt(j);
                if (s1.getHeader().equals(s2.getHeader()))
                rVal.removeElementAt(j);
            }
        }
        return rVal;
    }

    /**
     * ...
     *  
     * @param theGoals The the goals.
     * @return  The boolean.
     */
    public boolean searchPlan(Conjunction theGoals) {
        // if goals are memoized return no plan, null.
        if (this.searchTheLevel (theGoals)) // search this level
        {
            // determine new goal set
            Conjunction newGoals = new Conjunction();
            int len = applicable.size();
            for (int i = 0; i < len; i++)
            {
                Action op = (Action) applicable.elementAt(i);
                newGoals.addConjunction (op.getPreConditions());
                // remove duplicated goals
            }
            // search a plan for the new goal in next app layer
            ActionLayer app = prev.getPrevLayer();
            if (app == null)	// no more layers we reached the init state
            return true;
            if ( app.searchPlan (newGoals) )
            return true;
            // plan is found
            else
            return false;
        }
        else
        {
            // memoize this set of goals
            return false;
            // no plan
        }
    }

    /**
     * Returns the actions.
     *  
     * @return  The actions.
     */
    Hashtable getActions() {
        return actions;
    }

    /**
     * connect the operator theAct to it's precondition propositions
     * in the prev layer.
     *  
     * @param theAct The the act.
     */
    private void ConnectPreEdges(Action theAct) {
        Conjunction pre = theAct.getPreConditions();
        Proposition temp = null;
        int len = pre.size();
        for (int i = 0; i < len; i++)
        {
            String s = pre.getLiteral(i);
            temp = prev.getProposition (s);
            temp.putPreEdge (theAct);
            theAct.addPreProp (temp);
        }
    }

    /**
     * connect the operator theAct to it's add/del propositions
     * in the next layer.
     *  
     * @param theAct The the act.
     */
    private void ConnectADEdges(Action theAct) {
        Conjunction add = theAct.getAddEffects();
        Proposition temp = null;
        int len1 = add.size();
        // add edges
        for (int i = 0; i < len1; i++)
        {
            temp = next.addProposition ( add.getLiteral(i) );
            temp.putAddEdge (theAct, 0);
            theAct.addAddProp (temp);
        }
        // del edges
        Conjunction del = theAct.getDelEffects();
        int len2 = del.size();
        for (int j = 0; j < len2; j++)
        {
            temp = next.addProposition ( del.getLiteral(j) );
            temp.putDelEdge (theAct);
            theAct.addDelProp (temp);
        }
    }

    /**
     * add NOOPs to this layer
     */
    private void addNoops() {
        Conjunction cnj = prev.getConjunction();
        int len = cnj.size();
        for (int i = 0; i < len; i++)
        {
            // create noop object
            String s = new String ("noop"+ cnj.getLiteral(i));
            Action temp = new Action (s);
            temp.setPreConditions (new Conjunction (cnj.getLiteral(i)));
            temp.setAddEffects (new Conjunction (cnj.getLiteral(i)));
            // add noop to this action layer
            actions.put (temp.getHeader(), temp);
            conjunction.addLiteral (temp.getHeader());
            // connect precondition edge
            Proposition p = prev.getProposition (cnj.getLiteral(i));
            p.putPreEdge (temp);
            temp.addPreProp (p);
            p = next.addProposition (cnj.getLiteral(i));
            p.putAddEdge(temp, 1);
            temp.addAddProp (p);
        }
    }

    /**
     * this action vector is needed to retrieve actions based on an index value
     */
    private void createActionVector() {
        actions_vector = new Vector();
        for (Enumeration e = actions.elements(); e.hasMoreElements(); )
        actions_vector.addElement ((Action) e.nextElement());
    }

    /**
     * first effect negates second effect or vise versa
     *  
     * @param op1 The op1.
     * @param op2 The op2.
     * @return  The boolean.
     */
    private boolean checkEffects(Action op1, Action op2) {
        Conjunction c1 = op1.getAddEffects();
        Conjunction c2 = op2.getDelEffects();
        if (c1.size() > 0 && c2.size() > 0)
        if (c1.Intersect (c2))
        {
            op1.addMutexOp (op2);
            return true;
        }
        c1 = op1.getDelEffects();
        c2 = op2.getAddEffects();
        if (c1.size() > 0 && c2.size() > 0)
        if (c1.Intersect (c2))
        {
            op1.addMutexOp (op2);
            return true;
        }
        return false;
    }

    /**
     * first op negates precondition in the second op or vise versa
     *  
     * @param op1 The op1.
     * @param op2 The op2.
     * @return  The boolean.
     */
    private boolean checkPre(Action op1, Action op2) {
        Conjunction c1 = op1.getDelEffects();
        Conjunction c2 = op2.getPreConditions();
        if (c1.size() > 0 && c2.size() > 0)
        if (c1.Intersect (c2))
        {
            op1.addMutexOp (op2);
            return true;
        }
        c1 = op1.getPreConditions();
        c2 = op2.getDelEffects();
        if (c1.size() > 0&& c2.size() > 0)
        if (c2.Intersect (c1))
        {
            op1.addMutexOp (op2);
            return true;
        }
        return false;
    }

    /**
     * Verifies mutex existence among the two operator's preconditions.
     * @param op1 The op1.
     * @param op2 The op2.
     * @return  true if 
     */
    private boolean checkPreMutex(Action op1, Action op2) {
        // build two (Proposition) Vectors of preconditions of op1 and op2
        // check mutex relations among propositions
        Vector pre1 = op1.getPreProps();
        Vector pre2 = op2.getPreProps();
        int len1 = pre1.size();
        int len2 = pre2.size();
        for (int i = 0; i < len1; i++)
        {
            Proposition p1 = (Proposition) pre1.elementAt(i);
            for (int j = 0; j < len2; j++)
            {
                Proposition p2 = (Proposition) pre2.elementAt(j);
                if (p1.isMutex(p2))
                {
                    op1.addMutexOp (op2);
                    return true;
                }
            }
        }
        return false;
    }
    
	public Conjunction getPreConditionsOf(Vector<String> actionStrings)
	{
		Conjunction preConditions = new Conjunction();
		for(String actionString : actionStrings)
		{
			Vector<String> preProps = actions.get(actionString).getPreConditions().getLiterals();
			for(String preProp : preProps)
			{
				if(!preConditions.contains(preProp)) preConditions.addLiteral(preProp);
			}
		}
		return preConditions;
	}

    /**
     * Note: an operator may be added more than once so you must take this into
     * your account when generating the plan
     *  
     * @param theAct The the act.
     * @return  The boolean.
     */
    private boolean addApplicableAction(Action theAct) {
        /**
        		 * add theAct to applicable if it's not mutex with any applicable action
        		 * Note an operator may be added twice. 
        		 */
        int len = applicable.size();
        for (int i = 0; i < len; i++)
        {
            Action op = (Action) applicable.elementAt(i);
            if ( op.isMutex (theAct) )
            return false;
        }
        applicable.addElement (theAct);
        return true;
    }

    /**
     * Removes the most recenlty added applicable action (it is no longer applicable see it's use in searchTheLevel for more.
     */
    private void removeApplicableAction() {
        if (applicable.size() > 0)
        applicable.removeElementAt(applicable.size()-1);
        else
        System.out.println ("No More applicable actions" );
    }

    /**
     * Returns true if the actionlayer's conjunction contains the given action literal 
     * @param thisAction the action which may or may not be contained by this layer.
     * @return true when there is atleast one action that matches this 
     */
	public boolean contains(String thisAction)
	{
		for(String action : conjunction.getLiterals())
		{
			if(action.equals(thisAction)) return true;
		}
		return false;
	}

	/**
	 * Returns the actual actions matching certain strings.
	 * @param actionGoalStrings the action-names to be matched
	 * @return the actions with names in the string
	 */
	public Vector<Action> getActions(Vector<String> actionGoalStrings)
	{
		Vector<Action> matchingActions = new Vector<Action>();
		for(Action action : actions_vector)
		{
			if(actionGoalStrings.contains(action.getHeader())) matchingActions.add(action);
		}
		return matchingActions;
	}

} // end ActionLayer
