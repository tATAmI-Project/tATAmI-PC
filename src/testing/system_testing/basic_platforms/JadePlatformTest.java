package testing.system_testing.basic_platforms;

import net.xqhs.util.logging.LoggerSimple.Level;
import net.xqhs.util.logging.logging.Logging;
import tatami.simulation.Boot;
import tatami.simulation.BootSettingsManager;

/**
 * Tests composite agents deployed on the Jade Platform.
 * <p>
 * Everything is expected to work without error.
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
	public static final String	SCENARIO	= BootSettingsManager.SCENARIO_DIRECTORY
													+ "scenario/examples/jade/simpleA/scenario"
													+ (USE_CONFIG ? "-with-config" : "") + ".xml";
	
	/**
	 * The main method.
	 * 
	 * @param args
	 *            - not used.
	 */
	public static void main(String[] args)
	{
		Logging.getMasterLogging().setLogLevel(Level.ALL);
		Boot.main(new String[] { SCENARIO });
	}
}
