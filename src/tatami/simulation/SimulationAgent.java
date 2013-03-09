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
package tatami.simulation;

import jade.content.ContentElement;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.OntologyException;
import jade.content.onto.UngroundedException;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Result;
import jade.core.AID;
import jade.core.Location;
import jade.domain.JADEAgentManagement.QueryPlatformLocationsAction;
import jade.domain.mobility.MobilityOntology;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;

import tatami.core.agent.claim.ClaimMessage;
import tatami.core.agent.claim.parser.ClaimConstruct;
import tatami.core.agent.claim.parser.ClaimStructure;
import tatami.core.agent.claim.parser.ClaimValue;
import tatami.core.agent.visualization.VisualizableAgent;
import tatami.core.agent.visualization.VisualizationOntology;
import tatami.core.agent.visualization.VisualizationOntology.Vocabulary;
import tatami.pc.agent.visualization.PCDefaultAgentGui;
import tatami.pc.agent.visualization.PCSimulationGui.SimulationComponent;
import tatami.pc.util.XML.XMLTree.XMLNode;
import tatami.pc.util.jade.PCJadeInterface;

/**
 * Agent that handles the simulation.
 * 
 * <p>
 * It receives one argument, which is a {@link List} of {@link XMLNode}
 * instances that are read from the scenario timeline and represent events.
 * 
 * <p>
 * The interface provides buttons for starting, pausing (unimplemented) and
 * exiting the simulation.
 * 
 * @author Andrei Olaru
 * @author Nguyen Thi Thuy Nga
 * 
 */
public class SimulationAgent extends VisualizableAgent {
	private static final long serialVersionUID = 5153833693845730328L;

	List<XMLNode> events = new LinkedList<XMLNode>();
	long lastActivation = -1;

	long time = 0; // in tenths of second
	long lastEvent = 0; // the time of the last event that
						// took place (task
						// executed
	// or
	// executing)
	Timer displayTime = null;
	Timer theTime = null;
	boolean isPaused = false;

	PCJadeInterface jadeInterface = null;
	Map<String, Location> containerLocations = null;
	Collection<AgentCreationData> agentCreation = null;
	boolean agentsCreated = false;

	String visualizer = null;

