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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.xqhs.util.XML.XMLTree;
import net.xqhs.util.XML.XMLTree.XMLNode;
import net.xqhs.util.config.Config.ConfigLockedException;
import net.xqhs.util.logging.Logger.Level;
import net.xqhs.util.logging.Logging;
import net.xqhs.util.logging.UnitComponentExt;
import tatami.core.agent.parametric.AgentParameterName;
import tatami.core.agent.parametric.AgentParameters;
import tatami.core.util.platformUtils.PlatformUtils;
import tatami.pc.util.windowLayout.WindowLayout;
import tatami.simulation.AgentLoader.StandardAgentLoaderType;
import tatami.simulation.PlatformLoader.StandardPlatformType;

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
	protected UnitComponentExt	log					= (UnitComponentExt) new UnitComponentExt().setUnitName("boot");
	
	/**
	 * The method handling main functionality of {@link Boot}.
	 * <p>
	 * 
	 * @param args
	 *            - the arguments received by the program.
	 */
	public void boot(String args[])
	{
		log.trace("Booting World.");
		
		// load settings & scenario
		BootSettingsManager settings = new BootSettingsManager();
		XMLTree scenarioTree;
		try
		{
			scenarioTree = settings.load(args, true);
		} catch(ConfigLockedException e)
		{
			log.error("settings were locked (shouldn't ever happen): " + PlatformUtils.printException(e));
			return;
		}
		
		// create window layout
		WindowLayout.staticLayout = new WindowLayout(settings.getApplicationLayoutWidth(),
				settings.getApplicationLayoutHeight(), settings.getLayout(), null);
		
		// build agent creation data
		
		// the name of the default platform
		String defaultPlatform = PlatformLoader.DEFAULT_PLATFORM.toString();
		// platform name -> platform loader
		Map<String, PlatformLoader> platforms = new HashMap<String, PlatformLoader>();
		// agent loader name -> agent loader
		Map<String, AgentLoader> agentLoaders = new HashMap<String, AgentLoader>();
		// package names where agent code (adf & java) may be located
		Set<String> agentPackages = new HashSet<String>();
		// container name -> do create (true for local containers, false for remote)
		Map<String, Boolean> allContainers = new HashMap<String, Boolean>();
		// platform name -> names of containers to be present in the platform
		Map<String, Set<String>> platformContainers = new HashMap<String, Set<String>>();
		// platform name -> container name -> agent name -> agent manager
		// for the agent to be started in the container, on the platform
		Set<AgentCreationData> allAgents = new HashSet<AgentCreationData>();
		
		// add agent packages specified in the scenario
		Iterator<XMLNode> packagePathsIt = scenarioTree.getRoot().getNodeIterator(
				AgentParameterName.AGENT_PACKAGE.toString());
		while(packagePathsIt.hasNext())
			agentPackages.add((String) packagePathsIt.next().getValue());
		
		// iterate over platform entries in the scenario
		defaultPlatform = loadPlatforms(
				scenarioTree.getRoot().getNodeIterator(AgentParameterName.AGENT_PLATFORM.toString()), settings,
				platforms);
		
		// iterate over agent loader entries in the scenario
		loadAgentLoaders(scenarioTree.getRoot().getNodeIterator(AgentParameterName.AGENT_LOADER.toString()),
				agentLoaders);
		
		// iterate containers and find agents
		loadContainerAgents(scenarioTree.getRoot().getNodeIterator("initial").next().getNodeIterator("container"),
				defaultPlatform, platforms, agentLoaders, agentPackages, allContainers, platformContainers, allAgents);
		
		// agents prepared, time to start platforms and the containers.
		if(startPlatforms(platforms, platformContainers) > 0)
		{
			// load timeline (if any)
			XMLNode timeline = null;
			if(scenarioTree.getRoot().getNodeIterator(AgentParameterName.TIMELINE.toString()).hasNext())
				timeline = scenarioTree.getRoot().getNodeIterator(AgentParameterName.TIMELINE.toString()).next();
			
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
	 * Loads the available platform loaders and fills in the {@link Map} of platforms, also returning the default
	 * platform (decided according to the information in the scenario file).
	 * <p>
	 * The available platform loaders will be the ones mentioned in the scenario file. If the name of the platform is
	 * the name of a standard platform (see {@link StandardPlatformType}), the predefined class path will be used;
	 * otherwise, the class path must be present in the scenario.
	 * <p>
	 * If no platforms are specified in the scenario, the default platform {@link StandardPlatformType#DEFAULT} will be
	 * used, and this will be the default platform of the scenario.
	 * <p>
	 * If only one platform is specified in the scenario, this will be the default platform of the scenario.
	 * <p>
	 * If multiple platforms are specified, there will be no default platform (all agents have to specify their
	 * platform).
	 * 
	 * TODO: indicate default platform among multiple platforms; have a default per-container platform.
	 * 
	 * @param platformNodes
	 *            - {@link Iterator} over the nodes in the scenario file describing platforms.
	 * @param settings
	 *            - the {@link BootSettingsManager} containing settings set through application arguments or the
	 *            <code>config</code> node in the scenario file.
	 * @param platforms
	 *            - map in which to fill in the names of the platforms and the respective {@link PlatformLoader}
	 *            instances.
	 * @return the name of the default platform loader (which will be present in parameter <code>platforms</code>).
	 */
	protected String loadPlatforms(Iterator<XMLNode> platformNodes, BootSettingsManager settings,
			Map<String, PlatformLoader> platforms)
	{
		while(platformNodes.hasNext())
		{
			XMLNode platformNode = platformNodes.next();
			String platformName = PlatformUtils.getParameterValue(platformNode, PlatformLoader.NAME_ATTRIBUTE);
			if(platformName == null)
				log.error("Platform name is null.");
			else if(platforms.containsKey(platformName))
				log.error("Platform [" + platformName + "] already defined.");
			else
			{
				String platformClassPath = null;
				try
				{
					platformClassPath = StandardPlatformType.valueOf(platformName.toUpperCase()).getClassName();
				} catch(IllegalArgumentException e)
				{ // platform is not standard
					platformClassPath = PlatformUtils.getParameterValue(platformNode, PlatformLoader.CLASSPATH_ATTRIBUTE);
					if(platformClassPath == null)
						log.error("Class path for platform [" + platformName + "] is not known.");
				}
				if(platformClassPath != null)
					try
					{
						platforms.put(platformName, ((PlatformLoader) PlatformUtils.loadClassInstance(this,
								platformClassPath, new Object[0])).setConfig(platformNode, settings));
						log.info("Platform [" + platformName + "] prepared.");
					} catch(Exception e)
					{
						log.error("Loading platform [" + platformName + "] failed; platform will not be available:"
								+ PlatformUtils.printException(e));
					}
			}
		}
		// default platform
		if(platforms.isEmpty())
		{
			// load default platform
			StandardPlatformType platform = StandardPlatformType.DEFAULT;
			try
			{
				platforms
						.put(platform.toString(), ((PlatformLoader) PlatformUtils.loadClassInstance(this,
								platform.getClassName(), new Object[0])));
				log.info("Platform [" + platform.toString() + "] prepared.");
			} catch(Exception e)
			{
				log.error("Loading platform [" + platform.toString() + "] failed; platform will not be available:"
						+ PlatformUtils.printException(e));
			}
		}
		
		String defaultPlatform = null;
		if(platforms.size() == 1)
			defaultPlatform = platforms.values().iterator().next().getName();
		log.trace("Default platform is [" + defaultPlatform + "].");
		return defaultPlatform;
	}
	
	/**
	 * Loads the available agent loaders and fills in the {@link Map} of agent loaders. Event if not defined explicitly
	 * in the scenario file (which is possible), all loaders in {@link StandardAgentLoaderType} are also loaded.
	 * 
	 * @param loaderNodes
	 *            - {@link Iterator} over the nodes in the scenario file describing agent loaders.
	 * @param agentLoaders
	 *            - map in which to fill in the names of the agent loaders and the respective {@link AgentLoader}
	 *            instances.
	 */
	protected void loadAgentLoaders(Iterator<XMLNode> loaderNodes, Map<String, AgentLoader> agentLoaders)
	{
		while(loaderNodes.hasNext())
		{
			XMLNode loaderNode = loaderNodes.next();
			String loaderName = PlatformUtils.getParameterValue(loaderNode, AgentLoader.NAME_ATTRIBUTE);
			if(loaderName == null)
				log.error("Agent loader name is null.");
			else if(agentLoaders.containsKey(loaderName))
				log.error("Agent loader [" + loaderName + "] already defined.");
			else
			{
				String loaderClassPath = null;
				try
				{
					loaderClassPath = StandardAgentLoaderType.valueOf(loaderName.toUpperCase()).getClassName();
				} catch(IllegalArgumentException e)
				{ // platform is not standard
					loaderClassPath = PlatformUtils.getParameterValue(loaderNode, AgentLoader.CLASSPATH_ATTRIBUTE);
					if(loaderClassPath == null)
						log.error("Class path for agent loader [" + loaderName + "] is not known.");
				}
				if(loaderClassPath != null)
					try
					{
						agentLoaders.put(loaderName, ((AgentLoader) PlatformUtils.loadClassInstance(this,
								loaderClassPath, new Object[0])).setConfig(loaderNode));
						log.info("Agent loader [" + loaderName + "] prepared.");
					} catch(Exception e)
					{
						log.error("Loading agent loader [" + loaderName + "] failed; loader will not be available: "
								+ PlatformUtils.printException(e));
					}
			}
		}
		
		// add standard agent loaders (except if they have already been specified and configured explicitly.
		
		for(StandardAgentLoaderType loader : StandardAgentLoaderType.values())
			if(!agentLoaders.containsKey(loader.toString()) && (loader.getClassName() != null))
				try
				{
					agentLoaders.put(loader.toString(),
							(AgentLoader) PlatformUtils.loadClassInstance(this, loader.getClassName()));
					log.info("Agent loader [" + loader.toString() + "] prepared.");
				} catch(Exception e)
				{
					log.error("Loading agent loader [" + loader.toString() + "] failed; loader will not be available: "
							+ PlatformUtils.printException(e));
				}
	}
	
	/**
	 * Loads container and agent information from the scenario file. Based on the first 5 arguments, the method will
	 * fill in the information in the last 3 arguments.
	 * 
	 * @param containerNodes
	 *            - {@link Iterator} over the nodes in the scenario file describing containers (and, inside, agents).
	 * @param defaultPlatform
	 *            - the name of the default platform.
	 * @param platforms
	 *            - the {@link Map} of platform names and respective {@link PlatformLoader} instances.
	 * @param agentLoaders
	 *            - the {@link Map} of platform names and respective {@link AgentLoader} instances.
	 * @param agentPackages
	 *            - the {@link Set} of package names where agent code may be located.
	 * @param allContainers
	 *            - the {@link Map} in which the method will fill in all containers, specifying the name and whether the
	 *            container should be created.
	 * @param platformContainers
	 *            - the {@link Map} in which the method will fill in the containers to load on the local machine, for
	 *            each platform (the map contains: platform name &rarr; set of containers to load).
	 * @param allAgents
	 *            - the {@link Set} in which the method will fill in the {@link AgentCreationData} instances for all
	 *            agents.
	 */
	protected void loadContainerAgents(Iterator<XMLNode> containerNodes, String defaultPlatform,
			Map<String, PlatformLoader> platforms, Map<String, AgentLoader> agentLoaders, Set<String> agentPackages,
			Map<String, Boolean> allContainers, Map<String, Set<String>> platformContainers,
			Set<AgentCreationData> allAgents)
	{
		while(containerNodes.hasNext())
		{
			XMLNode containerConfig = containerNodes.next();
			
			// container information
			String containerName = containerConfig.getAttributeValue("name");
			boolean doCreateContainer = (containerConfig.getAttributeValue("create") == null)
					|| containerConfig.getAttributeValue("create").equals(new Boolean(true));
			allContainers.put(containerName, new Boolean(doCreateContainer));
			
			// container has no agents, but should be created in said platform
			if(doCreateContainer && !containerConfig.getNodeIterator("agent").hasNext())
			{
				String platformName = containerConfig.getAttributeValue("platform");
				if((platformName != null) && platforms.containsKey(platformName))
				{
					if(!platformContainers.containsKey(platformName))
						platformContainers.put(platformName, new HashSet<String>());
					platformContainers.get(platformName).add(containerName);
				}
			}
			
			// set up creation for all agents in the container
			for(Iterator<XMLNode> agentNodes = containerConfig.getNodeIterator("agent"); agentNodes.hasNext();)
			{
				XMLNode agentNode = agentNodes.next();
				// agent name
				String agentName = PlatformUtils.getParameterValue(agentNode, AgentParameterName.AGENT_NAME.toString());
				if(agentName == null)
				{
					log.error("agent has no name; will not be created.");
					continue;
				}
				// platform
				String platformName = PlatformUtils.getParameterValue(agentNode, AgentParameterName.AGENT_PLATFORM.toString());
				if(platformName == null)
					platformName = defaultPlatform; // no platform specified: go to default
				if(!platforms.containsKey(platformName))
				{
					log.error("unknown platform [" + platformName + "]; agent [" + agentName + "] will not be created.");
					continue;
				}
				
				// load agent
				AgentCreationData agentCreationData = loadAgent(agentNode, agentName, containerName, doCreateContainer,
						platformName, agentLoaders, agentPackages);
				if(agentCreationData == null)
					continue;
				allAgents.add(agentCreationData);
				
				// associate container with the platform
				if(doCreateContainer)
				{
					if(!platformContainers.containsKey(platformName))
						platformContainers.put(platformName, new HashSet<String>());
					platformContainers.get(platformName).add(containerName);
					log.trace("Agent [" + agentName + "] will be run on platform [" + platformName
							+ "], in local container [" + containerName + "]");
				}
				else
					log.trace("Agent [" + agentName + "] will be run on platform [" + platformName
							+ "], in remote container [" + containerName + "]");
			}
		}
	}
	
	/**
	 * Loads agent information from the scenario file and pre-loads the agent using the appropriate {@link AgentLoader}.
	 * <p>
	 * If successful, the method returns an {@link AgentCreationData} instance that can be subsequently be used in a
	 * call to {@link AgentLoader#load(AgentCreationData)} to obtain an {@link AgentManager} instance.
	 * 
	 * @param agentNode
	 *            - the {@link XMLNode} containing the information about the agent.
	 * @param agentName
	 *            - the name of the agent, already determined by the caller.
	 * @param containerName
	 *            - the name of the container the agent will reside in.
	 * @param doCreateContainer
	 *            - <code>true</code> if the container is local, <code>false</code> if remote.
	 * @param platformName
	 *            - the name of the platform the agent will execute on.
	 * @param agentLoaders
	 *            - the {@link Map} of agent loader names and respective {@link AgentLoader} instances.
	 * @param agentPackages
	 *            - the {@link Set} of packages containing agent code.
	 * @return an {@link AgentManager} instance that can be used to control the lifecycle of the just loaded agent, if
	 *         the loading was successful; <code>null</code> otherwise.
	 */
	protected AgentCreationData loadAgent(XMLNode agentNode, String agentName, String containerName,
			boolean doCreateContainer, String platformName, Map<String, AgentLoader> agentLoaders,
			Set<String> agentPackages)
	{
		// loader
		String agentLoaderName = PlatformUtils.getParameterValue(agentNode, AgentParameterName.AGENT_LOADER.toString());
		if(agentLoaderName == null)
			return (AgentCreationData) log.lr(null, "no agent loader specified. agent [" + agentName
					+ "] will not be created.");
		if(!agentLoaders.containsKey(agentLoaderName))
			return (AgentCreationData) log.lr(null, "agent loader [" + agentLoaderName + "] is unknown. agent [" + agentName
					+ "] will not be created.");
		AgentLoader loader = agentLoaders.get(agentLoaderName);
		
		// get all parameters and put them into an AgentParameters instance.
		AgentParameters parameters = new AgentParameters();
		for(Iterator<XMLNode> paramIt = agentNode.getNodeIterator("parameter"); paramIt.hasNext();)
		{
			XMLNode param = paramIt.next();
			AgentParameterName parName = AgentParameterName.getName(param.getAttributeValue("name"));
			if(parName != null)
				parameters.add(parName, param.getAttributeValue("value"));
			else
			{
				log.trace("adding unregistered parameter [" + param.getAttributeValue("name") + "].");
				parameters.add(param.getAttributeValue("name"), param.getAttributeValue("value"));
			}
		}
		for(String pack : agentPackages)
			parameters.add(AgentParameterName.AGENT_PACKAGE, pack);
		
		AgentCreationData agentCreationData = new AgentCreationData(agentName, parameters, containerName,
				!doCreateContainer, platformName, loader, agentNode);
		if(!loader.preload(agentCreationData, log))
		{
			log.error("Agent [" + agentName + "] cannot be loaded.");
			return null;
		}
		return agentCreationData;
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
		Logging.getMasterLogging().setLogLevel(Level.INFO);
		new Boot().boot(args);
	}
}
