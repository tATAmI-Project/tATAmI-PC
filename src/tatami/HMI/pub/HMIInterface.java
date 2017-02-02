package tatami.HMI.pub;

import tatami.HMI.src.PC.fx.HMIPCGUI;
import tatami.HMI.src.PC.fx.PCGUIThread;
import tatami.core.agent.io.AgentActiveIO;

public enum HMIInterface {
    INST;
    
    public enum HMIType{PC, ANDROID, CLI};
    
    final HMIType HMI_TYPE = HMIType.PC;
    
    AgentActiveIO hmi;
    
    public AgentActiveIO getHMI(){
        if(hmi != null)
            return hmi;
        switch (HMI_TYPE) {
        case PC:
            try {
                PCGUIThread guiThread = new PCGUIThread();
                guiThread.start();
                while (HMIPCGUI.self == null)
                    ;
                while (!HMIPCGUI.self.isStarted())
                    ;
                hmi = HMIPCGUI.self;
                System.out.println("Reach here");
            } catch (Exception e) {
                System.out.println("There was an issue starting the PC interface");
            }
            break;
        case ANDROID:
            break;
        case CLI:
            break;
        default:
            break;
        }
        return hmi;
    }
    
    
}
