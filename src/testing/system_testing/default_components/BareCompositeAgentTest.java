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

import net.xqhs.util.logging.LoggerSimple.Level;
import net.xqhs.util.logging.Unit;
import net.xqhs.util.logging.UnitComponent;
import net.xqhs.util.logging.logging.Logging;
import tatami.core.agent.AgentComponent;
import tatami.core.agent.AgentComponent.AgentComponentName;
import tatami.core.agent.AgentEvent;
import tatami.core.agent.AgentEvent.AgentEventHandler;
import tatami.core.agent.AgentEvent.AgentEventType;
import tatami.core.agent.CompositeAgent;

/**
 * Creates a bare composite agent (without a platform), and adds a test component to it. The test component intercepts
 * agent events and prints them. The agent is asked to exit soon after creation.
 * <p>
 * Expected output:
 * <ul>
 * <li>start successful
 * <li>interception of AGENT_START
 * <li>delay
 * <li>interception of AGENT_EXIT
 * <li>system exit
 * </ul>
 * 
 * @author Andrei Olaru
 */
public class BareCompositeAgentTest extends Unit
{
	/**
	 * General level for logs.
	 */
	static final Level generalLevel = Level.ALL;
	
	/**
	 * Main testing method.
	 */
	public BareCompositeAgentTest()
	{
		setUnitName("composite agent tester").setLogLevel(generalLevel);
		li("starting...");
		
		CompositeAgent agent = new CompositeAgent();
		agent.addComponent(new AgentComponent(AgentComponentName.TESTING_COMPONENT) {
			private static final long	serialVersionUID	= 1L;
			UnitComponent				locallog;
										
			@Override
			protected void componentInitializer()
			{
				super.componentInitializer();
				
				locallog = (UnitComponent) new UnitComponent().setUnitName("monitoring").setLogLevel(generalLevel);
				
				AgentEventHandler allEventHandler = new AgentEventHandler() {
					@Override
					public void handleEvent(AgentEvent event)
					{
						if(locallog == null)
							System.out.println("local log is null");
						else
							locallog.li("event: [" + event.toString() + "]");
						if(event.getType() == AgentEventType.AGENT_STOP)
							locallog.doExit();
					}
				};
				for(AgentEventType eventType : AgentEventType.values())
					registerHandler(eventType, allEventHandler);
			}
		});
		
		// trying to stop already stopped
		if(agent.stop())
			le("erroneous stop successful");
		else
			li("erroneous stop failed");
			
		// test transient state
		agent.toggleTransient();
		
		// erroneous start in transient
		if(agent.start())
			le("erroneous start successful");
		else
			li("erroneous start failed");
			
		// attempt to add component in transient state
		try
		{
			agent.addComponent(new AgentComponent(AgentComponentName.TESTING_COMPONENT) {
				private static final long serialVersionUID = 1L;
			});
		} catch(RuntimeException e1)
		{
			le("Failed adding component: ", e1);
		}
		
		agent.toggleTransient();
		
		// normal start
		if(agent.start())
			li("start successful");
		else
			le("start failed");
			
		// trying to start already started
		if(agent.start())
			le("re-start successful");
		else
			li("re-start failed");
		try
		{
			Thread.sleep(2000);
		} catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		// trying to start already started
		if(agent.start())
			le("re-re-start successful");
		else
			li("re-re-start failed");
			
		// attempting to stop then re-start the agent
		if(agent.stop())
			li("stop successful");
		else
			le("stop failed");
			
		int tries = 10;
		while(!agent.isStopped() && tries > 0)
			try
			{
				tries--;
				Thread.sleep(10);
			} catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		li("agent stopped after [] tries.", new Integer(tries));
			
		if(agent.start())
			li("start 2 successful");
		else
			le("start 2 failed");
			
		boolean done = false;
		while(!done)
		{
			done = agent.exit();
			if(!done)
				le("exit failed");
			try
			{
				Thread.sleep(50);
			} catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		li("exit successful");
		if(!agent.exit())
			li("re-exit failed");
		else
			li("re-exit successful");
		li("done.");
		doExit();
	}
	
	/**
	 * main
	 * 
	 * @param args
	 *            - not used
	 */
	@SuppressWarnings("unused")
	public static void main(String args[])
	{
		Logging.getMasterLogging().setLogLevel(generalLevel);
		new BareCompositeAgentTest();
	}
}
