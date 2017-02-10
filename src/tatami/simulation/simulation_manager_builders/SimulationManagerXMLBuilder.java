package tatami.simulation.simulation_manager_builders;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import net.xqhs.util.XML.XMLTree;
import net.xqhs.util.XML.XMLTree.XMLNode;
import net.xqhs.util.logging.UnitComponentExt;
import tatami.core.agent.AgentComponent;
import tatami.core.agent.agent_type.TatamiAgent;
import tatami.core.agent.agent_type.AgentLoaderFactory;
import tatami.core.agent.artefacts.ArtefactCreationData;
import tatami.core.agent.artefacts.ArtefactInterface;
import tatami.core.agent.artefacts.ArtefactsFactory;
import tatami.core.agent.components.ComponentCreationData;
import tatami.core.agent.io.AgentActiveIO;
import tatami.core.platforms.PlatformDescriptor;
import tatami.core.platforms.PlatformFactory;
import tatami.core.util.platformUtils.PlatformUtils;
import tatami.simulation.AgentCreationData;
import tatami.simulation.AgentManager;
import tatami.simulation.BootSettingsManager;
import tatami.simulation.PlatformLoader;
import tatami.simulation.PlatformLoader.StandardPlatformType;
import tatami.simulation.SimulationManager;




public class SimulationManagerXMLBuilder extends ISimulationManagerBuilder{
    
    public enum AgentParameterName {
        
        // ///////// Simulation/Boot
        /**
         * The class of the agent implementation, in case the loader needs it.
         * 
         * Used by {@link Boot}.
         */
        AGENT_CLASS("classpath"),
        
        /**
         * The {@link AgentLoader} to use for this agent.
         */
        AGENT_LOADER("loader"),
        
        /**
         * The {@link PlatformLoader} to use for this agent.
         */
        AGENT_PLATFORM("platform"),
        /**
         * The name of the agent.
         */
        AGENT_NAME("name");
        
        /**
         * The name of the parameter, as appearing in the scenario file.
         */
        String  name    = null;
        
        /**
         * @param parName
         *            - the name of the parameter as will appear in the scenario file.
         */
        private AgentParameterName(String parName){
            name = parName;
        }
        
        @Override
        public String toString(){
            return name;
        }
    }
    
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

    
    XMLTree scenarioTree;
    
    /**
     * Name of XML nodes in the scenario representing components.
     */
    private static final String COMPONENT_NODE_NAME         = "component";
    /**
     * The name of nodes containing component parameters.
     */
    private static final String PARAMETER_NODE_NAME         = "parameter";
    
    /**
     * The name of the attribute representing the name of the component in the component node.
     */
    private static final String COMPONENT_NAME_ATTRIBUTE    = "name";
    /**
     * The name of the attribute of a parameter node holding the name of the parameter.
     */
    private static final String PARAMETER_NAME              = "name";
    /**
     * The name of the attribute of a parameter node holding the value of the parameter.
     */
    private static final String PARAMETER_VALUE             = "value";
    
    public SimulationManagerXMLBuilder() throws SimulationEzception{
        
    }
    
    public void loadXML(String path) {
        if(getGUI() == null){
            log.error("An user interface needs to be builded before");
            return;
        }
        scenarioTree = BootSettingsManager.getInst().load(path);

        Vector<Object> components = new Vector<Object>();
        components.addElement(path);
        components.addElement(path.substring(path.lastIndexOf("/") + 1, path.length()));
        getGUI().doOutput("CORE-NEW_PROJECT", components);
    }
    
