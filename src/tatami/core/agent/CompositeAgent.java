package tatami.core.agent;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

import tatami.core.agent.AgentComponent.AgentComponentName;
import tatami.core.agent.AgentEventHandler.AgentEventType;
import tatami.core.agent.jade.JadeComponent;
import tatami.core.agent.parametric.AgentParameterName;
import tatami.core.agent.parametric.ParametricComponent;

/**
 * This class reunites the components of an agent in order for components to be able to call each
 * other and for events to be distributed to all components.
 * <p>
 * Various agent components -- instances of {@link AgentComponent} -- can be added. Components have
 * names that are instances of {@link AgentComponentName}, and at most one component with a name is
 * allowed (i.e. at most one component per functionality).
 * <p>
 * The class also offers direct access to a basic set of components. It is not recommended for
 * components to retain the reference, as it may become null if the component is removed.
 * <p>
 * It is this class that handles agent events, by means of the <code>postAgentEvent()</code> method,
 * which disseminates an event to all components, which handle it by means of registered handles
 * (each component registers a handle for an event with itself). See {@link AgentComponent}.
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
	 * The {@link Map} that links component names (functionalities) to component instances.
	 */
	protected Map<AgentComponentName, AgentComponent>	components			= new HashMap<AgentComponent.AgentComponentName, AgentComponent>();
	
	/**
	 * Adds a component to the agent that has been configured beforehand. The agent will register
	 * with the component, as parent.
	 * <p>
	 * The component will be identified by the agent by means of its <code>getComponentName</code>
	 * method. Only one instance per name (functionality) will be allowed.
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
		component.setParent(this);
		return this;
	}
	
	/**
	 * Removes an existing component of the agent.
	 * <p>
	 * The method will call the method <code>getComponentName()</code> of the component with a
	 * <code>null</code> parameter.
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
		components.remove(component);
		return component;
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
	 * It is <i>strongly recommended</i> that the reference is not kept, as the component may be
	 * removed without notice.
	 * 
	 * @param name
	 *            - the name of the component to retrieve (as instance of {@link AgentComponentName}
	 *            .
	 * @return the {@link AgentComponent} instance, if any. <code>null</code> otherwise.
	 */
	AgentComponent getComponent(AgentComponentName name)
	{
		return components.get(name);
	}
	
	/**
	 * The method should be called by an agent component (relayed through {@link AgentComponent}) to
	 * disseminate a standard event (one of {@link AgentEventType}) to the other components.
	 * 
	 * @param event
	 *            the event to disseminate.
	 */
	void postAgentEvent(AgentEventType event)
	{
		// TODO
		// QUEST: synchronous or asynchronous handling of events?
	}
	
	/**
	 * Returns the name of the agent. It can either be a name that has been set through the
	 * <code>AGENT_NAME</code> parameter, or the name of the Jade agent underpinning the
	 * {@link JadeComponent}, if any.
	 * 
	 * @return the name of the agent.
	 */
	String getAgentName()
	{
		String agentName = null;
		if(hasComponent(AgentComponentName.PARAMETRIC_COMPONENT))
			agentName = ((ParametricComponent)getComponent(AgentComponentName.PARAMETRIC_COMPONENT))
					.parVal(AgentParameterName.AGENT_NAME);
		if((agentName == null) && getComponent(AgentComponentName.JADE_COMPONENT) != null)
			agentName = ((JadeComponent)getComponent(AgentComponentName.JADE_COMPONENT))
					.getLocalName();
		return agentName;
	}
}
