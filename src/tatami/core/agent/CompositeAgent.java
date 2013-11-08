package tatami.core.agent;

import java.util.HashMap;
import java.util.Map;

import tatami.core.agent.AgentComponent.AgentComponentName;
import tatami.core.agent.AgentEventHandler.AgentEventType;
import tatami.core.agent.jade.JadeComponent;
import tatami.core.agent.kb.CognitiveComponent;
import tatami.core.agent.messaging.MessagingComponent;
import tatami.core.agent.movement.MovementComponent;
import tatami.core.agent.parametric.AgentParameterName;
import tatami.core.agent.parametric.ParametricComponent;
import tatami.core.agent.visualization.VisualizableComponent;

/**
 * This class reunites the components of an agent in order for components to be able to call each other and for events
 * to be distributed to all components.
 * <p>
 * Various agent components -- instances of {@link AgentComponent} -- can be added. Components have names that are
 * instances of {@link AgentComponentName}, and at most one component with a name is allowed (i.e. at most one component
 * per functionality).
 * <p>
 * The class also offers direct access to a basic set of components. It is not recommended for components to retain the
 * reference, as it may become null if the component is removed.
 * <p>
 * It is this class that handles agent events, by means of the <code>postAgentEvent()</code> method, which disseminates
 * an event to all components, which handle it by means of registered handles (each component registers a handle for an
 * event with itself). See {@link AgentComponent}.
 * 
 * @author Andrei Olaru
 * 
 */
public class CompositeAgent
{
	/**
	 * The {@link Map} that links component names (functionalities) to component instances.
	 */
	protected Map<AgentComponentName, AgentComponent>	components				= new HashMap<AgentComponent.AgentComponentName, AgentComponent>();
	
	
	public CompositeAgent addComponent(AgentComponent component)
	{
		// TODO
		return null;
	}
	
	public AgentComponent removeComponent(AgentComponentName name)
	{
		return null;
	}
	
	public boolean hasComponent(AgentComponentName name)
	{
		return components.containsKey(name);
	}
	
	public AgentComponent getComponent(AgentComponentName name)
	{
		return components.get(name);
	}
	
	public ParametricComponent getParametric()
	{
		if(hasComponent(AgentComponentName.PARAMETRIC_COMPONENT))
			return (ParametricComponent) getComponent(AgentComponentName.PARAMETRIC_COMPONENT);
		return null;
	}
	
	public void postAgentEvent(AgentEventType event)
	{
		// TODO
		// QUEST: synchronous or asynchronous handling of events?
	}
	
	/**
	 * Returns the name of the agent. It can either be a name that has been set through the <code>AGENT_NAME</code>
	 * parameter, or the name of the Jade agent.
	 * 
	 * @return the name of the agent.
	 */
	public final String getAgentName()
	{
		String agentName = null;
		if(getComponent(AgentComponentName.PARAMETRIC_COMPONENT) != null)
			agentName = ((ParametricComponent) getComponent(AgentComponentName.PARAMETRIC_COMPONENT))
					.parVal(AgentParameterName.AGENT_NAME);
		if((agentName == null) && getComponent(AgentComponentName.JADE_COMPONENT) != null)
			agentName = ((JadeComponent) getComponent(AgentComponentName.JADE_COMPONENT)).getLocalName();
		return agentName;
	}
}
