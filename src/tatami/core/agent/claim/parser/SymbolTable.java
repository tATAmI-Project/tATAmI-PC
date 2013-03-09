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
package tatami.core.agent.claim.parser;

import java.io.Serializable;
import java.util.HashMap;


import tatami.core.interfaces.Logger;
import tatami.core.util.logging.Log;


/**
 * Symbol table designed to support scopes. The main idea is to have a linked list of symbol tables, with the head being
 * the current scope (in our case, the current behavior) and with the tail (in the direction of 'prev' being the symbol
 * table of the most outer scope (which is the agent's scope).
 * 
 * @author tudor
 * @author Andrei Olaru
 * 
 */
public class SymbolTable implements Serializable
{
	public enum VariableStatus {
		DOESNT_EXIST, UNBOUND, BOUND
	}
	
	private static final long			serialVersionUID	= 8322310088284342473L;
	
	/**
	 * The correspondence between names of {@link ClaimVariable}s and {@link ClaimValue}s.
	 */
	private HashMap<ClaimVariable, ClaimValue>	table;
	/**
	 * The {@link SymbolTable} instance of broader scope / outer context / higher level
	 */
	protected SymbolTable				prev;
	
	/** the logger */
	protected transient Logger log = null;

	public Logger getLog() {
		return log;
	}

	public void setLog(Logger log) {
		this.log = log;
	}

	public SymbolTable(SymbolTable link)
	{
		table = new HashMap<ClaimVariable, ClaimValue>();
		prev = link;
	}
	
	/**
	 * puts a new variable, with the specified name and with the associated value in this symbol table if the specified
	 * name is not found in one of the symbol tables of the hierarchy, starting with the current one.
	 * 
	 * @param variable
	 *            - name of the variable
	 * @param value
	 *            - the value of the variable
	 */
	public void put(ClaimVariable variable, ClaimValue value)
	{
		SymbolTable st = getSymbolTableContainingKey(variable);
		if(st == null)
			table.put(variable, value);
		else
		{
			if (variable.isAffectable() || st.table.get(variable)==null)
				st.table.put(variable, value);
			else
			{
				log.error("Trying to bind the already bound unaffectable variable "+variable.getName()+
						" in behavior "+variable.getMyBehavior().getName()+". The old value: "+
						st.table.get(variable).getValue()+" was not replaced by the new one: "+
						(value==null?"null":value.getValue()));
				
				System.exit(1);
			}
		}
	}
	
	/**
	 * returns the symbol table that contains a certain key (if any, <code>null</code> otherwise)
	 * 
	 * @param variable
	 *            - variable name to be verified
	 * @return - the symbol table which contains a certain key
	 */
	public SymbolTable getSymbolTableContainingKey(ClaimVariable variable)
	{
		for(SymbolTable st = this; st != null; st = st.prev)
		{
			if(st.table.containsKey(variable))
				return st;
		}
		return null;
	}

	
	public VariableStatus getStatus(ClaimVariable variable)
	{
		SymbolTable st = getSymbolTableContainingKey(variable);
		if(st == null)
			return VariableStatus.DOESNT_EXIST;
		if(st.get(variable) != null)
			return VariableStatus.BOUND;
		return VariableStatus.UNBOUND;
	}
	
	/**
	 * get the value of the specified variable after a search to find it in the whole hierarchy of symbol
	 * tables
	 * 
	 * @param variable
	 *            - the variable itself
	 * @return - the value, if found (can be null, if no value is associated with the name present in one of the symbol
	 *         tables), or null otherwise.
	 */
	public ClaimValue get(ClaimVariable variable)
	{
		for(SymbolTable st = this; st != null; st = st.prev)
		{
			ClaimValue found = (st.table.get(variable));
			if(found != null)
				return found;
		}
		return null;
	}
	
	/**
	 * returns true if the specified variable is present in the whole hierarchy of symbol
	 * tables and false otherwise
	 * 
	 * @param variable
	 *            - the name of the variable
	 * @return - returns true if the specified variable is present in the whole hierarchy of symbol
	 * tables and false otherwise
	 */
	public boolean containsSymbol(ClaimVariable variable)
	{
		for(SymbolTable st = this; st != null; st = st.prev)
		{
			boolean found = (st.table.containsKey(variable));
			if(found != false)
				return found;
		}
		return false;
	}
	
	/**
	 * Unbinds all the variables in the scope of one behavior. Useful after one execution of the behavior or before the following one. 
	 */
	public void clearSymbolTable()
	{
		table.clear();
		return;
	}
}
