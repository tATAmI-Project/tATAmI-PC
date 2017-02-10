package tatami.core.agent.artefacts;

import java.util.Vector;

import tatami.HMI.pub.HMIInterface;
import tatami.core.agent.io.AgentActiveIO.InputListener;

public class UserArtefact extends ArtefactInterface implements InputListener{
    
    public enum UserCommands{START, STOP};
    
    public static final int STANDARD_SIZE = 100;
    
    public UserArtefact(ArtefactCreationData artefactCreationData){
        mArtefactId = artefactCreationData.getId();
        mName = artefactCreationData.getName();
        HMIInterface.INST.getHMI().connectInput("USER-ARTEFACT", this);
    }

    @Override
    public void receiveInput(String portName, Vector<Object> arguments) {
        
        if(portName.equals("ARTEFACT-START-SIMULATION")){
            onData(UserCommands.START.name().getBytes());
        }
        
        if(portName.equals("ARTEFACT-STOP-SIMULATION")){
            onData(UserCommands.STOP.name().getBytes());
        }
        
    }

}
