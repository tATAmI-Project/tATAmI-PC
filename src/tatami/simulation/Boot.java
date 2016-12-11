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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.xqhs.util.XML.XMLTree;
import net.xqhs.util.XML.XMLTree.XMLNode;
import net.xqhs.util.config.Config.ConfigLockedException;
import net.xqhs.util.logging.LoggerSimple.Level;
import net.xqhs.util.logging.UnitComponentExt;
import net.xqhs.util.logging.logging.Logging;
import net.xqhs.windowLayout.WindowLayout;
import net.xqhs.windowLayout.grid.GridWindowLayout;
import tatami.core.agent.parametric.AgentParameterName;
import tatami.core.agent.parametric.AgentParameters;
import tatami.core.util.platformUtils.PlatformUtils;
import tatami.simulation.AgentLoader.StandardAgentLoaderType;
import tatami.simulation.PlatformLoader.StandardPlatformType;
import tatami.simulation.simulation_manager_builders.SimulationManagerXMLBuilder;

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
	protected UnitComponentExt	log	= (UnitComponentExt) new UnitComponentExt().setUnitName("boot").setLoggerType(
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
		
		// create window layout
		WindowLayout.staticLayout = new GridWindowLayout(BootSettingsManager.getInst().getLayout());
		
		Map<String, PlatformLoader> platforms = builder.getPlatform();
		
		Map<String, Set<String>> platformContainers = builder.getPlatformContainers();
		
		
		// agents prepared, time to start platforms and the containers.
		if(startPlatforms(platforms, platformContainers) > 0)
		{
			
		    Map<String, Boolean> allContainers = builder.getAllContainers();
		    
		    Set<AgentCreationData> allAgents = builder.getAllAgents();
		    
		    XMLNode timeline = builder.getTimeline();
		    
			
			// start simulation
			if(!new SimulationManager(platforms, allContainers, allAgents, timeline).start())
			{
				log.error("Simulation start failed.");
				for(PlatformLoader platform : platforms.values())
					if(!platform.stop())
						log.error("Stopping platform [" + platform.getName() + "] failed");
				if(WindowLayout.staticLayout != null)
					WindowLayout.staticLayout.doexit();
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
		
		SimulationManagerXMLBuilder builder = new SimulationManagerXMLBuilder(args);
		builder.buildPlatform();
		builder.buildAgentLoaders();
		builder.buildAgentPackages();
		builder.buildContainerAgents();
		builder.buildTimeline();
		
		new Boot().boot(builder);
	}
}
