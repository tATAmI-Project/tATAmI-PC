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
package tatami.core.agent.visualization;

import java.lang.reflect.Constructor;

import tatami.core.agent.CompositeAgent;
import tatami.core.agent.messaging.MessagingComponent;
import tatami.core.agent.movement.MovementComponent;
import tatami.core.agent.parametric.ParametricComponent;
import tatami.core.interfaces.AgentComponent;
import tatami.core.interfaces.AgentEventHandler;
import tatami.core.interfaces.AgentEventHandler.AgentEventType;
import tatami.core.interfaces.AgentGui;
import tatami.core.interfaces.AgentGui.AgentGuiConfig;
import tatami.core.interfaces.AgentGui.DefaultComponent;
import tatami.core.interfaces.AgentParameterName;
import tatami.core.interfaces.Logger;
import tatami.core.interfaces.Logger.Level;
import tatami.core.util.Config;
import tatami.core.util.logging.Log.ReportingEntity;
import tatami.core.util.logging.Unit;
import tatami.core.util.logging.Unit.UnitConfigData;
import tatami.core.util.platformUtils.PlatformUtils;

public class VisualizableComponent extends AgentComponent implements ReportingEntity
{
	public static enum Vocabulary {
		/**
		 * The name of the component.
		 */
		VISUALIZATION,
		
		/**
		 * Content is an update to the logging info of the reporting agent.
		 */
		LOGGING_UPDATE,
		
		/**
		 * Content is the name of the agent that is now a parent of the reporting agent.
		 */
		ADD_PARENT,
		
		/**
		 * Content is the name of the agent that is not anymore a parent of the reporting agent.
		 */
		REMOVE_PARENT,
		
		/**
		 * Content is the name of the container now containing the reporting agent.
		 */
		MOVE,
		
		/**
		 * Content is the name of the agent that will be monitoring the activity of the receiver.
		 */
		VISUALIZATION_MONITOR,
		
		/**
		 * Sent by Simulator to the Visualizer, to instruct all visualized agents to exit, following
		 * user request to end simulation.
		 */
		PREPARE_EXIT,
		
		/**
		 * Instructs immediate exit, indicating the end of the simulation.
		 */
		DO_EXIT,
	}
	
	// logging
	/**
	 * the logging {@link Unit}.
	 */
	protected transient Unit		loggingUnit			= null;
	/**
	 * provides easier access to the log. Should always be equal to
	 * <code>loggingUnit.getLog()</code>. Also provided by <code>getLog()</code> .
	 */
	protected transient Logger		log					= null;
	/**
	 * The {@link Config} fort the GUI. Should remain the same object throughout the agent's
	 * lifecycle, although it may be changed, and the agent's gui will be recreated.
	 */
	protected AgentGuiConfig		guiConfig			= new AgentGuiConfig();
	protected transient AgentGui	gui					= null;
	private String					visualizationParent	= null;
	private String					currentContainer	= null;
	
	/**
	 * windowType should be set before if a special value is needed
	 */
	public VisualizableComponent(CompositeAgent parent)
	{
		super(parent, AgentComponentName.VISUALIZABLE_COMPONENT);
		ParametricComponent parametric = (ParametricComponent)parentAgent
				.getComponent(AgentComponentName.PARAMETRIC_COMPONENT);
		
		// set gui
		guiConfig.setWindowName(parent.getAgentName());
		if((parametric != null) && parametric.hasPar(AgentParameterName.WINDOW_TYPE))
			guiConfig.setWindowType(parametric.parVal(AgentParameterName.WINDOW_TYPE));
		
		resetVisualization();
		
		// behaviors
		
		// receive the visualization root
		final MessagingComponent msgr = (MessagingComponent)parentAgent
				.getComponent(AgentComponentName.MESSAGING_COMPONENT);
		if(msgr != null)
		{
			msgr.registerMessageReceiver(msgr.makePath(Vocabulary.VISUALIZATION.toString(),
					Vocabulary.VISUALIZATION_MONITOR.toString()), new AgentEventHandler() {
				@Override
				public void handleEvent(AgentEventType eventType, Object eventData)
				{
					visualizationParent = msgr.extractContent(eventData);
					getLog().info("visualization root received: [" + visualizationParent + "]");
				}
			});
			msgr.registerMessageReceiver(
					msgr.makePath(Vocabulary.VISUALIZATION.toString(),
							Vocabulary.DO_EXIT.toString()), new AgentEventHandler() {
						@SuppressWarnings("synthetic-access")
						@Override
						public void handleEvent(AgentEventType eventType, Object eventData)
						{
							getLog().info("exiting...");
							parentAgent.postAgentEvent(AgentEventType.AGENT_EXIT);
						}
					});
		}
		
		registerHandler(AgentEventType.BEFORE_MOVE, new AgentEventHandler() {
			@Override
			public void handleEvent(AgentEventType eventType, Object eventData)
			{
				MovementComponent mvmt = (MovementComponent)parentAgent
						.getComponent(AgentComponentName.MOVEMENT_COMPONENT);
				if(mvmt != null)
				{
					String destination = mvmt.extractDestination(eventData);
					if(!destination.equals(currentContainer))
					{
						getLog().info("moving to [" + destination.toString() + "]");
						removeVisualization();
					}
				}
			}
		});
		
		registerHandler(AgentEventType.BEFORE_MOVE, new AgentEventHandler() {
			@Override
			public void handleEvent(AgentEventType eventType, Object eventData)
			{
				resetVisualization();
				log.info(parentAgent.getAgentName() + ": arrived after move");
			}
		});
		
		registerHandler(AgentEventType.AGENT_EXIT, new AgentEventHandler() {
			@Override
			public void handleEvent(AgentEventType eventType, Object eventData)
			{
				removeVisualization();
			}
		});
	}
	
