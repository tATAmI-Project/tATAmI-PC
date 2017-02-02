package tatami.HMI.src.PC.fx.data;

import tatami.HMI.src.PC.fx.Tree.ITreeNode;

public class PlatformDescription implements ITreeNode{
    
    String mName;
    
    public static class AllPlatformsDescription implements ITreeNode{
        
        public String toString(){
            return "Platforms";
        }
    }
   
    public PlatformDescription(String name){
        mName = name;
    }
    
    public String toString(){
        return mName;
    }
}
