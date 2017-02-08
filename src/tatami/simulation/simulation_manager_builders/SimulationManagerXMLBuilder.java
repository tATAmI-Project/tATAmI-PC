package tatami.simulation.simulation_manager_builders;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import net.xqhs.util.XML.XMLTree;
import net.xqhs.util.XML.XMLTree.XMLNode;
import net.xqhs.util.config.Config.ConfigLockedException;
import net.xqhs.util.logging.UnitComponentExt;
import tatami.HMI.pub.HMIInterface;
import tatami.core.agent.agent_type.AgentLoaderFactory;
import tatami.core.agent.parametric.AgentParameterName;
import tatami.core.agent.parametric.AgentParameters;
import tatami.core.platforms.PlatformFactory;
import tatami.core.util.platformUtils.PlatformUtils;
import tatami.simulation.AgentCreationData;
import tatami.simulation.AgentLoader;
import tatami.simulation.AgentLoader.StandardAgentLoaderType;
import tatami.simulation.AgentManager;
import tatami.simulation.BootDefaultArguments;
import tatami.simulation.BootSettingsManager;
import tatami.simulation.PlatformLoader;
import tatami.simulation.PlatformLoader.StandardPlatformType;
import tatami.simulation.SimulationManager;




public class SimulationManagerXMLBuilder extends ISimulationManagerBuilder{
    
    protected UnitComponentExt  log = (UnitComponentExt) new UnitComponentExt().setUnitName("XML Builder").setLoggerType(
            PlatformUtils.platformLogType());
    
    public class SimulationEzception extends Exception{
        public SimulationEzception(String message){
            super(message);
        }
    }
    
    public class PlatformException extends Exception{
        public PlatformException(String message){
            super(message);
        }
    }
    
    public class AgentLoaderException extends Exception {
        public AgentLoaderException(String message){
            super(message);
        }
    }
    
    XMLTree scenarioTree;
    
