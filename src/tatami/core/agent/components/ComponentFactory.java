package tatami.core.agent.components;

import tatami.core.agent.components.test.TestMessagingComponent;
import tatami.simulation.Agent;

public class ComponentFactory {
    private static ComponentFactory singleton = null;

    public static ComponentFactory getInst() {
        if (singleton == null)
            singleton = new ComponentFactory();
        return singleton;
    }
    
    public ComponentInterface newInst(String name, Agent parent){
        if(name.equals("test_messaging_component")){
            return new TestMessagingComponent(parent);
        }
        return null;
    }
}
