package tatami.HMI.src.PC.fx;

import java.util.Vector;

import javafx.application.Application;

public class PCGUIThread extends Thread {
    
    public void run() {
        Application.launch(HMIPCGUI.class);
    }

}