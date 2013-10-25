package tatami.core.agent.kb;

import tatami.core.agent.CompositeAgent;
import tatami.core.agent.kb.simple.SimpleKB;
import tatami.core.agent.kb.simple.SimpleKnowledge;
import tatami.core.agent.parametric.ParametricComponent;
import tatami.core.interfaces.AgentComponent;
import tatami.core.interfaces.AgentParameterName;
import tatami.core.interfaces.KnowledgeBase;
import tatami.core.util.graph.Edge;
import tatami.core.util.graph.Graph;
import tatami.core.util.graph.representation.TextGraphRepresentation;

public class CognitiveComponent extends AgentComponent
{
	/**
	 * The agent's knowledge base. It cannot be changed (i.e. create a different instance)
	 * throughout the agent's lifecycle. It can only be accessed by this level of the agent, which
	 * serves knowledge-related requests to the layers above through specialized functions.
	 */
	private final KnowledgeBase	knowledgeBase	= new SimpleKB();
	
	public CognitiveComponent(CompositeAgent parent)
	{
		super(parent, AgentComponentName.COGNITIVE_COMPONENT);
		
		if(parentAgent.hasComponent(AgentComponentName.PARAMETRIC_COMPONENT))
		{
			ParametricComponent parametricComponent = (ParametricComponent)parentAgent
					.getComponent(AgentComponentName.PARAMETRIC_COMPONENT);
			if(parametricComponent.hasPar(AgentParameterName.KNOWLEDGE))
			{
				Graph kg = TextGraphRepresentation.readRepresentation(
						parametricComponent.parVal(AgentParameterName.KNOWLEDGE), null, null);// new
				// UnitConfigData().setName("agent ["+agentName+"] k reader").setLevel(Level.ALL));
				for(Edge edge : kg.getEdges())
				{
					SimpleKnowledge k = new SimpleKnowledge(edge.getLabel(), edge.getFrom()
							.getLabel(), edge.getTo().getLabel());
					knowledgeBase.add(k);
				}
			}
		}
	}
	
	/**
	 * Provides access to the knowledge base.
	 * 
	 * @return the knowledge base.
	 */
	protected KnowledgeBase getKB()
	{
		return knowledgeBase;
	}
}
