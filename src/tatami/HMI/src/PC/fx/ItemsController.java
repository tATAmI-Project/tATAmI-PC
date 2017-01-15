package tatami.HMI.src.PC.fx;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import com.sun.glass.ui.MenuItem;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class ItemsController implements Initializable{
    
    @FXML
    private MenuItem startSimulationMenuItem;
    
    HashMap<String, EventHandler<ActionEvent> > eventHandlers = null;

    @Override
    public void initialize(URL url, ResourceBundle resource) {
        System.out.println("Something was initialized");
    }
    
    

}
