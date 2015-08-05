/*******************************************************************************
 * Copyright (C) 2015 Andrei Olaru, Marius-Tudor Benea, Nguyen Thi Thuy Nga, Amal El Fallah Seghrouchni, Cedric Herpson.
 * 
 * This file is part of tATAmI-PC.
 * 
 * tATAmI-PC is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * tATAmI-PC is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with tATAmI-PC.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package tatami.core.agent.kb;

import net.xqhs.graphs.graph.Edge;
import net.xqhs.graphs.graph.Graph;
import net.xqhs.graphs.representation.text.TextGraphRepresentation;
import tatami.core.agent.AgentComponent;
import tatami.core.agent.CompositeAgent;
import tatami.core.agent.kb.simple.SimpleKB;
import tatami.core.agent.kb.simple.SimpleKnowledge;
import tatami.core.agent.parametric.AgentParameterName;
import tatami.core.agent.parametric.ParametricComponent;

public class CognitiveComponent extends AgentComponent // TODO implement to be used as ContextComponent parent
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
					.getAgentComponent(AgentComponentName.PARAMETRIC_COMPONENT);
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
