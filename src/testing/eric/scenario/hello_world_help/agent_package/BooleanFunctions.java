package testing.eric.scenario.hello_world_help.agent_package;

import java.util.Vector;

import tatami.core.agent.claim.parser.ClaimValue;

public class BooleanFunctions
{
	public static boolean not(Vector<ClaimValue> arguments)
	{
		if(((String)arguments.get(0).getValue()).equals("true"))
			return false;
		else
			return true;
	}
	
	public static boolean isDifferent(Vector<ClaimValue> arguments)
	{
		String s1 = (String)arguments.get(0).getValue();
		String s2 = (String)arguments.get(1).getValue();
		return !s1.equals(s2);
	}
}
