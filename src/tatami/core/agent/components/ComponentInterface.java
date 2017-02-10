package tatami.core.agent.components;

import java.io.Serializable;

import net.xqhs.util.logging.UnitComponentExt;
import tatami.core.agent.artefacts.ArtefactListener;
import tatami.core.agent.messages.AgentMessage;
import tatami.core.agent.messages.Command;
import tatami.core.util.platformUtils.PlatformUtils;
import tatami.simulation.Agent;

public abstract class ComponentInterface implements ArtefactListener, Serializable{
    
    protected UnitComponentExt log = (UnitComponentExt) new UnitComponentExt().setUnitName("ComponentInterface").setLoggerType(
            PlatformUtils.platformLogType());
    
    String mName;
    
    protected Agent mParent;
    
    public abstract void onInput(AgentMessage event);
    
    public abstract void onCommand(Command command);
    
    public String getName(){
        return mName;
    }
    
}