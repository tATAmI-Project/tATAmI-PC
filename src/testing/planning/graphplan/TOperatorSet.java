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
 * Class:   graphplan.TOperatorSet
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
 * A container of all template operators
 *  
 * @author  Yasser EL-Manzalawy
 */
public class TOperatorSet {

    // ------------------------------------------------------------------------
    // --- field                                                            ---
    // ------------------------------------------------------------------------
    /**
     * The operators.
     */
    private Vector<TOperator> operators;


    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------
    /**
     * Creates a new instance of TOperatorSet.
     */
    public TOperatorSet() {
        operators = new Vector();
    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------
    /**
     * Adds a operator.
     *  
     * @param theOp The the op.
     */
    public void addOperator(TOperator theOp) {
        operators.addElement (theOp);
    }

    /**
     * get the TOperator at the specified index
     *  
     * @param index The index.
     * @return  The  TOperator element.
     */
    public TOperator operatorAt(int index) {
        return (TOperator) operators.elementAt (index);
    }

    /**
     * 
     *  
     * @return  The number of TOperators.
     */
    public int size() {
        return operators.size();
    }

    /**
     * init each TOperator with possible valid unifiers
     *  
     * @param os The object set.
     */
    public void initUnifiers(ObjectSet os) {
        int len = operators.size();
        for (int i = 0; i < len; i++)
	    operatorAt(i).getPossibleUnifiers(os);
    }

    /**
     * 
     *  
     * @param thePre the precondition used to determine applicability of each action.
     * @return  a vector of all applicable actions.
     */
    public Vector generateActions(Conjunction thePre) {
        Vector actions = new Vector();
        int len = operators.size();
        for (int i = 0; i < len; i++)
        {
            Vector temp = operatorAt(i).generateActions (thePre);
            if (temp != null)
              actions.addAll (temp);
        }
        return actions;
    }

    /**
     * ...
     *  
     * @return  The string.
     */
    public String toString() {
        String s = new String();
        int len = operators.size();
        for (int i = 0; i< len; i++)
        {
            s += operatorAt(i).toString();
        }
        return s;
    }


    // ------------------------------------------------------------------------
    // --- static method                                                    ---
    // ------------------------------------------------------------------------
    /**
     * ...
     *  
     * @param args The args array.
     */
    public static void main(String[] args) {

    }

} // end TOperatorSet
