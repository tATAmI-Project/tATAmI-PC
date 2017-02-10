package tatami.core.agent.components.test;

import tatami.core.agent.components.ComponentInterface;
import tatami.core.agent.messages.AgentMessage;
import tatami.core.agent.messages.Command;
import tatami.simulation.Agent;

public class TestMessagingComponent extends ComponentInterface {
    
    public TestMessagingComponent(Agent parent){
        mParent = parent;
        log.trace("Test Messaging Component created successfully");
    }

    /**
     * Message received from another component
     */
    @Override
    public void onInput(AgentMessage message) {
       
    }

    /**
     * Data received from the artefact
     */
    @Override
    public void onArtefactData(int artefactId, byte[] data) {
        
    }

    /**
     * Command received from the agent
     */
    @Override
    public void onCommand(Command command) {
        // TODO Auto-generated method stub
        
    }
}
