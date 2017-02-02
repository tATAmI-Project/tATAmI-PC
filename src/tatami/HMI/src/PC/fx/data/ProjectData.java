package tatami.HMI.src.PC.fx.data;

import tatami.HMI.src.PC.fx.Tree.ITreeNode;

public class ProjectData implements ITreeNode{
    public String mFilePath;
    public String mProjectName;
    
    
    
    public ProjectData(String filepath, String projectName){
        mFilePath = filepath;
        mProjectName = projectName;
    }
    
    public String toString(){
        return mProjectName;
    }
}