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
package testing.system_testing.default_components;

import java.util.Arrays;

import net.xqhs.util.logging.LoggerSimple.Level;
import net.xqhs.util.logging.Unit;
import net.xqhs.util.logging.UnitComponent;
import tatami.core.agent.AgentComponent;
import tatami.core.agent.AgentComponent.AgentComponentName;
import tatami.core.agent.AgentEvent;
import tatami.core.agent.AgentEvent.AgentEventHandler;
import tatami.core.agent.AgentEvent.AgentEventType;
import tatami.core.agent.CompositeAgent;
import tatami.core.agent.parametric.AgentParameterName;
import tatami.core.agent.parametric.AgentParameters;
import tatami.core.agent.parametric.ParametricComponent;

/**
 * Tests the parametric component of a {@link CompositeAgent}. Creates a bare composite agent (without a platform), and
 * adds a parametric and a test component to it. The test component intercepts agent events and prints them. The agent
 * is asked to exit soon after creation.
 * <p>
 * The testing component gets the name of the agent from the {@link ParametricComponent} and prints it out.
 * 
 * @author Andrei Olaru
 *
 */
public class ParametricComponentTest extends Unit
{
	/**
	 * Main testing.
	 */
	public ParametricComponentTest()
	{
		setUnitName("parametric component tester");
		li("starting...");
		
		CompositeAgent agent = new CompositeAgent();
		final AgentParameters agentParameters = (AgentParameters) new AgentParameters().add(
				AgentParameterName.AGENT_NAME, "test parametric agent").addObject("test parameter",
				Arrays.asList("a", "b", "c"));
		
		agent.addComponent(new ParametricComponent() {
			private static final long	serialVersionUID	= 1L;
			
			// pre-loading is only possible from the inside of the component or from the core package
			@Override
			protected void componentInitializer()
			{
				preload((ComponentCreationData) new ComponentCreationData().addObject(COMPONENT_PARAMETER_NAME,
						agentParameters), null, null, null);
			}
		});
		
		agent.addComponent(new AgentComponent(AgentComponentName.TESTING_COMPONENT) {
			private static final long	serialVersionUID	= 1L;
			UnitComponent				locallog;
			
			@Override
			protected AgentComponent getAgentComponent(AgentComponentName name)
			{
				return super.getAgentComponent(name);
			}
			
			@Override
			protected void componentInitializer()
			{
				super.componentInitializer();
				
				locallog = (UnitComponent) new UnitComponent().setUnitName("monitoring").setLogLevel(Level.ALL);
				AgentEventHandler allEventHandler = new AgentEventHandler() {
					@Override
					public void handleEvent(AgentEvent event)
					{
						locallog.li("event: [" + event.getType().toString() + "]");
						ParametricComponent parametric = (ParametricComponent) getAgentComponent(AgentComponentName.PARAMETRIC_COMPONENT);
						if(parametric == null)
							locallog.li("\t parametric component is currently null");
						else
							locallog.li("\t parameter value: []; unregistered parameters: []",
									parametric.parVal(AgentParameterName.AGENT_NAME),
									parametric.getUnregisteredParameters());
						if(event.getType() == AgentEventType.AGENT_STOP)
							locallog.doExit();
					}
				};
				for(AgentEventType eventType : AgentEventType.values())
					registerHandler(eventType, allEventHandler);
			}
		});
		
		agent.start();
		try
		{
			Thread.sleep(200);
		} catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		agent.exit();
		li("done.");
		doExit();
	}
	
	/**
	 * Main method.
	 * 
	 * @param args
	 *            - not used.
	 */
	@SuppressWarnings("unused")
	public static void main(String args[])
	{
		new ParametricComponentTest();
	}
}
