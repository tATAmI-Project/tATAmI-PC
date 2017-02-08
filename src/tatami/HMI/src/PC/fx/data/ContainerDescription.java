package tatami.HMI.src.PC.fx.data;

import tatami.HMI.src.PC.fx.Tree.ITreeNode;

public class ContainerDescription implements ITreeNode{

    String mName;
    
    public static class AllContainersDescription implements ITreeNode{
        
        public String toString(){
            return "Containers";
        }
    }
   
    public ContainerDescription(String name){
        mName = name;
    }
    
    public String toString(){
        return mName;
    }
}
