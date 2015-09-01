package scenario.examples.sclaim_tatami2.simpleScenarioE;

import tatami.simulation.Boot;
import tatami.simulation.BootSettingsManager;

/**
 * Simple class running the scenario present in the same folder with it.
 * 
 * @author Andrei Olaru
 */
public class RunScenarioE
{
	/**
	 * Runs the file scenario.xml in the same directory as this class.
	 * 
	 * @param args
	 *            - not used.
	 */
	public static void main(String[] args)
	{
		String cp = RunScenarioE.class.getName();
		String scenarioPath = BootSettingsManager.SCENARIO_DIRECTORY
				+ cp.substring(0, cp.lastIndexOf(".")).replace(".", "/") + "/scenarioE-tATAmI2.xml";
		Boot.main(new String[] { scenarioPath });
	}
}
