package tatami.HMI.pub;

import tatami.core.agent.io.AgentActiveIO;

public enum HMIInterface {
    INST;
    
    public enum HMIType{PC, ANDROID, CLI};
    
    final HMIType HMI_TYPE = HMIType.CLI;
    
    AgentActiveIO hmi;
    
    private HMIInterface() {
        
    }
    
    public AgentActiveIO getHMI(){
        if(hmi == null){
            //TODO Add the actual instantiation
            
            switch(HMI_TYPE){
            case PC:
                break;
            case ANDROID:
                break;
            case CLI:
                break;
            default:
                break;
            }
            
        }
        return hmi;
    }
    
    
}
