package tatami.HMI.src.PC.fx.data;

import tatami.HMI.src.PC.fx.Tree.ITreeNode;

public class AgentDescription implements ITreeNode{
    String mName;
   
    public AgentDescription(String name){
        mName = name;
    }
    
    public String toString(){
        return mName;
    }
}
