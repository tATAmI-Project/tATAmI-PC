package scenario.examples;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import net.xqhs.util.logging.Logger;
import tatami.core.agent.AgentComponent;
import tatami.core.agent.AgentEvent;
import tatami.core.agent.AgentEvent.AgentEventHandler;
import tatami.core.agent.AgentEvent.AgentEventType;
import tatami.core.agent.CompositeAgent;
import tatami.core.agent.messaging.MessagingComponent;

/**
 * An {@link AgentComponent} implementation that sends messages to other agents.
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
	 * 
	 * @param arguments - component arguments.
	 */
	public PingTestComponent(HashSet<Entry<String, Object>> arguments)
	{
		super(AgentComponentName.TESTING_COMPONENT);
		
		for(Entry<String, Object> e : arguments)
			if(e.getKey().equals(OTHER_AGENT_PARAMETER_NAME))
				otherAgent = (String) e.getValue();
	}
	
	/**
	 * @return the log provided by the visualizable component.
	 */
	public Logger getLog()
	{
		return getVisualizable().getLog();
	}
	
	@Override
	protected void componentInitializer()
	{
		super.componentInitializer();
		
		AgentEventHandler allEventHandler = new AgentEventHandler() {
			@Override
			public void handleEvent(AgentEvent event)
			{
				String eventMessage = "agent [" + thisAgent + "] event: [" + event.getType().toString() + "]";
				getLog().li(eventMessage);
				
				if(event.getType() == AgentEventType.AGENT_START)
				{
					registerMessageReceiver("", new AgentEventHandler() {
						@Override
						public void handleEvent(AgentEvent messageEvent)
						{
							getLog().li("message received: ", messageEvent);
						}
					});
					
					pingTimer = new Timer();
					pingTimer.schedule(new Pinger(), PING_INITIAL_DELAY, PING_PERIOD);
				}
			}
		};
		for(AgentEventType eventType : AgentEventType.values())
			registerHandler(eventType, allEventHandler);
	}
	
	@Override
	protected boolean registerMessageReceiver(String prefix, AgentEventHandler receiver)
	{
		return super.registerMessageReceiver(prefix, receiver);
	}
	
	@Override
	protected void parentChangeNotifier(CompositeAgent oldParent)
	{
		super.parentChangeNotifier(oldParent);
		
		if(getParent() != null)
		{
			messenger = getMessaging();
			thisAgent = getAgentName();
		}
	}
}
