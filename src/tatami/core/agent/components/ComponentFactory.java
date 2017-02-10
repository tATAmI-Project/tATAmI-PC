package tatami.core.agent.components;

import tatami.core.agent.agent_type.TatamiAgent;
import tatami.core.agent.components.test.TestMessagingComponent;

public class ComponentFactory {
    private static ComponentFactory singleton = null;

    public static ComponentFactory getInst() {
        if (singleton == null)
            singleton = new ComponentFactory();
        return singleton;
    }
    
    public ComponentInterface newInst(String name, TatamiAgent parent){
        if(name.equals("test_messaging_component")){
            return new TestMessagingComponent(parent);
        }
        return null;
    }
}
