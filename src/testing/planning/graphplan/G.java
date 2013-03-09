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
 * Class:   graphplan.G
 *
 * Date:    2004-03-31
 *
 * Author:  Yasser EL-Manzalawy
 * Email:   ymelmanz@yahoo.com
 */

package testing.planning.graphplan;

import java.util.*;

/**
 * Class G.
 *  
 * @author  Yasser EL-Manzalawy
 */
public final class G {

    // ------------------------------------------------------------------------
    // --- static fields                                                    ---
    // ------------------------------------------------------------------------
    /**
     * Constant MaxPara.
     */
    public static final int MaxPara=10;

    /**
     * The max level.
     */
    public static int MaxLevel=10;


    // ------------------------------------------------------------------------
    // --- static methods                                                   ---
    // ------------------------------------------------------------------------
    /**
     * uses the u unifier to unify each string int the vector v.
     *  
     * @param v vector of strings.
     * @param u The unifier.
     * @return  The unified string in which each vector element is 
     * separated with "&".
     */
    public static String substitute(Vector v, Unifier u) {
        if (v == null)
        return null;
        String sub = new String();
        int len = v.size();
        for (int i = 0; i < len; i++)
        {
            String s = (String) v.elementAt(i);
            StringTokenizer st = new StringTokenizer (s, " (),\t\n\r\f", true);
            while (st.hasMoreTokens())
            {
                String tok = st.nextToken();
                if (tok.startsWith("?") || tok.startsWith("@"))
                tok = u.get (tok);
                sub += tok;
            }
            sub += " & ";
        }
        return sub.substring(0, sub.length()- 3);
    }

    /**
     *  
     *  
     * @param h The template operator head.
     * @param u The unifier.
     * @return  a string represents an action call (e.g. "Move (A, B)" ).
     */
    public static String substitute(TOpHead h, Unifier u) {
        String sub = new String();
        sub += h.getName() + " ( ";
        Vector vars = h.getVars();
        if (vars == null)		// no arguments
        sub += ")";
        else
        {
            int len = vars.size();
            for (int i = 0; i < len -1; i++)
            {
                String s = u.get ((String) vars.elementAt(i));
                sub += s + ", ";
            }
            sub += u.get ((String) vars.elementAt(len-1)) + " )";
        }
        return sub;
    }

    /**
     * Unit testing method
     *  
     * @param args The args array.
     */
    public static void main(String[] args) {
        Vector v = new Vector();
        v.addElement (new String ("On (?x, ?y)"));
        Unifier u = new Unifier ("?x ?y ?z", "A B C");
        System.out.println (substitute (v, u));
    }

} // end G
