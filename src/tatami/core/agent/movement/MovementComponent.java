package tatami.core.agent.movement;

import tatami.core.agent.AgentComponent;
import tatami.core.agent.CompositeAgent;

public class MovementComponent extends AgentComponent
{
	protected MovementComponent(CompositeAgent parent)
	{
		super(parent, AgentComponentName.MOVEMENT_COMPONENT);
		// TODO Auto-generated constructor stub
	}
	
	public String extractDestination(Object eventData)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
}
