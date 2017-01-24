package tatami.HMI.src.PC.fx;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Vector;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.stage.FileChooser;

public class MenuItemsController implements Initializable{
    
    @FXML
    private MenuItem startSimulationMenuItem;
    
    @FXML
    private MenuItem stopSimulationMenuItem;
    
    @FXML
    private MenuItem loadScenarioMenuItem;
    
    @FXML
    private TableColumn<String, String> firstColumnTreeTable;  
    
    @FXML
    private TableColumn secondColumnTreeTable; 
    
    HashMap<String, EventHandler<ActionEvent> > eventHandlers = null;
    
    private HMIPCGUI mParent;
    
    final FileChooser fileChooser = new FileChooser();
    
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
        }
    }
}