    public void setGUI(AgentActiveIO uInterface){
        userInterface = uInterface;
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
    private void loadPlatforms(Iterator<XMLNode> platformNodes, BootSettingsManager settings) throws SimulationEzception, PlatformException
    {

        while (platformNodes.hasNext()) {
            XMLNode platformNode = platformNodes.next();
            
            PlatformDescriptor platformDescriptor = new PlatformDescriptor(platformNode);

            String platformName = platformDescriptor.getName();

            if (platformDescriptor.getName() == null)
                throw new SimulationEzception("The platform name is null");

            try {
                platforms.put(platformName, PlatformFactory.getInst().newInst(platformName).setConfig(platformDescriptor));
                
                log.info("Platform [" + platformName + "] prepared.");
            } catch (Exception e) {
                throw new SimulationEzception("Loading platform [" + platformDescriptor.getName()
                        + "] failed; platform will not be available:" + PlatformUtils.printException(e));
            }
        }
        
        if(platforms.isEmpty()){
            throw new SimulationEzception("No Platform could be loaded!");
        }
        for(String platformName: platforms.keySet()){
            Vector<Object> args = new Vector<Object>();
            args.addElement(platformName);
            userInterface.doOutput("CORE-NEW-PLATFORM", args);
        }
    }
    
    protected ArrayList<ComponentCreationData> loadComponenets(Iterator<XMLNode> componenetsNodes){
        ArrayList<ComponentCreationData> componenetsCreationData = new ArrayList<ComponentCreationData>();
        while(componenetsNodes.hasNext())
        {
            XMLNode componentNode = componenetsNodes.next();
            String componentName = componentNode.getAttributeValue(COMPONENT_NAME_ATTRIBUTE);
            AgentComponent component = null;
            
            // load component arguments
            ComponentCreationData componentData = new ComponentCreationData();
            Iterator<XMLNode> paramsIt = componentNode.getNodeIterator(PARAMETER_NODE_NAME);
            componentData.put(COMPONENT_NAME_ATTRIBUTE, componentNode.getAttributeValue(COMPONENT_NAME_ATTRIBUTE));
            
            while(paramsIt.hasNext())
            {
                XMLNode param = paramsIt.next();
                componentData.put(param.getAttributeValue(PARAMETER_NAME), param.getAttributeValue(PARAMETER_VALUE));
            }
            componenetsCreationData.add(componentData);
        }
        
        return componenetsCreationData;
    }
    
    /**
     * Loads agent information from the scenario file and pre-loads the agent using the appropriate {@link TatamiAgent}.
     * <p>
     * If successful, the method returns an {@link AgentCreationData} instance that can be subsequently be used in a
     * call to {@link TatamiAgent#load(AgentCreationData)} to obtain an {@link AgentManager} instance.
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
     *            - the {@link Map} of agent loader names and respective {@link TatamiAgent} instances.
     * @param agentPackages
     *            - the {@link Set} of packages containing agent code.
     * @return an {@link AgentManager} instance that can be used to control the lifecycle of the just loaded agent, if
     *         the loading was successful; <code>null</code> otherwise.
     */
    protected AgentCreationData buildAgent(XMLNode agentNode, String agentName, String containerName, String agentType)
    {
        ArrayList<ComponentCreationData> components = loadComponenets(agentNode.getNodeIterator(COMPONENT_NODE_NAME));
        AgentCreationData agentCreationData = new AgentCreationData(agentName, containerName, agentType, components);
        
        // get all parameters and put them into an AgentParameters instance.
        for(Iterator<XMLNode> paramIt = agentNode.getNodeIterator("parameter"); paramIt.hasNext();){
            XMLNode param = paramIt.next();
            agentCreationData.put(param.getAttributeValue("name"), param.getAttributeValue("value"));

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
     *            - the {@link Map} of platform names and respective {@link TatamiAgent} instances.
     * @param agentPackages
     *            - the {@link Set} of package names where agent code may be located.
     * @param allContainers
     *            - the {@link Map} in which the method will fill in all containers, specifying the name and whether the
     *            container should be created.
     * @param platformContainers
     *            - the {@link Map} in which the method will fill in the containers to load on the local machine, for
     *            each platform (the map contains: platform name &rarr; set of containers to load).
     * @param allAgentDetails
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
            userInterface.doOutput("CORE-NEW-CONTAINER", args);
            
            // set up creation for all agents in the container
            for(Iterator<XMLNode> agentNodes = containerConfig.getNodeIterator("agent"); agentNodes.hasNext();)
            {
                XMLNode agentNode = agentNodes.next();
                loadAgent(agentNode, containerName);
            }
        
        }
    }
    
    public void loadAgent(XMLNode agentNode, String containerName){
     // agent name
        String agentName = PlatformUtils.getParameterValue(agentNode, AgentParameterName.AGENT_NAME.toString());
        
        String agentType = agentNode.getAttributeValue("type");
        // load agent
        AgentCreationData agentCreationData = buildAgent(agentNode, agentName, containerName, agentType);
        if(agentCreationData == null)
            return;
        allAgents.put(agentName, AgentLoaderFactory.getInst().newInst(agentCreationData));
        
        Vector<Object> agentArgs = new Vector<Object>();
        agentArgs.add(containerName);
        agentArgs.add(agentName);
        userInterface.doOutput("CORE-NEW-AGENT", agentArgs);
        
        log.trace("Agent [" + agentName + "] will be run in local container [" + containerName + "]");
    }

    @Override
    public void buildPlatform() throws SimulationEzception, PlatformException{
        // iterate over platform entries in the scenario
        
        loadPlatforms(scenarioTree.getRoot().getNodeIterator(AgentParameterName.AGENT_PLATFORM.toString()), BootSettingsManager.getInst());
        
        for(String platformName: platforms.keySet()){
            log.info("Platform " + platformName + " loaded.");
        }

    }

    @Override
    public void buildContainerAgents() {
        if (scenarioTree.getRoot().getNodeIterator("initial").hasNext())
            // iterate containers and find agents
            loadContainerAgents(scenarioTree.getRoot().getNodeIterator("initial").next().getNodeIterator("container"));

    }

    @Override
    public void buildArtefacts() {
        allArtefacts = new TreeMap<String, ArtefactInterface>();
        Iterator<XMLNode> artefactNodes = scenarioTree.getRoot().getNodeIterator("artefact");
        while(artefactNodes.hasNext()){
            XMLNode platformNode = artefactNodes.next();
            ArtefactCreationData artefactCreationData = new ArtefactCreationData();
            artefactCreationData.put("name", platformNode.getAttributeValue("name"));
            artefactCreationData.put("id", platformNode.getAttributeValue("id"));
            allArtefacts.put(artefactCreationData.getName(), ArtefactsFactory.getInst().newInst(artefactCreationData));
        }
    }
}
