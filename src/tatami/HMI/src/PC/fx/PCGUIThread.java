package tatami.HMI.src.PC.fx;

import javafx.application.Application;

public class PCGUIThread extends Thread {

    public void run() {
        Application.launch(HMIPCGUI.class);
    }

}