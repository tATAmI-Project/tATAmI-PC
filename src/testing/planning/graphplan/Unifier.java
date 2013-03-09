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
 * Class:   graphplan.Unifier
 *
 * Date:    2004-03-31
 *
 * Author:  Yasser EL-Manzalawy
 * Email:   ymelmanz@yahoo.com
 */

package testing.planning.graphplan;

import java.util.*;

/**
 * Class Unifier.
 * is a hashtable in the format
 * -------------------------- 
 *   Key      |  value
 * --------------------------
 *   ?x	  |  A
 *   ?y	  |  B
 *   ?z       |  C
 * --------------------------
 * @author  Yasser EL-Manzalawy
 */
public class Unifier {

    // ------------------------------------------------------------------------
    // --- field                                                            ---
    // ------------------------------------------------------------------------
    /**
     * The table.
     */
    private Hashtable table;


    // ------------------------------------------------------------------------
    // --- constructors                                                     ---
    // ------------------------------------------------------------------------
    /**
     * default constructor
     */
    public Unifier() {
        table = new Hashtable();
    }

    /**
     * Creates a new instance of Unifier.
     *  
     * @param varList a string represented a list of variables separated by spaces or ",".
     * @param valList a string represented a list of types separated by spaces or ","
     */
    public Unifier(String varList, String valList) {
        StringTokenizer st1 = new StringTokenizer (varList, " (),\t\n\r\f" , false);
        StringTokenizer st2 = new StringTokenizer (valList, " (),\t\n\r\f" , false);
        if (st1.countTokens() != st2.countTokens())
        {
            // thror exception and exit
        }
        table = new Hashtable();
        while (st1.hasMoreTokens())
        {
            table.put (st1.nextToken(), st2.nextToken());
        }
    }

    /**
     * Creates a new instance of Unifier.
     *  
     * @param varList The var list.
     * @param valList The val list.
     */
    public Unifier(Vector varList, Vector valList) {
        if (varList.size()!= valList.size())
        {
            // TODO: throw RuntimeException and exit
        }
        table = new Hashtable();
        int len = varList.size();
        for (int i = 0; i < len ; i++)
        table.put ((String) varList.elementAt(i), (String) valList.elementAt(i));
    }

    /**
     * Creates a new instance of Unifier.
     *  
     * @param varList The var list.
     * @param valList The val list.
     */
    public Unifier(Vector varList, String valList) {
        StringTokenizer st2 = new StringTokenizer (valList, " (),\t\n\r\f" , false);
        int len = varList.size();
        if ( len != st2.countTokens())
        {
            // TODO: throw RuntimeException and exit
        }
        table = new Hashtable();
        for (int i = 0; i < len; i++)
        {
            table.put ((String) varList.elementAt(i), st2.nextToken());
        }
    }


    // ------------------------------------------------------------------------
    // --- method                                                           ---
    // ------------------------------------------------------------------------
    /**
     * get a unifier for the passed variable.
     *  
     * @param var The variable to be unified.
     * @return  a string represents the unified variable.
     */
    String get(String var) {
        if (var.startsWith ("@"))	// even numbers will start with @
        return var.substring(1);
        String val = (String) table.get(var);
        return val;
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
        String var = new String ("?x, ?y, ?z");
        String val = new String ("A B C");
        Unifier u = new Unifier (var, val);
        System.out.println (u.get("?y"));
        System.out.println (u.get("@Table"));
    }

} // end Unifier
