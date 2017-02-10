package tatami.core.agent.messages;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class AgentMessage<T extends Serializable> implements Serializable{

    private static final long serialVersionUID = -8969819199236815656L;
    
    EntityPath mSource;
    
    ArrayList<EntityPath> mDestinations;
    
    String mTag;
    
    T mContent;
    
    public AgentMessage(String source, String destination, String tag, T content){
        mSource = new EntityPath(source);
        mDestinations = new ArrayList<EntityPath>();
        mDestinations.add(new EntityPath(destination));
        mContent = content;
        mTag = tag;
    }
    
    public AgentMessage(String source, ArrayList<String> destinations, String tag, T content){
        mSource = new EntityPath(source);
        for(String destination: destinations){
            mDestinations.add(new EntityPath(destination));
        }
        mContent = content;
        mTag = tag;
    }
    
    
    public String getSource(){
        return mSource.toString();
    }
    
    public ArrayList<EntityPath> getDestinations(){
        return mDestinations;
    }
    
    public T getContent(){
        return mContent;
    }
    
    public String getTag(){
        return mTag;
    }
}
