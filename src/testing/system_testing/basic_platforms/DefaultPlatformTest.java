package testing.system_testing.basic_platforms;

import tatami.simulation.Boot;

/**
 * Tests two composite agents on the default platform, with no messaging components.
 * 
 * @author Andrei Olaru
 */
public class DefaultPlatformTest
{
	/**
	 * The scenario.
	 */
	private static final String	SCENARIO	= "../simpleA/scenario";

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
