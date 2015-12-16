/*******************************************************************************
 * Copyright (C) 2013 Andrei Olaru, Marius-Tudor Benea, Nguyen Thi Thuy Nga, Amal El Fallah Seghrouchni, Cedric Herpson.
 * 
 * This file is part of tATAmI-PC.
 * 
 * tATAmI-PC is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * tATAmI-PC is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with tATAmI-PC.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package tatami.simulation;

import net.xqhs.windowLayout.LayoutIndications.BarPosition;
import net.xqhs.windowLayout.grid.GridLayoutIndications;
import scenario.application.amilab_follow_me_v1.PC.AmILabGui;
import tatami.core.agent.visualization.AgentGuiConfig;

/**
 * This class traditionally contains settings for simulations, in order to decouple it from Boot (making it easy to
 * ignore changes in BootSettings when committing).
 * <p>
 * The functionality of this class was initially offered under the name of BootSettings.
 * 
 * @author Andrei Olaru
 */
public class BootDefaultArguments
{
	/**
	 * The scenario file.
	 */
	static final String scenarioFileName = BootSettingsManager.SCENARIO_DIRECTORY + "scenario/examples/sclaim_tatami2/simpleScenarioE/scenarioE-tATAmI2.xml";
//	static final String scenarioFileName = BootSettingsManager.SCENARIO_DIRECTORY + "scenario/examples/sclaim_tatami2/simpleScenarioE/scenarioE-tATAmI2-plus.xml";
	/**
	 * Main (Jade) host.
	 */
	static final String	mainHost			= null;
	/**
	 * Main (Jade) port.
	 */
	static final String	mainPort			= null;
	/**
	 * Local host.
	 */
	static final String	localHost			= null;
	/**
	 * Local port.
	 */
	static final String	localPort			= null;
	/**
	 * Main container name.
	 */
	static final String	localContainerName	= null;

	/**
	 * The width of the application space.
	 */
	static final int applicationLayoutWidth = 1200;

	/**
	 * The height of the application space.
	 */
	static final int applicationLayoutHeight = 600;

	/**
	 * The layout indications for the PC application.
	 */
	static GridLayoutIndications layout = (GridLayoutIndications) new GridLayoutIndications(15, 10)

	// .indicateBar(BarPosition.LEFT, 0, 0) // otherwise

	.indicateWindow(AgentGuiConfig.DEFAULT_WINDOW_TYPE, 5, 3)

	.indicateWindow(SimulationManager.WINDOW_TYPE, 10, 4)

	.indicateWindowPosition(SimulationManager.WINDOW_NAME, 0, 0)
	
	.indicateWindow(AmILabGui.WINDOW_TYPE, 10, 6)

	.indicateBar(BarPosition.LEFT, 100, 0)

	.indicatePositionY(100)

	.indicateW(applicationLayoutWidth)

	.indicateH(applicationLayoutHeight)

	;

}
