package tatami.core.agent.artefacts;

import java.util.TreeMap;

public class ArtefactCreationData extends TreeMap<String, String>{
    
    public ArtefactCreationData(){
        
    }
    
    public int getId(){
        return Integer.parseInt(get("id"));
    }
    
    public String getName(){
        return get("name");
    }
}
