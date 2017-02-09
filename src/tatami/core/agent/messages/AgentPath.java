package tatami.core.agent.messages;

import java.util.ArrayList;

public class AgentPath {
    String[] mLocations = null;
    
    String mRaw;
    
    public AgentPath(String path){
        mLocations = path.split("/");
        mRaw = path;
    }
    
    public int depth(){
        return mLocations.length;
    }
    
    public String getNode(int index){
        return mLocations[index];
    }
    
    public String getComponenet(){
        return mLocations[mLocations.length-1];
    }
    
    public String getAgent(){
        return mLocations[mLocations.length-2];
    }
    
    public String toString(){
        return mRaw;
    }
}
