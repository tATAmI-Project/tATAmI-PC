package tatami.core.environment;

import tatami.core.agent.agent_type.AgentLoaderFactory;

public class Environment {
    private static Environment singleton = null;
    
    private Environment(){
        
    }

    public static Environment getInst() {
        if (singleton == null)
            singleton = new Environment();
        return singleton;
    }
    
    public void addPlatform(){
        
    }
    
    public void onPlatformMessage(String from, String to, byte[] content){
        
    }
}
