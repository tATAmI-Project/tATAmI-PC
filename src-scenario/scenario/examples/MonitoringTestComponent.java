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
package scenario.examples;

import net.xqhs.util.logging.LoggerSimple.Level;
import net.xqhs.util.logging.UnitComponent;
import tatami.core.agent.AgentComponent;
import tatami.core.agent.AgentEvent;
import tatami.core.agent.AgentEvent.AgentEventHandler;
import tatami.core.agent.AgentEvent.AgentEventType;
import tatami.core.agent.CompositeAgent;
import tatami.core.agent.parametric.AgentParameterName;
import tatami.core.agent.parametric.ParametricComponent;
import tatami.core.agent.visualization.VisualizableComponent;

/**
 * An {@link AgentComponent} implementation that monitors all agent events.
 * 
 * @author Andrei Olaru
 */
public class MonitoringTestComponent extends AgentComponent
{
	/**
	 * The UID.
	 */
	private static final long	serialVersionUID	= 5214882018809437402L;
	/**
	 * The log.
	 */
	UnitComponent				locallog			= null;
	
	/**
	 * Default constructor
	 */
	public MonitoringTestComponent()
	{
		super(AgentComponentName.TESTING_COMPONENT);
	}
	
	@Override
	protected String getAgentName()
	{
		return super.getAgentName();
	}
	
	@Override
	protected AgentComponent getAgentComponent(AgentComponentName name)
	{
		return super.getAgentComponent(name);
	}
	
	@Override
	protected void componentInitializer()
	{
		super.componentInitializer();
		
		AgentEventHandler allEventHandler = new AgentEventHandler() {
			@Override
			public void handleEvent(AgentEvent event)
			{
				String eventMessage = "agent [" + getAgentName() + "] event: [" + event.toString() + "]";
				locallog.li(eventMessage);
				ParametricComponent parametric = (ParametricComponent) getAgentComponent(AgentComponentName.PARAMETRIC_COMPONENT);
				if(parametric != null)
					locallog.li("\t parameter value: [" + parametric.parVal(AgentParameterName.AGENT_NAME) + "]");
				else
					locallog.li("\t parametric component is currently null");
				VisualizableComponent vis = (VisualizableComponent) getAgentComponent(AgentComponentName.VISUALIZABLE_COMPONENT);
				if(vis != null)
					if(vis.getLog() != null)
						vis.getLog().info(eventMessage);
				if(event.getType() == AgentEventType.AGENT_STOP)
					locallog.doExit();
			}
		};
		for(AgentEventType eventType : AgentEventType.values())
			registerHandler(eventType, allEventHandler);
	}
	
	@Override
	protected void parentChangeNotifier(CompositeAgent oldParent)
	{
		super.parentChangeNotifier(oldParent);
		
		if(getParent() != null)
		{
			locallog = (UnitComponent) new UnitComponent().setUnitName("monitoring-" + getAgentName()).setLogLevel(
					Level.ALL);
			locallog.lf("testing started.");
		}
		else if(locallog != null)
		{
			locallog.doExit();
			locallog = null;
		}
	}
}
