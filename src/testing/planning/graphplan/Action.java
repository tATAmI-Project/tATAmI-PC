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
 * Class:   graphplan.Action
 *
 * Date:    2004-03-31
 *
 * Author:  Yasser EL-Manzalawy
 * Email:   ymelmanz@yahoo.com
 */

package testing.planning.graphplan;

import java.util.*;

/**
 * Class Action.
 *  
 * @author  Yasser EL-Manzalawy
 */
public class Action {

    // ------------------------------------------------------------------------
    // --- fields                                                           ---
    // ------------------------------------------------------------------------
    /**
     * The pre condition.
     */
    private Conjunction preCondition;

    /**
     * The add effects.
     */
    private Conjunction addEffects;

    /**
     * The del effects.
     */
    private Conjunction delEffects;

    /**
     * The head.
     */
    private String head;

    /**
     * The op type.
     */
    private int opType;

    /**
     * The mutex ops.
     */
    private Vector<Action> mutexOps;

    /**
     * The pre props.
     */
    private Vector<Proposition> preProps;

    /**
     * The add props.
     */
    private Vector<Proposition> addProps;

    /**
     * The del props.
     */
    private Vector<Proposition> delProps;


    // ------------------------------------------------------------------------
    // --- constructors                                                     ---
    // ------------------------------------------------------------------------
    /**
     * Creates a new instance of Action.
     *  
     * @param theHead the head, or name, of this action
     * @param thePre The preconditions of this action
     * @param theAdd The add effects
     * @param theDel The del effects
     */
    public Action(String theHead, String thePre, String theAdd, String theDel) {
        opType = 0;
        setHead(theHead);
        preCondition = new Conjunction (thePre);
        addEffects = new Conjunction (theAdd);
        delEffects = new Conjunction (theDel);
        init();
    }


	/**
     * Creates a new instance of Action.
     *  
     * @param name The name.
     */
    public Action(String name) {
    	setHead(name);
        opType = 1;
        preCondition = new Conjunction ();
        addEffects = new Conjunction ();
        delEffects = new Conjunction ();
        init();
    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------
    /**
     * Returns the header.
     *  
     * @return  The header.
     */
    public String getHeader() {
        return head;
    }
    

    private void setHead(String theHead)
	{
		head = theHead.replace(" ", "");
	}

    /**
     * Returns the type.
     *  
     * @return  The type.
     */
    public int getType() {
        return opType;
    }

    /**
     * Returns the pre conditions.
     *  
     * @return  The pre conditions.
     */
    public Conjunction getPreConditions() {
        return preCondition;
    }

    /**
     * Sets the pre conditions.
     *  
     * @param conjunction The pre conditions.
     */
    public void setPreConditions(Conjunction conjunction) {
        preCondition = conjunction;
    }

    /**
     * Returns the pre props.
     *  
     * @return  The pre props.
     */
    public Vector<Proposition> getPreProps() {
        return preProps;
    }

    /**
     * Returns the add effects.
     *  
     * @return  The add effects.
     */
    public Conjunction getAddEffects() {
        return addEffects;
    }

    /**
     * Sets the add effects.
     *  
     * @param conjunction The add effects.
     */
    public void setAddEffects(Conjunction conjunction) {
        addEffects = conjunction;
    }

    /**
     * Returns the add props.
     *  
     * @return  The add props.
     */
    public Vector getAddProps() {
        return addProps;
    }

    /**
     * Returns the del effects.
     *  
     * @return  The del effects.
     */
    public Conjunction getDelEffects() {
        return delEffects;
    }

    /**
     * Sets the del effects.
     *  
     * @param conjunction The del effects.
     */
    public void setDelEffects(Conjunction conjunction) {
        delEffects = conjunction;
    }

    /**
     * Returns the del props.
     *  
     * @return  The del props.
     */
    public Vector<Proposition> getDelProps() {
        return delProps;
    }

    /**
     * Returns the mutex ops.
     *  
     * @return  The mutex ops.
     */
    public Vector<Action> getMutexOps() {
        return mutexOps;
    }

    /**
     * Sets the mutex ops.
     *  
     * @param vector The mutex ops.
     */
    public void setMutexOps(Vector<Action> vector) {
        mutexOps = vector;
    }

    /**
     * Adds a mutex op.
     *  
     * @param action The action to be added as a mutex relation
     */
    public void addMutexOp(Action action) {
        if(!mutexOps.contains(action))
        {
            mutexOps.addElement(action);
            action.getMutexOps().addElement(this);
        }
    }

    /**
     * Returns the mutex op.
     *  
     * @param i The int.
     * @return  The mutex op.
     */
    public Action getMutexOp(int i) {
        return (Action)mutexOps.elementAt(i);
    }

    /**
     * Tests if this action is in mutex with the other
     *  
     * @param action The action to be tested for mutex.
     * @return  true when this action contains action in its mutex operators list, false otherwise.
     */
    public boolean isMutex(Action action) {
        return mutexOps.contains(action);
    }

    /**
     * Adds a pre prop.
     *  
     * @param proposition The proposition.
     */
    public void addPreProp(Proposition proposition) {
        preProps.addElement(proposition);
    }

    /**
     * Adds a add prop.
     *  
     * @param proposition The add proposition.
     */
    public void addAddProp(Proposition proposition) {
        addProps.addElement(proposition);
    }

    /**
     * Adds a delete proposition.
     *  
     * @param proposition The delete proposition to be added.
     */
    public void addDelProp(Proposition proposition) {
        addProps.addElement(proposition);
    }

    /**
     * Returns the ith preproposition.
     *  
     * @param i The index
     * @return  The pre prop.
     */
    public Proposition getPreProp(int i) {
        return preProps.elementAt(i);
    }

    /**
     * Return the ith element in the add proposition list.
     *  
     * @param i The int.
     * @return  The proposition.
     */
    public Proposition getaddProp(int i) {
        return addProps.elementAt(i);
    }

    /**
     * Return the ith element in the delete proposition list.
     *  
     * @param i The int.
     * @return  The proposition.
     */
    public Proposition delPreProp(int i) {
        return delProps.elementAt(i);
    }

    /**
     * The string representation of this action
     *  
     * @return  The string.
     */
    public String toString() {
    	String s = head;
    	/*
        String s = head + "\n Mutex With: ";
        int len = mutexOps.size();
        for (int i = 0; i <len; i++)
        {
            Action act = (Action) mutexOps.elementAt(i);
            s += act.getHeader() + ", ";
        }
        */
        return s;
    }

    /**
     * Initialize all the Vectors.
     */
    private void init() {
        mutexOps = new Vector<Action>();
        preProps = new Vector<Proposition>();
        addProps = new Vector<Proposition>();
        delProps = new Vector<Proposition>();
    }

} // end Action
