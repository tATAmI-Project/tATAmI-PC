package testing.system_testing.basic_platforms;

import tatami.simulation.Boot;

public class GeneralPlatformTest {

	/**
	 * The scenario.
	 */
	private static final String	SCENARIO	= "../../centralized/scenario-communication-empty-named-agents.xml";

	/**
	 * The main method.
	 * 
	 * @param args - not used.
	 */
	public static void main(String[] args)
	{
		Boot.main(new String[] {BasicPlatformTest.DIR + SCENARIO + BasicPlatformTest.EXT});
	}
}
