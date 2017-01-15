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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.xqhs.util.logging.LoggerSimple.Level;
import net.xqhs.util.logging.UnitComponentExt;
import net.xqhs.util.logging.logging.Logging;
import tatami.core.util.platformUtils.PlatformUtils;
import tatami.simulation.simulation_manager_builders.SimulationManagerXMLBuilder;
import tatami.simulation.simulation_manager_builders.SimulationManagerXMLBuilder.AgentLoaderException;
import tatami.simulation.simulation_manager_builders.SimulationManagerXMLBuilder.PlatformException;

/**
 * The Boot singleton class manages the startup of the multi-agent system. It manages settings, it loads the scenario,
 * loads the agent definitions (agents are actually created later).
 * <p>
 * After performing all initializations, it creates a {@link SimulationManager} instance that manages the actual
 * simulation.
 * 
 * @author Andrei Olaru
 * @author Nguyen Thi Thuy Nga
 */
public class Boot
{
	/**
	 * The log of the class.
	 */
	static protected UnitComponentExt	log	= (UnitComponentExt) new UnitComponentExt().setUnitName("boot").setLoggerType(
											PlatformUtils.platformLogType());
	
	/**
	 * The method handling main functionality of {@link Boot}.
	 * <p>
	 * 
	 * @param args
	 *            - the arguments received by the program.
	 */
	public void boot(SimulationManagerXMLBuilder builder)
	{
		log.trace("Booting World.");
		
		Map<String, PlatformLoader> platforms = builder.getPlatform();
		
		Map<String, Set<String>> platformContainers = builder.getPlatformContainers();
		
		// agents prepared, time to start platforms and the containers.
		if(startPlatforms(platforms, platformContainers) > 0)
		{

			// start simulation
			if(!new SimulationManager(builder).start())
			{
				log.error("Simulation start failed.");
				for(PlatformLoader platform : platforms.values())
					if(!platform.stop())
						log.error("Stopping platform [" + platform.getName() + "] failed");
			}
		}
		else
			log.error("No agent platforms loaded. Simulation will not start.");
		log.doExit();
	}

	/**
	 * The method starts the platforms specified in the first parameter and adds to each platform the containers
	 * corresponding to it, as indicated by the second parameter.
	 * 
	 * @param platforms
	 *            - the {@link Map} of platform names and respective {@link PlatformLoader} instances.
	 * @param platformContainers
	 *            - the {@link Map} containing platform name &rarr; {@link Set} of the names of the containers to add to
	 *            the platform.
	 * @return the number of platforms successfully started.
	 */
	protected int startPlatforms(Map<String, PlatformLoader> platforms, Map<String, Set<String>> platformContainers)
	{
		int platformsOK = 0;
		for(Iterator<PlatformLoader> itP = platforms.values().iterator(); itP.hasNext();)
		{
			PlatformLoader platform = itP.next();
			String platformName = platform.getName();
			if(!platform.start())
			{
				log.error("Platform [" + platformName + "] failed to start.");
				itP.remove();
				continue;
			}
			log.info("Platform [" + platformName + "] started.");
			platformsOK++;
			if(platformContainers.containsKey(platformName))
				for(Iterator<String> itC = platformContainers.get(platformName).iterator(); itC.hasNext();)
				{
					String containerName = itC.next();
					if(!platform.addContainer(containerName))
					{
						log.error("Adding container [" + containerName + "] to [" + platformName + "] has failed.");
						itC.remove();
					}
					else
						log.info("Container [" + containerName + "] added to [" + platformName + "].");
				}
		}
		return platformsOK;
	}
	
	/**
	 * Main method. It calls {@link Boot#boot(String[])} with the arguments received by the program.
	 * 
	 * @param args
	 *            - the arguments received by the program.
	 */
	public static void main(String[] args)
	{
		Logging.getMasterLogging().setLogLevel(Level.ALL);
		
		SimulationManagerXMLBuilder builder = null;
		try{
		    builder = new SimulationManagerXMLBuilder(args);
		    builder.buildPlatform();
		    builder.buildAgentLoaders();
		}
		catch(SimulationManagerXMLBuilder.SimulationEzception exception){
		    log.error(exception.getMessage());
		    return;
		}
		catch(PlatformException exception){
		    log.warn(exception.getMessage());
		}
		catch(AgentLoaderException exception){
		    log.error(exception.getMessage());
		    return;
		}
		
		/*
		builder.buildAgentPackages();
		builder.buildContainerAgents();
		builder.buildTimeline();
		*/
		
		new Boot().boot(builder);
	}
}
