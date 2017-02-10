package tatami.core.agent.artefacts;

import java.util.LinkedHashSet;
import java.util.Set;

public abstract class ArtefactInterface {
    
    protected Set<ArtefactListener> mComponents;
    
    protected int mArtefactId;
    
    protected String mName;
    
    public ArtefactInterface(){
        mComponents = new LinkedHashSet<ArtefactListener>();
        mName = "Untitled";
        mArtefactId = -1;
    }
    
    public void registerListener(ArtefactListener component){
        mComponents.add(component);
    }
    
    public void onData(byte[] data){
        for(ArtefactListener component: mComponents){
            component.onArtefactData(mArtefactId, data);
        }
    }
    
    public String getName(){
        return mName;
    }
}
