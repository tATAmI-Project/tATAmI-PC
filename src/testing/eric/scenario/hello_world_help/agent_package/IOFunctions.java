package testing.eric.scenario.hello_world_help.agent_package;

import java.util.Vector;

import tatami.core.agent.claim.parser.ClaimValue;

public class IOFunctions
{
	public static boolean echo(Vector<ClaimValue> arguments)
	{
		if (arguments != null)
		{
			for (ClaimValue value : arguments)
			{
				System.out.println((String)value.getValue());
			}
		}
		return true;
	}
}
