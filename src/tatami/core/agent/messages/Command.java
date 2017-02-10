package tatami.core.agent.messages;

public class Command {
    EntityPath mSource;
    String mCommand;
    
    public Command(EntityPath source, String command){
        mSource = source;
        mCommand = command;
    }
    
    public EntityPath getSource(){
        return mSource;
    }
    
    public String command(){
        return mCommand;
    }
}
