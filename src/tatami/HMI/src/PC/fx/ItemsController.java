package tatami.HMI.src.PC.fx;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Vector;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;

public class ItemsController implements Initializable{
    
    @FXML
    private MenuItem startSimulationMenuItem;
    
    HashMap<String, EventHandler<ActionEvent> > eventHandlers = null;
    
    private HMIPCGUI mParent;
    
    public ItemsController(HMIPCGUI parent) {
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
}
