package testing.system_testing.basic_platforms;

import tatami.simulation.Boot;
import tatami.simulation.BootSettingsManager;

/**
 * Tester for several cases of incompletely (not necessarily incorrectly) specified scenarios.
 * <p>
 * Use {@link #SCENARIO_INDEX} to select among test scenarios from {@link #SCENARIOS}.
 * 
 * @author Andrei Olaru
 */
public class BasicPlatformTest
{
	/**
	 * Scenarios to pick from using {@link #SCENARIO_INDEX}.
	 */
	public final static String[]	SCENARIOS		= {
													
													"scenario-syntax-error", // XML error is expected
			
			"scenario-no-initial", // messaging component error is expected
			
			"scenario-no-containers", // XML error is expected
			
			"scenario-empty-container", // messaging component error is expected
			
			"scenario-empty-agents", // messaging component error is expected; no agents.
			
			"scenario-empty-named-agents",// messaging component error is expected; agents, no GUI.
			
													};
	
	/**
	 * Directory for scenario files.
	 */
	public final static String		DIR				= BootSettingsManager.SCENARIO_DIRECTORY
															+ "scenario/examples/composite/platform/";
	/**
	 * Extension for scenario files.
	 */
	public final static String		EXT				= ".xml";
	
	/**
	 * Index of the scenario to test from {@link #SCENARIOS}.
	 */
	public final static int			SCENARIO_INDEX	= SCENARIOS.length - 1;
	
	/**
	 * Main method.
	 * 
	 * @param args
	 *            - not used.
	 */
	public static void main(String[] args)
	{
		Boot.main(new String[] { DIR + SCENARIOS[SCENARIO_INDEX] + EXT });
	}
}
