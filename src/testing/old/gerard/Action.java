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
package testing.gerard;

import java.util.ArrayList;

public class Action implements Node
{
	private ArrayList<Predicate> preConditions;
	private ArrayList<Predicate> deleteList;
	private ArrayList<Predicate> addList;
	private ActionType actionType;
	private BlocksWorld environment;
	private ArrayList<Block> parameters;
	
	public Action(ActionType actionType, ArrayList<Block> parameters, BlocksWorld environment)
	{
		preConditions = new ArrayList<Predicate>();
		deleteList = new ArrayList<Predicate>();
		addList = new ArrayList<Predicate>();
		this.actionType = actionType;
		this.environment = environment;
		this.parameters = parameters;
		switch (actionType) {
			case MOVE: 
				Block A = parameters.get(0);
				Block B = parameters.get(1);
				preConditions.add(new Predicate(A, PredicateType.CLEAR));
				preConditions.add(new Predicate(B, PredicateType.CLEAR));
				
				if(!(B instanceof Table)) deleteList.add(new Predicate(B, PredicateType.CLEAR));
				
				Block C = environment.getBlockBelow(A); 
				addList.add(new Predicate(C,PredicateType.CLEAR));
				addList.add(new Predicate(A,B,PredicateType.ON));
		}
	}

	public boolean preconditionsMet()
	{
		for(Predicate preCondition : preConditions)
		{
			if(!environment.containsPredicate(preCondition)) return false;
		}
		return true;
	}

	public ArrayList<Predicate >getDeleteList()
	{
		return deleteList;
	}
	
	public ArrayList<Predicate> getAddList()
	{
		return addList;
	}
	
	public String toString()
	{
		String toString = actionType.toString() + "(";
		for(int i = 0 ; i < parameters.size() ; i++)
		{
			toString += parameters.get(i);
			if(i != parameters.size() - 1)
			{
				toString += ",";
			}
			
		}
		toString += ")";
		/*
		toString += ")\nPRECONDITIONS:\t";
		for(Predicate preCondition : preConditions)
		{
			toString += preCondition + " ";
		}
		toString += "\nDELETE LIST: \t";
		for(Predicate deletePredicate : deleteList)
		{
			toString += deletePredicate + " ";
		}
		toString += "\nADD LIST: \t";
		for(Predicate addPredicate : addList)
		{
			toString += addPredicate + " ";
		}
		*/
		return toString;
	}
	
	public ArrayList<Block> getParameters()
	{
		return parameters;
	}
	
	public ActionType getType()
	{
		return actionType;
	}
}
