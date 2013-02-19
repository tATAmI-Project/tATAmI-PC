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
