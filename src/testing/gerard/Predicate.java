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
