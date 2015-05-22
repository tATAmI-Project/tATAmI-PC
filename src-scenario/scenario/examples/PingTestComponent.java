package scenario.examples;

import java.util.Timer;
import java.util.TimerTask;

import net.xqhs.util.XML.XMLTree.XMLNode;
import net.xqhs.util.logging.Logger;
import tatami.core.agent.AgentComponent;
import tatami.core.agent.AgentEvent;
import tatami.core.agent.AgentEvent.AgentEventHandler;
<<<<<<< HEAD
import tatami.core.agent.AgentEvent.AgentEventType;
import tatami.core.agent.CompositeAgent;
=======
import tatami.core.agent.messaging.MessagingComponent;
>>>>>>> refs/heads/tATAmI-2/cosmin

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
			
<<<<<<< HEAD
			sendMessage("ping no " + tick, thisAgent, otherAgent);
=======
			doSend(tick);
>>>>>>> refs/heads/tATAmI-2/cosmin
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
	protected boolean preload(ComponentCreationData parameters, XMLNode scenarioNode, Logger log)
	{
		if(!super.preload(parameters, scenarioNode, log))
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
	protected void atAgentStart(AgentEvent event)
	{
		super.atAgentStart(event);
		
		if(getParent() != null)
		{
			messenger = (MessagingComponent) getAgentComponent(AgentComponentName.MESSAGING_COMPONENT);
			thisAgent = getAgentName();
		}
		
		registerMessageReceiver(new AgentEventHandler() {
			@Override
			public void handleEvent(AgentEvent messageEvent)
			{
				getAgentLog().li("message received: ", messageEvent);
			}
		}, "");
		
		pingTimer = new Timer();
	}
	
	@Override
	protected void atSimulationStart(AgentEvent event)
	{
		super.atSimulationStart(event);
		System.out.println("here");
		pingTimer.schedule(new Pinger(), PING_INITIAL_DELAY, PING_PERIOD);
	}
	
	@Override
	protected void atAgentStop(AgentEvent event)
	{
<<<<<<< HEAD
		super.parentChangeNotifier(oldParent);
		
		if(getParent() != null)
		{
			thisAgent = getAgentName();
		}
=======
		super.atAgentStop(event);
		pingTimer.cancel();
>>>>>>> refs/heads/tATAmI-2/cosmin
	}
	
	/**
<<<<<<< HEAD
	 * Relay.
	 */
	@Override
	protected boolean sendMessage(String content, String sourceEndpoint, String targetAgent,
			String... targetPathElements)
	{
		return super.sendMessage(content, sourceEndpoint, targetAgent, targetPathElements);
	}
=======
	 * Send the ping.
	 * 
	 * @param tick - tick no.
	 */
	protected void doSend(int tick)
	{
		getAgentLog().trace("Sending ping");
		sendMessage("ping no " + tick, thisAgent, otherAgent);
	}
	
>>>>>>> refs/heads/tATAmI-2/cosmin
}
