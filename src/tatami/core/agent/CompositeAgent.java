package tatami.core.agent;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import tatami.core.agent.AgentComponent.AgentComponentName;
import tatami.core.agent.AgentEvent.AgentEventType;
import tatami.core.agent.AgentEvent.AgentSequenceType;
import tatami.core.agent.claim.ClaimComponent;
import tatami.core.agent.parametric.AgentParameterName;
import tatami.core.agent.parametric.ParametricComponent;
import tatami.simulation.AgentManager;
import tatami.simulation.PlatformLoader.PlatformLink;

/**
 * This class reunites the components of an agent in order for components to be able to call each other and for events
 * to be distributed to all components.
 * <p>
 * Various agent components -- instances of {@link AgentComponent} -- can be added. 'Standard' components have names
 * that are instances of {@link AgentComponentName}. 'Other' (non-standard) components can have any name (TODO). At most
 * one component with a name is allowed (i.e. at most one component per functionality).
 * <p>
 * It is this class that handles agent events, by means of the <code>postAgentEvent()</code> method, which disseminates
 * an event to all components, which handle it by means of registered handles (each component registers a handle for an
 * event with itself). See {@link AgentComponent}.
 * <p>
 * A composite agent instance is its own {@link AgentManager}.
 * 
 * @author Andrei Olaru
 * 
 */
public class CompositeAgent implements Serializable, AgentManager
{
	/**
	 * Values indicating the current state of the agent, especially with respect to processing events.
	 * <p>
	 * The normal transition between states is the following: <br/>
	 * <ul>
	 * <li> {@link #INITIALIZING} [here components are normally added] &rarr; {@link #STARTING} [starting thread;
	 * starting components] &rarr; {@link #RUNNING}.
	 * <li>while in {@link #RUNNING}, components can be added or removed.
	 * <li> {@link #RUNNING} + {@link AgentEventType#AGENT_STOP} &rarr; {@link #STOPPING} [no more events accepted; stop
	 * components; stop thread] &rarr; {@link #STOPPED} (equivalent with {@link #INITIALIZING}).
	 * </ul>
	 * 
	 * @author Andrei Olaru
	 */
	enum AgentState {
		/**
		 * State indicating that the agent is currently behaving normally and agent events are processed in good order.
		 * All components are running.
		 */
		RUNNING,
		
		/**
		 * State indicating that the agent has been created and that it is waiting to start. The agent's thread has not
		 * yet started.
		 */
		INITIALIZING,
		
		/**
		 * State indicating that the agent is in the process of starting, but is not currently accepting events. The
		 * thread may or may not have been started. The components are in the process of starting.
		 */
		STARTING,
		
		/**
		 * State indicating that the agent is currently stopping. It is not accepting events any more. The thread may or
		 * may not have been started. The components are in the process of stopping.
		 */
		STOPPING,
		
		/**
		 * State indicating that the agent has been stopped and is unable to process events. The agent's thread has been
		 * stopped. All components are stopped.
		 */
		STOPPED,
	}
	
	/**
	 * THis is the event-processing thread of the agent.
	 * 
	 * @author Andrei Olaru
	 */
	class AgentThread implements Runnable
	{
		@Override
		public void run()
		{
			boolean doexit = false;
			while(!doexit)
			{
				// System.out.println("oops");
				if((eventQueue != null) && eventQueue.isEmpty())
					try
					{
						synchronized(eventQueue)
						{
							eventQueue.wait();
						}
					} catch(InterruptedException e)
					{
						// do nothing
					}
				else
				{
					AgentEvent event = eventQueue.poll();
					switch(event.getType().getSequenceType())
					{
					case CONSTRUCTIVE:
					case UNORDERED:
						for(AgentComponent component : componentOrder)
							component.signalAgentEvent(event);
						break;
					case DESTRUCTIVE:
						for(ListIterator<AgentComponent> it = componentOrder.listIterator(componentOrder.size()); it
								.hasPrevious();)
							it.previous().signalAgentEvent(event);
						break;
					}
					switch(event.getType())
					{
					case AGENT_START: // the agent has completed starting and all components are up.
						synchronized(eventQueue)
						{
							state = AgentState.RUNNING;
							// FIXME this should be done by intercepting AGENT_START
							if(getComponent(AgentComponentName.S_CLAIM_COMPONENT) != null)
								((ClaimComponent) getComponent(AgentComponentName.S_CLAIM_COMPONENT))
										.registerBehaviors();
						}
						break;
					case AGENT_STOP:
						synchronized(eventQueue)
						{
							if(!eventQueue.isEmpty())
							{
								// TODO: do something
							}
						}
						eventQueue = null;
						state = AgentState.STOPPED;
						doexit = true;
						break;
					default:
						// do nothing
					}
				}
			}
		}
	}
	
