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
            System.out.println("//////////////// composite");
            return new CompositeAgentLoader();
        }
        return null;
    }
}
