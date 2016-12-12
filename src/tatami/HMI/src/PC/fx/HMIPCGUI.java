package tatami.HMI.src.PC.fx;

import java.util.Vector;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import tatami.core.agent.io.AgentActiveIO;

public class HMIPCGUI extends Application implements AgentActiveIO {
    
    final static int SCREEN_WIDTH = 1280;
    final static int SCREEN_HEIGHT = 720;
    
    InputListener core;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("tATAmI-PC");

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        primaryStage.setX(bounds.getMinX());
        primaryStage.setY(bounds.getMinY());
        primaryStage.setWidth(bounds.getWidth());
        primaryStage.setHeight(bounds.getHeight());
        primaryStage.show();
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
        core = null;
        
    }
    
}
