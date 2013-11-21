package tatami.core.agent;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import tatami.core.agent.AgentComponent.AgentComponentName;
import tatami.core.agent.AgentEvent.AgentEventType;
import tatami.core.agent.AgentEvent.AgentSequenceType;
import tatami.core.agent.jade.JadeComponent;
import tatami.core.agent.parametric.AgentParameterName;
import tatami.core.agent.parametric.ParametricComponent;

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
 * 
 * @author Andrei Olaru
 * 
 */
public class CompositeAgent implements Serializable
{
	/**
	 * The class UID
	 */
	private static final long							serialVersionUID	= -2693230015986527097L;
	
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
			throw new InvalidParameterException("Coomponent [" + name + "] does not exist");
		AgentComponent component = getComponent(name);
		componentOrder.remove(component);
		components.remove(component);
		return component;
	}
	
	/**
	 * Starts the lifecycle of the agent. All components will receive an <code>AGENT_START</code> event.
	 */
	public void start()
	{
		eventQueue = new LinkedBlockingQueue<AgentEvent>();
		agentThread = new Thread(new Runnable() {
			@Override
			public void run()
			{
				boolean doexit = false;
				while(!doexit)
				{
					if(eventQueue.isEmpty())
						try
						{
							synchronized(this)
							{
								wait();
							}
						} catch(InterruptedException e)
						{
							// do nothing
						}
					else
					{
						AgentEvent event = eventQueue.poll();
						for(AgentComponent component : componentOrder)
						{
							component.signalAgentEvent(event);
						}
					}
				}
			}
		});
		agentThread.start();
		postAgentEvent(new AgentEvent(AgentEventType.AGENT_START));
	}
	
	/**
	 * Returns <code>true</code> if the agent contains said component.
	 * 
	 * @param name
	 *            - the name of the component to search (as instance of {@link AgentComponentName}.
	 * @return <code>true</code> if the component exists, false otherwise.
	 */
	boolean hasComponent(AgentComponentName name)
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
	AgentComponent getComponent(AgentComponentName name)
	{
		return components.get(name);
	}
	
	/**
	 * The method should be called by an agent component (relayed through {@link AgentComponent}) to disseminate a an
	 * {@link AgentEvent} to the other components.
	 * 
	 * @param event
	 *            the event to disseminate.
	 */
	void postAgentEvent(AgentEvent event)
	{
		try
		{
			if(eventQueue != null)
				eventQueue.put(event);
			if(agentThread != null)
				synchronized(agentThread)
				{
					agentThread.notify();
				}
		} catch(InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the name of the agent. It can either be a name that has been set through the <code>AGENT_NAME</code>
	 * parameter, or the name of the Jade agent underpinning the {@link JadeComponent}, if any.
	 * 
	 * @return the name of the agent.
	 */
	String getAgentName()
	{
		String agentName = null;
		if(hasComponent(AgentComponentName.PARAMETRIC_COMPONENT))
			agentName = ((ParametricComponent) getComponent(AgentComponentName.PARAMETRIC_COMPONENT))
					.parVal(AgentParameterName.AGENT_NAME);
		if((agentName == null) && getComponent(AgentComponentName.JADE_COMPONENT) != null)
			agentName = ((JadeComponent) getComponent(AgentComponentName.JADE_COMPONENT)).getLocalName();
		return agentName;
	}
}
