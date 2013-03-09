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
 * Class:   graphplan.Proposition
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
 * Implements a Proposition in the planning graph.
 *  
 * @author  Yasser EL-Manzalawy
 */
public class Proposition {

    // ------------------------------------------------------------------------
    // --- fields                                                           ---
    // ------------------------------------------------------------------------
    /**
     * The pre_list.
     */
    Vector pre_list;

    /**
     * The add_list.
     */
    Vector add_list;

    /**
     * The del_list.
     */
    Vector del_list;

    /**
     * The mutex_props.
     */
    Vector mutex_props;

    /**
     * The name.
     */
    private String name;


    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------
    /**
     * Creates a new instance of Proposition.
     *  
     * @param theName The the name.
     */
    public Proposition(String theName) {
        name = theName;
        pre_list = new Vector();
        add_list = new Vector();
        del_list = new Vector();
        mutex_props = new Vector();
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
        return name;
    }

    /**
     * ...
     *  
     * @param theAct The the act.
     */
    public void putPreEdge(Action theAct) {
        pre_list.addElement (theAct);
    }

    /**
     * ...
     *  
     * @param theAct The the act.
     * @param noop The noop.
     */
    public void putAddEdge(Action theAct, int noop) {
        if (noop == 1)
        add_list.insertElementAt (theAct, 0);
        // put noop at the top
        else
        add_list.addElement (theAct);
    }

    /**
     * ...
     *  
     * @param theAct The the act.
     */
    public void putDelEdge(Action theAct) {
        del_list.addElement (theAct);
    }

    /**
     * Returns the pre conditions.
     *  
     * @return  The pre conditions.
     */
    public Vector getPreConditions() {
        return pre_list;
    }

    /**
     * Returns the add effects.
     *  
     * @return  The add effects.
     */
    public Vector getAddEffects() {
        return add_list;
    }

    /**
     * Returns the del effects.
     *  
     * @return  The del effects.
     */
    public Vector getDelEffects() {
        return del_list;
    }

    /**
     * Returns the mutex props.
     *  
     * @return  The mutex props.
     */
    public Vector getMutexProps() {
        return mutex_props;
    }

    /**
     * Returns the mutex prop.
     *  
     * @param index The index.
     * @return  The mutex prop.
     */
    public Proposition getMutexProp(int index) {
        return (Proposition) mutex_props.elementAt(index);
    }

    /**
     * sets a mutex relation between me and theProp and vise versa.
     *  
     * @param theProp The the prop.
     */
    public void addMutexProp(Proposition theProp) {
        if (mutex_props.contains (theProp) == false) // check that I don't know this fact
        {
            mutex_props.addElement (theProp);
            theProp.getMutexProps().addElement (this);
        }
    }

    /**
     * Tests if ...
     *  
     * @param theProp The the prop.
     * @return  The boolean.
     */
    public boolean isMutex(Proposition theProp) {
        return mutex_props.contains (theProp);
    }

    /**
     * ...
     *  
     * @return  The string.
     */
    public String toString() {
        String s = new String();
        s += "\nProposition: " + getName();
        s+= "\n Mutex with: ";
        for (int i = 0; i < mutex_props.size(); i++)
        {
            s += getMutexProp(i).getName()+ ", ";
        }
        return s;
    }

} // end Proposition
