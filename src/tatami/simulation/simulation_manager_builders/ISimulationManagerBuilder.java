package tatami.simulation.simulation_manager_builders;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.xqhs.util.XML.XMLTree.XMLNode;
import tatami.core.agent.artefacts.ArtefactInterface;
import tatami.core.agent.components.ComponentInterface;
import tatami.core.agent.io.AgentActiveIO;
import tatami.simulation.Agent;
import tatami.simulation.AgentCreationData;
import tatami.simulation.PlatformLoader;
import tatami.simulation.simulation_manager_builders.SimulationManagerXMLBuilder.PlatformException;
import tatami.simulation.simulation_manager_builders.SimulationManagerXMLBuilder.SimulationEzception;

public abstract class ISimulationManagerBuilder {
    
    Map<String, Agent> allAgents = new HashMap<String, Agent>();
    // platform name -> platform loader
    Map<String, PlatformLoader> platforms = new HashMap<String, PlatformLoader>();
    // container name -> do create (true for local containers, false for remote)
    Map<String, Boolean> allContainers = new HashMap<String, Boolean>();
    // platform name -> names of containers to be present in the platform
    Map<String, Set<String>> platformContainers = new HashMap<String, Set<String>>();
    // platform name -> container name -> agent name -> agent manager
    // for the agent to be started in the container, on the platform
    Set<AgentCreationData> allAgentDetails = new HashSet<AgentCreationData>();
    
    Map<String, ComponentInterface> allComponenets;
    
    Map<String, ArtefactInterface> allArtefacts;
    
    AgentActiveIO userInterface;
    
    abstract public void buildPlatform() throws SimulationEzception, PlatformException;
    
    abstract public void buildContainerAgents();
    
    abstract public void loadXML(String path);
    
    abstract public void setGUI(AgentActiveIO uInterface);
    
    abstract public void buildArtefacts();
    
    
    public Map<String, PlatformLoader> getPlatform(){
        return platforms;
    }
    
    public Map<String, Set<String>> getPlatformContainers(){
        return platformContainers;
    }
    
    public Map<String, Boolean> getAllContainers(){
        return allContainers;
    }
    
    public Set<AgentCreationData> getAllAgents(){
        return allAgentDetails;
    }
    
    public AgentActiveIO getGUI(){
        return userInterface;
    }
}
