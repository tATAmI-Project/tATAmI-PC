package tatami.HMI.pub;

import javafx.application.Application;
import net.xqhs.util.logging.UnitComponentExt;
import tatami.HMI.src.PC.fx.HMIPCGUI;
import tatami.core.agent.io.AgentActiveIO;
import tatami.core.util.platformUtils.PlatformUtils;

public enum HMIInterface {
    INST;
    
    public enum HMIType{PC, ANDROID, CLI};
    
    final HMIType HMI_TYPE = HMIType.PC;
    
    AgentActiveIO hmi;
    
    public AgentActiveIO getHMI(){
        if(hmi == null){
            switch(HMI_TYPE){
            case PC:
                Application.launch(HMIPCGUI.class);
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
