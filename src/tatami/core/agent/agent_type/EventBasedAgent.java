package tatami.core.agent.agent_type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import net.xqhs.util.logging.UnitComponentExt;
import tatami.core.agent.AgentComponent.ComponentCreationData;
import tatami.core.agent.AgentEvent.AgentSequenceType;
import tatami.core.agent.components.ComponentInterface;
import tatami.core.agent.messages.AgentMessage;
import tatami.core.agent.messages.AgentPath;
import tatami.core.util.platformUtils.PlatformUtils;
import tatami.simulation.AgentLoader;

public class EventBasedAgent implements Runnable, AgentLoader {
    
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
    
    String mContainerPath;
    
    String mName;

    public EventBasedAgent() {
        log.trace("Event based agent creation started...");
    }
    
    public void buildComponents(ComponentCreationData agentData){
        
    }

    @Override
    public void run() {
        while (mRunningFlag) {
           
            try {
                AgentMessage message = mEventQueue.take();
                ArrayList<AgentPath> allDestinations = message.getDestinations();
                for(AgentPath destination: allDestinations){
                    mComponents.get(destination.getComponenet()).onEvent(message);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    synchronized public void event(AgentMessage agentEvent){
        mEventQueue.add(agentEvent);
    }

    @Override
    public String getName() {
        return mName;
    }
}
