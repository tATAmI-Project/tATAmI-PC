package tatami.core.agent.messages;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class AgentMessage<T extends Serializable> implements Serializable{

    private static final long serialVersionUID = -8969819199236815656L;
    
    AgentPath mSource;
    
    ArrayList<AgentPath> mDestinations;
    
    String mTag;
    
    T mContent;
    
    public AgentMessage(String source, String destination, String tag, T content){
        mSource = new AgentPath(source);
        mDestinations = new ArrayList<AgentPath>();
        mDestinations.add(new AgentPath(destination));
        mContent = content;
        mTag = tag;
    }
    
    public AgentMessage(String source, ArrayList<String> destinations, String tag, T content){
        mSource = new AgentPath(source);
        for(String destination: destinations){
            mDestinations.add(new AgentPath(destination));
        }
        mContent = content;
        mTag = tag;
    }
    
    
    public String getSource(){
        return mSource.toString();
    }
    
    public ArrayList<AgentPath> getDestinations(){
        return mDestinations;
    }
    
    public T getContent(){
        return mContent;
    }
    
    public String getTag(){
        return mTag;
    }
}
