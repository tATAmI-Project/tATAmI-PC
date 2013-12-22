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

import net.xqhs.util.config.Config;
import net.xqhs.util.logging.Logger;
import net.xqhs.util.logging.Logger.Level;
import net.xqhs.util.logging.Logging.ReportingEntity;
import net.xqhs.util.logging.Unit;
import net.xqhs.util.logging.UnitComponentExt;
import tatami.core.agent.AgentComponent;
import tatami.core.agent.AgentEvent;
import tatami.core.agent.CompositeAgent;
import tatami.core.agent.AgentEvent.AgentEventHandler;
import tatami.core.agent.AgentEvent.AgentEventType;
import tatami.core.agent.messaging.MessagingComponent;
import tatami.core.agent.movement.MovementComponent;
import tatami.core.agent.parametric.AgentParameterName;
import tatami.core.agent.parametric.ParametricComponent;
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
	 * Create a new {@link VisualizableComponent} instance:
	 * <ul>
	 * <li>Creates a GUI
	 * <li>Creates a log, links it to the GUI.
	 * <li>Registers message receivers with the {@link MessagingComponent} (if any).
	 * <li>Registers handlers for agent movement.
	 * </ul>
	 * 
	 * windowType should be set before if a special value is needed
	 */
	public VisualizableComponent()
	{
		super(AgentComponentName.VISUALIZABLE_COMPONENT);
		
		// registers event handlers
		registerHandler(AgentEventType.BEFORE_MOVE, new AgentEventHandler() {
			@Override
			public void handleEvent(AgentEvent event)
			{
				MovementComponent mvmt = getMovement();
				if(mvmt != null)
				{
					String destination = mvmt.extractDestination(event);
					if(!destination.equals(getCurrentContainer()))
					{
						getLog().info("moving to [" + destination.toString() + "]");
						removeVisualization();
					}
				}
			}
		});
		
		registerHandler(AgentEventType.BEFORE_MOVE, new AgentEventHandler() {
			@Override
			public void handleEvent(AgentEvent event)
			{
				resetVisualization();
				getLog().info(getAgentName() + ": arrived after move");
			}
		});
		
		registerHandler(AgentEventType.AGENT_EXIT, new AgentEventHandler() {
			@Override
			public void handleEvent(AgentEvent event)
			{
				removeVisualization();
			}
		});
	}
	
	@Override
	protected void parentChangeNotifier(CompositeAgent oldParent)
	{
		super.parentChangeNotifier(oldParent);
		
		if(getParent() == null)
		{ // parent was removed;
			getLog().trace("parent removed.");
			removeVisualization();
		}
		else
		{ // setup additional GUI configuration
			guiConfig.setWindowName(getAgentName());
			ParametricComponent parametric = getParametric();
			if((parametric != null))
			{
				if(parametric.hasPar(AgentParameterName.WINDOW_TYPE))
					guiConfig.setWindowType(parametric.parVal(AgentParameterName.WINDOW_TYPE));
				if(parametric.hasPar(AgentParameterName.GUI))
					guiConfig.setGuiClass(parametric.parVal(AgentParameterName.GUI),
							parametric.parVals(AgentParameterName.AGENT_PACKAGE));
			}
			// creates visualization & log
			resetVisualization();
			
			// registers message receivers: receive the visualization root; receive exit message.
			final MessagingComponent msgr = getMessaging();
			if(msgr != null)
			{
				msgr.registerMessageReceiver(
						msgr.makePath(Vocabulary.VISUALIZATION.toString(), Vocabulary.VISUALIZATION_MONITOR.toString()),
						new AgentEventHandler() {
							@Override
							public void handleEvent(AgentEvent event)
							{
								String parent = msgr.extractContent(event);
								setVisualizationParent(parent);
								getLog().info("visualization root received: [" + parent + "]");
							}
						});
				msgr.registerMessageReceiver(
						msgr.makePath(Vocabulary.VISUALIZATION.toString(), Vocabulary.DO_EXIT.toString()),
						new AgentEventHandler() {
							@Override
							public void handleEvent(AgentEvent event)
							{
								getLog().info("exiting...");
								postAgentEvent(new AgentEvent(AgentEventType.AGENT_EXIT));
							}
						});
			}
		}
	}
	
	/**
	 * Creates the [platform-specific] visualization elements: the GUI and the log.
	 */
	protected void resetVisualization()
	{
		// configure log / logging Unit
		loggingUnit = (UnitComponentExt) new UnitComponentExt().setUnitName(getAgentName()).setLogEnsureNew()
				.setLogReporter(this).setLogType(PlatformUtils.platformLogType()).setLogLevel(Level.ALL);
		
		// load GUI
		try
		{
			gui = (AgentGui) PlatformUtils.loadClassInstance(this, guiConfig.guiClassName, guiConfig);
		} catch(Exception e)
		{
			getLog().error("Load GUI failed: " + PlatformUtils.printException(e));
		}
		if(gui != null)
			loggingUnit.setLogDisplay(new Log2AgentGui(gui, DefaultComponent.AGENT_LOG.toString()));
		
		getLog().trace("visualization started on platform " + PlatformUtils.getPlatform());
	}
	
	/**
	 * Unloads elements that were loaded during <code>resetVisualization</code>.
	 */
	protected void removeVisualization()
	{
		getLog().trace("closing visualization");
		if(gui != null)
			gui.close();
		if(loggingUnit != null)
			loggingUnit.doExit();
	}
	
	/**
	 * Setter for the name of the visualization parent.
	 * 
	 * @param parentName
	 *            - name of the visualization parent.
	 */
	protected void setVisualizationParent(String parentName)
	{
		visualizationParent = parentName;
	}
	
	/**
	 * Reports an update to the log.
	 * <p>
	 * This method will be called by the logging infrastructure, that sees this component as a {@link ReportingEntity}.
	 */
	@Override
	public boolean report(String content)
	{
		return report(Vocabulary.LOGGING_UPDATE, content);
	}
	
	/**
	 * Reports a new hierarchical parent for the agent.
	 * 
	 * @param parent
	 *            - the name of the parent.
	 */
	public void reportAddParent(String parent)
	{
		report(Vocabulary.ADD_PARENT, parent);
	}
	
	/**
	 * Reports the removal of a parent of the agent.
	 * 
	 * @param parent
	 *            - the name of the parent.
	 */
	public void reportRemoveParent(String parent)
	{
		report(Vocabulary.REMOVE_PARENT, parent);
	}
	
	/**
	 * Implements reporting as sending a message to the visualization parent.
	 * <p>
	 * Uses the messaging capabilities offered by {@link MessagingComponent}.
	 * 
	 * @param reportType
	 *            - an adequate member of the {@link Vocabulary} (<code>LOGGING_UPDATE</code>, <code>ADD_PARENT</code>,
	 *            <code>REMOVE_PARENT</code>, <code>MOVE</code>).
	 * @param content
	 *            - the content of the message.
	 * @return - true if the message was successfully sent.
	 */
	protected boolean report(Vocabulary reportType, String content)
	{
		if(visualizationParent != null)
		{
			if((reportType == Vocabulary.LOGGING_UPDATE) || (reportType == Vocabulary.ADD_PARENT)
					|| (reportType == Vocabulary.REMOVE_PARENT) || (reportType == Vocabulary.MOVE))
			{
				MessagingComponent msgr = (MessagingComponent) getComponent(AgentComponentName.MESSAGING_COMPONENT);
				if(msgr != null)
					return msgr.sendMessage(visualizationParent, msgr.makePath(reportType.toString()), content);
			}
			else
				throw new IllegalArgumentException("Parameter is not a correct report type:" + reportType);
		}
		return false;
	}
	
	/**
	 * Getter for the log.
	 * 
	 * @return the log, as a {@link Logger} instance.
	 */
	public Logger getLog()
	{
		return loggingUnit;
	}
	
	/**
	 * Getter for the GUI.
	 * 
	 * @return the GUI, as an {@link AgentGui} instance.
	 */
	public AgentGui getGUI()
	{
		return gui;
	}
	
	/**
	 * Relays calls to the underlying {@link AgentComponent} instance in order to avoid synthetic access warnings for
	 * event handlers.
	 */
	@Override
	protected String getAgentName()
	{
		return super.getAgentName();
	}
	
	/**
	 * Getter for <code>currentContainer</code>.
	 * 
	 * @return the name of the current container.
	 */
	protected String getCurrentContainer()
	{
		return currentContainer;
	}
	
	/**
	 * Relays calls to the underlying {@link AgentComponent} instance in order to avoid synthetic access warnings for
	 * event handlers.
	 */
	@Override
	protected MessagingComponent getMessaging()
	{
		return super.getMessaging();
	}
	
	/**
	 * Relays calls to the underlying {@link AgentComponent} instance in order to avoid synthetic access warnings for
	 * event handlers.
	 * 
	 * @return the movement component.
	 */
	protected MovementComponent getMovement()
	{
		if(super.hasComponent(AgentComponentName.MOVEMENT_COMPONENT))
			return (MovementComponent) super.getComponent(AgentComponentName.MOVEMENT_COMPONENT);
		return null;
	}
	
	/**
	 * Relays calls to the underlying {@link AgentComponent} instance in order to avoid synthetic access warnings for
	 * event handlers.
	 * 
	 */
	@Override
	protected void postAgentEvent(AgentEvent event)
	{
		super.postAgentEvent(event);
	}
	
}
