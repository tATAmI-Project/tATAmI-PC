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

import java.util.Vector;

import net.xqhs.util.config.Config;
import net.xqhs.util.logging.DumbLogger;
import net.xqhs.util.logging.Logger;
import net.xqhs.util.logging.LoggerSimple.Level;
import net.xqhs.util.logging.ReportingEntity;
import net.xqhs.util.logging.Unit;
import net.xqhs.util.logging.UnitComponentExt;
import tatami.core.agent.AgentComponent;
import tatami.core.agent.AgentEvent;
import tatami.core.agent.AgentEvent.AgentEventHandler;
import tatami.core.agent.AgentEvent.AgentEventType;
import tatami.core.agent.CompositeAgent;
import tatami.core.agent.io.AgentActiveIO.InputListener;
import tatami.core.agent.messaging.MessagingComponent;
import tatami.core.agent.mobility.MobilityComponent;
import tatami.core.agent.parametric.AgentParameterName;
import tatami.core.agent.parametric.ParametricComponent;
import tatami.core.agent.visualization.AgentGui.DefaultComponent;
import tatami.core.util.platformUtils.PlatformUtils;

/**
 * This component manages all things related to the visualization and remote control of the agent. Namely:
 * <ul>
 * <li>the agent's log
 * <li>reporting the agent's log to the (central) VisualizationAngent (if any)
 * <li>reporting information related to the hierarchical structure of agents
 * <li>handling of orderly exit of agents
 * </ul>
 * 
 * @author Andrei Olaru
 */
