package tatami.core.agent.agent_type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import net.xqhs.util.logging.UnitComponentExt;
import tatami.core.agent.AgentEvent.AgentSequenceType;
import tatami.core.agent.components.ComponentCreationData;
import tatami.core.agent.components.ComponentFactory;
import tatami.core.agent.components.ComponentInterface;
import tatami.core.agent.messages.AgentMessage;
import tatami.core.agent.messages.Command;
import tatami.core.agent.messages.EntityPath;
import tatami.core.util.platformUtils.PlatformUtils;
import tatami.simulation.AgentCreationData;
import tatami.simulation.Agent;

public class EventBasedAgent implements Runnable, Agent{
    
    UnitComponentExt log = (UnitComponentExt) new UnitComponentExt().setUnitName("EventBasedAgent").setLoggerType(
            PlatformUtils.platformLogType());

    Boolean mRunningFlag = true;

    /**
     * A synchronized queue of agent events, as posted by the components.
     */
    protected LinkedBlockingQueue<AgentMessage> mEventQueue = new LinkedBlockingQueue<AgentMessage>();
    
    /**
     * A {@link List} that holds the order in which components were added, so as to signal agent events to components in
     * the correct order (as specified by {@link AgentSequenceType}).
     * <p>
     * It is important that this list is managed together with <code>components</code>.
     */
    protected HashMap<String, ComponentInterface> mComponents = new HashMap<String, ComponentInterface>();
    
    HashMap<String, Object> sharedMemory;

    String mContainerPath;
    
    String mName;

    public EventBasedAgent(AgentCreationData agentCreationData) {
        log.trace("Event based agent creation started...");
        mName = agentCreationData.getAgentName();
        sharedMemory = new HashMap<String, Object>();
        for(ComponentCreationData componentInfo: agentCreationData.getComponentsData()){
            mComponents.put(componentInfo.get("name"), ComponentFactory.getInst().newInst(componentInfo.get("name"), this));
        }
        log.trace("Event based agent creation finished...");
    }

    @Override
    public void run() {
        while (mRunningFlag) {
            try {
                AgentMessage message = mEventQueue.take();
                ArrayList<EntityPath> allDestinations = message.getDestinations();
                for(EntityPath destination: allDestinations){
                    mComponents.get(destination.getComponenet()).onInput(message);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void insertData(String key, Object data){
        sharedMemory.put(key, data);
    }
    
    public Object getData(String key){
        return sharedMemory.get(key);
    }
    
    public void removeData(String key){
        sharedMemory.remove(key);
    }
    
    synchronized public void doEvent(AgentMessage agentEvent){
        mEventQueue.add(agentEvent);
    }
    
    public void internalCommand(Command command){
        
    }

    @Override
    public String getName() {
        return mName;
    }
}
