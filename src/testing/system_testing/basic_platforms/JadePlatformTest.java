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

import net.xqhs.util.logging.LoggerSimple.Level;
import net.xqhs.util.logging.logging.Logging;
import tatami.simulation.Boot;

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
	public static final String	SCENARIO	= "src-scenario/scenario/examples/jade/simpleA/scenario"
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
