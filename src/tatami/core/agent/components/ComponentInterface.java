package tatami.core.agent.components;

import tatami.core.agent.artefacts.ArtefactListener;
import tatami.core.agent.messages.AgentMessage;
import tatami.simulation.AgentLoader;

public abstract class ComponentInterface implements ArtefactListener{
    
    String mName;
    
    protected AgentLoader mParent;
    
    public abstract void onInput(AgentMessage event);
    
    public String getName(){
        return mName;
    }
    
}