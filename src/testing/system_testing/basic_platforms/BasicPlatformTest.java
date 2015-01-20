package testing.system_testing.basic_platforms;

import tatami.simulation.Boot;

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
													
													"scenario-syntax-error",
													
													"scenario-no-initial",
													
													"scenario-no-containers",
													
													"scenario-empty-container",
													
													"scenario-empty-agents",
													
													"scenario-empty-named-agents",
													
													};
	
	/**
	 * Directory for scenario files.
	 */
	public final static String		DIR				= "src-scenario/scenario/examples/composite/platform/";
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
	 * @param args - not used.
	 */
	public static void main(String[] args)
	{
		Boot.main(new String[] { DIR + SCENARIOS[SCENARIO_INDEX] + EXT });
	}
}
