package tatami.core.agent;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import tatami.core.agent.AgentEventHandler.AgentEventType;
import tatami.core.agent.claim.ClaimComponent;
import tatami.core.agent.hierarchical.HierarchicalComponent;
import tatami.core.agent.jade.JadeComponent;
import tatami.core.agent.kb.CognitiveComponent;
import tatami.core.agent.messaging.MessagingComponent;
import tatami.core.agent.movement.MovementComponent;
import tatami.core.agent.parametric.ParametricComponent;
import tatami.core.agent.visualization.VisualizableComponent;
import tatami.core.agent.webServices.WebserviceComponent;

/**
 * This class serves as base for agent component. a component is characterized by its functionality,
 * denominated by means of its name -- an instance of {@link AgentComponentName}.
 * <p>
 * A component can belong to at most one {@link CompositeAgent}, which is its parent.
 * <p>
 * The class serves as a relay to access some package-accessible functionality from
 * {@link CompositeAgent}.
 * 
 * @author Andrei Olaru
 */
public abstract class AgentComponent implements Serializable
{
	/**
	 * The class UID.
	 */
	private static final long	serialVersionUID	= -8282262747231347473L;
	
	/**
	 * Enumeration of available component names / functionalities.
	 * 
	 * @author Andrei Olaru
	 */
	public static enum AgentComponentName {
		/**
		 * The name of a component extending {@link ParametricComponent}.
		 */
		PARAMETRIC_COMPONENT,
		
		/**
		 * The name of a component extending {@link VisualizableComponent}.
		 */
		VISUALIZABLE_COMPONENT,
		
		/**
		 * The name of a component extending {@link CognitiveComponent}.
		 */
		COGNITIVE_COMPONENT,
		
		/**
		 * The name of a component extending {@link MessagingComponent}.
		 */
		MESSAGING_COMPONENT,
		
		/**
		 * The name of a component extending {@link MovementComponent}.
		 */
		MOVEMENT_COMPONENT,
		
		/**
		 * The name of a component extending {@link WebserviceComponent}.
		 */
		WEBSERVICE_COMPONENT,
		
		/**
		 * The name of a component extending {@link HierarchicalComponent}.
		 */
		HIERARCHICAL_COMPONENT,
		
		/**
		 * The name of a component extending {@link ClaimComponent}.
		 */
		S_CLAIM_COMPONENT,
		
		/**
		 * The name of a component extending {@link JadeComponent}.
		 */
		JADE_COMPONENT,
	}
	
	/**
	 * The name of the component, as instance of {@link AgentComponentName}.
	 */
	protected AgentComponentName						componentName	= null;
	/**
	 * The {@link CompositeAgent} instance that this instance is part of.
	 */
	protected CompositeAgent							parentAgent;
	/**
	 * The {@link AgentEventHandler} instances that respond to various events in the agent.
	 */
	protected Map<AgentEventType, AgentEventHandler>	eventHandlers	= new HashMap<AgentEventHandler.AgentEventType, AgentEventHandler>();
	
	/**
	 * The constructor assigns the name to the component
	 * 
	 * @param name
	 *            - the name of the component, as instance of {@link AgentComponentName}.
	 */
	protected AgentComponent(AgentComponentName name)
	{
		componentName = name;
	}
	
	/**
	 * Setter for the parent of the agent. If an agent instance is already a parent of this
	 * component, <code>removeParent</code> must be called first.
	 * <p>
	 * After assigning the parent, <code>the parentChangeNotifier</code> method will be called, so
	 * that extending classes can take appropriate action.
	 * 
	 * @param parent
	 *            - the {@link CompositeAgent} instance that this component is part of.
	 */
	final void setParent(CompositeAgent parent)
	{
		CompositeAgent oldParent = parentAgent;
		parentAgent = parent;
		parentChangeNotifier(oldParent);
	}
	
