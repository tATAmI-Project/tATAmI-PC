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

public class Predicate implements Node {
	
	private PredicateType type;
	private ArrayList<Block> blocks;
	
	public Predicate(String name)
	{
		 
	}
	
	public Predicate(Block block, PredicateType function){
		if(function == PredicateType.CLEAR)
		{
			type = function;
			blocks = new ArrayList<Block>();
			blocks.add(block);
		}
	}
	
	public Predicate(Block block1, Block block2, PredicateType function)
	{
		if(function == PredicateType.ON)
		{
			type = function;
			blocks = new ArrayList<Block>();
			blocks.add(block1);
			blocks.add(block2);
		}
	}
	
	public boolean equals(Object o)
	{
		if(o instanceof Predicate)
		{
			Predicate that = (Predicate)o;
			if(this.getType() == that.getType())
			{
				return this.getBlocks().equals(that.getBlocks());
			}
			else return false;
		}
		else return false;
	}
	
	public PredicateType getType()
	{
		return type;
	}
	
	public ArrayList<Block> getBlocks(){
		return blocks;
	}

	public String toString()
	{
		String toString = type.toString() + "(";
		for(int i = 0 ; i < blocks.size() ; i++)
		{
			toString += blocks.get(i).getName();
			if(i != blocks.size() - 1) toString += ",";
			else toString+= ") ";
		}
		return toString;
	}

}
