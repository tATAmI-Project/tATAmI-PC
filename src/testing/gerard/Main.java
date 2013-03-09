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
