package tatami.core.agent.behavior;

import tatami.core.agent.AgentComponent;
import tatami.core.agent.AgentEvent;

public class BehaviorComponent extends AgentComponent
{
	protected BehaviorComponent()
	{
		super(AgentComponentName.BEHAVIOR_COMPONENT);
		// TODO Auto-generated constructor stub
	}

	public static abstract class ActivationRecord
	{
		AgentEvent event;
		
		public ActivationRecord(AgentEvent activationEvent)
		{
			event = activationEvent;
		}
		
		public AgentEvent getEvent()
		{
			return event;
		}
	}
	
	public interface Behavior
	{
		public boolean canActivate(AgentEvent activationEvent);
		
		public boolean activate(ActivationRecord activationRecord);
	}
}
