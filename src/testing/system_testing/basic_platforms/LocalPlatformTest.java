package testing.system_testing.basic_platforms;

import tatami.simulation.Boot;

/**
 * Tests composite agents deployed on the Local Platform.
 * 
 * @author Andrei Olaru
 */
public class LocalPlatformTest
{
	/**
	 * The main method.
	 * 
	 * @param args - not used.
	 */
	public static void main(String[] args)
	{
		Boot.main(new String[] {"src-scenario/scenario/examples/composite/simpleA/scenario-local-platform.xml"});
	}
}