    public SimulationManagerXMLBuilder(String args[]) throws SimulationEzception{
        
        if(graphicalUserInterface == null)
            graphicalUserInterface = HMIInterface.INST.getHMI();
        
        try {
            /*Load the DOM tree*/
            scenarioTree = BootSettingsManager.getInst().load(args, true);
            if (scenarioTree == null){
                throw new SimulationEzception("The scenario file could not be loaded");
            }
        } catch (ConfigLockedException e) {
            throw new SimulationEzception("settings were locked (shouldn't ever happen): " + PlatformUtils.printException(e));
        }
        
        
        Vector<Object> components = new Vector<Object>();
        components.addElement(BootDefaultArguments.scenarioFileName);
        components.addElement(BootDefaultArguments.scenarioFileName.substring(BootDefaultArguments.scenarioFileName.lastIndexOf("/")+1, BootDefaultArguments.scenarioFileName.length()));
        getGUI().doOutput("CORE", components);
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
     * @param defaultPlatformSuggested
     *            - default platform as suggested by Boot.
     * @return the name of the default platform loader (which will be present in parameter <code>platforms</code>).
     */
    private String loadPlatforms(Iterator<XMLNode> platformNodes, BootSettingsManager settings) throws SimulationEzception, PlatformException
    {
        while (platformNodes.hasNext()) {
            XMLNode platformNode = platformNodes.next();

            String platformName = PlatformUtils.getParameterValue(platformNode, PlatformLoader.NAME_ATTRIBUTE);

            if (platformName == null)
                throw new SimulationEzception("The platform name is null");

            if (platforms.containsKey(platformName))
                throw new PlatformException("Platform [" + platformName + "] already defined.");

            try {
                platforms.put(platformName,
                        PlatformFactory.getInst().newInst(platformName).setConfig(platformNode, settings));
                log.info("Platform [" + platformName + "] prepared.");
            } catch (Exception e) {
                throw new SimulationEzception("Loading platform [" + platformName
                        + "] failed; platform will not be available:" + PlatformUtils.printException(e));
            }
        }
        // default platform
        if(platforms.isEmpty())
        {
            // load default platform
            StandardPlatformType platform = StandardPlatformType.DEFAULT;
            try
            {
                platforms.put(platform.toString(), PlatformFactory.getInst().newInst(platform.toString()));
                log.info("Default platform [" + platform.toString() + "] prepared.");
            }
            catch(Exception e){
                throw new SimulationEzception("Loading platform [" + platform.toString() + "] failed; platform will not be available:"
                        + PlatformUtils.printException(e));
            }
        }
        
        if(platforms.isEmpty()){
            throw new SimulationEzception("No Platform could be loaded!");
        }
        for(String platformName: platforms.keySet()){
            Vector<Object> args = new Vector<Object>();
            args.addElement(platformName);
            graphicalUserInterface.doOutput("CORE-NEW-PLATFORM", args);
        }
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
     * @param defaultLoaderSuggested
     *            - default agent loader as suggested by Boot.
     * @return the name of the default agent loader (which will be present in parameter <code>agentLoaders</code>).
     */
    protected String loadAgentLoaders(Iterator<XMLNode> loaderNodes) throws AgentLoaderException
    {
        while(loaderNodes.hasNext())
        {
            XMLNode loaderNode = loaderNodes.next();
            String loaderName = PlatformUtils.getParameterValue(loaderNode, AgentLoader.NAME_ATTRIBUTE);
            if(loaderName == null)
                throw new AgentLoaderException("Agent loader name is null.");
            
            if(agentLoaders.containsKey(loaderName))
                log.error("Agent loader [" + loaderName + "] already defined.");
            else
            {
                /*
                String loaderClassPath = null;
                try
                {
                    loaderClassPath = StandardAgentLoaderType.valueOf(loaderName.toUpperCase()).getClassName();
                } catch(IllegalArgumentException e)
                { // agent loader is not standard
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
                    */
            }
        }
        
        // add standard agent loaders (except if they have already been specified and configured explicitly.
        /*
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
        */
        
        for(StandardAgentLoaderType loader : StandardAgentLoaderType.values()){
            if(agentLoaders.containsKey(loader.toString()))
                continue;
            AgentLoader specificLoader = AgentLoaderFactory.getInst().newInst(loader.toString());
            if(specificLoader == null){
                log.error("Loading agent loader [" + loader.toString() + "] failed; loader will not be available: ");
                continue;
            }
            agentLoaders.put(loader.toString(), specificLoader);
        }
        
        String defaultLoader = null;
        if(agentLoaders.size() == 1)
            defaultLoader = agentLoaders.values().iterator().next().getName();
        log.trace("Default agent loader is [" + defaultLoader + "].");
        return (defaultLoader != null) ? defaultLoader : defaultAgentLoader;
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
     * @param platform
     *            - the platform loader for the platform the agent will execute on.
     * @param defaultAgentLoader
     *            - the name of the default agent loader.
     * @param agentLoaders
     *            - the {@link Map} of agent loader names and respective {@link AgentLoader} instances.
     * @param agentPackages
     *            - the {@link Set} of packages containing agent code.
     * @return an {@link AgentManager} instance that can be used to control the lifecycle of the just loaded agent, if
     *         the loading was successful; <code>null</code> otherwise.
     */
    protected AgentCreationData preloadAgent(XMLNode agentNode, String agentName, String containerName, PlatformLoader platform)
    {
        // loader
        String agentLoaderName = PlatformUtils.getParameterValue(agentNode, AgentParameterName.AGENT_LOADER.toString());
        if(agentLoaderName == null)
            agentLoaderName = defaultAgentLoader;
        if(!agentLoaders.containsKey(agentLoaderName))
            return (AgentCreationData) log.lr(null, "agent loader [" + agentLoaderName + "] is unknown. agent ["
                    + agentName + "] will not be created.");
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
        
        AgentCreationData agentCreationData = new AgentCreationData(agentName, parameters, agentPackages,
                containerName, platform.getName(), loader, agentNode);
        if(!loader.preload(agentCreationData, platform, log))
        {
            log.error("Agent [" + agentName + "] cannot be loaded.");
            return null;
        }
        return agentCreationData;
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
     * @param defaultAgentLoader
     *            - the name of the default agent loader.
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
    protected void loadContainerAgents(Iterator<XMLNode> containerNodes)
    {
        while(containerNodes.hasNext())
        {
            XMLNode containerConfig = containerNodes.next();
            
            // container information
            String containerName = containerConfig.getAttributeValue("name");
            allContainers.put(containerName, true);
            
            
            Vector<Object> args = new Vector<Object>();
            args.add(containerName);
            graphicalUserInterface.doOutput("CORE-NEW-CONTAINER", args);
            
            /*
            // container has no agents, but should be created in said platform
            if(!containerConfig.getNodeIterator("agenlt").hasNext())
            {
                String platformName = containerConfig.getAttributeValue("platform");
                if(platformName == null)
                    platformName = defaultPlatform;
                if((platformName != null) && platforms.containsKey(platformName))
                {
                    if(!platformContainers.containsKey(platformName))
                        platformContainers.put(platformName, new HashSet<String>());
                    platformContainers.get(platformName).add(containerName);
                }
            }
            */
            
            // set up creation for all agents in the container
            for(Iterator<XMLNode> agentNodes = containerConfig.getNodeIterator("agent"); agentNodes.hasNext();)
            {
                System.out.println("Gets here?????????");
                XMLNode agentNode = agentNodes.next();
                // agent name
                String agentName = PlatformUtils.getParameterValue(agentNode, AgentParameterName.AGENT_NAME.toString());
                if(agentName == null)
                {
                    log.error("agent has no name; will not be created.");
                    continue;
                }
                // platform
                String platformName = PlatformUtils.getParameterValue(agentNode,
                        AgentParameterName.AGENT_PLATFORM.toString());
                if(platformName == null)
                    platformName = defaultPlatform; // no platform specified: go to default
                if(!platforms.containsKey(platformName))
                {
                    log.error("unknown platform [" + platformName + "]; agent [" + agentName + "] will not be created.");
                    continue;
                }
                
                // load agent
                AgentCreationData agentCreationData = preloadAgent(agentNode, agentName, containerName, platforms.get(platformName));
                if(agentCreationData == null)
                    continue;
                allAgents.add(agentCreationData);
                
                Vector<Object> agentArgs = new Vector<Object>();
                agentArgs.add(containerName);
                agentArgs.add(agentName);
                graphicalUserInterface.doOutput("CORE-NEW-AGENT", agentArgs);
                /*
                if (!platformContainers.containsKey(platformName))
                    platformContainers.put(platformName, new HashSet<String>());
                platformContainers.get(platformName).add(containerName);
                */
                log.trace("Agent [" + agentName + "] will be run on platform [" + platformName
                        + "], in local container [" + containerName + "]");
            }
        }
    }
    
    
    @Override
    public void buildAgentPackages() {
        // add agent packages specified in the scenario
        Iterator<XMLNode> packagePathsIt = scenarioTree.getRoot()
                .getNodeIterator(AgentParameterName.AGENT_PACKAGE.toString());
        while (packagePathsIt.hasNext())
            agentPackages.add((String) packagePathsIt.next().getValue());

    }

    @Override
    public void buildPlatform() throws SimulationEzception, PlatformException{
        // iterate over platform entries in the scenario
        defaultPlatform = loadPlatforms(
                scenarioTree.getRoot().getNodeIterator(AgentParameterName.AGENT_PLATFORM.toString()),
                BootSettingsManager.getInst());
        
        log.info("Default platform builded");

    }

    @Override
    public void buildAgentLoaders() throws AgentLoaderException {
        // iterate over agent loader entries in the scenario
        defaultAgentLoader = loadAgentLoaders(scenarioTree.getRoot().getNodeIterator(AgentParameterName.AGENT_LOADER.toString()));
        
    }

    @Override
    public void buildContainerAgents() {
        if (scenarioTree.getRoot().getNodeIterator("initial").hasNext())
            // iterate containers and find agents
            loadContainerAgents(scenarioTree.getRoot().getNodeIterator("initial").next().getNodeIterator("container"));

    }

    @Override
    public void buildTimeline() {
        // load timeline (if any)

        if (scenarioTree.getRoot().getNodeIterator(SimulationManager.TIMELINE_NODE.toString()).hasNext())
            timeline = scenarioTree.getRoot().getNodeIterator(SimulationManager.TIMELINE_NODE.toString()).next();

    }

    @Override
    public void buildGUI() {
        
    }
}
