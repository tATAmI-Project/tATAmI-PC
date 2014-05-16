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

import java.util.HashSet;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import net.xqhs.graphs.graph.Graph;
import net.xqhs.util.logging.Logger;
import net.xqhs.util.logging.UnitComponentExt;

import tatami.core.agent.AgentComponent;
import tatami.core.agent.AgentEvent;
import tatami.sclaim.constructs.basic.ClaimAgentDefinition;
import tatami.sclaim.constructs.basic.ClaimBehaviorDefinition;
import tatami.sclaim.constructs.basic.ClaimBehaviorType;
import tatami.sclaim.constructs.basic.ClaimConstruct;
import tatami.sclaim.constructs.basic.ClaimFunctionCall;
import tatami.sclaim.constructs.basic.ClaimFunctionType;
import tatami.sclaim.constructs.basic.ClaimValue;
import tatami.sclaim.constructs.basic.ClaimVariable;
import tatami.core.agent.AgentEvent.AgentEventHandler;
import tatami.core.agent.AgentEvent.AgentEventType;
import tatami.core.agent.kb.ContextComponent;
import tatami.core.agent.messaging.MessagingComponent;
import tatami.core.agent.parametric.AgentParameterName;
import tatami.core.agent.parametric.ParametricComponent;
import tatami.core.agent.visualization.VisualizableComponent;
import tatami.core.agent.webServices.WebserviceComponent;

public class ClaimComponent extends AgentComponent
{

	@SuppressWarnings("javadoc")
	private static final long		serialVersionUID	= 3562319445295180030L;
	
	/**
	 * The list of behaviors of the agent.
	 */
	protected Vector<ClaimBehavior>	behaviors			= null;

	/**
	 * The agent definition describing this agent.
	 */
	protected ClaimAgentDefinition	cad;
	protected SymbolTable st;
	
	protected Logger log;
	
	public ClaimComponent() 
	{
		super(AgentComponentName.S_CLAIM_COMPONENT);
	}
	
	public ClaimComponent(HashSet<Entry<String, Object>> className) {
		this();

		log = (UnitComponentExt) new UnitComponentExt().setUnitName("testing").setLogLevel(Logger.Level.ALL);
			
		cad = (ClaimAgentDefinition) className.iterator().next().getValue();
		if(cad == null)
		{
			log.error("agent definition not found");
			throw new IllegalArgumentException("agent definition not found");
		}

		st = new SymbolTable(null, cad.getParameters());
		st.setLog(log);
		
		behaviors = new Vector<ClaimBehavior>();
	}
	
	public void setup() {
		
		// retrieve claim agent definition
		if(cad.getParameters() != null)
		{
			Map<String, Object> claimParams = getParametric().getUnregisteredParameters();
			for(ClaimVariable agentParam : cad.getParameters()) 
			{
				AgentParameterName registeredParam = AgentParameterName.getName(agentParam.getName());
				
				if(registeredParam != null)
				{
					if (getParametric().hasPar(registeredParam)) {
						st.put(agentParam, new ClaimValue((String)getParametric().parObj(registeredParam)));
					}
					else
					{
						if(agentParam.getName().equals("parent"))
							st.put(new ClaimVariable("parent", true), null);
						else
							log.error("registered agent parameter [" + agentParam + "] not found");
					}
				}
				else 
				{
					if(claimParams.containsKey(agentParam.getName()))
						st.put(agentParam, new ClaimValue(claimParams.get(agentParam.getName())));
					else if(!agentParam.getName().equals("this"))
						log.error("agent parameter [" + agentParam + "] not found");
					 
				}
			}
		}
		
		// bind value for "this" parameter (agent's local name)
		st.put(new ClaimVariable("this"), new ClaimValue(getParametric().parObj(AgentParameterName.AGENT_NAME)));
		
		for(int i = 0; i < this.cad.getBehaviors().size(); i++)
		{
			ClaimBehaviorDefinition cbd = this.cad.getBehaviors().get(i);
			ClaimBehavior cb = new ClaimBehavior(cbd, st, this);
			
			behaviors.add(cb);
			
			if(cbd.getBehaviorType().equals(ClaimBehaviorType.REACTIVE))
				// register wb service
				if (getWebService() != null)
					getWebService().registerWSBehavior();
		}
			
	}

