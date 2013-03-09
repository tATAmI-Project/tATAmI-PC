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
 * Class:   graphplan.Conjunction
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
 * A conjunction is a vector of literals. Each literal is a java String
 *  
 * @author  Yasser EL-Manzalawy
 */
public class Conjunction {

    // ------------------------------------------------------------------------
    // --- field                                                            ---
    // ------------------------------------------------------------------------
    /**
     * The literals.
     */
    private Vector<String> literals;


    // ------------------------------------------------------------------------
    // --- constructors                                                     ---
    // ------------------------------------------------------------------------
    /**
     * Creates a new instance of Conjunction.
     */
    public Conjunction() {
        literals = new Vector<String>();
    }
    
    public Conjunction(Vector<String> literals)
    {
    	this.literals = literals;
    }

    /**
     * DONE: will be modified to consider the passed String a list of literals not
     * a single one.
     *  
     * @param literal The literal.
     */
    public Conjunction(String literal) {
        literals = new Vector<String>();
        if (literal == null)		// allow null parameter
        return;
        // is is a list of litersla
        if (literal.indexOf("&") > 0)
        {
            StringTokenizer tok = new StringTokenizer (literal, "&", false);
            while (tok.hasMoreTokens())
            {
                String s = tok.nextToken();
                s = s.trim();
                addLiteral (s);
            }
        }
        else
        addLiteral (literal);
    }

    /**
     * copy constructor. (tested and works propoerly)
     *  
     * @param theCnj The the cnj.
     */
    public Conjunction(Conjunction theCnj) {
        literals = new Vector<String>();
        int len = theCnj.size();
        for (int i = 0; i < len; i++)
        	addLiteral(theCnj.getLiteral(i));
    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------
    /**
     * Adds a literal.
     *  
     * @param lit The lit.
     */
    public void addLiteral(String lit) 
    {
        literals.addElement(lit.replace(" ", ""));
    }

    /**
     * Returns the literal.
     *  
     * @param index The index.
     * @return  The literal.
     */
    public String getLiteral(int index) {
        return (String) literals.elementAt(index);
    }

    /**
     * Removes a literal.
     *  
     * @param index The index.
     */
    public void removeLiteral(int index) {
        literals.removeElementAt (index);
    }

    /**
     * Removes a literal.
     *  
     * @param literal The literal.
     * @return  The boolean.
     */
    public boolean removeLiteral(String literal) {
        return literals.removeElement (literal);
    }

    /**
     * adds theCnj to this conjunction and avoid duplication in literals
     *  
     * @param theCnj The the cnj.
     */
    public void addConjunction(Conjunction theCnj) {
        int len = theCnj.size();
        for (int i =0; i < len; i++)
        {
            if (this.contains (theCnj.getLiteral(i)) == false) // remove duplicated members
            literals.addElement (theCnj.getLiteral(i));
        }
    }

    /**
     * 
     *  
     * @return  The size of the literals.
     */
    public int size() {
        return literals.size();
    }

    /**
     * tests if a sub conjunction is included in that conjunction
     *  
     * @param theSub to be tested for inclusion
     * @return  true if all literals of theSub or contained in this conjunction's literal.
     */
    public boolean contains(Conjunction theSub) {
        int len = theSub.size();
        for (int i = 0; i <len ; i++)
        {
            if (this.contains (theSub.getLiteral(i))== false)
            return false;
        }
        return true;
    }

    /**
     * Tests that literal is a literal in this conjunction
     *  
     * @param literal The literal.
     * @return  The boolean.
     */
    public boolean contains(String literal) {
        return (literals.contains (literal));
    }

    /**
     * returns true if one or more literals are shared
     * TODO: modify it to not consider != pre conditions
     *  
     * @param theCnj The the cnj.
     * @return  The boolean.
     */
    public boolean Intersect(Conjunction theCnj) {
        int len = theCnj.size();
        for (int i = 0; i < len; i++)
        {
            if (this.contains (theCnj.getLiteral(i)))
            return true;
        }
        return false;
    }

    /**
     * Tests whether two conjunction 
     *  
     * @param theCnj The the cnj.
     * @return  The boolean.
     */
    public boolean equal(Conjunction theCnj) {
        if (literals.size() != theCnj.size())
        return false;
        int len = literals.size();
        for (int i = 0; i < len; i++)
        {
            if (this.contains (theCnj.getLiteral(i)) == false)
            return false;
        }
        return true;
    }

    /**
     * for debugging
     *  
     * @return  The string.
     */
    public String toString() {
        String s = new String();
        int size = literals.size();
        if (size == 0)
        return null;
        for (int i = 0; i < size; i++)
        {
            s += (String) literals.elementAt(i)+ "& ";
        }
        return s.substring(0, s.length()-2);
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
        Conjunction cnj = new Conjunction ("On (A, B) & Clear (A)");
        System.out.println (cnj);
    }
    
    public Vector<String> getLiterals()
    {
    	return literals;
    }

	public void add(Conjunction conjunction)
	{
		for(String literal : conjunction.getLiterals())
		{
			addLiteral(literal);
		}
	}

} // end Conjunction
