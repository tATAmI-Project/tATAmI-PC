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
 * Class:   graphplan.TOpHead
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
 * Class TOpHead.
 *  
 * @author  Yasser EL-Manzalawy
 */
public class TOpHead {

    // ------------------------------------------------------------------------
    // --- fields                                                           ---
    // ------------------------------------------------------------------------
    /**
     * The name of the operator.
     */
    private String name;

    /**
     * The list of parameters passed to this operator.
     */
    private TParaList pl;


    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------
    /**
     * Creates a new instance of TOpHead.
     *  
     * @param name The name.
     * @param pl The list of parameters.
     */
    public TOpHead(String name, TParaList pl) {
        this.name = name;
        this.pl = pl;
    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------
    /**
     * Returns the types.
     *  
     * @return  The types.
     */
    public Vector getTypes() {
        if (pl != null)
          return pl.getTypes();
        return null;
    }

    /**
     * Returns the vars.
     *  
     * @return  The vars.
     */
    public Vector getVars() {
        if (pl != null)
          return pl.getVars();
        return null;
    }

    /**
     * Returns the name.
     *  
     * @return  The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Unit testing method
     *  
     * @return  The string.
     */
    public String toString() {
        return (name + pl.toString());
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
        String prop = new String ("Move (Block ?x, Block ?y, Car ?z)");
        GParser p = new GParser (new StringReader (prop));
        TOpHead v = null;
        try {
            v = p.OpHead();
        } catch (ParseException ex) {
            System.out.println ("Error Parsing Proposition");
            ex.printStackTrace();
            return;
        }
        System.out.println (v.name);
        if ( v.pl != null)
        System.out.println (v.pl);
    }

} // end TOpHead
