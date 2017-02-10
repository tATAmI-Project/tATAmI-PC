package tatami.core.agent.agent_type;

import tatami.core.agent.CompositeAgent;
import tatami.core.agent.CompositeAgentLoader;
import tatami.simulation.AgentCreationData;
import tatami.simulation.Agent;
import tatami.simulation.DefaultPlatform;

public class AgentLoaderFactory {
    public enum AgentLoaderType{COMPOSITE_AGENT};
    
    private static AgentLoaderFactory singleton = null;
    
    public static AgentLoaderFactory getInst(){
        if(singleton == null)
            singleton = new AgentLoaderFactory();
        return singleton;
    }
    
    public Agent newInst(AgentCreationData agentCreationData){
        //if(loaderName.equals("composite")){
        //    return new CompositeAgentLoader();
        //}
        if(agentCreationData.getType().equals("eventbased")){
            return new EventBasedAgent(agentCreationData);
        }
        return null;
    }
}
