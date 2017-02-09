package tatami.core.agent.components;

public class ComponentFactory {
    private static ComponentFactory singleton = null;

    public static ComponentFactory getInst() {
        if (singleton == null)
            singleton = new ComponentFactory();
        return singleton;
    }
    
    public ComponentInterface newInst(String name){
        return null;
    }
}
