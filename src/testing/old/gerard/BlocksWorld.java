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

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class BlocksWorld
{
	
	private ArrayList<Block> blocks;
	private ArrayList<Predicate> predicates;
	private BlocksAgent theAgent;
	
	private Block[][] blocksWorldView;
	
	public BlocksWorld()
	{
		init();
		createAgent();
		test();
	}
	
	public BlocksWorld(String blocksWorldFile)
	{
		init();
		createBlocksWorldFromFile(blocksWorldFile);
		createAgent();
		test();
	}
	
	public void init()
	{
		blocksWorldView = new Block[10][10];
		blocks = new ArrayList<Block>(); 
		predicates = new ArrayList<Predicate>();
	}
	
	public void addBlock(Block b)
	{
		blocks.add(b);
	}
	
	public void createBlocksWorldFromFile(String fileName)
	{
		try 
		{
			//use buffering, reading one line at a time
			//FileReader always assumes default encoding is OK!
			System.out.println(fileName);
			BufferedReader input =  new BufferedReader(new FileReader(fileName));		   

			try 
			{
				String line = null; //not declared within while loop
				int lineNumber = 0;
				HashMap<Integer, Block> previousBlockToColumnMap = new HashMap<Integer, Block>();
				HashMap<Integer, Block> currentBlockToColumnMap = new HashMap<Integer, Block>();
				
				while (( line = input.readLine()) != null)
				{
					for (int column = 0; column < line.length(); column++)
					{
						char c = line.charAt(column);
						if(c != ' ')
						{
							Block block = new Block(c + "");
							addPredicate(new Predicate(block, PredicateType.CLEAR));
							blocksWorldView[lineNumber + 3][column + 3] = block;
							blocks.add(block);
							currentBlockToColumnMap.put(column, block);
							if(previousBlockToColumnMap.get(column) != null)
							{
								
								Block topBlock = (Block) previousBlockToColumnMap.get(column);
								removePredicate(new Predicate(block, PredicateType.CLEAR));
								addPredicate(new Predicate(topBlock, block, PredicateType.ON));
							}
						}
					}
					//Lowest blocks should all be set on table
					previousBlockToColumnMap = (HashMap<Integer,Block>)currentBlockToColumnMap.clone();
					lineNumber++;
				}
				Table table = new Table();
				blocks.add(table);
				
				//Create a collection of the last HashMap containing the last line of blocks.
				java.util.Collection<Block> c = currentBlockToColumnMap.values();
			    //obtain an Iterator for Collection
			    Iterator<Block> itr = c.iterator();
			 
			    //iterate through HashMap values iterator
			    while(itr.hasNext()) //The last line of the text files should all be on top of the table.
			    {
			    	Block b = (Block)itr.next();
			    	addPredicate(new Predicate(b,table,PredicateType.ON)); //Add predicate that the block is on the table
			    }
			    addPredicate(new Predicate(table,PredicateType.CLEAR)); //Table is always clear
			}
			finally 
			{
				input.close();
			}
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}
	
	private void test()
	{
		System.out.println(this);
		Block D = getBlock("D");
		Block B = getBlock("B");
		theAgent.move(D,B);
		System.out.println(this);
		
		theAgent.move(getBlock("C"), D);
		
		System.out.println(this);
		
	}
	
	private void createAgent()
	{
		ArrayList<ActionType> actionRepertoire = new ArrayList<ActionType>();
		actionRepertoire.add(ActionType.MOVE);
		
		theAgent = new BlocksAgent(this, actionRepertoire);
		

		
		
	}
	
	private void removePredicate(Predicate predicate)
	{
		predicates.remove(predicate);
	}
	
	public String toString()
	{
		String toString = "This blockworlds contains " + blocks.size() + " blocks:\n";
		for(int i = 0 ; i < blocksWorldView.length ; i++)
		{
			for(int j = 0 ; j < blocksWorldView[i].length ; j++)
			{
				Block b = blocksWorldView[i][j];
				if(b != null) toString += b + " ";
			}
			toString += "\n";
		}
		toString += "The predicates are: \t";
		for(Predicate p : predicates)
		{
			toString += p.toString();
		}		
		return toString;
	}
	
	public void addPredicate(Predicate newPredicate)
	{
		if(!predicates.contains(newPredicate))
		{
			predicates.add(newPredicate);
		}
	}
	
	public boolean On(Block a, Block b)
	{
		if(a != null && b !=null)
		{
			Predicate onPredicate = new Predicate(a,b,PredicateType.ON);
			return containsPredicate(onPredicate);
		}
		else return false;
	}
	
	public boolean Clear(Block block)
	{
		if(block != null)
		{
			Predicate clearPredicate = new Predicate(block, PredicateType.CLEAR);
			return containsPredicate(clearPredicate);
		}
		else return false;
	}
	
	/*
	public boolean Move(Block a, Block b)
	{
		if(a != null && b !=null)
		{
			Predicate aClear = new Predicate(a,PredicateType.CLEAR);
			Predicate bClear = new Predicate(b,PredicateType.CLEAR); 
			if(predicates.contains(aClear) && predicates.contains(bClear))
			{
				for(Predicate toRemovePredicate : predicates) //Remove the predicate (if any exists) that A is on some other block C 
				{
					if(toRemovePredicate.getType().equals(PredicateType.ON) && toRemovePredicate.getBlocks().get(0).equals(a)) //Is it a ON predicate with A as first variable? - ON(A,C)
					{
						Block c = toRemovePredicate.getBlocks().get(1);
						addPredicate(new Predicate(c,PredicateType.CLEAR)); //add CLEAR(C);
						removePredicate(toRemovePredicate); //Remove ON(A,C) for some C
						return true;
					}
				}
				if(!(bClear.getBlocks().get(1) instanceof Table)) predicates.remove(bClear);
			}
		}
		return false;
	}
	*/
	
	public boolean containsPredicate(Predicate thisPredicate)
	{
		return predicates.contains(thisPredicate);
	}
	
	public Block getBlockBelow(Block thisBlock)
	{
		for(Predicate p : predicates)
		{
			if(p.getType().equals(PredicateType.ON) && p.getBlocks().get(0).equals(thisBlock))
			{
				return p.getBlocks().get(1);
			}
		}
		return null;
	}
	
	public Block getBlockOnTopOf(Block thisBlock)
	{
		for(Predicate p : predicates)
		{
			if(p.getType().equals(PredicateType.ON) && p.getBlocks().get(1).equals(thisBlock))
			{
				return p.getBlocks().get(0);
			}
		}
		return null;
	}
	
	public void executeAction(Action a)
	{
		//System.out.println("EXECUTING ACTION " + a);
		
		updateBlocksWorldView(a);
		
		predicates.removeAll(a.getDeleteList());
		predicates.addAll(a.getAddList());
	}
	
	private void updateBlocksWorldView(Action a)
	{
		switch (a.getType()) 
		{
			case MOVE:
				Block A = a.getParameters().get(0);
				Block B = a.getParameters().get(1);
				Point Acoord = getLocationOfBlockInWorldView(A);
				Point Bcoord = getLocationOfBlockInWorldView(B);
				blocksWorldView[Bcoord.y - 1][Bcoord.x] = A;
				blocksWorldView[Acoord.y][Acoord.x] = null;
		}
	}
	
	public Point getLocationOfBlockInWorldView(Block thisBlock)
	{
		for(int i = 0 ; i < blocksWorldView.length ; i++)
		{
			for(int j = 0 ; j < blocksWorldView[i].length ; j++)
			{
				Block b = blocksWorldView[i][j];
				if(b != null && b.equals(thisBlock)) return new Point(j,i);
			}
		}
		return new Point(-1,-1);
	}

	public Block getBlock(String name)
	{
		for(Block b: blocks)
		{
			if (b.getName().equals(name))
			{
				return b;
			}
		}
		return null;
	}
	
	
}
