package testing.system_testing.basic_platforms;

import tatami.simulation.Boot;

/**
 * Tests composite agents deployed on the Jade Platform.
 * 
 * @author Andrei Olaru
 */
public class JadePlatformTest
{
	/**
	 * Use the scenario in the variant where it has a config element containing host and port information.
	 */
	public static final boolean	USE_CONFIG	= true;
	/**
	 * Scenario file to use.
	 */
	public static final String	SCENARIO	= "src-scenario/scenario/examples/jade/simpleA/scenario"
													+ (USE_CONFIG ? "-with-config" : "") + ".xml";
	
	/**
	 * The main method.
	 * 
	 * @param args
	 *            - not used.
	 */
	public static void main(String[] args)
	{
		Boot.main(new String[] { SCENARIO });
	}
}