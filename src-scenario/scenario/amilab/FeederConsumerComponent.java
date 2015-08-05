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
package scenario.amilab;

import tatami.amilab.AmILabComponent;
import tatami.core.agent.AgentEvent;

/**
 * Tester for {@link AmILabComponent}.
 * 
 * @author Claudiu-Mihai Toma
 */
public class FeederConsumerComponent extends AmILabComponent
{

	/**
	 * The serial UID.
	 */
	private static final long serialVersionUID = 7753773976224362270L;

	/**
	 * Number of useless entries.
	 */
	private static final int USELESS_DATA_COUNT = 5000;

	/**
	 * Useless data.
	 */
	private static final String USELESS_DATA = "useless data";

	/**
	 * Depth data.
	 */
	private static final String DEPTH_DATA = "image_depth data";

	/**
	 * Waiting time.
	 */
	private static final long WAIT = 5000;

	@Override
	protected void atSimulationStart(AgentEvent event)
	{
		super.atSimulationStart(event);

		clearQueue();

		for (int i = 0; i < USELESS_DATA_COUNT; i++)
		{
			set(USELESS_DATA);
		}

		set(DEPTH_DATA);

		System.out.println(get(AmILabDataType.IMAGE_DEPTH, WAIT));
	}
}
