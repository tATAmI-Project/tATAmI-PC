package tatami.jade;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import tatami.core.agent.AgentComponent;
import tatami.core.agent.CompositeAgent;

public class JadeComponent extends AgentComponent
{
	Agent	jadeAgent;
	
	public JadeComponent()
	{
		super(AgentComponentName.JADE_COMPONENT);
	}
	
	public void addBehaviour(Behaviour behaviour)
	{
		jadeAgent.addBehaviour(behaviour);
	}
	
	public void doDelete()
	{
		jadeAgent.doDelete();
	}
	
	public String getLocalName()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
}
