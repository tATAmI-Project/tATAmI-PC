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

import net.xqhs.util.config.Config;
import net.xqhs.util.logging.Log;
import net.xqhs.util.logging.Logger;
import net.xqhs.util.logging.Logger.Level;
import net.xqhs.util.logging.UnitComponent;
import net.xqhs.util.logging.Logging.ReportingEntity;
import net.xqhs.util.logging.Unit;
import net.xqhs.util.logging.UnitComponentExt;

import tatami.core.agent.AgentComponent;
import tatami.core.agent.AgentEventHandler;
import tatami.core.agent.CompositeAgent;
import tatami.core.agent.AgentEventHandler.AgentEventType;
import tatami.core.agent.messaging.MessagingComponent;
import tatami.core.agent.movement.MovementComponent;
import tatami.core.agent.parametric.AgentParameterName;
import tatami.core.agent.parametric.ParametricComponent;
import tatami.core.agent.visualization.AgentGui.AgentGuiConfig;
import tatami.core.agent.visualization.AgentGui.DefaultComponent;
import tatami.core.util.platformUtils.PlatformUtils;

/**
 * This component manages all things related to the visualization and remote control of the agent. Namely:
 * <ul>
 * <li>the agent's log
 * <li>reporting the agent's log to the VisualizationAngent (if any)
 * <li>reporting information related to the hierarchical structure of agents
 * <li>handling of orderly exit of agents
 * </ul>
 * 
 * @author Andrei Olaru
 */
public class VisualizableComponent extends AgentComponent implements ReportingEntity
{
	/**
	 * Class UID.
	 */
	private static final long	serialVersionUID	= -5680276720645213673L;
	
	/**
	 * The vocabulary of message types related to visualization.
	 * 
	 * @author Andrei Olaru
	 */
	public static enum Vocabulary {
		/**
		 * The name of this component.
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
		 * Sent by Simulator to the Visualizer, to instruct all visualized agents to exit, following user request to end
		 * simulation.
		 */
		PREPARE_EXIT,
		
		/**
		 * Instructs immediate exit, indicating the end of the simulation.
		 */
		DO_EXIT,
	}
	
	/**
	 * The logging {@link Unit}.
	 */
	protected transient UnitComponentExt	loggingUnit			= null;
	/**
	 * The {@link Config} for the GUI. Should remain the same object throughout the agent's lifecycle, although it may
	 * be changed, and the agent's gui will be recreated.
	 */
	protected AgentGuiConfig				guiConfig			= new AgentGuiConfig();
	/**
	 * The GUI implementation. Depends on platform, but implements {@link AgentGui}.
	 */
	protected transient AgentGui			gui					= null;
	/**
	 * The name of the entity to which the agent has to report.
	 */
	private String							visualizationParent	= null;
	/**
	 * The name of the entity which serves as the current container for the agent.
	 */
	private String							currentContainer	= null;
	
	/**
	 * windowType should be set before if a special value is needed
	 */
	public VisualizableComponent()
	{
		super(AgentComponentName.VISUALIZABLE_COMPONENT);
		
		// set GUI
		guiConfig.setWindowName(getAgentName());
		ParametricComponent parametric = getParametric();
		if((parametric != null) && parametric.hasPar(AgentParameterName.WINDOW_TYPE))
			guiConfig.setWindowType(parametric.parVal(AgentParameterName.WINDOW_TYPE));
		
		resetVisualization();
		
		// behaviors
		
		// receive the visualization root
		final MessagingComponent msgr = getMessaging();
		if(msgr != null)
		{
			msgr.registerMessageReceiver(
					msgr.makePath(Vocabulary.VISUALIZATION.toString(), Vocabulary.VISUALIZATION_MONITOR.toString()),
					new AgentEventHandler() {
						@Override
						public void handleEvent(AgentEventType eventType, Object eventData)
						{
							String parent = msgr.extractContent(eventData);
							setVisualizationParent(parent);
							getLog().info("visualization root received: [" + parent + "]");
						}
					});
			msgr.registerMessageReceiver(
					msgr.makePath(Vocabulary.VISUALIZATION.toString(), Vocabulary.DO_EXIT.toString()),
					new AgentEventHandler() {
						@SuppressWarnings("synthetic-access")
						@Override
						public void handleEvent(AgentEventType eventType, Object eventData)
						{
							getLog().info("exiting...");
							postAgentEvent(AgentEventType.AGENT_EXIT);
						}
					});
		}
		
		registerHandler(AgentEventType.BEFORE_MOVE, new AgentEventHandler() {
			@Override
			public void handleEvent(AgentEventType eventType, Object eventData)
			{
				MovementComponent mvmt = (MovementComponent) getComponent(AgentComponentName.MOVEMENT_COMPONENT);
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
				getLog().info(getAgentName() + ": arrived after move");
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
		 * if (hasPar(AgentParameterName.GUI)) { guiConfig.setGuiClass(parVal(AgentParameterName.GUI),
		 * parVals(AgentParameterName.AGENT_PACKAGE)); // overrides any }
		 */
		
		ParametricComponent parametric = getParametric();
		if(parametric != null && parametric.hasPar(AgentParameterName.GUI))
			guiConfig.setGuiClass(parametric.parVal(AgentParameterName.GUI),
					parametric.parVals(AgentParameterName.AGENT_PACKAGE));
		
		try
		{
			/*
			 * if (PlatformUtils.Platform.ANDROID.equals(PlatformUtils .getPlatform()))
			 * 
			 * guiConfig.setGuiClass(parVal(AgentParameterName.GUI), parVals(AgentParameterName.AGENT_PACKAGE));
			 */
			
			cl = new ClassLoader(getClass().getClassLoader()) {
				// nothing to extend
			};
			Constructor<?> cons = cl.loadClass(guiConfig.guiClassName).getConstructor(AgentGuiConfig.class);
			gui = (AgentGui) cons.newInstance(guiConfig);
		} catch(Exception e)
		{
			e.printStackTrace(); // there is no log yet
		}
		
		// configure log / logging Unit
		
		loggingUnit = new UnitComponentExt().setName(getAgentName()).ensureNew().setReporter(this)
				.setType(PlatformUtils.platformLogType()).setLevel(Level.ALL);
		if(gui != null)
			// unitConfig.setTextArea((TextArea)((PCDefaultAgentGui)gui).getComponent(DefaultComponent.AGENT_LOG.toString()));
			loggingUnit.setDisplay(new Log2AgentGui(gui, DefaultComponent.AGENT_LOG.toString()));
		
		getLog().trace("visualization on platform " + PlatformUtils.getPlatform());
	}
	
	protected void removeVisualization()
	{
		getLog().trace("closing visualization");
		loggingUnit.doExit();
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
			MessagingComponent msgr = (MessagingComponent) getComponent(AgentComponentName.MESSAGING_COMPONENT);
			if(msgr != null)
			{
				msgr.sendMessage(visualizationParent, msgr.makePath(reportType.toString()), content);
				return true;
			}
		}
		return false;
	}
	
	protected void setVisualizationParent(String parentName)
	{
		visualizationParent = parentName;
	}
	
	public Logger getLog()
	{
		return loggingUnit;
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