	/**
	 * setup
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void setup() {

		guiConfig.setWindowType("systemSmall");
		// FIXME: should be in constant
		guiConfig.setClassNameOverride("tatami.pc.agent.visualization.PCSimulationGui");
		super.setup();

		log.info("simulation agent [" + getAgentName() + "] on");

		jadeInterface = (PCJadeInterface) parObj(AgentParameterName.JADE_INTERFACE);
		agentCreation = (Collection<AgentCreationData>) parObj(AgentParameterName.AGENTS);
		if (hasPar(AgentParameterName.TIMELINE))
			for (Iterator<XMLNode> it = ((XMLNode) parObj(AgentParameterName.TIMELINE))
					.getNodeIterator("event"); it.hasNext();)
				events.add(it.next());
		visualizer = parVal(AgentParameterName.VISUALIZTION_AGENT);

		// GUI
		setupGui();
	}

	protected void fillContainerLocations() {
		getContentManager().registerLanguage(new SLCodec());
		getContentManager().registerOntology(MobilityOntology.getInstance());

		Action action = new Action(getAMS(), new QueryPlatformLocationsAction());
		ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
		request.setLanguage(new SLCodec().getName());
		request.setOntology(MobilityOntology.getInstance().getName());
		try {
			getContentManager().fillContent(request, action);
			request.addReceiver(action.getActor());
			send(request);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		MessageTemplate mt = MessageTemplate.and(
				MessageTemplate.MatchSender(getAMS()),
				MessageTemplate.MatchPerformative(ACLMessage.INFORM));
		ACLMessage resp = blockingReceive(mt);
		ContentElement ce = null;
		try {
			ce = getContentManager().extractContent(resp);
		} catch (UngroundedException e) {
			e.printStackTrace();
		} catch (CodecException e) {
			e.printStackTrace();
		} catch (OntologyException e) {
			e.printStackTrace();
		}
		Result result = (Result) ce;

		containerLocations = new HashMap<String, Location>();
		if (result != null) {
			@SuppressWarnings("unchecked")
			Iterator<Location> it = result.getItems().iterator();
			while (it.hasNext()) {
				Location loc = it.next();
				containerLocations.put(loc.getName(), loc);
			}
		}
		log.trace("container locations obtained: " + containerLocations);
	}

	protected void createAgents(PCJadeInterface jadeInt,
			Collection<AgentCreationData> agents, String viz) {
		jadeInterface = jadeInt;
		visualizer = viz;
		for (AgentCreationData data : agents) {
			if (data.isRemote) {
				if (containerLocations == null) {
					log.trace("obtaining location list...");
					fillContainerLocations();
				}
				if (!containerLocations.containsKey(data.destinationContainer))
					log.error("container not found ["
							+ data.destinationContainer + "]");
				else {
					log.info("starting remote agent [" + data.agentName
							+ "] on [" + data.destinationContainer + "]");
					data.parameters.addObject(
							AgentParameterName.INITIAL_LOCATION.toString(),
							containerLocations.get(data.destinationContainer));
				}
			} else
				log.info("starting agent [" + data.agentName + "]");
			jadeInterface.addAgentToContainer(
					(data.isRemote ? jadeInterface.getMainContainerName()
							: data.destinationContainer), data.agentName,
					data.classpath, new Object[] { data.parameters });
		}

		if (visualizer != null)
			for (AgentCreationData data : agents) {
				ACLMessage msg = VisualizationOntology
						.setupMessage(Vocabulary.VISUALIZATION_MONITOR);
				msg.setContent(visualizer);
				msg.addReceiver(new AID(data.agentName, AID.ISLOCALNAME));
				send(msg);
				log.info("visualization root inform sent to [" + getAgentName()
						+ "]");
			}
		else
			log.warn("visualizer unknown");

	}

	protected void setupGui() {
		((JButton) ((PCDefaultAgentGui) gui)
				.getComponent(SimulationComponent.CREATE.toString()))
				.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (jadeInterface != null && agentCreation != null) {
							agentsCreated = true;
							createAgents(jadeInterface, agentCreation,
									visualizer);
						}
					}
				});

		((JButton) ((PCDefaultAgentGui) gui)
				.getComponent(SimulationComponent.CLEAR.toString()))
				.addActionListener(new ActionListener() {
					@SuppressWarnings("synthetic-access")
					@Override
					public void actionPerformed(ActionEvent e) {
						log.info(".... exiting");
						ACLMessage exitMsg = VisualizationOntology
								.setupMessage(Vocabulary.PREPARE_EXIT);
						exitMsg.addReceiver(new AID(visualizer.toString(),
								AID.ISLOCALNAME));
						send(exitMsg);
					}
				});

		((JButton) ((PCDefaultAgentGui) gui)
				.getComponent(SimulationComponent.EXIT.toString()))
				.addActionListener(new ActionListener() {
					@SuppressWarnings("synthetic-access")
					@Override
					public void actionPerformed(ActionEvent e) {
						log.info("...and out");
						ACLMessage exitMsg = VisualizationOntology
								.setupMessage(Vocabulary.DO_EXIT);
						exitMsg.addReceiver(new AID(visualizer.toString(),
								AID.ISLOCALNAME));
						send(exitMsg);
						doDelete();
						System.exit(0);
					}
				});

		if (events.isEmpty()) {
			((JButton) ((PCDefaultAgentGui) gui)
					.getComponent(SimulationComponent.START.toString()))
					.setEnabled(false);
			((JButton) ((PCDefaultAgentGui) gui)
					.getComponent(SimulationComponent.PAUSE.toString()))
					.setEnabled(false);
			((JLabel) ((PCDefaultAgentGui) gui)
					.getComponent(SimulationComponent.TIME.toString()))
					.setText("no events");
		} else {
			((JButton) ((PCDefaultAgentGui) gui)
					.getComponent(SimulationComponent.START.toString()))
					.addActionListener(new ActionListener() {
						@SuppressWarnings("synthetic-access")
						@Override
						public void actionPerformed(ActionEvent e) {
							if (!agentsCreated && jadeInterface != null
									&& agentCreation != null) {
								agentsCreated = true;
								createAgents(jadeInterface, agentCreation,
										visualizer);
							}
							if (!events.isEmpty()) {
								int firstDelay = Integer.parseInt(events.get(0)
										.getAttributeValue("time"));
								log.info("starting simulation. next event at "
										+ firstDelay);

								startTimers(firstDelay);
							}
						}
					});

			((JButton) ((PCDefaultAgentGui) gui)
					.getComponent(SimulationComponent.PAUSE.toString()))
					.addActionListener(new ActionListener() {
						@SuppressWarnings("synthetic-access")
						@Override
						public void actionPerformed(ActionEvent e) {
							if (events.isEmpty()) {
								((JLabel) ((PCDefaultAgentGui) gui)
										.getComponent(SimulationComponent.TIME
												.toString()))
										.setText("no more events");
								if (!isPaused) {
									displayTime.cancel();
									theTime.cancel();
								}
							} else if (isPaused) {
								long delay = Integer.parseInt(events.get(0)
										.getAttributeValue("time"))
										- time
										* 100;
								log.info("simulation restarting, next event in "
										+ delay);
								startTimers(delay);
							} else {
								displayTime.cancel();
								theTime.cancel();
								long delay = Integer.parseInt(events.get(0)
										.getAttributeValue("time"))
										- time
										* 100;
								log.info("simulation stopped at " + time * 100
										+ ", next event was in " + delay);
							}
							isPaused = !isPaused;
						}
					});
		}
	}

	protected void startTimers(long delay) {
		TimerTask displayTimeTask = new TimerTask() {
			@SuppressWarnings("synthetic-access")
			@Override
			public void run() {
				time++;
				((JLabel) ((PCDefaultAgentGui) gui)
						.getComponent(SimulationComponent.TIME.toString()))
						.setText("___" + (int) (time / 600) + ":"
								+ (int) ((time % 600) / 10) + "." + (time % 10)
								+ "___");
			}
		};
		final TimerTask theTask = new SimulationTimerTask();
		displayTime = new Timer();
		displayTime.schedule(displayTimeTask, 0, 100);
		lastEvent = delay;
		theTime = new Timer();
		theTime.schedule(theTask, delay);
	}

	class SimulationTimerTask extends TimerTask {
		@SuppressWarnings("synthetic-access")
		@Override
		public void run() {
			int delay = -1;
			if (events.size() > 1) {
				delay = Integer.parseInt(events.get(1)
						.getAttributeValue("time"));

				delay -= lastEvent;

				lastEvent += delay;
				theTime.schedule(new SimulationTimerTask(), delay);
			}
			if (lastEvent > 0)
				log.info("next event at " + lastEvent);
			else
				log.info("no more events");
			log.trace("processing new event");
			XMLNode event = events.get(0);
			for (XMLNode task : event.getNodes()) {
				log.info("task: " + task.getName());
				// CLAIM Message event
				// contains a structure
				if (task.getName().equals("CLAIMMessage")) {

					ClaimMessage eventMess = new ClaimMessage();
					for (Iterator<XMLNode> it = task.getNodeIterator("to"); it
							.hasNext();)
						eventMess.addReceiver(new AID((String) (it.next()
								.getValue()), AID.ISLOCALNAME));
					for (Iterator<XMLNode> it = task
							.getNodeIterator("protocol"); it.hasNext();)
						eventMess.setProtocol((String) (it.next().getValue()));
					for (Iterator<XMLNode> it = task.getNodeIterator("content"); it
							.hasNext();)
						try {
							eventMess.setContentObject(ClaimStructure
									.parseString(
											(String) (it.next().getValue()),
											getLog()));
						} catch (IOException e) {
							e.printStackTrace();
						}
					if (eventMess.getProtocol() == null) {
						Vector<ClaimConstruct> fields = null;
						try {
							fields = ((ClaimStructure) eventMess
									.getContentObject()).getFields();
						} catch (UnreadableException e) {
							e.printStackTrace();
						}
						if (fields != null && fields.size() >= 2) {
							String protocol = ((ClaimValue) fields.get(1))
									.toString();
							log.trace("protocol detected: [" + protocol + "]");
							eventMess.setProtocol(protocol);
						}
					}
					if (eventMess.getProtocol() != null) {
						log.trace("sending message [" + eventMess.toString()
								+ "]");
						send(eventMess);
					} else
						log.error("unable to send message; unable to determine protocol");
				}
			}
			events.remove(0);
		}
	}

	@Override
	protected void takeDown() {
		// do not call super.takeDown() because that will generate a wait and an
		// Interrupted exception; just exit log
		loggingUnit.exit();
	}
}
