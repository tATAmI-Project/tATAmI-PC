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
 * Class:   graphplan.Goal
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
 * Class Goal.
 *  
 * @author  Yasser EL-Manzalawy
 */
public class Goal {

    // ------------------------------------------------------------------------
    // --- fields                                                           ---
    // ------------------------------------------------------------------------
    /**
     * The ntried.
     */
    int ntried;

    /**
     * The achieved.
     */
    boolean achieved;

    /**
     * The prop.
     */
    private Proposition prop;

    /**
     * The nactions.
     */
    private int nactions;


    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------
    /**
     * Creates a new instance of Goal.
     *  
     * @param theLiteral The the literal.
     * @param theLayer The the layer.
     */
    public Goal(String theLiteral, PropositionLayer theLayer) {
        prop = theLayer.getProposition (theLiteral);
        nactions = prop.getAddEffects().size();
        ntried = 0;
        achieved = false;
    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------
    /**
     * returns the no. of actions supporting that goal
     *  
     * @return  The int.
     */
    public int length() {
        return nactions;
    }

    /**
     * Note: always NOOP is the first returned action.
     *  
     * @return  The action.
     */
    public Action nextAction() {
        if (ntried < nactions)
        {
            ntried++;
            return (Action) prop.getAddEffects().elementAt(ntried-1);
        }
        return null;
    }

    /**
     * Tests if ...
     *  
     * @return  The boolean.
     */
    public boolean isAchieved() {
        return achieved;
    }

    /**
     * ...
     *  
     * @param value The value.
     */
    public void achieved(boolean value) {
        achieved = value;
    }

    /**
     * Returns the name.
     *  
     * @return  The name.
     */
    public String getName() {
        return prop.getName();
    }

    /**
     * ...
     *  
     * @return  The string.
     */
    public String toString() {
        return getName() + "Supporting actions: " + nactions;
    }

    /**
     * ...
     */
    void init() {
        ntried = 0;
    }

} // end Goal
