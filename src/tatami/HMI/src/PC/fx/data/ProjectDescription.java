package tatami.HMI.src.PC.fx.data;

import java.util.Vector;

import tatami.HMI.src.PC.fx.MenuItemsController;

public class ProjectDescription {
    
    ProjectData mProjetData;
    Vector<PlatformDescription> mPlatformDescription;
    MenuItemsController mController;
    
    PlatformDescription.AllPlatformsDescription mAllPlatformDescription;
    
    
    public ProjectDescription(MenuItemsController controller, String filepath, String projectName){
        mProjetData = new ProjectData(filepath, projectName);
        mPlatformDescription = new Vector<PlatformDescription>();
        mController = controller;
        mAllPlatformDescription = new PlatformDescription.AllPlatformsDescription();
    }
    
    public PlatformDescription.AllPlatformsDescription getAllPlatformsDescription(){
        return mAllPlatformDescription;
    }
    
    public void addPlatformName(String name){
        mPlatformDescription.addElement(new PlatformDescription(name));
    }
    
    public int platformsCount(){
        return mPlatformDescription.size();
    }
    
    public PlatformDescription getPlatformDescription(int index){
        return mPlatformDescription.get(index);
    }
    
    public ProjectData getProjectData(){
        return mProjetData;
    }
}