	/**
	 * Creates the [platform-specific] visualization elements: the GUI and the log.
	 */
	protected void resetVisualization()
	{
		ClassLoader cl = null;
		
		/*
		 * if (hasPar(AgentParameterName.GUI)) {
		 * guiConfig.setGuiClass(parVal(AgentParameterName.GUI),
		 * parVals(AgentParameterName.AGENT_PACKAGE)); // overrides any }
		 */
		
		ParametricComponent parametric = (ParametricComponent)parentAgent
				.getComponent(AgentComponentName.PARAMETRIC_COMPONENT);
		if(parametric != null && parametric.hasPar(AgentParameterName.GUI))
			guiConfig.setGuiClass(parametric.parVal(AgentParameterName.GUI),
					parametric.parVals(AgentParameterName.AGENT_PACKAGE));
		
		try
		{
			/*
			 * if (PlatformUtils.Platform.ANDROID.equals(PlatformUtils .getPlatform()))
			 * 
			 * guiConfig.setGuiClass(parVal(AgentParameterName.GUI),
			 * parVals(AgentParameterName.AGENT_PACKAGE));
			 */
			
			cl = new ClassLoader(getClass().getClassLoader()) {
				// nothing to extend
			};
			Constructor<?> cons = cl.loadClass(guiConfig.guiClassName).getConstructor(
					AgentGuiConfig.class);
			gui = (AgentGui)cons.newInstance(guiConfig);
		} catch(Exception e)
		{
			e.printStackTrace(); // there is no log yet
		}
		
		// configure log / logging Unit
		
		UnitConfigData unitConfig = new UnitConfigData().setName(parentAgent.getAgentName())
				.ensureNew().setReporter(this);
		unitConfig.setType(PlatformUtils.platformLogType());
		unitConfig.setLevel(Level.ALL);
		if(gui != null)
			// unitConfig.setTextArea((TextArea)((PCDefaultAgentGui)gui).getComponent(DefaultComponent.AGENT_LOG.toString()));
			unitConfig.setDisplay(new Log2AgentGui(gui, DefaultComponent.AGENT_LOG.toString()));
		loggingUnit = new Unit(unitConfig);
		log = getLog();
		
		log.trace("visualization on platform " + PlatformUtils.getPlatform());
	}
	
	protected void removeVisualization()
	{
		getLog().trace("closing visualization");
		loggingUnit.exit();
		if(gui != null)
			gui.close();
	}
	
	@Override
	public boolean report(String content)
	{
		return report(Vocabulary.LOGGING_UPDATE, content);
	}
	
	public void reportAddParent(String parent)
	{
		report(Vocabulary.ADD_PARENT, parent);
	}
	
	public void reportRemoveParent(String parent)
	{
		report(Vocabulary.REMOVE_PARENT, parent);
	}
	
	protected boolean report(Vocabulary reportType, String content)
	{
		// FIXME: check correct type
		if(visualizationParent != null)
		{
			final MessagingComponent msgr = (MessagingComponent)parentAgent
					.getComponent(AgentComponentName.MESSAGING_COMPONENT);
			if(msgr != null)
			{
				msgr.sendMessage(visualizationParent, msgr.makePath(reportType.toString()), content);
				return true;
			}
		}
		return false;
	}
	
	public Logger getLog()
	{
		return loggingUnit.getLog();
	}
	
	public AgentGui getGUI()
	{
		return gui;
	}
	
	public AgentGuiConfig getGuiConfig()
	{
		return guiConfig;
	}
}
