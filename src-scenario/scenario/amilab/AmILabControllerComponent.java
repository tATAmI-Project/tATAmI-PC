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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import tatami.amilab.AmILabBuffer;
import tatami.amilab.AmILabBuffer.LimitType;
import tatami.amilab.AmILabComponent;
import tatami.amilab.AmILabComponent.AmILabDataType;
import tatami.amilab.NotificationTarget;
import tatami.amilab.Perception;
import tatami.core.agent.AgentComponent;
import tatami.core.agent.AgentEvent;

/**
 * @author Claudiu-Mihai Toma
 *
 */
public class AmILabControllerComponent extends AgentComponent
{
	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 5909313431564468753L;

	/**
	 * Testing limit.
	 */
	private static final long LIMIT = 30 * 1000;

	/**
	 * Default constructor.
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

		// Perception perception = null;
		//
		// perception = amilab.get(AmILabDataType.IMAGE_DEPTH);
		// System.out.println(perception.getType() + " " + perception.getTimestamp());
		//
		// amilab.startInternalBuffer();
		//
		// perception = amilab.get(AmILabDataType.IMAGE_DEPTH);
		// System.out.println(perception.getType() + " " + perception.getTimestamp());

		List<AmILabDataType> types = new ArrayList<AmILabDataType>();
		types.add(AmILabDataType.IMAGE_DEPTH);

		NotificationTarget notificator = new NotificationTarget()
		{
			@Override
			public void notify(Map<AmILabDataType, ConcurrentLinkedQueue<Perception>> bufferResult)
			{
				// TODO: Do what?
			}
		};

		AmILabBuffer buffer = amilab.startBuffer(types, LimitType.TIME, LIMIT, notificator);

		// TODO: Wait for what?
		// while (!ready);

		while (buffer.getTotalSize() != 0)
		{
			Perception perception = buffer.getElement(AmILabDataType.IMAGE_DEPTH);
			System.out.println("testing: " + perception.getType() + " " + perception.getTimestamp());
		}
	}
}
