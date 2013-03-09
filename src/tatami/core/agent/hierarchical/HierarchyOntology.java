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
package tatami.core.agent.hierarchical;

import tatami.core.util.jade.JadeUtil;
import jade.content.onto.BasicOntology;
import jade.content.onto.Ontology;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class HierarchyOntology extends Ontology
{
	private static final long	serialVersionUID	= -6661495858190009827L;
	
	public static enum Vocabulary {
		
		/**
		 * The name of the Hierarchical ontology
		 */
		HIERARCHICAL_ONTOLOGY,

		// protocols:
		/**
		 * The sending is necessarily a child of the receiving agent
		 */
		HELLODAD,

		/**
		 * The sending agent demands if he can be a child of the receiving agent
		 */
		DEMANDPARENT,

		/**
		 * The sending agent agrees to be the parent of the receiving agent
		 */
		AGREEPARENT,

		/**
		 * The sending agent wishes to remove the relationship between itself and the receiving agent
		 */
		REMOVEME,

		/**
		 * The sending agent demands to the receiving agent to follow him to the container specified in the content
		 */
		FOLLOWME,
	}
	
	private static Ontology	theInstance	= new HierarchyOntology();
	
	public static Ontology getInstance()
	{
		return theInstance;
	}
	
	private HierarchyOntology()
	{
		super(Vocabulary.HIERARCHICAL_ONTOLOGY.toString(), BasicOntology.getInstance());
	}
	
	public static MessageTemplate template()
	{
		return MessageTemplate.MatchOntology(Vocabulary.HIERARCHICAL_ONTOLOGY.toString());
	}
	
	private static int getPerformative(Vocabulary protocol)
	{
		switch(protocol)
		{
		case DEMANDPARENT:
		case REMOVEME:
		case FOLLOWME:
			return ACLMessage.REQUEST;
		case AGREEPARENT:
			return ACLMessage.AGREE;
		case HELLODAD:
		default:
			return ACLMessage.INFORM;
		}
	}
	
	public static MessageTemplate template(Vocabulary protocol)
	{
		int performative = getPerformative(protocol);
		
		return JadeUtil.templateAssemble(
				MessageTemplate.MatchPerformative(performative),
				MessageTemplate.MatchOntology(Vocabulary.HIERARCHICAL_ONTOLOGY.toString()),
				MessageTemplate.MatchProtocol(protocol.toString())
				);
	}
	
	public static ACLMessage setupMessage(Vocabulary protocol)
	{
		ACLMessage msg = new ACLMessage(getPerformative(protocol));
		msg.setOntology(Vocabulary.HIERARCHICAL_ONTOLOGY.toString());
		msg.setProtocol(protocol.toString());
		return msg;
	}
}
