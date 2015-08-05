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
import tatami.amilab.AmILabComponent.AmILabDataType;
import tatami.core.agent.AgentComponent;
import tatami.core.agent.AgentEvent;

/**
 * @author Claudiu-Mihai Toma
 *
 */
public class AmILabControllerComponent extends AgentComponent
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5909313431564468753L;

	/**
	 * Number of useless entries.
	 */
	private static final int USELESS_DATA_COUNT = 2000;

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
	private static final long WAIT = 3000;

	/**
	 * 
	 */
	public AmILabControllerComponent()
	{
		super(AgentComponentName.TESTING_COMPONENT);
	}

	@Override
	protected void atSimulationStart(AgentEvent event)
	{
		super.atSimulationStart(event);

		AmILabComponent amilab = (AmILabComponent) getAgentComponent(AgentComponentName.AMILAB_COMPONENT);
		amilab.clearQueue();
		amilab.startInternalBuffer();

		for (int i = 0; i < USELESS_DATA_COUNT; i++)
		{
			amilab.set(USELESS_DATA);
			// amilab.set(DEPTH_DATA);
		}

		amilab.set(DEPTH_DATA);

		long startingTime = System.currentTimeMillis();
		System.out.println(amilab.get(AmILabDataType.IMAGE_DEPTH, WAIT));
		long endingTime = System.currentTimeMillis();
		System.out.println(endingTime - startingTime);

		System.out.println(amilab.isInternalThreadAlive());
		amilab.stopInternalBuffer();
		try
		{
			Thread.sleep(10);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// FIXME: This sometimes returns true.
		System.out.println(amilab.isInternalThreadAlive());
	}
}
