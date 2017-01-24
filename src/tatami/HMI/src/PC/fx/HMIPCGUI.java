package tatami.HMI.src.PC.fx;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Vector;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tatami.core.agent.io.AgentActiveIO;
import tatami.core.agent.io.AgentActiveIO.InputListener;

public class HMIPCGUI extends Application implements AgentActiveIO {
    
    final static int SCREEN_WIDTH = 1280;
    final static int SCREEN_HEIGHT = 720;
    
    volatile public static HMIPCGUI self = null;

    HashMap<String, InputListener> mListeners;
    
    public HMIPCGUI() {
        self = this;
        mListeners = new HashMap<String, AgentActiveIO.InputListener>();
    }
    
    Stage stage;

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        try{
            MenuItemsController controller = new MenuItemsController(this);
            FXMLLoader loader = new FXMLLoader();
            String fxmlDocPath = "tATAmI.fxml";
            FileInputStream fxmlStream = new FileInputStream(fxmlDocPath);
            loader.setController(controller);

            Parent root = (Parent) loader.load(fxmlStream);

            primaryStage.setTitle("tATAmI-PC Simulator");
            Scene scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT);
            primaryStage.setScene(scene);
            primaryStage.show();
        } 
        catch (Exception e) {
            System.out.println("HMIPCGUI error " + e.getMessage());
            e.printStackTrace();
        }
    }
    

    @Override
    public void doOutput(String portName, Vector<Object> arguments) {
        if (portName.equals("GUI")) {
            for (String key : mListeners.keySet()) {
                mListeners.get(key).receiveInput(portName, arguments);
            }
        }
        if(portName.equals("CORE")){
            incomingMessage(portName, arguments);
        }
    }
    
    private void incomingMessage(String portName, Vector<Object> arguments){
        
    }

    @Override
    public Vector<Object> getInput(String portName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void connectInput(String componentName, InputListener listener) {
        System.out.println("Input connected");
        mListeners.put(componentName, listener);
    }

    @Override
    public void setDefaultListener(InputListener listener) {
    }
    
    public Stage getStage(){
        return stage;
    }
}