	/**
	 * The class UID
	 */
	private static final long							serialVersionUID	= -2693230015986527097L;
	
	/**
	 * Time (in milliseconds) to wait for the agent thread to exit.
	 */
	protected static final long							EXIT_TIMEOUT		= 500;
	
	/**
	 * This can be used by platform-specific components to contact the platform.
	 */
	protected Object									platformLink		= null;
	
	/**
	 * The {@link Map} that links component names (functionalities) to standard component instances.
	 */
	protected Map<AgentComponentName, AgentComponent>	components			= new HashMap<AgentComponentName, AgentComponent>();
	
	/**
	 * A {@link List} that holds the order in which components were added, so as to signal agent events to components in
	 * the correct order (as specified by {@link AgentSequenceType}).
	 * <p>
	 * It is important that this list is managed together with <code>components</code>.
	 */
	protected ArrayList<AgentComponent>					componentOrder		= new ArrayList<AgentComponent>();
	
	// TODO: add support for non-standard components.
	// /**
	// * The {@link Map} that holds the non-standard components (names are {@link String}).
	// */
	// protected Map<String, AgentComponent> otherComponents = new HashMap<String,
	// AgentComponent>();
	
	/**
	 * A synchronized queue of agent events, as posted by the components.
	 */
	protected LinkedBlockingQueue<AgentEvent>			eventQueue			= null;
	
	/**
	 * The thread managing the agent's lifecycle (managing events).
	 */
	protected Thread									agentThread			= null;
	
	/**
	 * The agent state. See {@link AgentState}. Access to this member should be synchronized with the lock of
	 * <code>eventQueue</code>.
	 */
	protected AgentState								state				= AgentState.INITIALIZING;
	
	/**
	 * Adds a component to the agent that has been configured beforehand. The agent will register with the component, as
	 * parent.
	 * <p>
	 * The component will be identified by the agent by means of its <code>getComponentName</code> method. Only one
	 * instance per name (functionality) will be allowed.
	 * 
	 * @param component
	 *            - the {@link AgentComponent} instance to add.
	 * @return the agent instance itself. This can be used to continue adding other components.
	 */
	public CompositeAgent addComponent(AgentComponent component)
	{
		if(!canAddComponents())
			throw new IllegalStateException("Cannot add components in state [" + state + "].");
		if(component == null)
			throw new InvalidParameterException("Component is null");
		if(hasComponent(component.getComponentName()))
			throw new InvalidParameterException("Cannot add multiple components for name ["
					+ component.getComponentName() + "]");
		components.put(component.getComponentName(), component);
		componentOrder.add(component);
		component.setParent(this);
		return this;
	}
	
	/**
	 * Removes an existing component of the agent.
	 * <p>
	 * The method will call the method <code>getComponentName()</code> of the component with a <code>null</code>
	 * parameter.
	 * 
	 * @param name
	 *            - the name of the component to remove (as instance of {@link AgentComponentName}.
	 * @return a reference to the just-removed component instance.
	 */
	public AgentComponent removeComponent(AgentComponentName name)
	{
		if(!hasComponent(name))
			throw new InvalidParameterException("Component [" + name + "] does not exist");
		AgentComponent component = getComponent(name);
		componentOrder.remove(component);
		components.remove(component);
		return component;
	}
	
	/**
	 * Starts the lifecycle of the agent. All components will receive an <code>AGENT_START</code> event.
	 * 
	 * @return true if the event has been successfully posted. See <code>postAgentEvent()</code>.
	 */
	@Override
	public boolean start()
	{
		if(eventQueue != null)
			return false;
		if(!((state == AgentState.INITIALIZING) || (state == AgentState.STOPPED)))
			return false;
		state = AgentState.STARTING;
		eventQueue = new LinkedBlockingQueue<AgentEvent>();
		agentThread = new Thread(new AgentThread());
		agentThread.start();
		return postAgentEvent(new AgentEvent(AgentEventType.AGENT_START));
	}
	
