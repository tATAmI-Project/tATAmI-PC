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
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import net.xqhs.util.config.Config.ConfigLockedException;
import net.xqhs.util.logging.UnitComponentExt;
import tatami.core.agent.AgentComponent;
import tatami.core.agent.CompositeAgent;
import tatami.core.agent.claim.ClaimComponent;
import tatami.core.agent.claim.parser.ClaimAgentDefinition;
import tatami.core.agent.jade.JadeInterface;
import tatami.core.agent.jade.JadeInterface.JadeConfig;
import tatami.core.agent.parametric.AgentParameterName;
import tatami.core.agent.parametric.AgentParameters;
import tatami.core.util.platformUtils.PlatformUtils;
import tatami.pc.agent.visualization.VisualizationAgent;
import tatami.pc.util.XML.XMLTree;
import tatami.pc.util.XML.XMLTree.XMLNode;
import tatami.pc.util.windowLayout.WindowLayout;

/**
 * The Boot class manages the startup of the multi-agent system. It manages settings, it loads the scenario, loads the
 * agent definitions (agents are actually created later).
 * 
 * @author Andrei Olaru
 * @author Nguyen Thi Thuy Nga
 */
public class Boot
{
	/**
	 * Available types of loaders for agents.
	 * 
	 * @author Andrei Olaru
	 */
	enum Loader {
		
		/**
		 * The agent is described by means of an .adf2 file. The ADF2 agent is a special kind of {@link CompositeAgent},
		 * with some pre-configured components. An S-CLAIM agent can be easily loaded as a COMPOSITE agent. This
		 * enumeration value is kept only for backwards compatibility of scenarios.
		 */
		ADF2,
		
		/**
		 * The agent is a {@link CompositeAgent} that is made up of various components.
		 */
		COMPOSITE,
		
		/**
		 * The agent is described by means of traditional Java code. Currently unimplemented.
		 */
		JAVA,
		
		;
		
		/**
		 * The name of the loader.
		 */
		private String	name	= null;
		
		/**
		 * Creates a new loader type, giving it a name unrelated to the enumeration value.
		 * 
		 * @param loaderName
		 *            - the name of the loader.
		 */
		private Loader(String loaderName)
		{
			name = loaderName;
		}
		
		/**
		 * Creates a new loader type, giving it a name that is the lower case version of the enumeration value.
		 */
		private Loader()
		{
			name = super.toString().toLowerCase();
		}
		
		/**
		 * Overrides the inherited method to return the 'given name' of the loader.
		 */
		@Override
		public String toString()
		{
			return name;
		}
		
	}
	
	/**
	 * The log of the class.
	 */
	protected static UnitComponentExt	log	= (UnitComponentExt) new UnitComponentExt().setUnitName("boot");
	
