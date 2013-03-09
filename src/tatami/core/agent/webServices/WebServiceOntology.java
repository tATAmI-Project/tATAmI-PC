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
package tatami.core.agent.webServices;

import tatami.core.util.jade.JadeUtil;
import jade.content.AgentAction;
import jade.content.onto.BasicOntology;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.schema.AgentActionSchema;
import jade.content.schema.PrimitiveSchema;
import jade.lang.acl.MessageTemplate;

public class WebServiceOntology extends Ontology
{
	public static class ReceiveOperation implements AgentAction
	{
		private static final long	serialVersionUID	= 35413327850012501L;
		
		private String				operationArgument	= null;
		
		public String getOperationArgument()
		{
			return operationArgument;
		}
		
		public void setOperationArgument(String value)
		{
			operationArgument = value;
		}
	}
	
	private static final long	serialVersionUID	= 8161558355285802443L;
	public static final String	NAME				= "service-ontology";
	public static final String	LANGUAGE			= "service-language";
	
	public static final String	RECEIVE_OPERATION	= "receive-operation";
	public static final String	OPERATION_ARGUMENT	= "operation-argument";
	
	private static Ontology		theInstance			= new WebServiceOntology();
	
	public static Ontology getInstance()
	{
		return theInstance;
	}
	
	private WebServiceOntology()
	{
		super(NAME, BasicOntology.getInstance());
		try
		{
			add(new AgentActionSchema(RECEIVE_OPERATION), ReceiveOperation.class);
			
			AgentActionSchema as = (AgentActionSchema)getSchema(RECEIVE_OPERATION);
			as.add(OPERATION_ARGUMENT, (PrimitiveSchema)getSchema(BasicOntology.STRING));
			as.setResult((PrimitiveSchema)getSchema(BasicOntology.STRING));
			// ConceptSchema cs = (ConceptSchema) getSchema("agentInfo");
			// cs.add("agentAid", (TermSchema) getSchema(BasicOntology.AID));
			// AgentActionSchema as = (AgentActionSchema)getSchema("getAgentInfo");
			// as.setResult((ConceptSchema)getSchema("agentInfo"));
		} catch(OntologyException oe)
		{
			oe.printStackTrace();
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static MessageTemplate template()
	{
		return JadeUtil.templateAssemble(MessageTemplate.MatchLanguage(LANGUAGE), MessageTemplate.MatchOntology(NAME));
	}
	
}
