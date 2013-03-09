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
 * Class:   graphplan.TOperator
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
 * Class TOperator.
 * Represents an operator template. 
 * @author  Yasser EL-Manzalawy
 */
public class TOperator {

    // ------------------------------------------------------------------------
    // --- fields                                                           ---
    // ------------------------------------------------------------------------
    /**
     * The head.
     */
    private TOpHead head;

    /**
     * The parameter conditions.
     */
    private Vector cond;

    /**
     * The precondition.
     */
    private Vector pre;

    /**
     * The add effects.
     */
    private Vector add;

    /**
     * The del effects.
     */
    private Vector del;

    /**
     * 1. no arguments --> allUnifiers and validUnifiers are null
     * 2. arguments and cond == null --> allUnifiers = validUnifiers
     * 3. arguments and cond != null --> allUnifiers >= validUnifiers
     */
    private Vector allUnifiers;

    /**
     * The valid unifiers.
     */
    private Vector validUnifiers;


    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------
    /**
     * Creates a new instance of TOperator.
     *  
     * @param h The t op head.
     * @param c The vector.
     * @param p The vector.
     * @param a The vector.
     * @param d The vector.
     */
    public TOperator(TOpHead h, Vector c, Vector p, Vector a, Vector d) {
        head = h;
        // any of those vectors could be null
        cond = c;
        pre = p;
        add = a;
        del = d;
    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------
    /**
     * uses the types of parameters and the set of objects to determine
     * a list of all possible unifiers. however, some of them is never been
     * applicable after applying the ParaCond condition.
     *  
     * @param os The object set.
     */
    public void getPossibleUnifiers(ObjectSet os) {
        Vector types = head.getTypes();
        if (types == null)	// no arguments
          return;
        allUnifiers = new Vector();
        int len = types.size();
        for (int i = 0; i < len; i++)
        {
            String type = (String) types.elementAt(i);
            Vector para = os.getObjects(type);
            if (para.size() == 0)	// no objects found
            {
                System.out.print("Error in operator: " + head.getName());
                System.out.print("No object of the type: " + type);
                System.exit(0);
            }
            join (para);
        }
        // Now remove irrelevant unifiers
        if (cond == null)
        validUnifiers = allUnifiers;
        else
        {
            len = allUnifiers.size();
            Vector vars = head.getVars();
            validUnifiers = new Vector();
            for (int i = 0; i < len; i++)
            {
                Unifier un = new Unifier (vars, (String) allUnifiers.elementAt(i));
                if (evaluateCond (un) )
                validUnifiers.addElement (allUnifiers.elementAt(i));
            }
        } // end else
    }

    /**
     * for each valid unifier, evaluate the precondition according to the given
     * conjunction (which is the initial state or the prev. proposition layer.
     * if operator is not applicable then it will return a Vector with ZERO size.
     *  
     * @param thePre the precondition used to determine applicability of each action.
     * @return  a vector of all applicable actions.
     */
    public Vector generateActions(Conjunction thePre) {
        Vector actions = new Vector();
        Vector vars = head.getVars();
        // note: there must be at least one variable
        // or there is no pre, add, del == no operator
        int len = validUnifiers.size();
        for (int i = 0; i < len; i++)
        {
            String s = (String) validUnifiers.elementAt(i);
            Unifier un = new Unifier (vars, s);
            String apre = G.substitute (pre, un);
            Conjunction cnj = new Conjunction (apre);
            // apre may be null
            if (thePre.contains(cnj))	// pre-condition is satisfied
            {
                // create an action
                String adel = G.substitute (del, un);
                String aadd = G.substitute (add, un);
                String ahead = G.substitute (head, un);
                // add it to actions vector
                actions.addElement (new Action (ahead, apre, aadd, adel));
            }
        }
        return actions;
    }

    /**
     * for testing 
     */
    public void show() {
        if (allUnifiers == null)
        System.out.println ("Please Call getPossibleUnifiers() first");
        System.out.println (allUnifiers);
        System.out.println (validUnifiers);
    }

    /**
     * i/p unifier used with the expression
     * returns true if the whole condition is evaluated as true
     *  
     * @param un The unifier.
     * @return  The boolean.
     */
    private boolean evaluateCond(Unifier un) {
        int len = cond.size();
        for (int i = 0; i < len; i++)
        {
            Expression e = (Expression) cond.elementAt(i);
            if (e.evaluate (un) == true)
            continue;
            else
            return false;
        }
        return true;
    }

    /**
     * joins allUnifiers with second and keep the result at allUnifiers
     *  
     * @param second The second.
     */
    private void join(Vector second) {
        int len = allUnifiers.size();
        if (len == 0)
        {
            allUnifiers.addAll (second);
            return;
        }
        Vector temp = new Vector();
        temp.addAll(allUnifiers);
        allUnifiers.clear();
        for (int i = 0; i < len; i++)
        {
            String s1 = (String) temp.elementAt(i);
            int len2 = second.size();
            for (int j = 0; j < len2; j++)
            {
                String s2 = new String();
                s2 = s1 + " " + (String) second.elementAt(j);
                allUnifiers.addElement (s2);
            }
        }
    }


    // ------------------------------------------------------------------------
    // --- static method                                                    ---
    // ------------------------------------------------------------------------
    /**
     * Unit testing method
     *  
     * @param args The args array.
     */
    public static void main(String[] args) {
        GParser p = null;
        try {
            p = new GParser (new java.io.FileInputStream("input.txt"));
        }catch (java.io.FileNotFoundException e) {
            System.out.println("GParser Version 0.1:  input.txt not found.");
            return;
        }
        TOperator v = null;
        try {
            v = p.Operator();
        } catch (ParseException ex) {
            System.out.println ("Error Parsing Proposition");
            ex.printStackTrace();
            return;
        }
        System.out.println (v.head);
        if ( v.cond != null)
        System.out.println (v.cond);
        if ( v.pre != null)
        System.out.println (v.pre);
        if ( v.add != null)
        System.out.println (v.add);
        if ( v.del != null)
        System.out.println (v.del);
    }
    
    public String toString()
    {
    	String toString = head.toString();
    	toString += "\nParameters:" + cond;
    	toString += "\nPres: " + pre;
    	toString += "\nAdds: " + add;
    	toString += "\nDels: " + del;
    	
    	return toString;
    }

} // end TOperator
