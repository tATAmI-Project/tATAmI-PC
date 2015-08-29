package scenario.examples.sclaim_tatami2.simpleScenarioE;

import tatami.simulation.Boot;
import tatami.simulation.BootSettingsManager;

/**
 * Simple class running the scenario present in the same folder with it.
 * 
 * @author Andrei Olaru
 */
public class RunThisScenario
{
	/**
	 * Runs the file scenario.xml in the same directory as this class.
	 * 
	 * @param args
	 *            - not used.
	 */
	public static void main(String[] args)
	{
		String cp = RunThisScenario.class.getName();
		String scenarioPath = BootSettingsManager.SCENARIO_DIRECTORY
				+ cp.substring(0, cp.lastIndexOf(".")).replace(".", "/") + "/scenario.xml";
		Boot.main(new String[] { scenarioPath });
	}
}
