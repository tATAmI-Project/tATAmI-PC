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
 * Class:   graphplan.ObjectSet
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
 * Class ObjectSet.
 *  
 * @author  Yasser EL-Manzalawy
 */
public class ObjectSet {

    // ------------------------------------------------------------------------
    // --- field                                                            ---
    // ------------------------------------------------------------------------
    /**
     * The vector of Pair(s).
     */
    protected Vector objects;


    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------
    /**
     * Creates a new instance of ObjectSet.
     */
    public ObjectSet() {
        objects = new Vector();
    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------
    /**
     * will be invoked by Interpret.addObject
     *  
     * @param objType The obj type.
     * @param objName The obj name.
     */
    public void addObject(String objType, String objName) {
        Pair p = new Pair (objType, objName);
        objects.addElement (p);
    }

    /**
     * Adds a object.
     *  
     * @param thePair The the pair.
     */
    public void addObject(Pair thePair) {
        objects.addElement (thePair);
    }

    /**
     * ...
     *  
     * @return  The int.
     */
    public int size() {
        return objects.size();
    }

    /**
     * Removes a object.
     *  
     * @param thePair The the pair.
     */
    public void removeObject(Pair thePair) {
        objects.removeElement (thePair);
    }

    /**
     * Returns the object.
     *  
     * @param index The index.
     * @return  The object.
     */
    public Pair getObject(int index) {
        return (Pair) objects.elementAt(index);
    }

    /**
     * Returns the objects.
     *  
     * @param theType The the type.
     * @return  The objects.
     */
    public Vector getObjects(String theType) {
        Vector s = new Vector();
        int len  = objects.size();
        for (int i = 0; i < len; i++)
        {
            Pair p = getObject(i);
            if (theType.equals (p.getType()))
            s.addElement (p.getName());
        }
        return s;
    }

    /**
     * ...
     *  
     * @return  The string.
     */
    public String toString() {
        return objects.toString();
    }

} // end ObjectSet