	/**
	 * Extending classes can override this method to perform actions when the parent of the
	 * component changes.
	 * 
	 * @param oldParent
	 *            - the previous value for the parent, if any.
	 */
	protected void parentChangeNotifier(CompositeAgent oldParent)
	{
		// this class does not do anything here.
	}
	
	/**
	 * Sets the parent of the component to <code>null</code>, effectively eliminating the component
	 * from the agent.
	 * <p>
	 * After assigning the parent, <code>the parentChangeNotifier</code> method will be called, so
	 * that extending classes can take appropriate action.
	 */
	final void removeParent()
	{
		CompositeAgent oldParent = parentAgent;
		parentAgent = null;
		parentChangeNotifier(oldParent);
	}
	
	/**
	 * Extending classes should use this method to register {@link AgentEventHandler} instances that
	 * would be invoked when the specified {@link AgentEventType} appears.
	 * <p>
	 * Should a handler for the same event already exist, the old handler will be discarded. A
	 * reference to it will be returned.
	 * 
	 * @param event
	 *            - the agent event to be handled, as an {@link AgentEventType} instance.
	 * @param handler
	 *            - the {@link AgentEventHandler} instance to handle the event.
	 * @return the handler being replaced, if any (<code>null</code> otherwise).
	 */
	protected AgentEventHandler registerHandler(AgentEventType event, AgentEventHandler handler)
	{
		AgentEventHandler oldHandler = null;
		if(eventHandlers.containsKey(event))
			oldHandler = eventHandlers.get(event);
		eventHandlers.put(event, handler);
		return oldHandler;
	}
	
	/**
	 * @return the name of the component (instance of {@link AgentComponentName}).
	 */
	AgentComponentName getComponentName()
	{
		return componentName;
	}
	
	/**
	 * Relay for calls to the method in {@link CompositeAgent}.
	 * 
	 * @param name
	 *            - the name of the component.
	 * @return <code>true</code> if the component exists, false otherwise.
	 */
	boolean hasComponent(AgentComponentName name)
	{
		return parentAgent.hasComponent(name);
	}
	
	/**
	 * Relay for calls to the method in {@link CompositeAgent}.
	 * 
	 * @param name
	 *            - the name of the component.
	 * @return the {@link AgentComponent} instance, if any. <code>null</code> otherwise.
	 */
	AgentComponent getComponent(AgentComponentName name)
	{
		return parentAgent.getComponent(name);
	}
	
	/**
	 * Retrieves a direct reference to the {@link ParametricComponent} of the agent, if any.
	 * <p>
	 * It is <i>strongly recommended</i> that the reference is not kept, as the component may be
	 * removed without notice.
	 * 
	 * @return the component instance, if any. <code>null</code> otherwise.
	 */
	public ParametricComponent getParametric()
	{
		if(parentAgent.hasComponent(AgentComponentName.PARAMETRIC_COMPONENT))
			return (ParametricComponent)parentAgent
					.getComponent(AgentComponentName.PARAMETRIC_COMPONENT);
		return null;
	}
	
	/**
	 * Retrieves a direct reference to the {@link MessagingComponent} of the agent, if any.
	 * <p>
	 * It is <i>strongly recommended</i> that the reference is not kept, as the component may be
	 * removed without notice.
	 * 
	 * @return the component instance, if any. <code>null</code> otherwise.
	 */
	public MessagingComponent getMessaging()
	{
		if(parentAgent.hasComponent(AgentComponentName.MESSAGING_COMPONENT))
			return (MessagingComponent)parentAgent
					.getComponent(AgentComponentName.MESSAGING_COMPONENT);
		return null;
	}
	
	/**
	 * Relay for calls to the method in {@link CompositeAgent}.
	 * 
	 * @param event
	 *            - the event to disseminate.
	 */
	void postAgentEvent(AgentEventType event)
	{
		parentAgent.postAgentEvent(event);
	}
	
	/**
	 * Relay for calls to the method in {@link CompositeAgent}.
	 * 
	 * @return the name of the agent.
	 */
	String getAgentName()
	{
		return parentAgent.getAgentName();
	}
	
}
