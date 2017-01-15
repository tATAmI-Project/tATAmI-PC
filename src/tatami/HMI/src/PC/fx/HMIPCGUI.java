package tatami.HMI.src.PC.fx;

import java.io.FileInputStream;
import java.util.Vector;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import tatami.core.agent.io.AgentActiveIO;

public class HMIPCGUI extends Application implements AgentActiveIO {
    
    final static int SCREEN_WIDTH = 1280;
    final static int SCREEN_HEIGHT = 720;
    
    InputListener core;
    
    MenuItemsHandlers menuItemHandlers = null;

    @Override
    public void start(Stage primaryStage) {
        menuItemHandlers = new MenuItemsHandlers(core);
        try{
            ItemsController controller = new ItemsController();
            FXMLLoader loader = new FXMLLoader();
            String fxmlDocPath = "tATAmI.fxml";
            FileInputStream fxmlStream = new FileInputStream(fxmlDocPath);
            loader.setController(controller);

            Parent root = (Parent) loader.load(fxmlStream);

            primaryStage.setTitle("tATAmI-PC Simulator");
            primaryStage.setScene(new Scene(root, 300, 275));
            primaryStage.show();
        } 
        catch (Exception e) {
            System.out.println("HMIPCGUI error " + e.getMessage());
            e.printStackTrace();
        }
    }
    

    @Override
    public void doOutput(String portName, Vector<Object> arguments) {
        // TODO Auto-generated method stub
    }

    @Override
    public Vector<Object> getInput(String portName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void connectInput(String componentName, InputListener listener) {
        core = listener;
    }

    @Override
    public void setDefaultListener(InputListener listener) {
        core = listener;
    }
    
}
