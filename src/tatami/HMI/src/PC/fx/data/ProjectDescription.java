package tatami.HMI.src.PC.fx.data;

import tatami.HMI.src.PC.fx.Tree.ITreeNode;

public class ProjectDescription implements ITreeNode{
    public String mFilePath;
    public String mProjectName;
    
    
    
    public ProjectDescription(String filepath, String projectName){
        mFilePath = filepath;
        mProjectName = projectName;
    }
    
    public String toString(){
        return mProjectName;
    }
}