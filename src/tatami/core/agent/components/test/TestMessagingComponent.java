package tatami.core.agent.components.test;

import tatami.core.agent.components.ComponentInterface;
import tatami.core.agent.messages.AgentMessage;
import tatami.simulation.AgentLoader;

public class TestMessagingComponent extends ComponentInterface {
    
    public TestMessagingComponent(AgentLoader parent){
        mParent = parent;
    }

    @Override
    public void onInput(AgentMessage message) {
       //doStuff
        
    }

    @Override
    public void onArtefactData(byte[] data) {
        
    }
}
