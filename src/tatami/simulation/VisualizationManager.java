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

import java.awt.TextArea;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import net.xqhs.util.logging.Log;
import net.xqhs.util.logging.Logger.Level;
import tatami.core.agent.visualization.AgentGui;
import tatami.core.agent.visualization.VisualizableComponent.Vocabulary;
import net.xqhs.graphs.graph.*;
import net.xqhs.graphs.representation.text.*;
import tatami.pc.agent.visualization.PCDefaultAgentGui;
import tatami.pc.agent.visualization.PCVisualizationGui.VisualizationComponent;

/**
 * Singleton class managing the visualization and control of agents residing on a machine or on a set of machines
 * (possibly all).
 * <p>
 * It receives updates from all agents regarding logging messages and parent and location changes.
 * <p>
 * Although not an agent of any platform, the {@link SimulationManager} can be viewed as an agent; for convenience, it
 * implements {@link AgentManager} and it features a GUI based on {@link AgentGui}.
 * <p>
 * This implementation presumes that {@link java.util.Timer} and related classes {@link TimerTask} are available on the
 * execution platform.
 * 
 * @author Andrei Olaru
 */
public class VisualizationManager
{
	private static final long	serialVersionUID	= 5153833693845730328L;
	
	// structures for displaying
	Timer						updateTimer			= null;
	long						updateDelay			= 1000;
	int							lastUpdateSize		= 0;
	
	// structure for storing monitor data
	Set<String>					centralLog			= new HashSet<String>();
	Graph						agentGraph			= null;
	TextGraphRepresentation		grapher				= null;
	
	/**
	 * setup
	 */
	@Override
	public void setup()
	{
		System.out.println("Aici in     VisualizationAgent setup");
		guiConfig.setWindowType("system");
		// FIXME: should be in constant
		guiConfig.setClassNameOverride("tatami.pc.agent.visualization.PCVisualizationGui");
		super.setup();
		
		log.info("visualization root on");
		
		log.trace("creating agent graph");
		agentGraph = new Graph(new UnitConfigData().setName("AgentVisualizationGraph").setLink(getLocalName())
				.setLevel(Level.INFO));
		grapher = new TextGraphRepresentation(new TextGraphRepresentation.GraphConfig(agentGraph).setLayout("\n", "  ",
				2));
		
		// GUI
		updateTimer = new Timer();
		updateTimer.schedule(new TimerTask() {
			@Override
			public void run()
			{
				if(lastUpdateSize == centralLog.size())
					return;
				lastUpdateSize = centralLog.size();
				List<String> entries = new LinkedList<String>(centralLog);
				Collections.sort(entries);
				String output = "";
				for(String entry : entries)
					output += entry + "\n";
				
				TextArea centralLogDisplay = (TextArea) ((PCDefaultAgentGui) gui)
						.getComponent(VisualizationComponent.CENTRAL_LOG.toString());
				centralLogDisplay.setText(output);
				centralLogDisplay.append(".");
				centralLogDisplay.repaint();
				
				grapher.update(); // FIXME
				TextArea graphDisplay = (TextArea) ((PCDefaultAgentGui) gui)
						.getComponent(VisualizationComponent.AGENT_GRAPH.toString());
				graphDisplay.setText(grapher.toString());
				graphDisplay.append("\n\n");
				graphDisplay.append(agentGraph.toString());
			}
		}, 0, updateDelay);
		
		addBehaviour(new CyclicBehaviour() {
			private static final long	serialVersionUID	= 1L;
			
			@Override
			public void action()
			{
				// log.trace("check");
				ACLMessage msg = myAgent.receive(VisualizationOntology.template());
				if(msg != null)
				{
					String sender = msg.getSender().getLocalName();
					String content = msg.getContent();
					Collection<Node> nodes_a = agentGraph.getNodesNamed(sender);
					Collection<Node> nodes_b = null;
					Node senderNode = null;
					Node parentNode = null;
					if(nodes_a.isEmpty())
					{
						senderNode = new Node(sender);
						agentGraph.addNode(senderNode);
					}
					else
						senderNode = nodes_a.iterator().next(); // assumes the names are unique
					switch(Vocabulary.valueOf(msg.getProtocol()))
					{
					case LOGGING_UPDATE:
						log.trace("new logging data received from: [" + sender + "]");
						String[] entries = content.split(Log.AWESOME_SEPARATOR.toString());
						for(String entry : entries)
							centralLog.add(entry);
						break;
					
					case ADD_PARENT:
						log.trace("parent added by: [" + sender + "]: [" + content + "]");
						nodes_b = agentGraph.getNodesNamed(content);
						if(nodes_b.isEmpty())
						{
							log.warn("parent node [" + content + "] not found; creating new node");
							parentNode = new Node(content);
							agentGraph.addNode(parentNode);
						}
						else
							parentNode = nodes_b.iterator().next(); // assumes the names are unique
						agentGraph.addEdge(new Edge(parentNode, senderNode, null));
						break;
					
					case REMOVE_PARENT:
						log.trace("parent removed by: [" + sender + "]: [" + content + "]");
						nodes_b = agentGraph.getNodesNamed(content);
						if(nodes_b.isEmpty())
							log.warn("parent node [" + content + "] not found");
						else
						{
							parentNode = nodes_b.iterator().next(); // assumes the names are unique
							Edge toRemove = parentNode.getEdgeTo(senderNode);
							agentGraph.removeEdge(toRemove);
						}
						break;
					
					case MOVE:
						break;
					
					case PREPARE_EXIT:
						ACLMessage exitMsg = VisualizationOntology.setupMessage(Vocabulary.DO_EXIT);
						for(Node agentNode : agentGraph.getNodes())
						{ // FIXME: maybe should use an actual list of agent names
							if(sender.equals(agentNode.toString()))
								log.trace("agent node: " + agentNode + " .... not");
							else
							{
								log.trace("agent node: " + agentNode);
								exitMsg.addReceiver(new AID(agentNode.toString(), AID.ISLOCALNAME));
							}
						}
						send(exitMsg);
						break;
					
					default:
						log.error("incorrect message received");
					}
				}
				else
				{
					block();
				}
			}
		});
	}
	
	@Override
	protected void takeDown()
	{
		// do not call super.takeDown() because that will generate a wait and an Interrupted exception; just exit log
		if(updateTimer != null)
			updateTimer.cancel();
		getLog().info("visualization out");
		loggingUnit.exit();
	}
	
}
