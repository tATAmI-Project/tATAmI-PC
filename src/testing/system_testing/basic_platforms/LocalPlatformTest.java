package testing.system_testing.basic_platforms;

import tatami.simulation.Boot;

/**
 * Tests composite agents deployed on the Local Platform. Pings should be sent between the agents, immediately after
 * creation.
 * <p>
 * Everything is expected to work without error.
 * 
 * @author Andrei Olaru
 */
public class LocalPlatformTest
{
	/**
	 * The scenario.
	 */
	private static final String	SCENARIO	= "../simpleA/scenario-local-platform";
	
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