	/**
	 * The <code>main</code> method, handling all functionality of {@link Boot}.
	 * <p>
	 * 
	 * @param args
	 *            - the arguments received by the program.
	 */
	public static void main(String args[])
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
			log.error("settings were locked (shouldn't ever happen): " + e.getStackTrace().toString());
			return;
		}
		
		// create window layout
		WindowLayout.staticLayout = new WindowLayout(settings.getApplicationLayoutWidth(),
				settings.getApplicationLayoutHeight(), settings.getLayout(), null);
		
		// build agent creation data
		
		Map<String, AgentCreationData> allAgents = new HashMap<String, AgentCreationData>();
		Map<String, Boolean> allContainers = new HashMap<String, Boolean>(); // container name -> do create
		
		Set<String> adfPaths = new HashSet<String>(), agentPackages = new HashSet<String>();
		Iterator<XMLNode> adfPathsIt = scenarioTree.getRoot().getNodeIterator("adfPath");
		while(adfPathsIt.hasNext())
			adfPaths.add((String) adfPathsIt.next().getValue());
		Iterator<XMLNode> packagePathsIt = scenarioTree.getRoot().getNodeIterator(
				AgentParameterName.AGENT_PACKAGE.toString());
		while(packagePathsIt.hasNext())
			agentPackages.add((String) packagePathsIt.next().getValue());
		
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
				
				String agentName = getParameterValue(agentConfig, AgentParameterName.AGENT_NAME.toString());
				if(agentName == null)
				{
					log.error("agent has no name; will not be created.");
					continue;
				}
				
				Loader loader = null;
				try
				{
					loader = Loader.valueOf(getParameterValue(agentConfig, "loader"));
				} catch(NullPointerException e)
				{
					log.error("agent loader is null. agent [" + agentName + "] will not be created.");
					continue;
				} catch(IllegalArgumentException e)
				{
					log.error("unknown agent loader [" + getParameterValue(agentConfig, "loader") + "]; agent ["
							+ agentName + "] will not be created.");
					continue;
				} // agent loader must be ok here.
				
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
				
				ArrayList<AgentComponent> components = new ArrayList<AgentComponent>();
				switch(loader)
				{
				case JAVA:
					// TODO: to do.
					break;
				case ADF2:
				{
					ClaimAgentDefinition cad = ClaimUtils
							.fillCAD(parameters.get(AgentParameterName.AGENT_CLASS.toString()),
									parameters.getValues(AgentParameterName.JAVA_CODE.toString()), adfPaths,
									agentPackages, log);
					parameters.addObject(AgentParameterName.AGENT_DEFINITION, cad);
					// register agent
					break;
				}
				case COMPOSITE:
					allAgents.put(agentName, new AgentCreationData(agentName, CompositeAgent.class.getCanonicalName(),
							parameters, containerName, !doCreateContainer));
					log.info("configured [" + loader.toString() + "] agent [" + agentName + "] in container ["
							+ containerName + "]");
					break;
				}
				
			}
		}
		
		if(settings.doLoadJade())
		{
			// boot JADE platform
			log.info("booting JADE");
			
			// identify appropriate JadeInterface instance
			JadeInterface jade;
			try
			{
				jade = (JadeInterface) PlatformUtils.loadClassInstance(new Object(), PlatformUtils.jadeInterfaceClass()
						.getName(), new Object[] {});
			} catch(Exception e)
			{
				log.error("unable to create jade interface: " + e.toString() + ": " + e.getStackTrace().toString());
				return;
			}
			if(jade == null)
			{
				log.error("unable to create jade interface");
				return;
			}
			
			// configure jade
			JadeConfig jadeConfig = jade.fillConfig(new JadeConfig());
			jadeConfig.setLocalHost(settings.getLocalHost()).setLocalPort(settings.getLocalPort())
					.setMainHost(settings.mainHost).setMainPort(settings.mainPort);
			if(settings.doCreateMainContainer())
				jadeConfig.setPlatformID(settings.getJadePlatformName()).setMainContainerName(
						settings.getMainContainerName());
			jade.setConfig(jadeConfig);
			
			boolean isMain = settings.doCreateMainContainer();
			String mainContainerName = settings.getMainContainerName();
			
			// start jade
			if(isMain)
			{
				log.info("booting platform / main container");
				jade.startPlatform();
			}
			
			// start containers
			for(Map.Entry<String, Boolean> containerData : allContainers.entrySet())
			{
				if(containerData.getValue().booleanValue() && !containerData.getKey().equals(mainContainerName))
				{
					log.info("creating container [" + containerData.getKey() + "]...");
					jade.startContainer(containerData.getKey());
					log.info("container created.");
				}
			}
			
			if(isMain)
			{
				// FIXME visualizer / simulator name should be set by scenario
				String vizName = "visualizer";
				jade.addAgentToContainer(mainContainerName, vizName, VisualizationAgent.class.getCanonicalName(), null);
				
				XMLNode timeline = null;
				if(scenarioTree.getRoot().getNodeIterator(AgentParameterName.TIMELINE.toString()).hasNext())
					timeline = scenarioTree.getRoot().getNodeIterator(AgentParameterName.TIMELINE.toString()).next();
				
				AgentParameters parameters = new AgentParameters();
				parameters.addObject(AgentParameterName.JADE_INTERFACE, jade);
				parameters.addObject(AgentParameterName.AGENTS, allAgents.values());
				if(timeline != null)
					parameters.addObject(AgentParameterName.TIMELINE, timeline);
				parameters.add(AgentParameterName.VISUALIZTION_AGENT, vizName);
				jade.addAgentToContainer(mainContainerName, "simulator", SimulationAgent.class.getCanonicalName(),
						new Object[] { parameters });
				
			}
		}
		
		log.doExit();
	}
	
	/**
	 * method to simplify the access to a parameter of an agent. Having the {@link XMLNode} instance associated with the
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
		Iterator<XMLNode> paramsIt = agentConfig.getNodeIterator("parameter");
		while(paramsIt.hasNext())
		{
			XMLNode param = paramsIt.next();
			if(param.getAttributeValue("name").equals(parameterName))
				return param.getAttributeValue("value");
		}
		return null;
	}
}
