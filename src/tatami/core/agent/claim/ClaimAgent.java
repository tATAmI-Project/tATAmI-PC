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
package tatami.core.agent.claim;

import java.util.Map;
import java.util.Vector;

import tatami.core.agent.claim.parser.ClaimAgentDefinition;
import tatami.core.agent.claim.parser.ClaimBehaviorDefinition;
import tatami.core.agent.claim.parser.ClaimBehaviorType;
import tatami.core.agent.claim.parser.ClaimValue;
import tatami.core.agent.claim.parser.ClaimVariable;
import tatami.core.agent.hierarchical.HierarchicalAgent;
import tatami.core.interfaces.KnowledgeBase;

public class ClaimAgent extends HierarchicalAgent
{
	@SuppressWarnings("javadoc")
	private static final long		serialVersionUID	= 3562319445295180030L;
	
	/**
	 * The agent definition describing this agent.
	 */
	protected ClaimAgentDefinition	cad;
	
	/**
	 * The list of behaviors of the agent.
	 */
	protected Vector<ClaimBehavior>	behaviors			= null;
	
	// Agent's setup
	@Override
	public void setup()
	{
		super.setup();
		
		behaviors = new Vector<ClaimBehavior>();
		
		cad = (ClaimAgentDefinition) parObj(AgentParameterName.AGENT_DEFINITION);
		if(cad == null)
		{
			log.error("agent definition not found");
			throw new IllegalArgumentException("agent definition not found");
		}
		
		cad.getSymbolTable().setLog(log);
		
		// retrieve claim agent definition
		if(cad.getParameters() != null)
		{
			Map<String, Object> claimParams = getUnregisteredParameters();
			for(ClaimVariable agentParam : cad.getParameters())
			{
				AgentParameterName registeredParam = AgentParameterName.getName(agentParam.getName());
				if(registeredParam != null)
				{
					if(hasPar(registeredParam))
						this.cad.getSymbolTable().put(agentParam, new ClaimValue(parObj(registeredParam)));
					else
					{
						if(agentParam.getName().equals("parent"))
							cad.getSymbolTable().put(new ClaimVariable("parent", true), null);
						else
							log.error("registered agent parameter [" + agentParam + "] not found");
					}
				}
				else
				{
					if(claimParams.containsKey(agentParam.getName()))
						this.cad.getSymbolTable()
								.put(agentParam, new ClaimValue(claimParams.get(agentParam.getName())));
					else if(!agentParam.getName().equals("this"))
						log.error("agent parameter [" + agentParam + "] not found");
				}
			}
		}
		
		// bind value for "this" parameter (agent's local name)
		this.cad.getSymbolTable().put(new ClaimVariable("this"), new ClaimValue(this.getLocalName()));
		
		try
		{
			Thread.sleep(3000);
		} catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		
		for(int i = 0; i < this.cad.getBehaviors().size(); i++)
		{
			ClaimBehaviorDefinition cbd = this.cad.getBehaviors().get(i);
			ClaimBehavior cb = new ClaimBehavior(cbd);
			addBehaviour(cb);
			behaviors.add(cb);
			
			if(cbd.getBehaviorType().equals(ClaimBehaviorType.REACTIVE))
				// register wb service
				registerWSBehavior();
			
			// else if((this instanceof GoalAgent) && cbd.getBehaviorType().equals(ClaimBehaviorType.PROACTIVE))
			// {
			// // Some kind of Goal driven behavior here?
			//
			// ((GoalAgent)this).setGoals(cbd.getStatements());
			// }
		}
	}
	
	/**
	 * Allows access to the agent's knowledge base from this package (e.g. for {@link ClaimBehavior}).
	 * 
	 * @return the knowledge base.
	 */
	KnowledgeBase getKBase()
	{
		return getKB();
	}
	
	@Override
	protected void resetVisualization()
	{
		
		super.resetVisualization();
		
		if(behaviors != null)
			for(Object cb : behaviors.toArray())
			{
				((ClaimBehavior) cb).resetGui();
			}
		
	}
}
