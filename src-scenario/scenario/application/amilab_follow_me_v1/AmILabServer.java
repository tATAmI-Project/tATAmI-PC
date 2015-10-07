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
package scenario.application.amilab_follow_me_v1;

import java.util.TimerTask;

import tatami.core.agent.AgentEvent;

/**
 * 
 * The server is a special kind of client that is capable of sending periodic requests for proximity to all the clients.
 * 
 * @author Claudiu-Mihai Toma
 *
 */
public class AmILabServer extends AmILabClient
{

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1406553420405779596L;

	/**
	 * How often to send pings.
	 */
	private static final int PING_PERIOD = 500;

	/**
	 * Send requests to all clients.
	 */
	protected void sendRequests()
	{
		gui.sendRequests();
	}

	@Override
	protected void atSimulationStart(AgentEvent event)
	{
		super.atSimulationStart(event);

		// Periodically send pings.
		timer.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				sendRequests();
			}
		}, NO_DELAY, PING_PERIOD);
	}
}
