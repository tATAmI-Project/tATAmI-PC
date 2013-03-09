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

import jade.core.AID;
import jade.core.Location;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.ControllerException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import tatami.core.agent.BaseAgent;
import tatami.core.agent.visualization.VisualizationOntology.Vocabulary;
import tatami.core.interfaces.AgentGui;
import tatami.core.interfaces.Logger;
import tatami.core.interfaces.AgentGui.AgentGuiConfig;
import tatami.core.interfaces.AgentGui.DefaultComponent;
import tatami.core.interfaces.Logger.Level;
import tatami.core.util.Config;
import tatami.core.util.jade.JadeUtil;
import tatami.core.util.logging.Unit;
import tatami.core.util.logging.Log.ReportingEntity;
import tatami.core.util.logging.Unit.UnitConfigData;
import tatami.core.util.platformUtils.PlatformUtils;

public class VisualizableAgent extends BaseAgent implements ReportingEntity {
	private static final long serialVersionUID = 5153833693845730328L;

	// logging
	/**
	 * the logging {@link Unit}.
	 */
	protected transient Unit loggingUnit = null;
	/**
	 * provides easier access to the log. Should always be equal to
	 * <code>loggingUnit.getLog()</code>. Also provided by <code>getLog()</code>
	 * .
	 */
	protected transient Logger log = null;
	/**
	 * The {@link Config} fort the GUI. Should remain the same object throughout
	 * the agent's lifecycle, although it may be changed, and the agent's gui
	 * will be recreated.
	 */
	protected AgentGuiConfig guiConfig = new AgentGuiConfig();
	protected transient AgentGui gui = null;
	private AID visualizationParent = null;

	public VisualizableAgent() {
		super();
	}

	/**
	 * setup
	 * 
	 * windowType should be set before if a special value is needed
	 */
	@Override
	public void setup() {
		super.setup();

		// set gui
		guiConfig.setWindowName(getAgentName());
		if (hasPar(AgentParameterName.WINDOW_TYPE))
			guiConfig.setWindowType(parVal(AgentParameterName.WINDOW_TYPE));

		resetVisualization();

		// behaviors

		// receive the visualization root
		addBehaviour(new CyclicBehaviour() {
			private static final long serialVersionUID = 1L;

			@SuppressWarnings({ "synthetic-access", "incomplete-switch" })
			@Override
			public void action() {
				ACLMessage msg = myAgent
						.receive(JadeUtil.templateAssemble(
								VisualizationOntology.template(),
								MessageTemplate.or(
										MessageTemplate
												.MatchProtocol(Vocabulary.VISUALIZATION_MONITOR
														.toString()),
										MessageTemplate
												.MatchProtocol(Vocabulary.DO_EXIT
														.toString()))));
				if (msg != null) {
					switch (VisualizationOntology.Vocabulary.valueOf(msg
							.getProtocol())) {
					case VISUALIZATION_MONITOR:
						visualizationParent = new AID(msg.getContent(),
								AID.ISLOCALNAME);
						getLog().info(
								"visualization root received: ["
										+ visualizationParent + "]");
						break;
					case DO_EXIT:
						// TODO: should check if the message comes from the
						// Simulator to Visualizer, or form the Visualizer to
						// any other agent
						getLog().info("exiting...");
						doDelete();
						break;
					}
				} else {
					block();
				}
			}
		});
	}

	/**
	 * Creates the [platform-specific] visualization elements: the GUI and the
	 * log.
	 */
	protected void resetVisualization() {
		ClassLoader cl = null;

		/*if (hasPar(AgentParameterName.GUI)) {
			guiConfig.setGuiClass(parVal(AgentParameterName.GUI),
					parVals(AgentParameterName.AGENT_PACKAGE)); // overrides any
		}*/

		guiConfig.setGuiClass(parVal(AgentParameterName.GUI),
				parVals(AgentParameterName.AGENT_PACKAGE));
		
		try {
			/*if (PlatformUtils.Platform.ANDROID.equals(PlatformUtils
					.getPlatform()))

				guiConfig.setGuiClass(parVal(AgentParameterName.GUI),
						parVals(AgentParameterName.AGENT_PACKAGE));*/

			cl = new ClassLoader(getClass().getClassLoader()) {
				// nothing to extend
			};
			Constructor<?> cons = cl.loadClass(guiConfig.guiClassName)
					.getConstructor(AgentGuiConfig.class);
			gui = (AgentGui) cons.newInstance(guiConfig);
		} catch (Exception e) {
			e.printStackTrace(); // there is no log yet
		}

		// configure log / logging Unit

		UnitConfigData unitConfig = new UnitConfigData()
				.setName(getAgentName()).ensureNew().setReporter(this);
		unitConfig.setType(PlatformUtils.platformLogType());
		unitConfig.setLevel(Level.ALL);
		if (gui != null)
			// unitConfig.setTextArea((TextArea)((PCDefaultAgentGui)gui).getComponent(DefaultComponent.AGENT_LOG.toString()));
			unitConfig.setDisplay(new Log2AgentGui(gui,
					DefaultComponent.AGENT_LOG.toString()));
		loggingUnit = new Unit(unitConfig);
		log = getLog();

		log.trace("visualization on platform " + PlatformUtils.getPlatform());
	}

	protected void removeVisualization() {
		getLog().trace("closing visualization");
		loggingUnit.exit();
		if (gui != null)
			gui.close();
	}

	@Override
	public void doMove(Location destination) {
		try {
			if (!destination.getName().equals(
					this.getContainerController().getContainerName())) {
				getLog().info("moving to [" + destination.toString() + "]");
				removeVisualization();
			}
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		super.doMove(destination);
	}

	@Override
	protected void afterMove() {
		super.afterMove();
		resetVisualization();
		log.info(getLocalName() + ": arrived after move");

	}

	@Override
	protected void takeDown() {
		super.takeDown();
		removeVisualization();
	}

	@Override
	public boolean report(String content) {
		if (visualizationParent != null) {
			ACLMessage msg = VisualizationOntology
					.setupMessage(Vocabulary.LOGGING_UPDATE);
			msg.setContent(content);
			msg.addReceiver(visualizationParent);
			send(msg);
			return true;
			// if(log != null)
			// log.trace("logging info reported to visualization root");
		}
		return false;
	}

	public void reportAddParent(String parent) {
		ACLMessage msg = VisualizationOntology
				.setupMessage(Vocabulary.ADD_PARENT);
		msg.setContent(parent);
		msg.addReceiver(visualizationParent);
		send(msg);
	}

	public void reportRemoveParent(String parent) {
		ACLMessage msg = VisualizationOntology
				.setupMessage(Vocabulary.REMOVE_PARENT);
		msg.setContent(parent);
		msg.addReceiver(visualizationParent);
		send(msg);
	}

	public Logger getLog() {
		return loggingUnit.getLog();
	}

	public AgentGui getGUI() {
		return gui;
	}

	public AgentGuiConfig getGuiConfig() {
		return guiConfig;
	}
}
