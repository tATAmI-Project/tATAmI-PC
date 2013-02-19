package testing.gerard;

public class Goal {
	
	private String state;
	
	public Goal(String g)
	{
		state = g;
	}

	
	public boolean equals(Object o)
	{
		if(o instanceof Goal)
		{
			Goal that = (Goal)o;
			return this.state.equals(that.getState());
		}
		else return false;
	}
	
	public String getState()
	{
		return state;
	}

}
