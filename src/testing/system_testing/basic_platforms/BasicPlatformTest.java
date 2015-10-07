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
