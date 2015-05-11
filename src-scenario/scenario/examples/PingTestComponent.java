package scenario.examples;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.xqhs.util.XML.XMLTree.XMLNode;
import net.xqhs.util.logging.Logger;
import tatami.core.agent.AgentComponent;
import tatami.core.agent.AgentEvent;
import tatami.core.agent.AgentEvent.AgentEventHandler;
import tatami.core.agent.AgentEvent.AgentEventType;
import tatami.core.agent.CompositeAgent;
import tatami.core.agent.messaging.MessagingComponent;

/**
 * An {@link AgentComponent} implementation that sends messages to other agents.
 * <p>
 * This is a rather older implementation, that starts pinging immediately after agent start.
 * 
 * @author Andrei Olaru
 */
public class PingTestComponent extends AgentComponent
{
	/**
	 * The instance sends a message to the "other agent".
	 * 
	 * @author Andrei Olaru
	 */
	class Pinger extends TimerTask
	{
		/**
		 * The index of the message sent.
		 */
		int	tick	= 0;
		
		@Override
		public void run()
		{
			tick++;
			
			messenger.sendMessage(otherAgent, thisAgent, "ping no " + tick);
		}
		
	}
	
	/**
	 * The UID.
	 */
	private static final long		serialVersionUID			= 5214882018809437402L;
	/**
	 * The name of the component parameter that contains the id of the other agent.
	 */
	protected static final String	OTHER_AGENT_PARAMETER_NAME	= "other agent";
	/**
	 * Initial delay before the first ping message.
	 */
	protected static final long		PING_INITIAL_DELAY			= 0;
	/**
	 * Time between ping messages.
	 */
	protected static final long		PING_PERIOD					= 1000;
	
	/**
	 * Timer for pinging.
	 */
	Timer							pingTimer					= null;
	/**
	 * Reference to the messenger component.
	 */
	MessagingComponent				messenger					= null;
	/**
	 * Cache for the name of this agent.
	 */
	String							thisAgent					= null;
	/**
	 * Cache for the name of the other agent.
	 */
	String							otherAgent					= null;
	
	/**
	 * Default constructor
	 */
	public PingTestComponent()
	{
		super(AgentComponentName.TESTING_COMPONENT);
	}
	
	@Override
	protected boolean preload(ComponentCreationData parameters, XMLNode scenarioNode, List<String> agentPackages,
			Logger log)
	{
		if(!super.preload(parameters, scenarioNode, agentPackages, log))
			return false;
		otherAgent = getComponentData().get(OTHER_AGENT_PARAMETER_NAME);
		return true;
	}
	
	/**
	 * @return the log provided by the visualizable component.
	 */
	@Override
	public Logger getAgentLog()
	{
		return super.getAgentLog();
	}
	
	@Override
	protected void componentInitializer()
	{
		super.componentInitializer();
		
		AgentEventHandler allEventHandler = new AgentEventHandler() {
			@Override
			public void handleEvent(AgentEvent event)
			{
				getAgentLog().li("agent [] event: []", thisAgent, event.getType());
				
				if(event.getType() == AgentEventType.AGENT_START)
					atAgentStart(event);
			}
		};
		for(AgentEventType eventType : AgentEventType.values())
			registerHandler(eventType, allEventHandler);
	}
	
	@Override
	protected void atAgentStart(AgentEvent event)
	{
		super.atAgentStart(event);
		
		registerMessageReceiver(new AgentEventHandler() {
			@Override
			public void handleEvent(AgentEvent messageEvent)
			{
				getAgentLog().li("message received: ", messageEvent);
			}
		}, "");
		
		pingTimer = new Timer();
		pingTimer.schedule(new Pinger(), PING_INITIAL_DELAY, PING_PERIOD);
	}
	
	@Override
	protected void parentChangeNotifier(CompositeAgent oldParent)
	{
		super.parentChangeNotifier(oldParent);
		
		if(getParent() != null)
		{
			messenger = (MessagingComponent) getAgentComponent(AgentComponentName.MESSAGING_COMPONENT);
			thisAgent = getAgentName();
		}
	}
}
