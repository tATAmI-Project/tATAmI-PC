package tatami.HMI.src.PC.fx;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Vector;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import tatami.HMI.src.PC.fx.Tree.ITreeNode;
import tatami.HMI.src.PC.fx.Tree.TreeElementsFactory;
import tatami.HMI.src.PC.fx.data.AgentDescription;
import tatami.HMI.src.PC.fx.data.ArtefactDescription;
import tatami.HMI.src.PC.fx.data.ContainerDescription;
import tatami.HMI.src.PC.fx.data.PlatformDescription;
import tatami.HMI.src.PC.fx.data.ProjectDetails;

public class MenuItemsController implements Initializable{
    
    @FXML
    private MenuItem startSimulationMenuItem;
    
    @FXML
    private MenuItem stopSimulationMenuItem;
    
    @FXML
    private MenuItem loadScenarioMenuItem; 
    
    @FXML
    private TreeTableView<ITreeNode> mainTreeTable; 
    
    @FXML
    private TreeTableColumn<ITreeNode, String> mainColumnTableView;
    
    @FXML
    private ImageView startSimulationTooblarIcon;
    
    @FXML
    private ImageView stopSimulationTooblarIcon;
    
    ContextMenu platformDescriptionContextMenu;
    
    HashMap<String, EventHandler<ActionEvent> > eventHandlers = null;
    
    private HMIPCGUI mParent;
    
    final FileChooser fileChooser = new FileChooser();
    
    ProjectDetails mProjectDescription;
    
    public MenuItemsController(HMIPCGUI parent) {
        mParent = parent;
    }

    @Override
    public void initialize(URL url, ResourceBundle resource) {
        startSimulationTooblarIcon.setImage(new Image(new File("res/icons/caret-right-2x.png").toURI().toString()));
        stopSimulationTooblarIcon.setImage(new Image(new File("res/icons/circle-x-2x.png").toURI().toString()));
        platformDescriptionContextMenu = new ContextMenu();
        platformDescriptionContextMenu.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.SECONDARY) {
                    System.out.println("consuming right release button in cm filter");
                    event.consume();
                }
            }
        });
        
        MenuItem menuItem1 = new MenuItem("line 1");
        MenuItem menuItem2 = new MenuItem("line 2");
        MenuItem menuItem3 = new MenuItem("line 3");

        platformDescriptionContextMenu.getItems().addAll(menuItem1, menuItem2, menuItem3);
    }
    
    @FXML
    private void onStartSimulationMenuItemPressed(ActionEvent event) {
        Vector<Object> args = new Vector<Object>();
        args.addElement("start");
        mParent.doOutput("GUI", args);
    }
    
    @FXML
    private void onStopSimulationMenuItemPressed(ActionEvent event) {
        Vector<Object> args = new Vector<Object>();
        args.addElement("stop");
        mParent.doOutput("GUI", args);
    }
    
    @FXML
    private void onLoadScenarioMenuItem(ActionEvent event) {
        File file = fileChooser.showOpenDialog(mParent.getStage());
        if (file != null) {
            Vector<Object> args = new Vector<Object>();
            args.addElement(file);
            mParent.doOutput("GUI-LOAD_SCENARIO", args);
            String path = file.toString();
            
        }
    }
    
    @FXML
    private void onStartSimulationTooblarButton(ActionEvent event){
        Vector<Object> args = new Vector<Object>();
        mParent.doOutput("GUI-START-SIMULATION", args);
    }
    @FXML
    private void onStopSimulationTooblarButton(ActionEvent event){
        
    }
    
    public void newProjectDescription(String path, String name){
        MenuItemsController controller = this;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                mainColumnTableView.setCellValueFactory(
                        new Callback<TreeTableColumn.CellDataFeatures<ITreeNode, String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<ITreeNode, String> p) {
                        return new ReadOnlyStringWrapper(p.getValue().getValue().toString());
                    }
                });
                
                mainColumnTableView.setCellFactory(new Callback<TreeTableColumn<ITreeNode, String>, TreeTableCell<ITreeNode, String> >(){

                    @Override
                    public TreeTableCell<ITreeNode, String> call(TreeTableColumn<ITreeNode, String> param) {
                        return new TreeElementsFactory(MenuItemsController.this);
                    }
                    
                });
                
                mProjectDescription = new ProjectDetails(controller, path, name);
                TreeItem<ITreeNode> projectNode = new TreeItem<>(mProjectDescription.getProjectData());
                mainTreeTable.setRoot(projectNode);
                
                TreeItem<ITreeNode> allPlatformsNode = new TreeItem<>(new PlatformDescription.AllPlatformsDescription());
                projectNode.getChildren().add(allPlatformsNode);
                
                TreeItem<ITreeNode> allContainersNode = new TreeItem<>(new ContainerDescription.AllContainersDescription());
                projectNode.getChildren().add(allContainersNode);
                
                TreeItem<ITreeNode> allArtefactsNode = new TreeItem<ITreeNode>(new ArtefactDescription.AllArtefactDescription());
                projectNode.getChildren().add(allArtefactsNode);
            }
        });
    }
    
    public void newPlatformDescription(String platformName){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                TreeItem<ITreeNode> treeItem = mainTreeTable.getRoot().getChildren().get(0);
                mProjectDescription.addPlatformName(platformName);

                TreeItem platformDescriptionItem = new TreeItem<>(mProjectDescription.getPlatformDescription(mProjectDescription.platformsCount()-1));
                
                treeItem.getChildren().add(platformDescriptionItem);
            }
        });
    }
    
    public void newContainerDescription(String containerName){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                TreeItem<ITreeNode> treeItem = mainTreeTable.getRoot().getChildren().get(1);
                mProjectDescription.addContainerDescription(containerName);

                TreeItem containerDescriptionItem = new TreeItem<>(mProjectDescription.getContainerDescription(mProjectDescription.containersCount()-1));
                treeItem.getChildren().add(containerDescriptionItem);
            }
        });
    }
    
    public void newAgentDescritpion(String container, String agentName){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                TreeItem<ITreeNode> containers =  mainTreeTable.getRoot().getChildren().get(1);
                for(int i = 0; i < containers.getChildren().size(); ++i){
                    if(containers.getChildren().get(i).getValue().toString().equals(container)){
                        TreeItem containerDescriptionItem = new TreeItem<>(new AgentDescription(agentName));
                        containers.getChildren().get(i).getChildren().add(containerDescriptionItem);
                    }
                }
            }
        });
    }
    
    public void newArtefactDescription(String artefactName) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                TreeItem<ITreeNode> artefacts = mainTreeTable.getRoot().getChildren().get(2);
                TreeItem artefactDescriptionItem = new TreeItem<>(new ArtefactDescription(artefactName));
                artefacts.getChildren().add(artefactDescriptionItem);
            }
        });
    }
    
    public void onStartPlatform(String name){
        Vector<Object> args = new Vector<Object>();
        args.addElement(name);
        mParent.doOutput("GUI-START-PLATFORM", args);
    }
}
