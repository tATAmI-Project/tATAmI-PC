package testing.gerard;

public class Block
{
	
	private String name;
	
	public Block(String name)
	{
		this.name = name;
	}
	
	public String toString()
	{
		return name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public boolean equals(Object o)
	{
		if(o instanceof Block)
		{
			Block that = (Block)o;
			if(that.getName().equals(this.getName()))
			{
				return true;
			}
			else return false;
		}
		else return false;
	}
}
