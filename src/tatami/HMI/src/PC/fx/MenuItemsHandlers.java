package tatami.HMI.src.PC.fx;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import tatami.core.agent.io.AgentActiveIO.InputListener;

public class MenuItemsHandlers implements EventHandler<ActionEvent>{
    
    InputListener core;
    
    public MenuItemsHandlers(InputListener core){
        this.core = core;
    }

    @Override
    public void handle(ActionEvent event) {
        // TODO Auto-generated method stub
        
    }
}
