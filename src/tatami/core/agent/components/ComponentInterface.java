package tatami.core.agent.components;

import tatami.core.agent.AgentComponent.ComponentCreationData;
import tatami.core.agent.messages.AgentMessage;

public abstract class ComponentInterface {
    public abstract void onEvent(AgentMessage event);
    
    public void buildCOmponenet(ComponentCreationData parameters){
        
    }
}
