package testing.system_testing.composite_agent;

import tatami.simulation.Boot;
import testing.system_testing.basic_platform.BasicPlatformTest;

/**
 * Tests two composite agents on the default platform, with no messaging components.
 * 
 * @author Andrei Olaru
 */
public class CompositeAgentDefaultPlatformTest
{
	/**
	 * The scenario.
	 */
	private static final String	SCENARIO	= "../simpleA/scenario.xml";

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
