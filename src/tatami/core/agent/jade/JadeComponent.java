package tatami.core.agent.jade;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import tatami.core.agent.AgentComponent;
import tatami.core.agent.CompositeAgent;

public class JadeComponent extends AgentComponent
{
	Agent	jadeAgent;
	
	public JadeComponent(CompositeAgent parent)
	{
		super(parent, AgentComponentName.JADE_COMPONENT);
	}
	
	public void addBehaviour(Behaviour behaviour)
	{
		jadeAgent.addBehaviour(behaviour);
	}
	
	public void doDelete()
	{
		jadeAgent.doDelete();
	}
	
}
