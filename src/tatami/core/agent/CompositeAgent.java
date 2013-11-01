package tatami.core.agent;

import java.util.HashMap;
import java.util.Map;

import tatami.core.agent.jade.JadeComponent;
import tatami.core.agent.parametric.ParametricComponent;
import tatami.core.interfaces.AgentComponent;
import tatami.core.interfaces.AgentComponent.AgentComponentName;
import tatami.core.interfaces.AgentEventHandler.AgentEventType;
import tatami.core.interfaces.AgentParameterName;

public class CompositeAgent
{
	protected Map<AgentComponentName, AgentComponent>	components	= new HashMap<AgentComponent.AgentComponentName, AgentComponent>();
	
	public boolean hasComponent(AgentComponentName name)
	{
		return components.containsKey(name);
	}
	
	public AgentComponent getComponent(AgentComponentName name)
	{
		return components.get(name);
	}
	
	public AgentComponent loadComponent(AgentComponentName name)
	{
		// TODO
		return null;
	}
	
	public void postAgentEvent(AgentEventType event)
	{
		// TODO
		// QUEST: synchronous or asynchronous handling of events?
	}
	
	/**
	 * Returns the name of the agent. It can either be a name that has been set through the
	 * <code>AGENT_NAME</code> parameter, or the name of the Jade agent.
	 * 
	 * @return the name of the agent.
	 */
	public final String getAgentName()
	{
		String agentName = null;
		if(getComponent(AgentComponentName.PARAMETRIC_COMPONENT) != null)
			agentName = ((ParametricComponent)getComponent(AgentComponentName.PARAMETRIC_COMPONENT))
					.parVal(AgentParameterName.AGENT_NAME);
		if((agentName == null) && getComponent(AgentComponentName.JADE_COMPONENT) != null)
			agentName = ((JadeComponent)getComponent(AgentComponentName.JADE_COMPONENT))
					.getLocalName();
		return agentName;
	}
}
