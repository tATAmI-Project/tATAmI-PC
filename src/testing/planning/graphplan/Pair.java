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
 * Class:   graphplan.Pair
 *
 * Date:    2004-03-31
 *
 * Author:  Yasser EL-Manzalawy
 * Email:   ymelmanz@yahoo.com
 */

package testing.planning.graphplan;

import java.util.*;
import java.lang.*;

/**
 * Class Pair.
 * Each Pair objects represents and pair of (type, name) strings 
 * @author  Yasser EL-Manzalawy
 */
public class Pair {

    // ------------------------------------------------------------------------
    // --- fields                                                           ---
    // ------------------------------------------------------------------------
    /**
     * The obj name.
     */
    protected String objName;

    /**
     * The obj type.
     */
    protected String objType;


    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------
    /**
     * Creates a new instance of Pair.
     *  
     * @param theType The the type.
     * @param theName The the name.
     */
    public Pair(String theType, String theName) {
        objType = theType;
        objName = theName;
    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------
    /**
     * Returns the name.
     *  
     * @return  The name.
     */
    public String getName() {
        return objName;
    }

    /**
     * Returns the type.
     *  
     * @return  The type.
     */
    public String getType() {
        return objType;
    }

    /**
     * Returns the pair.
     *  
     * @return  The pair.
     */
    public Pair getPair() {
        return this;
    }

    /**
     * Unit testing method
     *  
     * @return  The string.
     */
    public String toString() {
        String s = new String();
        s = "(" + objType + "," + objName + ")";
        return s;
    }

} // end Pair
