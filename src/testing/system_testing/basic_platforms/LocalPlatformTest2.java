package testing.system_testing.basic_platforms;

import tatami.simulation.Boot;

/**
 * Tests composite agents deployed on the Local Platform. One agent initiates pinging and the other replies, and so on.
 * <p>
 * Everything is expected to work without error.
 * 
 * @author Andrei Olaru
 */
public class LocalPlatformTest2
{
	/**
	 * The scenario.
	 */
	private static final String	SCENARIO	= "../simpleA/scenario-local-pingback";
	
	/**
	 * The main method.
	 * 
	 * @param args
	 *            - not used.
	 */
	public static void main(String[] args)
	{
		Boot.main(new String[] { BasicPlatformTest.DIR + SCENARIO + BasicPlatformTest.EXT });
	}
}