public class VisualizableComponent extends AgentComponent implements ReportingEntity, InputListener
{
	/**
	 * Class UID.
	 */
	private static final long serialVersionUID = -5680276720645213673L;
	
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
		 * Posts an event to the agent's event queue, as requested by the simulation agent.
		 */
		CONTROL,
	}
	
	/**
	 * The name of the parameter in the component parameter set that corresponds to the name of the GUI.
	 */
	public static final String				GUI_PARAMETER_NAME					= "GUI";
	/**
	 * The name of the parameter in the component parameter set that corresponds to the type of the window.
	 */
	public static final String				WINDOW_TYPE_PARAMETER_NAME			= "window-type";
																				
	/**
	 * The name of the parameter in the event corresponding to GUI input, designating the name of the activated GUI
	 * component.
	 */
	public static final String				GUI_COMPONENT_EVENT_PARAMETER_NAME	= "component";
	/**
	 * The name of the parameter in the event corresponding to GUI input, containing arguments (information about the
	 * GUI event).
	 */
	public static final String				GUI_ARGUMENTS_EVENT_PARAMETER_NAME	= "arguments";
																				
	/**
	 * Debug constant to disable reporting (lowers quantity of messages). Normally should be set to <code>false</code>.
	 */
	private static final boolean			DEBUG_DISABLE_REPORTING				= true;
																				
	/**
	 * The logging {@link Unit}.
	 */
	protected transient UnitComponentExt	loggingUnit							= null;
	/**
	 * The {@link Config} for the GUI. Should remain the same object throughout the agent's lifecycle, although it may
	 * be changed, and the agent's GUI will be recreated.
	 */
	protected AgentGuiConfig				guiConfig							= new AgentGuiConfig();
	/**
	 * The GUI implementation. Depends on platform, but implements {@link AgentGui}.
	 */
	protected transient AgentGui			gui									= null;
																				
	/**
	 * The name of the entity to which the agent has to report.
	 */
	private String							visualizationParent					= null;
	/**
	 * The name of the entity which serves as the current container for the agent.
	 */
	private String							currentContainer					= null;
																				
	/**
	 * Create a new {@link VisualizableComponent} instance:
	 */
	public VisualizableComponent()
	{
		super(AgentComponentName.VISUALIZABLE_COMPONENT);
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
			if(getComponentData().isSet(WINDOW_TYPE_PARAMETER_NAME))
				guiConfig.setWindowType(getComponentData().get(WINDOW_TYPE_PARAMETER_NAME));
			if(getParent().isRunning())
			{ // creates visualization & log
				resetVisualization();
				registerMessageHandlers();
			}
		}
	}
	
	@Override
	protected void atAgentStart(AgentEvent event)
	{
		super.atAgentStart(event);
		
		resetVisualization();
		registerMessageHandlers();
		mActive = true;
	}
	
	@Override
	protected void atAgentStop(AgentEvent event)
	{
		super.atAgentStop(event);
		removeVisualization();
	}
	
	protected void atBeforeAgentMove(AgentEvent event)
	{
		// FIX
//		super.atBeforeAgentMove(event);
//		
//		MobilityComponent mvmt = (MobilityComponent) getAgentComponent(AgentComponentName.MOBILITY_COMPONENT);
//		if(mvmt != null)
//		{
//			String destination = mvmt.extractDestination(event);
//			if(!destination.equals(getCurrentContainer()))
//			{
//				getLog().info("moving to []", destination.toString());
//				setCurrentContainer(destination.toString());
//				removeVisualization();
//			}
//		}
//		
//		mActive = false;
	}
	
	protected void atAfterAgentMove(AgentEvent event)
	{
		// FIX
//		super.atAfterAgentMove(event);
//		
//		resetVisualization();
//		getLog().info("arrived after move");
	}
	
	/**
	 * Registers the message handlers with the messaging component of the agent.
	 */
	protected void registerMessageHandlers()
	{
		// registers message receivers: receive the visualization root; receive exit message.
		getLog().trace("Registering message handlers");
		if(!registerMessageReceiver(new AgentEventHandler() {
			@Override
			public void handleEvent(AgentEvent event)
			{
				String parent = ((MessagingComponent) getAgentComponent(AgentComponentName.MESSAGING_COMPONENT))
						.extractContent(event);
				setVisualizationParent(parent);
				getLog().info("visualization root received: []", parent);
			}
		}, Vocabulary.VISUALIZATION.toString(), Vocabulary.VISUALIZATION_MONITOR.toString()))
			getLog().warn("No messaging component present");
		registerMessageReceiver(new AgentEventHandler() {
			@Override
			public void handleEvent(AgentEvent event)
			{
				String content = event.get(MessagingComponent.CONTENT_PARAMETER);
				getLog().info("received control event [].", content);
				postAgentEvent(new AgentEvent(AgentEventType.valueOf(content)));
			}
		}, Vocabulary.VISUALIZATION.toString(), Vocabulary.CONTROL.toString());
	}
	
	@Override
	protected AgentComponent getAgentComponent(AgentComponentName name)
	{
		return super.getAgentComponent(name);
	}
	
	/**
	 * Creates the [platform-specific] visualization elements: the GUI and the log.
	 */
	public void resetVisualization()
	{
		// configure log / logging Unit
		loggingUnit = (UnitComponentExt) new UnitComponentExt().setUnitName(getAgentName()).setLogEnsureNew()
				.setLoggerType(PlatformUtils.platformLogType()).setLogLevel(Level.ALL);
		if(!DEBUG_DISABLE_REPORTING)
			loggingUnit.setLogReporter(this);
			
		// load GUI
		try
		{
			if(getComponentData().isSet(GUI_PARAMETER_NAME))
				guiConfig.setGuiClass(getComponentData().get(GUI_PARAMETER_NAME),
						((ParametricComponent) getAgentComponent(AgentComponentName.PARAMETRIC_COMPONENT))
								.parVals(AgentParameterName.AGENT_PACKAGE),
						getLog());
		} catch(NullPointerException e)
		{
			// it's ok, no parametric component
			getLog().warn("Failed to set GUI class");
		}
		
		try
		{
			gui = (AgentGui) PlatformUtils.loadClassInstance(this, guiConfig.getGuiClass(), guiConfig);
		} catch(Exception e)
		{
			getLog().error("Load GUI failed: []", PlatformUtils.printException(e));
		}
		
		if(gui != null)
		{
			loggingUnit.setLogDisplay(new Log2AgentGui(gui, DefaultComponent.AGENT_LOG.toString()));
			gui.setDefaultListener(this);
		}
		
		getLog().trace("visualization started on platform [] with GUI class []", guiConfig.guiClassName,
				PlatformUtils.getPlatform());
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
				sendMessage(content, getComponentEndpoint(Vocabulary.VISUALIZATION.toString()), visualizationParent,
						Vocabulary.VISUALIZATION.toString(), reportType.toString());
			}
			else
				throw new IllegalArgumentException("Parameter is not a correct report type:" + reportType);
		}
		return false;
	}
	
	/**
	 * Getter for the log. It never returns <code>null</code>, but in faulty cases it might returned a
	 * {@link DumbLogger} instance.
	 * 
	 * @return the log, as a {@link Logger} instance.
	 */
	public Logger getLog()
	{
		if(loggingUnit != null)
			return loggingUnit;
		return DumbLogger.get();
	}
	
	/**
	 * Getter for the GUI. This method should only be used for advanced applications where direct access to the GUI is
	 * necessary. Otherwise, the GUI should be used by means of the methods {@link #outputToGUI(String, Vector)} and
	 * {@link #inputFromGUI(String)}, together with receiving GUI events from the agent event mechanism.
	 * <p>
	 * IMPORTANT NOTE: registering input receivers via {@link AgentGui#connectInput} should only be used if it is not
	 * desired to receive GUI input events via the agent event mechanism. By default, {@link VisualizableComponent}
	 * registers a default handler for all GUI events and posts these events to the event queue. If an input is
	 * overridden (via this method) the events generated by the specified GUI component will not pass through the event
	 * queue anymore (and will therefore not be available to other components).
	 * 
	 * @return the GUI, as an {@link AgentGui} instance.
	 */
	public AgentGui getGUI()
	{
		return gui;
	}
	
	/**
	 * Relay for {@link AgentGui#doOutput(String, Vector)}.
	 * 
	 * @param guiComponentName
	 *            - the name of the component.
	 * @param arguments
	 *            - the information to transmit.
	 */
	public void outputToGUI(String guiComponentName, Vector<Object> arguments)
	{
		gui.doOutput(guiComponentName, arguments);
	}
	
	/**
	 * Relay for {@link AgentGui#getInput(String)}.
	 * 
	 * @param guiComponentName
	 *            - the name of the component.
	 * @return the received information.
	 */
	public Vector<Object> inputFromGUI(String guiComponentName)
	{
		return gui.getInput(guiComponentName);
	}
	
	/**
	 * method that receives active input events in the GUI (by default).
	 */
	@Override
	public void receiveInput(String componentName, Vector<Object> arguments)
	{
		AgentEvent event = new AgentEvent(AgentEventType.GUI_INPUT);
		try
		{
			event.add(GUI_COMPONENT_EVENT_PARAMETER_NAME, componentName);
			event.addObject(GUI_ARGUMENTS_EVENT_PARAMETER_NAME, arguments);
		} catch(IllegalStateException e)
		{
			// can't get here
			throw new IllegalStateException("should not be here " + e);
		}
		postAgentEvent(event);
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
	 * Setter for <code>currentContainer</code>.
	 * 
	 * @param containerName
	 *            - the name of the container the agent is in or going to move to.
	 */
	protected void setCurrentContainer(String containerName)
	{
		currentContainer = containerName;
	}
	
	/**
	 * Relays calls to the underlying {@link AgentComponent} instance in order to avoid synthetic access warnings for
	 * event handlers.
	 */
	@Override
	protected void postAgentEvent(AgentEvent event)
	{
		super.postAgentEvent(event);
	}
	
}