	/**
	 * Instructs the agent to unload all components and exit. All components will receive an <code>AGENT_EXIT</code>
	 * event.
	 * <p>
	 * No events will be successfully received after this event has been posted.
	 * 
	 * @return true if the event has been successfully posted. See <code>postAgentEvent()</code>.
	 */
	public boolean exit()
	{
		if(!postAgentEvent(new AgentEvent(AgentEventType.AGENT_STOP)))
			return false;
		try
		{
			agentThread.join(0);// EXIT_TIMEOUT);
		} catch(InterruptedException e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Alias for {@link #exit()}.
	 */
	@Override
	public boolean stop()
	{
		return exit();
	}
	
	@Override
	public boolean setPlatformLink(PlatformLink link)
	{
		if(!canAddComponents() || isRunning())
			return false;
		platformLink = link;
		return true;
	}
	
	/**
	 * The method should be called by an agent component (relayed through {@link AgentComponent}) to disseminate a an
	 * {@link AgentEvent} to the other components.
	 * <p>
	 * If the event has been successfully posted, the method returns <code>true</code>, guaranteeing that, except in the
	 * case of abnormal termination, the event will be processed eventually. Otherwise, it returns <code>false</code>,
	 * indicating that either the agent has not been started, or has been instructed to exit, or is in another
	 * inappropriate state.
	 * 
	 * @param event
	 *            the event to disseminate.
	 * @return <code>true</code> if the event has been successfully posted; <code>false</code> otherwise.
	 */
	protected boolean postAgentEvent(AgentEvent event)
	{
		if(!(((state == AgentState.STARTING) && (event.getType() == AgentEventType.AGENT_START)) || (state == AgentState.RUNNING)))
			return false;
		boolean exiting = (event.getType() == AgentEventType.AGENT_STOP);
		try
		{
			if(eventQueue != null)
				synchronized(eventQueue)
				{
					if(exiting)
						state = AgentState.STOPPING;
					eventQueue.put(event);
					eventQueue.notify();
				}
			else
				return false;
		} catch(InterruptedException e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Returns <code>true</code> if the agent contains said component.
	 * 
	 * @param name
	 *            - the name of the component to search (as instance of {@link AgentComponentName}.
	 * @return <code>true</code> if the component exists, false otherwise.
	 */
	protected boolean hasComponent(AgentComponentName name)
	{
		return components.containsKey(name);
	}
	
	/**
	 * Retrieves a component of the agent, by name.
	 * <p>
	 * It is <i>strongly recommended</i> that the reference is not kept, as the component may be removed without notice.
	 * 
	 * @param name
	 *            - the name of the component to retrieve (as instance of {@link AgentComponentName} .
	 * @return the {@link AgentComponent} instance, if any. <code>null</code> otherwise.
	 */
	protected AgentComponent getComponent(AgentComponentName name)
	{
		return components.get(name);
	}
	
	/**
	 * Retrieves the platform link.
	 * 
	 * @return the platform link.
	 */
	protected Object getPlatformLink()
	{
		return platformLink;
	}
	
	/**
	 * Returns the name of the agent. It is the name that has been set through the <code>AGENT_NAME</code> parameter.
	 * 
	 * @return the name of the agent.
	 */
	@Override
	public String getAgentName()
	{ // TODO name should be cached
		String agentName = null;
		if(hasComponent(AgentComponentName.PARAMETRIC_COMPONENT))
			agentName = ((ParametricComponent) getComponent(AgentComponentName.PARAMETRIC_COMPONENT))
					.parVal(AgentParameterName.AGENT_NAME);
		return agentName;
	}
	
	/**
	 * Checks if the agent is currently in RUNNING state. In case components are added during this state, they must
	 * consider that the agent is already running and no additional {@link AgentEventType#AGENT_START} events will be
	 * issued.
	 * 
	 * @return <code>true</code> if the agent is currently RUNNING; <code>false</code> otherwise.
	 */
	public boolean isRunning()
	{
		return state == AgentState.RUNNING;
	}
	
	/**
	 * Checks if the state of the agent allows adding components. Components should not be added in intermediary states
	 * in which the agent is starting or stopping.
	 * 
	 * @return <code>true</code> if in the current state components can be added.
	 */
	public boolean canAddComponents()
	{
		return (state == AgentState.INITIALIZING) || (state == AgentState.STOPPED) || (state == AgentState.RUNNING);
	}
}
