package tatami.core.agent.agent_type;

import tatami.core.agent.CompositeAgent;
import tatami.core.agent.CompositeAgentLoader;
import tatami.simulation.AgentLoader;
import tatami.simulation.DefaultPlatform;

public class AgentLoaderFactory {
    public enum AgentLoaderType{COMPOSITE_AGENT};
    
    private static AgentLoaderFactory singleton = null;
    
    public static AgentLoaderFactory getInst(){
        if(singleton == null)
            singleton = new AgentLoaderFactory();
        return singleton;
    }
    
    public AgentLoader newInst(String loaderName){
        if(loaderName.equals("composite")){
            return new CompositeAgentLoader();
        }
        if(loaderName.equals("eventbased")){
            return new EventBasedAgent();
        }
        return null;
    }
}
