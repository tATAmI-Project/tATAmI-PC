package tatami.core.agent;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import tatami.core.agent.AgentEvent.AgentEventHandler;
import tatami.core.agent.AgentEvent.AgentEventType;
import tatami.core.agent.claim.ClaimComponent;
import tatami.core.agent.hierarchical.HierarchicalComponent;
import tatami.core.agent.kb.CognitiveComponent;
import tatami.core.agent.kb.ContextComponent;
import tatami.core.agent.messaging.MessagingComponent;
import tatami.core.agent.movement.MovementComponent;
import tatami.core.agent.parametric.ParametricComponent;
import tatami.core.agent.visualization.VisualizableComponent;
import tatami.core.agent.webServices.WebserviceComponent;
import tatami.jade.JadeComponent;
import tatami.core.agent.baseKnowledge.KnowledgeComponent;

/**
 * This class serves as base for agent component. a component is characterized by its functionality, denominated by
 * means of its name -- an instance of {@link AgentComponentName}.
 * <p>
 * A component can belong to at most one {@link CompositeAgent}, which is its parent. When created, the component does
 * no have a parent; a parent will be set afterwards.
 * <p>
 * The class also offers direct access to a basic set of components. It is not recommended for components to retain the
 * reference, as it may become null if the component is removed. // TODO: implement mechanisms to enable caching of
 * component references, invalidating the cache only at specific agent events; add the event COMPONENT_CHANGE.
 * <p>
 * The class serves as a relay to access some package-accessible functionality from {@link CompositeAgent}.
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
	 * Enumeration of available component names / functionalities. These are the standard types of components. Other
	 * types of components may be added to an agent (TODO).
	 * <p>
	 * The enumeration entries also contain information about the default implementation of the specified component
	 * type. The name of the implementation class can be given when creating the entry, or can be inferred based on the
	 * name of the entry and the constants in the enumeration.
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
		VISUALIZABLE_COMPONENT(AgentComponentName.AGENT_COMPONENT_PACKAGE_ROOT + ".visualization.VisualizableComponent"),
		
		/**
		 * The name of a component extending {@link CognitiveComponent}.
		 */
		COGNITIVE_COMPONENT(AgentComponentName.AGENT_COMPONENT_PACKAGE_ROOT + ".kb.ContextComponent"),
		
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
		
		/**
		 * TEMPORARY type for testing. TODO: remove this type.
		 */
		TESTING_COMPONENT,
		
		;
		
		
		
		/**
		 * Suffix for component classes.
		 */
		private static final String	AGENT_COMPONENT_CLASS_SUFFIX	= "Component";
		/**
		 * Default parent package packages containing default component implementations.
		 */
		private static final String	AGENT_COMPONENT_PACKAGE_ROOT	= "tatami.core.agent";
		
		/**
		 * The fully qualified class name of the default component implementation.
		 */
		String						componentClass;
		
		/**
		 * The name of the component, as appearing in the scenario file.
		 */
		String						componentName;
		
		/**
		 * Specifies the fully qualified class name of the component implementation.
		 * 
		 * @param classname
		 *            - the fully qualified class name.
		 */
		private AgentComponentName(String classname)
		{
			// FIXME: check that package and class exist
			componentClass = classname;
			componentName = this.name().split("_")[0].toLowerCase();
		}
		
		/**
		 * Inferres the class of the component implementation based on the name of the component and constants in this
		 * class.
		 */
		private AgentComponentName()
		{
			// FIXME: check that package and class exist
			// lower case entry name, without "_COMPONENT" suffix.
			componentName = this.name().split("_")[0].toLowerCase();
			String componentPackage = AGENT_COMPONENT_PACKAGE_ROOT + "." + componentName;
			componentClass = componentPackage + "." + componentName.substring(0, 1).toUpperCase()
					+ componentName.substring(1) + AGENT_COMPONENT_CLASS_SUFFIX;
		}
		
		/**
		 * Gets the specified or inferred class name for the default implementation of the component.
		 * 
		 * @return the class name.
		 */
		public String getClassName()
		{
			return componentClass;
		}
		
		/**
		 * Gets the name of the component, as appearing in the scenario file.
		 * 
		 * @return the name of the component.
		 */
		public String componentName()
		{
			return componentName;
		}
		
		/**
		 * Returns the {@link AgentComponentName} instance that corresponds to the specified name.
		 * <p>
		 * E.g. for the name "parametric" the return value will be the instance named PARAMETRIC_COMPONENT.
		 * 
		 * @param componentName
		 *            - the name of the component, as appearing in the scenario file.
		 * @return the corresponding {@link AgentComponentName} instance.
		 */
		public static AgentComponentName toComponentName(String componentName)
		{
			try
			{
				return AgentComponentName.valueOf(componentName.toUpperCase() + "_"
						+ AGENT_COMPONENT_CLASS_SUFFIX.toUpperCase());
			} catch(Exception e)
			{
				return null;
			}
		}
	}
	
	/**
	 * The name of the component, as instance of {@link AgentComponentName}.
	 */
	private AgentComponentName						componentName	= null;
	/**
	 * The {@link CompositeAgent} instance that this instance is part of.
	 */
	private CompositeAgent							parentAgent;
	/**
	 * The {@link AgentEventHandler} instances that respond to various events in the agent.
	 */
	private Map<AgentEventType, AgentEventHandler>	eventHandlers	= new HashMap<AgentEventType, AgentEventHandler>();
	
	/**
	 * The constructor assigns the name to the component.
	 * <p>
	 * IMPORTANT: extending classes should only perform in the constructor initializations that do not depend on the
	 * parent agent or on other components, as when the component is created, the {@link AgentComponent#parentAgent}
	 * member is <code>null</code>. The assignment of a parent (as any parent change) is notified to extending classes
	 * by calling the method {@link AgentComponent#parentChangeNotifier(CompositeAgent)}.
	 * <p>
	 * Event registration is not dependent on the parent, so it can be performed in the constructor or in the
	 * {@link #componentInitializer()} method.
	 * 
	 * @param name
	 *            - the name of the component, as instance of {@link AgentComponentName}.
	 */
	protected AgentComponent(AgentComponentName name)
	{
		componentName = name;
		componentInitializer();
	}
	
	/**
	 * Extending anonymous classes can override this method to perform actions when the component is created. The method
	 * is called at the end of the constructor.
	 * <p>
	 * Extending classes should always call super.componentInitializer() first.
	 * <p>
	 * IMPORTANT: The note in {@link #AgentComponent(AgentComponentName)} also applies to this method.
	 */
	protected void componentInitializer()
	{
		// this class does not do anything here.
	}
	
	/**
	 * Setter for the parent of the agent. If an agent instance is already a parent of this component,
	 * <code>removeParent</code> must be called first.
	 * <p>
	 * After assigning the parent, <code>the parentChangeNotifier</code> method will be called, so that extending
	 * classes can take appropriate action.
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
	 * Sets the parent of the component to <code>null</code>, effectively eliminating the component from the agent.
	 * <p>
	 * After assigning the parent, <code>the parentChangeNotifier</code> method will be called, so that extending
	 * classes can take appropriate action.
	 */
	final void removeParent()
	{
		CompositeAgent oldParent = parentAgent;
		parentAgent = null;
		parentChangeNotifier(oldParent);
	}
	
	/**
	 * Extending classes can override this method to perform actions when the parent of the component changes.
	 * <p>
	 * The previous reference to the parent can be found in the first parameter. The current parent can be obtained by
	 * calling {@link #getParent()}.
	 * <p>
	 * Such actions may be initializations that depend on the parent or on other components of the same agent.
	 * <p>
	 * Extending classes should always call super.parentChangeNotifier() first.
	 * 
	 * @param oldParent
	 *            - the previous value for the parent, if any.
	 */
	protected void parentChangeNotifier(CompositeAgent oldParent)
	{
		// this class does not do anything here.
	}
	
	/**
	 * Extending classes should use this method to register {@link AgentEventHandler} instances that would be invoked
	 * when the specified {@link AgentEventType} appears.
	 * <p>
	 * Should a handler for the same event already exist, the old handler will be discarded. A reference to it will be
	 * returned.
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
	 * Relay for calls to the method in {@link CompositeAgent}.
	 * 
	 * @return the name of the agent.
	 */
	protected String getAgentName()
	{
		return (parentAgent != null) ? parentAgent.getAgentName() : null;
	}
	
	/**
	 * Relay for calls to the method in {@link CompositeAgent}.
	 * 
	 * @return the platform link.
	 */
	protected Object getPlatformLink()
	{
		return parentAgent.getPlatformLink();
	}

	/**
	 * @return the name of the component (instance of {@link AgentComponentName}).
	 */
	protected AgentComponentName getComponentName()
	{
		return componentName;
	}
	
	/**
	 * Retrieves the parent of the component.
	 * 
	 * @return the {@link CompositeAgent} instance this component belongs to.
	 */
	protected CompositeAgent getParent()
	{
		return parentAgent;
	}
	
	/**
	 * Relay for calls to the method in {@link CompositeAgent}.
	 * 
	 * @param name
	 *            - the name of the component.
	 * @return <code>true</code> if the component exists, false otherwise.
	 */
	protected boolean hasComponent(AgentComponentName name)
	{
		return (parentAgent != null) ? parentAgent.hasComponent(name) : false;
	}
	
	/**
	 * Relay for calls to the method in {@link CompositeAgent}.
	 * 
	 * @param name
	 *            - the name of the component.
	 * @return the {@link AgentComponent} instance, if any. <code>null</code> otherwise.
	 */
	protected AgentComponent getComponent(AgentComponentName name)
	{
		return (parentAgent != null) ? parentAgent.getComponent(name) : null;
	}
	
	/**
	 * Retrieves a direct reference to the {@link ParametricComponent} of the agent, if any.
	 * <p>
	 * It is <i>strongly recommended</i> that the reference is not kept, as the component may be removed without notice.
	 * 
	 * @return the component instance, if any. <code>null</code> otherwise.
	 */
	protected ParametricComponent getParametric()
	{
		if((parentAgent != null) && parentAgent.hasComponent(AgentComponentName.PARAMETRIC_COMPONENT))
			return (ParametricComponent) parentAgent.getComponent(AgentComponentName.PARAMETRIC_COMPONENT);
		return null;
	}
	
	/**
	 * Retrieves a direct reference to the {@link VisualizableComponent} of the agent, if any.
	 * <p>
	 * It is <i>strongly recommended</i> that the reference is not kept, as the component may be removed without notice.
	 * 
	 * @return the component instance, if any. <code>null</code> otherwise.
	 */
	protected VisualizableComponent getVisualizable()
	{
		if((parentAgent != null) && parentAgent.hasComponent(AgentComponentName.VISUALIZABLE_COMPONENT))
			return (VisualizableComponent) parentAgent.getComponent(AgentComponentName.VISUALIZABLE_COMPONENT);
		return null;
	}
	
	/**
	 * Retrieves a direct reference to the {@link MessagingComponent} of the agent, if any.
	 * <p>
	 * It is <i>strongly recommended</i> that the reference is not kept, as the component may be removed without notice.
	 * 
	 * @return the component instance, if any. <code>null</code> otherwise.
	 */
	protected MessagingComponent getMessaging()
	{
		if((parentAgent != null) && parentAgent.hasComponent(AgentComponentName.MESSAGING_COMPONENT))
			return (MessagingComponent) parentAgent.getComponent(AgentComponentName.MESSAGING_COMPONENT);
		return null;
	}
	
	/**
	 * Retrieves a direct reference to the {@link CognitiveComponent} of the agent, if any.
	 * <p>
	 * It is <i>strongly recommended</i> that the reference is not kept, as the component may be removed without notice.
	 * 
	 * @return the component instance, if any. <code>null</code> otherwise.
	 */
	protected ContextComponent getCognitive()
	{
		if((parentAgent != null) && parentAgent.hasComponent(AgentComponentName.COGNITIVE_COMPONENT))
			return (ContextComponent) parentAgent.getComponent(AgentComponentName.COGNITIVE_COMPONENT);
		return null;
	}
	
	
	/**
	 * Handles the registration of an event handler for messages to a target (inside the agent) with the specified
	 * prefix.
	 * <p>
	 * If a {@link MessagingComponent} exists, the handler will be registered with the messaging component. Otherwise,
	 * the handler will also be registered directly with the agent.
	 * 
	 * @param prefix
	 *            - the target prefix.
	 * @param receiver
	 *            - the receiving {@link AgentEventHandler} instance.
	 * @return <code>true</code> if the registration was successful; <code>false</code> otherwise.
	 */
	protected boolean registerMessageReceiver(String prefix, AgentEventHandler receiver)
	{
		// TODO: if the messaging component disappears, register with the agent; if the messaging component appears,
		// register with that.
		if(parentAgent == null)
			return false;
		if(parentAgent.hasComponent(AgentComponentName.MESSAGING_COMPONENT))
			return parentAgent.getComponent(AgentComponentName.MESSAGING_COMPONENT).registerMessageReceiver(prefix,
					receiver);
		registerHandler(AgentEventType.AGENT_MESSAGE, receiver);
		return true;
	}
	
	/**
	 * Relay for calls to the method in {@link CompositeAgent}.
	 * 
	 * @param event
	 *            - the event to disseminate.
	 */
	protected void postAgentEvent(AgentEvent event)
	{
		if(parentAgent != null)
			parentAgent.postAgentEvent(event);
	}
	
	/**
	 * The method calls the event handler of the component for the event which occurred.
	 * <p>
	 * It relays the call from the parent {@link CompositeAgent}.
	 * 
	 * @param event
	 *            - the event which occurred.
	 */
	void signalAgentEvent(AgentEvent event)
	{
		if(eventHandlers.containsKey(event.getType()))
			eventHandlers.get(event.getType()).handleEvent(event);
	}
	
}
