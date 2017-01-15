package tatami.simulation.simulation_manager_builders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.xqhs.util.XML.XMLTree.XMLNode;
import tatami.simulation.AgentCreationData;
import tatami.simulation.AgentLoader;
import tatami.simulation.PlatformLoader;
import tatami.simulation.simulation_manager_builders.SimulationManagerXMLBuilder.AgentLoaderException;
import tatami.simulation.simulation_manager_builders.SimulationManagerXMLBuilder.PlatformException;
import tatami.simulation.simulation_manager_builders.SimulationManagerXMLBuilder.SimulationEzception;

public abstract class ISimulationManagerBuilder {
    
 // the name of the default platform
    String defaultPlatform = PlatformLoader.DEFAULT_PLATFORM.toString();
    // the name of the default agent loader
    String defaultAgentLoader = AgentLoader.DEFAULT_LOADER.toString();
    // platform name -> platform loader
    Map<String, PlatformLoader> platforms = new HashMap<String, PlatformLoader>();
    // agent loader name -> agent loader
    Map<String, AgentLoader> agentLoaders = new HashMap<String, AgentLoader>();
    // package names where agent code (adf & java) may be located
    List<String> agentPackages = new ArrayList<String>();
    // container name -> do create (true for local containers, false for remote)
    Map<String, Boolean> allContainers = new HashMap<String, Boolean>();
    // platform name -> names of containers to be present in the platform
    Map<String, Set<String>> platformContainers = new HashMap<String, Set<String>>();
    // platform name -> container name -> agent name -> agent manager
    // for the agent to be started in the container, on the platform
    Set<AgentCreationData> allAgents = new HashSet<AgentCreationData>();
    
    XMLNode timeline = null;;
    
    abstract public void buildPlatform() throws SimulationEzception, PlatformException;
    
    abstract public void buildAgentLoaders() throws AgentLoaderException;
    
    abstract public void buildAgentPackages();
    
    abstract public void buildContainerAgents();
    
    abstract public void buildTimeline();
    
    
    public Map<String, PlatformLoader> getPlatform(){
        return platforms;
    }
    
    public String getAgentLoaders(){
        return defaultAgentLoader;
    }
    
    public Map<String, Set<String>> getPlatformContainers(){
        return platformContainers;
    }
    
    public Map<String, Boolean> getAllContainers(){
        return allContainers;
    }
    
    public Set<AgentCreationData> getAllAgents(){
        return allAgents;
    }
    
    public XMLNode getTimeline(){
        return timeline;
    }
}
