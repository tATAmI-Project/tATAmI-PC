package testing.andrei.agent_component_tests;

import net.xqhs.util.logging.Unit;

public class ParametricComponentTest extends Unit
{
	public ParametricComponentTest()
	{
		setUnitName("parametric component tester");
		li("starting...");
		li("done.");
		doExit();
	}
	
	public static void main(String args[])
	{
		new ParametricComponentTest();
	}
}
