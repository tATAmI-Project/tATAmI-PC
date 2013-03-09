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
 * Class:   graphplan.PropositionLayer
 *
 * Date:    2004-03-31
 *
 * Author:  Yasser EL-Manzalawy
 * Email:   ymelmanz@yahoo.com
 */

package testing.planning.graphplan;

import java.util.*;

/**
 * Implements a proposition layer in the planning graph
 *  
 * @author  Yasser EL-Manzalawy
 */
public class PropositionLayer {

    // ------------------------------------------------------------------------
    // --- fields                                                           ---
    // ------------------------------------------------------------------------
    /**
     * previous and next action layers
     */
    private ActionLayer prev=null;

    /**
     * The next.
     */
    private ActionLayer next=null;

    /**
     * hashtable for all propositions in that layer
     */
    private Hashtable propositions;

    /**
     * The propositions_vector.
     */
    private Vector propositions_vector;

    /**
     * a conjunction object formed from all the propositions in the layer
     */
    private Conjunction conjunction=null;


    // ------------------------------------------------------------------------
    // --- constructors                                                     ---
    // ------------------------------------------------------------------------
    /**
     * Creates a new instance of PropositionLayer.
     */
    public PropositionLayer() {
        propositions = new Hashtable();
        conjunction = new Conjunction();
    }

    /**
     * Creates a new instance of PropositionLayer.
     *  
     * @param theprev The theprev.
     */
    public PropositionLayer(ActionLayer theprev) {
        this.prev = theprev;
        propositions = new Hashtable();
        conjunction = new Conjunction();
    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------
    /**
     * Sets the next layer.
     *  
     * @param theNext The next layer.
     */
    public void setNextLayer(ActionLayer theNext) {
        next = theNext;
    }

    /**
     * Returns the next layer.
     *  
     * @return  The next layer.
     */
    public ActionLayer getNextLayer() {
        return next;
    }

    /**
     * Returns the prev layer.
     *  
     * @return  The prev layer.
     */
    public ActionLayer getPrevLayer() {
        return prev;
    }

    /**
     * Returns the proposition.
     *  
     * @param key The key.
     * @return  The proposition.
     */
    public Proposition getProposition(String key) {
        return (Proposition) propositions.get (key);
    }

    /**
     * Returns the proposition.
     *  
     * @param index The index.
     * @return  The proposition.
     */
    public Proposition getProposition(int index) {
        return (Proposition) propositions_vector.elementAt (index);
    }

    /**
     * Returns the conjunction.
     *  
     * @return  The conjunction.
     */
    public Conjunction getConjunction() {
        return conjunction;
    }

    /**
     * If the proposition with the same name is already in this layer
     * return the existing proposition.
     * else
     * add the new proposition and return it.
     *  
     * @param thePro The the pro.
     * @return  The proposition.
     */
    public Proposition addProposition(Proposition thePro) {
        // first check that it doesn't exixt
        if (propositions.containsKey(thePro.getName()))
        return (Proposition) propositions.get (thePro.getName());
        propositions.put (thePro.getName(), thePro);
        conjunction.addLiteral (thePro.getName());
        return thePro;
    }

    /**
     * Adds a proposition.
     *  
     * @param theName The the name.
     * @return  The proposition.
     */
    public Proposition addProposition(String theName) {
        Proposition p = new Proposition (theName);
        return addProposition (p);
    }

    /**
     * ...
     *  
     * @return  The int.
     */
    public int size() {
        return propositions.size();
    }

    /**
     * Called to construct the initial state proposition layer
     *  
     * @param theConjunction The init layer.
     */
    public void setInitLayer(Conjunction theConjunction) {
        int len = theConjunction.size();
        for (int i = 0; i < len; i++)
        {
            addProposition (theConjunction.getLiteral(i));
        }
        createPropositionVector();
    }

    /**
     * ...
     *  
     * @param theLayer The the layer.
     * @return  The boolean.
     */
    public boolean equal(PropositionLayer theLayer) {
        return conjunction.equal (theLayer.getConjunction());
    }

    /**
     * determine and set all the mutex relations among propositions in that layer
     */
    public void calculateMutex() {
        createPropositionVector();
        int len = propositions_vector.size();
        for (int prop = 0; prop < len ; prop++) // for each prop in that layer
        {
            Proposition pro1 = getProposition (prop);
            for (int otherprop = prop + 1; otherprop < len; otherprop++)
            {
                Proposition pro2 = getProposition (otherprop);
                checkMutex (pro1,pro2);
            }
        }
    }

    /**
     * ...
     *  
     * @return  The string.
     */
    public String toString() {
        String s = new String();
        s += "\nProposition Layer";
        for (int i = 0 ; i < propositions_vector.size(); i++)
        s += getProposition(i).toString();
        return s;
    }

    /**
     * important for building createPropositionVector to retrieve props by index
     */
    private void createPropositionVector() {
        propositions_vector = new Vector();
        for (Enumeration e = propositions.elements(); e.hasMoreElements(); )
        propositions_vector.addElement ((Proposition) e.nextElement());
    }

    /**
     * ...
     *  
     * @param p1 The proposition.
     * @param p2 The proposition.
     */
    private void checkMutex(Proposition p1, Proposition p2) {
        Vector p1Actions = p1.getAddEffects();
        Vector p2Actions = p2.getAddEffects();
        int len1 = p1Actions.size();
        int len2 = p2Actions.size();
        // if all actions supporting p1 are mutex with actions supporting p2
        // then p1 is mutex with p2.
        for (int i = 0 ; i < len1; i++)
        {
            Action op1 = (Action) p1Actions.elementAt(i);
            for (int j = 0; j < len2; j++)
            {
                Action op2 = (Action) p2Actions.elementAt(j);
                if( op1.isMutex(op2) == false)
                return;
                // at least there is one path
            }
        }
        p1.addMutexProp(p2);
    }

} // end PropositionLayer
