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

import net.xqhs.util.config.Config.ConfigLockedException;
import net.xqhs.util.logging.Logging;
import net.xqhs.util.logging.UnitComponentExt;
import net.xqhs.util.logging.Logger.Level;
import tatami.core.agent.parametric.AgentParameterName;
import tatami.core.agent.parametric.AgentParameters;
import tatami.core.util.platformUtils.PlatformUtils;
import tatami.pc.util.XML.XMLTree;
import tatami.pc.util.XML.XMLTree.XMLNode;
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
	 * The name of nodes containing component parameters.
	 */
	private static final String	PARAMETER_NODE_NAME	= "parameter";
	/**
	 * The name of the attribute of a parameter node holding the name of the parameter.
	 */
	private static final String	PARAMETER_NAME		= "name";
	/**
	 * The name of the attribute of a parameter node holding the value of the parameter.
	 */
	private static final String	PARAMETER_VALUE		= "value";
	
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
		
		// load settings
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
		
		Map<String, AgentManager> allAgents = new HashMap<String, AgentManager>();
		Map<String, Boolean> allContainers = new HashMap<String, Boolean>(); // container name -> do create
		Map<String, Set<String>> platformContainers = new HashMap<String, Set<String>>();
		Map<String, AgentLoader> agentLoaders = new HashMap<String, AgentLoader>();
		Map<String, PlatformLoader> platforms = new HashMap<String, PlatformLoader>();
		Set<String> agentPackages = new HashSet<String>();
		String defaultPlatform = PlatformLoader.DEFAULT_PLATFORM.toString();
		
		// add agent packages specified in the scenario
		
		Iterator<XMLNode> packagePathsIt = scenarioTree.getRoot().getNodeIterator(
				AgentParameterName.AGENT_PACKAGE.toString());
		while(packagePathsIt.hasNext())
			agentPackages.add((String) packagePathsIt.next().getValue());
		
		// iterate over platform entries in the scenario
		
		Iterator<XMLNode> platformIt = scenarioTree.getRoot().getNodeIterator(
				AgentParameterName.AGENT_PLATFORM.toString());
		while(platformIt.hasNext())
		{
			XMLNode platformNode = platformIt.next();
			String platformName = getParameterValue(platformNode, PlatformLoader.NAME_ATTRIBUTE);
			if(platformName == null)
				log.error("Platform name is null.");
			else if(platforms.containsKey(platformName))
				log.error("Platform [" + platformName + "] already defined.");
			else
			{
				String platformClassPath = null;
				try
				{
					platformClassPath = StandardPlatformType.valueOf(platformName).getClassName();
				} catch(IllegalArgumentException e)
				{ // platform is not standard
					platformClassPath = getParameterValue(platformNode, PlatformLoader.CLASSPATH_ATTRIBUTE);
					if(platformClassPath == null)
						log.error("Class path for platform [" + platformName + "] is not known.");
				}
				if(platformClassPath != null)
					try
					{
						platforms.put(platformName, ((PlatformLoader) PlatformUtils.loadClassInstance(this,
								platformClassPath, new Object[0])).setConfig(platformNode));
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
		if(platforms.size() == 1)
			defaultPlatform = platforms.values().iterator().next().getName();
		log.trace("Default platform is [" + defaultPlatform + "].");
		
		// iterate over agent loader entries in the scenario
		
		Iterator<XMLNode> loaderIt = scenarioTree.getRoot().getNodeIterator(AgentParameterName.AGENT_LOADER.toString());
		while(loaderIt.hasNext())
		{
			XMLNode loaderNode = loaderIt.next();
			String loaderName = getParameterValue(loaderNode, AgentLoader.NAME_ATTRIBUTE);
			if(loaderName == null)
				log.error("Platform name is null.");
			else if(agentLoaders.containsKey(loaderName))
				log.error("Platform [" + loaderName + "] already defined.");
			else
			{
				String loaderClassPath = null;
				try
				{
					loaderClassPath = StandardPlatformType.valueOf(loaderName).getClassName();
				} catch(IllegalArgumentException e)
				{ // platform is not standard
					loaderClassPath = getParameterValue(loaderNode, AgentLoader.CLASSPATH_ATTRIBUTE);
					if(loaderClassPath == null)
						log.error("Class path for platform [" + loaderName + "] is not known.");
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
			if(!agentLoaders.containsKey(loader.toString()))
				try
				{
					agentLoaders.put(loader.toString(),
							(AgentLoader) PlatformUtils.loadClassInstance(this, loader.getClassName(), new Object[0]));
					log.info("Agent loader [" + loader.toString() + "] prepared.");
				} catch(Exception e)
				{
					log.error("Loading agent loader [" + loader.toString() + "] failed; loader will not be available: "
							+ PlatformUtils.printException(e));
				}
		
		// iterate containers and find agents
		
		XMLNode initial = scenarioTree.getRoot().getNodeIterator("initial").next();
		for(Iterator<XMLNode> itC = initial.getNodeIterator("container"); itC.hasNext();)
		{
			XMLNode containerConfig = itC.next();
			String containerName = containerConfig.getAttributeValue("name");
			boolean doCreateContainer = (containerConfig.getAttributeValue("create") == null)
					|| containerConfig.getAttributeValue("create").equals(new Boolean(true));
			allContainers.put(containerName, new Boolean(doCreateContainer));
			
			// set up creation for all agents in the container
			for(Iterator<XMLNode> itA = containerConfig.getNodeIterator("agent"); itA.hasNext();)
			{
				XMLNode agentConfig = itA.next();
				// get interesting parameters
				if(!agentConfig.getNodeIterator("parameter").hasNext())
				{
					log.error("agent has no parameters");
					continue;
				}
				
				// name
				String agentName = getParameterValue(agentConfig, AgentParameterName.AGENT_NAME.toString());
				if(agentName == null)
				{
					log.error("agent has no name; will not be created.");
					continue;
				}
				
				// loader
				String agentLoaderName = getParameterValue(agentConfig, AgentParameterName.AGENT_LOADER.toString());
				if(agentLoaderName == null)
				{
					log.error("no agent loader specified. agent [" + agentName + "] will not be created.");
					continue;
				}
				if(!agentLoaders.containsKey(agentLoaderName))
				{
					log.error("agent loader [" + agentLoaderName + "] is unknown. agent [" + agentName
							+ "] will not be created.");
					continue;
				}
				
				// platform
				String platformName = getParameterValue(agentConfig, AgentParameterName.AGENT_PLATFORM.toString());
				if(platformName == null)
				{
					platformName = defaultPlatform; // no platform specified: go to default
				}
				if(!platforms.containsKey(platformName))
				{
					log.error("unknown platform [" + platformName + "]; agent [" + agentName + "] will not be created.");
					continue;
				}
				
				// get all parameters and put them into an AgentParameters instance.
				AgentParameters parameters = new AgentParameters();
				for(Iterator<XMLNode> paramIt = agentConfig.getNodeIterator("parameter"); paramIt.hasNext();)
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
						!doCreateContainer, agentConfig);
				AgentManager manager = agentLoaders.get(agentLoaderName).load(agentCreationData);
				allAgents.put(agentName, manager);
				log.info("Agent [" + agentName + "] loaded.");
				
				// associate container
				if(doCreateContainer)
				{
					if(!platformContainers.containsKey(platformName))
						platformContainers.put(platformName, new HashSet<String>());
					platformContainers.get(platformName).add(containerName);
					log.trace("Agent [" + agentName + "] will be run on platform [" + platformName
							+ "], in local container [" + containerName + "]");
				}
				// switch(loader)
				// {
				// case JAVA: // TODO: to do.
				// break;
				// case ADF2:
				// {
				// ClaimAgentDefinition cad = ClaimUtils.fillCAD(
				// parameters.get(AgentParameterName.AGENT_CLASS.toString()),
				// parameters.getValues(AgentParameterName.JAVA_CODE.toString()), agentPackages,
				// agentPackages, log);
				// parameters.addObject(AgentParameterName.AGENT_DEFINITION, cad); // register agent
				// break;
				// }
				// case COMPOSITE:
				// allAgents.put(agentName, new AgentCreationData(agentName, CompositeAgent.class.getCanonicalName(),
				// parameters, containerName, !doCreateContainer));
				// log.info("configured [" + loader.toString() + "] agent [" + agentName + "] in container ["
				// + containerName + "]");
				// break;
				// }
				
			}
		}
		
		// agents prepared, time to start platforms and the containers.
		
		for(Iterator<PlatformLoader> itP = platforms.values().iterator(); itP.hasNext();)
		{
			PlatformLoader platform = itP.next();
			if(!platform.start())
			{
				log.error("Platform [" + platform.getName() + "] failed to start.");
				itP.remove();
				continue;
			}
			log.info("Platform [" + platform.getName() + "] started.");
			for(Iterator<String> itC = platformContainers.get(platform.getName()).iterator(); itC.hasNext();)
			{
				String containerName = itC.next();
				if(!platform.addContainer(containerName))
				{
					log.error("Adding container [" + containerName + "] to [" + platform.getName() + "] has failed.");
					itC.remove();
				}
				else
					log.info("Container [" + containerName + "] added to [" + platform.getName() + "].");
			}
		}
		
		// String vizName = "visualizer";
		// jade.addAgentToContainer(mainContainerName, vizName, VisualizationAgent.class.getCanonicalName(), null);
		
		XMLNode timeline = null;
		if(scenarioTree.getRoot().getNodeIterator(AgentParameterName.TIMELINE.toString()).hasNext())
			timeline = scenarioTree.getRoot().getNodeIterator(AgentParameterName.TIMELINE.toString()).next();
		
		new SimulationManager(platforms, allAgents, timeline);
		
		// boot JADE platform
		// log.info("booting JADE");
		//
		// // identify appropriate JadeInterface instance
		// JadeInterface jade;
		// try
		// {
		// jade = (JadeInterface) PlatformUtils.loadClassInstance(new Object(), PlatformUtils.jadeInterfaceClass()
		// .getName(), new Object[] {});
		// } catch(Exception e)
		// {
		// log.error("unable to create jade interface: " + e.toString() + ": " + e.getStackTrace().toString());
		// return;
		// }
		// if(jade == null)
		// {
		// log.error("unable to create jade interface");
		// return;
		// }
		//
		// // configure jade
		// JadeConfig jadeConfig = jade.fillConfig(new JadeConfig());
		// jadeConfig.setLocalHost(settings.getLocalHost()).setLocalPort(settings.getLocalPort())
		// .setMainHost(settings.mainHost).setMainPort(settings.mainPort);
		// if(settings.doCreateMainContainer())
		// jadeConfig.setPlatformID(settings.getJadePlatformName()).setMainContainerName(
		// settings.getMainContainerName());
		// jade.setConfig(jadeConfig);
		//
		// boolean isMain = settings.doCreateMainContainer();
		// String mainContainerName = settings.getMainContainerName();
		//
		// // start jade
		// if(isMain)
		// {
		// log.info("booting platform / main container");
		// jade.startPlatform();
		// }
		//
		// // start containers
		// for(Map.Entry<String, Boolean> containerData : allContainers.entrySet())
		// {
		// if(containerData.getValue().booleanValue() && !containerData.getKey().equals(mainContainerName))
		// {
		// log.info("creating container [" + containerData.getKey() + "]...");
		// jade.startContainer(containerData.getKey());
		// log.info("container created.");
		// }
		// }
		//
		// if(isMain)
		// {
		// // FIXME visualizer / simulator name should be set by scenario
		// String vizName = "visualizer";
		// jade.addAgentToContainer(mainContainerName, vizName, VisualizationAgent.class.getCanonicalName(), null);
		//
		// XMLNode timeline = null;
		// if(scenarioTree.getRoot().getNodeIterator(AgentParameterName.TIMELINE.toString()).hasNext())
		// timeline = scenarioTree.getRoot().getNodeIterator(AgentParameterName.TIMELINE.toString()).next();
		//
		// AgentParameters parameters = new AgentParameters();
		// parameters.addObject(AgentParameterName.JADE_INTERFACE, jade);
		// parameters.addObject(AgentParameterName.AGENTS, allAgents.values());
		// if(timeline != null)
		// parameters.addObject(AgentParameterName.TIMELINE, timeline);
		// parameters.add(AgentParameterName.VISUALIZTION_AGENT, vizName);
		// jade.addAgentToContainer(mainContainerName, "simulator", SimulationAgent.class.getCanonicalName(),
		// new Object[] { parameters });
		//
		// }
		
		log.doExit();
	}
	
	/**
	 * Method to simplify the access to a parameter of an agent. Having the {@link XMLNode} instance associated with the
	 * agent, the method retrieves the value associated with the first occurrence of the desired parameter name.
	 * 
	 * @param agentConfig
	 *            - the node containing the configuration information for the agent.
	 * @param parameterName
	 *            - the name of the searched parameter.
	 * @return the value associated with the searched name.
	 */
	static String getParameterValue(XMLNode agentConfig, String parameterName)
	{
		Iterator<XMLNode> paramsIt = agentConfig.getNodeIterator(PARAMETER_NODE_NAME);
		while(paramsIt.hasNext())
		{
			XMLNode param = paramsIt.next();
			if(param.getAttributeValue(PARAMETER_NAME).equals(parameterName))
				return param.getAttributeValue(PARAMETER_VALUE);
		}
		return null;
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
