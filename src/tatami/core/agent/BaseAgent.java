/*******************************************************************************
 * Copyright (C) 2013 Andrei Olaru, Marius-Tudor Benea, Nguyen Thi Thuy Nga, Amal El Fallah Seghrouchni, Cedric Herpson.
 * 
 * This file is part of tATAmI-PC.
 * 
 * tATAmI-PC is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * tATAmI-PC is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with tATAmI-PC.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package tatami.core.agent;

import jade.gui.GuiAgent;
import jade.gui.GuiEvent;

import java.util.Collection;
import java.util.Map;

import tatami.core.agent.kb.simple.SimpleKB;
import tatami.core.agent.kb.simple.SimpleKnowledge;
import tatami.core.interfaces.KnowledgeBase;
import tatami.core.interfaces.ParametrizedAgent;
import tatami.core.util.graph.Edge;
import tatami.core.util.graph.Graph;
import tatami.core.util.graph.representation.TextGraphRepresentation;

/**
 * The base layer of the agent. It handles the interface with Jade, agent parameters, and knowledge.
 * <p>
 * It is extended on top of and works directly with Jade's {@link GuiAgent}.
 * <p>
 * It implements {@link ParametrizedAgent}, contains the list of agent's parameters and implements methods for working
 * with parameters.
 * 
 * @author Andrei Olaru
 */
public class BaseAgent extends GuiAgent implements ParametrizedAgent
{
	@SuppressWarnings("javadoc")
	private static final long	serialVersionUID	= 1426668765300089941L;
	
	/**
	 * The agent's parameters.
	 * <p>
	 * They can only be accessed by this level of the agent, which serves them to the layers above through specialized
	 * functions.
	 */
	private AgentParameters		parameters			= null;
	
	/**
	 * The agent's knowledge base. It cannot be changed (i.e. create a different instance) throughout the agent's
	 * lifecycle. It can only be accessed by this level of the agent, which serves knowledge-related requests to the
	 * layers above through specialized functions.
	 */
	private final KnowledgeBase	knowledgeBase		= new SimpleKB();
	
	@Override
	protected void setup()
	{
		super.setup();
		
		// get agent parameters
		Object[] args = getArguments();
		if(args != null && args.length >= 1 && (args[0] instanceof ParametrizedAgent.AgentParameters))
			parameters = (AgentParameters) getArguments()[0];
		else
			parameters = new AgentParameters();
		
		if(hasPar(AgentParameterName.KNOWLEDGE))
		{
			Graph kg = TextGraphRepresentation.readRepresentation(parVal(AgentParameterName.KNOWLEDGE), null, null);// new
			// UnitConfigData().setName("agent ["+agentName+"] k reader").setLevel(Level.ALL));
			for(Edge edge : kg.getEdges())
			{
				SimpleKnowledge k = new SimpleKnowledge(edge.getLabel(), edge.getFrom().getLabel(), edge.getTo()
						.getLabel());
				knowledgeBase.add(k);
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
	
	/**
	 * Returns the name of the agent. It can either be a name that has been set through the <code>AGENT_NAME</code>
	 * parameter, or the name of the Jade agent.
	 * 
	 * @return the name of the agent.
	 */
	protected final String getAgentName()
	{
		String agentName = parVal(AgentParameterName.AGENT_NAME);
		if(agentName == null)
			agentName = getLocalName();
		return agentName;
	}
	
	@Override
	public final String parVal(AgentParameterName name)
	{
		return parameters.getValue(name.toString());
	}
	
	@Override
	public final Collection<String> parVals(AgentParameterName name)
	{
		return parameters.getValues(name.toString());
	}
	
	@Override
	public final Object parObj(AgentParameterName name)
	{
		return parameters.getObject(name.toString());
	}
	
	@Override
	public final boolean hasPar(AgentParameterName name)
	{
		return parameters.isSet(name.toString());
	}
	
	@Override
	public Map<String, Object> getUnregisteredParameters()
	{
		return parameters.getUnregisteredParameters();
	}
	
	@Override
	protected void onGuiEvent(GuiEvent ev)
	{
		// do nothing.
	}
	
}
