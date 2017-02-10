package tatami.core.agent.components;

import java.util.HashMap;

import tatami.core.util.ParameterSet;

/**
 * Alias of {@link ParameterSet}.
 * 
 * @author Andrei Olaru
 */
public class ComponentCreationData extends HashMap<String, String>
{
    /**
     * The serial UID.
     */
    private static final long serialVersionUID = 5069937206709568881L;
    
    public ComponentCreationData(){
        
    }
    
    public String getName(){
        return get("name");
    }
}