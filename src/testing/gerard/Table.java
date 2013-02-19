package testing.gerard;

import java.util.ArrayList;

public class Table extends Block
{
	
	public Table()
	{
		super("Table");
	}
	
	public boolean isClear()
	{
		return true;
	}
	
	public boolean move()
	{
		return false;
	}
	
	public String toString()
	{
		String toString = "Table\n";
		
		return toString;
	}
}
