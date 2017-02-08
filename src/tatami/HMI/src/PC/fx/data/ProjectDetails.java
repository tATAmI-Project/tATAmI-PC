package tatami.HMI.src.PC.fx.data;

import java.util.Vector;

import tatami.HMI.src.PC.fx.MenuItemsController;

public class ProjectDetails {
    
    ProjectDescription mProjetData;
    Vector<PlatformDescription> mPlatformDescription;
    MenuItemsController mController;
    Vector<ContainerDescription> mContainerDescription;
    
    PlatformDescription.AllPlatformsDescription mAllPlatformDescription;
    ContainerDescription.AllContainersDescription mAllContainerDescription;
    
    
    public ProjectDetails(MenuItemsController controller, String filepath, String projectName){
        mProjetData = new ProjectDescription(filepath, projectName);
        mPlatformDescription = new Vector<PlatformDescription>();
        mContainerDescription = new Vector<ContainerDescription>();
        mController = controller;
        mAllPlatformDescription = new PlatformDescription.AllPlatformsDescription();
        mAllContainerDescription = new ContainerDescription.AllContainersDescription();
    }
    
    public PlatformDescription.AllPlatformsDescription getAllPlatformsDescription(){
        return mAllPlatformDescription;
    }
    
    public void addContainerDescription(String name){
        mContainerDescription.addElement(new ContainerDescription(name));
    }
    
    public int containersCount(){
        return mContainerDescription.size();
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
    
    public ContainerDescription getContainerDescription(int index){
        return mContainerDescription.get(index);
    }
    
    public ProjectDescription getProjectData(){
        return mProjetData;
    }
}
