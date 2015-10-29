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

import tatami.pc.util.windowLayout.LayoutIndications;
import tatami.pc.util.windowLayout.LayoutIndications.BarPosition;

class BootSettings
{
	final String		scenarioFileName	= "scenario/examples/simpleScenarioF/scenario-distributed.xml";
	// final String scenarioFileName = "scenario/examples/simpleScenarioE/scenario-distributed.xml";
	// final String scenarioFileName = "scenario/examples/debateScenario-android/scenario.xml";
	// final String scenarioFileName = "scenario/2011/phase2-android/scenario_new_schema.xml";
	
	final int			windowLayoutWidth	= 1530;
	final int			windowLayoutHeight	= 600;
	
	LayoutIndications	layout				= new LayoutIndications(12, 6)
											
											.indicateBar(BarPosition.LEFT, 70, 0) // Taskbar on the left (70 pixels)
													
													// .indicateBar(BarPosition.LEFT, 0, 0) // otherwise
													
													.indicateWindowType("agent", 3, 2)
													
													.indicateWindowType("system", 6, 3)
													
													.indicateWindowType("systemSmall", 6, 1)
													
													.indicateWindowType("screen", 3, 4)
													
													.indicateReservation("system", "visualizer", 0, 0);
	
}
