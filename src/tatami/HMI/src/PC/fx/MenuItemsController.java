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
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import tatami.HMI.src.PC.fx.Tree.ITreeNode;
import tatami.HMI.src.PC.fx.Tree.TreeElementsFactory;
import tatami.HMI.src.PC.fx.data.PlatformDescription;
import tatami.HMI.src.PC.fx.data.ProjectDescription;

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
    
    HashMap<String, EventHandler<ActionEvent> > eventHandlers = null;
    
    private HMIPCGUI mParent;
    
    final FileChooser fileChooser = new FileChooser();
    
    ProjectDescription mProjectDescription;
    
    public MenuItemsController(HMIPCGUI parent) {
        mParent = parent;
    }

    @Override
    public void initialize(URL url, ResourceBundle resource) {
        
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
            args.addElement("LOAD SCENARIO");
            args.addElement(file);
            mParent.doOutput("GUI", args);
            String path = file.toString();
            
        }
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
                
                mainColumnTableView.setCellFactory(ttc -> new TreeElementsFactory());
                
                mProjectDescription = new ProjectDescription(controller, path, name);
                TreeItem<ITreeNode> projectNode = new TreeItem<>(mProjectDescription.getProjectData());
                mainTreeTable.setRoot(projectNode);
                
                TreeItem<ITreeNode> allPlatformsNode = new TreeItem<>(new PlatformDescription.AllPlatformsDescription());
                projectNode.getChildren().add(allPlatformsNode);
            }
        });
    }
    
    public void newPlatformDescription(String platformName){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                TreeItem<ITreeNode> treeItem = mainTreeTable.getRoot().getChildren().get(0);
                mProjectDescription.addPlatformName(platformName);
                for(int i = 0; i < mProjectDescription.platformsCount(); ++i){
                    TreeItem platformDescriptionItem = new TreeItem<>(mProjectDescription.getPlatformDescription(i));
                    treeItem.getChildren().add(platformDescriptionItem);
                }
            }
        });
    }
}
