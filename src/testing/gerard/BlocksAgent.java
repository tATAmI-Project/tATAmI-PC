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

import javax.swing.JApplet;



public class BlocksAgent{
	
	private ArrayList<Predicate> goalbase;
	private ArrayList<String> knowledgebase;
	private ArrayList<ActionType> actions;
	private BlocksWorld environment;
	
	
	public BlocksAgent(BlocksWorld env, ArrayList<ActionType> actions)
	{
		this.actions = actions;
		goalbase = new ArrayList<Predicate>();
		knowledgebase = new ArrayList<String>();
		environment = env;
		
		System.out.println("Create the PlanGraph:");
		System.out.println();
		
		
	}
	
	public void addGoal(Predicate g)
	{
		goalbase.add(g);
	}
	
	public void removeGoal(Predicate g)
	{
		goalbase.remove(g);
	}
	
	public void addKnowledge(String fact)
	{
		knowledgebase.add(fact);
	}
	
	public void removeKnowledge(String fact)
	{
		knowledgebase.remove(fact);
	}
	
	public void move(Block A, Block B)
	{
		ArrayList<Block> parameters = new ArrayList<Block>();
		parameters.add(A);
		parameters.add(B);
		for(ActionType actionType : actions)
		{
			Action a = new Action(actionType, parameters, environment);
			System.out.println("TRYING TO EXECUTE A " + a);
			if(a.preconditionsMet())
			{
				environment.executeAction(a);
			}
			else System.out.println("PRECONDITIONS DONT MEET");
		}
	}
		
	/**
     * Create a graph with node objects.
     *
     * @return a graph based on Node objects.
     */

    
    
}