	public void registerBehaviors()
	{
		try	
		{
			Thread.sleep(3000);
		} catch(InterruptedException e)
		{
			e.printStackTrace();
		}
			
		for (final ClaimBehavior cb : getBehaviors())
		{
			if (cb.getBehaviorType().equals(ClaimBehaviorType.INITIAL)) {
				
				// FIXME emma
				try	
				{
					Thread.sleep(6000);
				} catch(InterruptedException e)
				{
					e.printStackTrace();
				}
				
				registerHandler(AgentEventType.AGENT_INITIAL_BEHAVIOR, new AgentEventHandler() {
					@Override
					public void handleEvent(AgentEvent event)
					{
						System.out.println("aaaaaa");
						cb.action();
						System.out.println("bbbbb");
					}
				});
				postAgentEvent(new AgentEvent(AgentEventType.AGENT_INITIAL_BEHAVIOR));
			}
			
			/*else if (cb.getBehaviorType().equals(ClaimBehaviorType.REACTIVE)) {
				registerHandler(AgentEventType.AGENT_REACTIVE_BEHAVIOR, new AgentEventHandler() {
					@Override
					public void handleEvent(AgentEvent event)
					{
						cb.action();
					}
				});
				postAgentEvent(new AgentEvent(AgentEventType.AGENT_REACTIVE_BEHAVIOR));
			}*/
		}
	}
	
	/**
	 * Allows access to the agent's knowledge base from this package (e.g. for {@link ClaimBehavior}
	 * ).
	 * 
	 * @return the knowledge base.
	 */
	Graph getKBase()
	{
		return getCognitive().getKnowledge();
	}
	
	protected void resetVisualization()
	{
		getVisualizable().resetVisualization();
		
		if(behaviors != null)
			for(Object cb : behaviors.toArray())
			{
				((ClaimBehavior)cb).resetGui();
			}
	}
	
	public Vector<ClaimBehavior> getBehaviors()
	{
		return behaviors;
	}
	
	public void matchStatement(final String source, final String content)
	{
		if (!content.contains("struct message"))
			return;
		
		for (final ClaimBehavior cb : behaviors)
		{
			if (cb.getBehaviorType().equals(ClaimBehaviorType.REACTIVE)) {
				if (cb.getCurrentStatement() instanceof ClaimFunctionCall) {
					ClaimFunctionCall cf = (ClaimFunctionCall) cb.getCurrentStatement();
					if (cf.getFunctionType().equals(ClaimFunctionType.RECEIVE)) {
						Vector<ClaimConstruct> cc = cf.getArguments();
						String[] elem = cc.get(cc.size() - 1).toString().split(" ");
						
						boolean pass = true;
						for (String e : elem) {
							if (!e.contains("?") && !content.contains(e)) {
								pass = false;
								System.out.println(e);
							}
						}
						
						if (pass) {
							if (elem.length == content.split(" ").length) {
								registerHandler(AgentEventType.AGENT_REACTIVE_BEHAVIOR, new AgentEventHandler() {
									@Override
									public void handleEvent(AgentEvent event)
									{
										try	
										{
											Thread.sleep(6000);
										} catch(InterruptedException e)
										{
											e.printStackTrace();
										}
										
										cb.actionOnReceive(source, content);
									}
								});
								postAgentEvent(new AgentEvent(AgentEventType.AGENT_REACTIVE_BEHAVIOR));
							}
						}
					}
				}
			}
		}
		
	}
	
	public Logger getLog()
	{
		return log;
	}
	
	@Override
	protected MessagingComponent getMessaging()
	{
		return super.getMessaging();
	}
	
	@Override
	protected ContextComponent getCognitive()
	{
		return super.getCognitive();
	}
	
	@Override
	protected ParametricComponent getParametric()
	{
		return super.getParametric();
	}
	
	@Override
	protected 	WebserviceComponent getWebService()
	{
		return super.getWebService();
	}
	
	@Override
	protected VisualizableComponent getVisualizable()
	{
		return super.getVisualizable();
	}
}
