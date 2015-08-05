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
package tatami.jade;

import jade.core.Agent;
import net.xqhs.util.logging.LoggerSimple.Level;
import net.xqhs.util.logging.Unit;
import net.xqhs.util.logging.UnitComponentExt;
import tatami.core.util.platformUtils.PlatformUtils;
import tatami.simulation.AgentManager;
import tatami.simulation.PlatformLoader.PlatformLink;

/**
 * A wrapper for an {@link AgentManager} instance, that wraps it inside a Jade {@link Agent}.
 * <p>
 * When started as a Jade agent, the wrapper is supposed to receive one argument: the {@link AgentManager} instance.
 * 
 * @author Andrei Olaru
 */
public class JadeAgentWrapper extends Agent implements PlatformLink
{
	/**
	 * The serial UID.
	 */
	private static final long	serialVersionUID	= 3762116064764191232L;
	/**
	 * The log.
	 */
	UnitComponentExt			log;
	/**
	 * The wrapped agent.
	 */
	AgentManager				agent;
	
	@Override
	protected void setup()
	{
		super.setup();
		
		log = (UnitComponentExt) new UnitComponentExt().setUnitName(Unit.DEFAULT_UNIT_NAME).setLogLevel(Level.ALL)
				.setLoggerType(PlatformUtils.platformLogType());
		
		Object[] args = getArguments();
		if(args.length < 2)
		{
			log.error("Not enough arguments.");
			doExit();
		}
		try
		{
			agent = (AgentManager) args[0];
		} catch(ClassCastException e)
		{
			log.error("Agent argument not correct: " + PlatformUtils.printException(e));
			doExit();
		}
		if(agent.getAgentName() != null)
			log.setUnitName(agent.getAgentName() + "-JadeWrapper");
		log.info("Wrapper is up.");
		
		if(!agent.setPlatformLink(this))
			log.error("Setting platform link failed");
		
		Object lock = args[1];
		synchronized(lock)
		{
			lock.notifyAll(); // notify the setup is completed
		}
	}
	
	/**
	 * Performs the exit procedure for the wrapper, together with the wrapped agent.
	 */
	protected void doExit()
	{
		if(agent != null)
		{
			agent.stop();
			agent = null;
		}
		if(log != null)
			log.doExit();
		doDelete();
	}
}
