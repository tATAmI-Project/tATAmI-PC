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

import tatami.core.util.jade.JadeUtil;
import jade.content.onto.BasicOntology;
import jade.content.onto.Ontology;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class VisualizationOntology extends Ontology
{
	
	private static final long	serialVersionUID	= 2463944576371113605L;
	
	public static enum Vocabulary {
		
		/**
		 * The name of the visualization ontology.
		 */
		VISUALIZATION_ONTOLOGY,

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
		 * Sent by Simulator to the Visualizer, to instructs all agents to exit, following user request to end simulation.
		 */
		PREPARE_EXIT,
		
		/**
		 * Instructs immediate exit, indicating the end of the simulation.
		 */
		DO_EXIT,
	}
	
	public VisualizationOntology()
	{
		super(Vocabulary.VISUALIZATION_ONTOLOGY.toString(), BasicOntology.getInstance());
	}
	
	public static MessageTemplate template()
	{
		return JadeUtil.templateAssemble(
				MessageTemplate.MatchPerformative(ACLMessage.INFORM),
				MessageTemplate.MatchOntology(Vocabulary.VISUALIZATION_ONTOLOGY.toString())
				);
	}
	
	public static MessageTemplate protocol(Vocabulary protocol)
	{
		return JadeUtil.templateAssemble(MessageTemplate.MatchProtocol(protocol.toString()), template());
	}
	
	public static ACLMessage setupMessage(Vocabulary protocol)
	{
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setOntology(Vocabulary.VISUALIZATION_ONTOLOGY.toString());
		msg.setProtocol(protocol.toString());
		return msg;
	}
}
