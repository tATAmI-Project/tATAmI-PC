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

import java.util.Vector;

import net.xqhs.util.XML.XMLTree.XMLNode;
import net.xqhs.util.logging.Logger;
import tatami.core.agent.AgentComponent;
import tatami.core.agent.AgentEvent;
import tatami.core.agent.AgentEvent.AgentEventHandler;
import tatami.core.agent.kb.KnowledgeBase;
import tatami.core.agent.parametric.AgentParameterName;
import tatami.core.agent.parametric.ParametricComponent;
import tatami.sclaim.constructs.basic.ClaimAgentDefinition;
import tatami.sclaim.constructs.basic.ClaimBehaviorDefinition;
import tatami.sclaim.constructs.basic.ClaimBehaviorType;
import tatami.sclaim.constructs.basic.ClaimValue;
import tatami.sclaim.constructs.basic.ClaimVariable;

/**
 * The Claim component of an agent ensures that the agent is able to execute behaviors specified in an S-CLAIM agent
 * description file (.adf2).
 * 
 * @author Andrei Olaru
 */
public class ClaimComponent extends AgentComponent implements AgentEventHandler
{
	/**
	 * The serial version UID.
	 */
	private static final long	serialVersionUID	= 0L;
	
	/**
	 * @author Andrei Olaru
	 */
	public static enum Vocabulary {
		
		/**
		 * The name of this component, to serve also as name for the endpoint.
		 */
		CLAIM,
		
		// NAME = "claim-ontology";
		//
		// LANGUAGE = "claim-language";
		//
		// ASKLOCATION = "ask-for-location";
		//
		// LOC_RTN = "location-return";
	}
	
	/**
	 * The list of behaviors of the agent.
	 */
	protected Vector<ClaimBehavior>	behaviors	= null;
	
	/**
	 * The agent definition describing this agent.
	 */
	protected ClaimAgentDefinition	cad;
	/**
	 * The symbol table hierarchy associated with this instance (and in consequence, with this agent).
	 */
	protected SymbolTable			st;
	
	/**
	 * Default constructor.
	 */
	public ClaimComponent()
	{
		super(AgentComponentName.S_CLAIM_COMPONENT);
	}
	
	@Override
	protected boolean preload(ComponentCreationData parameters, XMLNode scenarioNode, Logger log)
	{
		if(!super.preload(parameters, scenarioNode, log))
			return false;
		
		// TODO preload cad (put it in a static map of cads)
		// preload parameters in the component creation data
		
		// retrieve parameters specified in the agent
		if(cad.getParameters() != null)
		{
			for(ClaimVariable agentParam : cad.getParameters())
				if(agentParam.getName().equals("parent"))
					st.put(new ClaimVariable("parent", true), null);
		}
		
		return true;
	}
	
	@Override
	protected void atAgentStart(AgentEvent event)
	{
		super.atAgentStart(event);
		
		ParametricComponent parametric = (ParametricComponent) getAgentComponent(AgentComponentName.PARAMETRIC_COMPONENT);
		
		// retrieve parameters specified in the agent
		if(cad.getParameters() != null)
		{
			for(ClaimVariable agentParam : cad.getParameters())
			{
				if(st.get(agentParam) == null) // not yet added
				{
					AgentParameterName registeredParam = AgentParameterName.getName(agentParam.getName());
					
					if(registeredParam != null) // is a registered agent parameter
						if(parametric.hasPar(registeredParam))
							st.put(agentParam, new ClaimValue(parametric.parVal(registeredParam)));
						else
							getAgentLog().error("registered agent parameter [" + agentParam + "] not found");
					else if(parametric.getUnregisteredParameters().isSet(agentParam.getName()))
						st.put(agentParam,
								new ClaimValue(parametric.getUnregisteredParameters().get(agentParam.getName())));
				}
			}
		}
		
		// bind value for "this" parameter (agent's local name)
		st.put(new ClaimVariable("this"), new ClaimValue(parametric.parVal(AgentParameterName.AGENT_NAME)));
		
		// create behaviors
		for(ClaimBehaviorDefinition cbd : cad.getBehaviors())
		{
			behaviors.add(new ClaimBehavior(cbd, st, this, getAgentLog()));
			
			if(true)
				// TODO if behavior is message-activated
				registerMessageReceiver(this, Vocabulary.CLAIM.toString());
			
			// TODO input-activated behaviors
			
			// TODO
			// if(cbd.getBehaviorType().equals(ClaimBehaviorType.REACTIVE))
			// // register wb service
			// if(getWebService() != null)
			// getWebService().registerWSBehavior();
		}
	}
	
	@Override
	protected void atSimulationStart(AgentEvent event)
	{
		super.atSimulationStart(event);
		
		for(ClaimBehavior cb : behaviors)
			if(cb.getBehaviorType().equals(ClaimBehaviorType.INITIAL))
				cb.action();
	}
	
	/**
	 * Allows access to the agent's knowledge base from this package (e.g. for {@link ClaimBehavior} ).
	 * 
	 * @return the knowledge base.
	 */
	KnowledgeBase getKBase()
	{
		// return getCognitive().getKnowledge();
		// TODO
		return null;
	}
	
	@Override
	public void handleEvent(AgentEvent event)
	{
		// TODO receive message
		
	}
	
	/**
	 * Method for use by ClaimBehavior, calling {@link AgentComponent#sendMessage()}.
	 * 
	 * @param content
	 *            - the content of the message.
	 * @param targetAgent
	 *            - the receiver of the message.
	 * @return an indication of success.
	 */
	protected boolean sendMessage(String content, String targetAgent)
	{
		return super.sendMessage(content, getComponentEndpoint(Vocabulary.CLAIM.toString()), targetAgent,
				Vocabulary.CLAIM.toString());
	}
	
	// public void matchStatement(final String source, final String content)
	// {
	// if(!content.contains("struct message"))
	// return;
	//
	// for(final ClaimBehavior cb : behaviors)
	// {
	// if(cb.getBehaviorType().equals(ClaimBehaviorType.REACTIVE))
	// {
	// if(cb.getCurrentStatement() instanceof ClaimFunctionCall)
	// {
	// ClaimFunctionCall cf = (ClaimFunctionCall) cb.getCurrentStatement();
	// if(cf.getFunctionType().equals(ClaimFunctionType.RECEIVE))
	// {
	// Vector<ClaimConstruct> cc = cf.getArguments();
	// String[] elem = cc.get(cc.size() - 1).toString().split(" ");
	//
	// boolean pass = true;
	// for(String e : elem)
	// {
	// if(!e.contains("?") && !content.contains(e))
	// {
	// pass = false;
	// }
	// }
	//
	// if(pass)
	// {
	// if(elem.length == content.split(" ").length)
	// {
	// registerHandler(AgentEventType.AGENT_REACTIVE_BEHAVIOR, new AgentEventHandler() {
	// @Override
	// public void handleEvent(AgentEvent event)
	// {
	// try
	// {
	// Thread.sleep(6000);
	// } catch(InterruptedException e)
	// {
	// e.printStackTrace();
	// }
	//
	// (new Thread() {
	// public void run()
	// {
	// cb.actionOnReceive(source, content);
	// }
	// }).start();
	// }
	// });
	// postAgentEvent(new AgentEvent(AgentEventType.AGENT_REACTIVE_BEHAVIOR));
	// }
	// }
	// }
	// }
	// }
	// }
	//
	// }
}
