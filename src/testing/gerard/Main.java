package testing.gerard;

public class Main
{
	public static void main(String[] args)
	{
		//Create blocks

		//createBlocks();
		
		BlocksWorld test = new BlocksWorld("src/testing/gerard/blocks_world.txt");
		
		
		Block A = test.getBlock("A");
		Block B = test.getBlock("B");
		Block C = test.getBlock("C");
		Block D = test.getBlock("D");
		Block T = test.getBlock("Table");
		
		//Test some predicates
		/*
		System.out.println("Clear(A):\t" + test.Clear(A));
		
		System.out.println("Clear(A):\t" + test.Clear(A));
		System.out.println("On(B,A):\t" + test.On(B,A));
		System.out.println("On(D,C):\t" + test.On(D,C));
		*/
	}	
}
