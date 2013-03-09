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
 * Class:   graphplan.Expression
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
 * Class Expression.
 *  
 * @author  Yasser EL-Manzalawy
 */
public class Expression {

    // ------------------------------------------------------------------------
    // --- fields                                                           ---
    // ------------------------------------------------------------------------
    /**
     * The left.
     */
    String left;

    /**
     * The right.
     */
    String right;

    /**
     * The op.
     */
    String op;

    /**
     * The op type.
     */
    int opType;


    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------
    /**
     * Creates a new instance of Expression.
     *  
     * @param l The string.
     * @param op The string.
     * @param r The string.
     */
    public Expression(String l, String op, String r) {
        left = l;
        right = r;
        this.op = op;
        if (op.equals ("=="))
        opType = 0;
        else
        opType = 1;
    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------
    /**
     * ...
     *  
     * @param unif The unif.
     * @return  The boolean.
     */
    public boolean evaluate(Unifier unif) {
        String ul = unif.get (left);
        String ur = unif.get (right);
        switch (opType)
        {
            case 0:
            return (ul.equals (ur));
            case 1:
            return (!ul.equals (ur));
        }
        return false;
    }

    /**
     * ...
     *  
     * @return  The string.
     */
    public String toString() {
        return (left + op + right);
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
        Expression e = new Expression ("?x", "==", "?y");
        String var = new String ("?x, ?y, ?z");
        String val = new String ("A B C");
        Unifier u = new Unifier (var, val);
        if ( e.evaluate (u))
        System.out.println("True");
        else
        System.out.println("False");
        System.out.println (e);
        // testing we can use the parser to get para condition
        GParser p = new GParser (new StringReader ("?x != ?y & ?x != ?z"));
        Vector v = null;
        try {
            v = p.ParaCond();
            // note null vector = "true"
        } catch (ParseException ex) {
            System.out.println ("Error Parsing condition");
            ex.printStackTrace();
            return;
        }
        System.out.println (v);
    }

} // end Expression
