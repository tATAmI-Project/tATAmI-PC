/*******************************************************************************
 * Copyright (C) 2015 Andrei Olaru, Marius-Tudor Benea, Nguyen Thi Thuy Nga, Amal El Fallah Seghrouchni, Cedric Herpson.
 * 
 * This file is part of tATAmI-PC.
 * 
 * tATAmI-PC is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * tATAmI-PC is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with tATAmI-PC.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package scenario.examples.sclaim_tatami2.simpleScenarioF;

import tatami.simulation.Boot;
import tatami.simulation.BootSettingsManager;

/**
 * Simple class running the scenario present in the same folder with it.
 * 
 * @author Andrei Olaru
 */
public class RunScenarioF
{
	/**
	 * Runs the file scenario.xml in the same directory as this class.
	 * 
	 * @param args
	 *            - not used.
	 */
	public static void main(String[] args)
	{
		String cp = RunScenarioF.class.getName();
		String scenarioPath = BootSettingsManager.SCENARIO_DIRECTORY
				+ cp.substring(0, cp.lastIndexOf(".")).replace(".", "/") + "/scenario.xml";
		Boot.main(new String[] { scenarioPath });
	}
}
