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
package tatami.core.agent.claim;

import tatami.core.util.jade.JadeUtil;
import jade.content.onto.Ontology;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.lang.acl.MessageTemplate;

public class ClaimOntology extends Ontology {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final static String NAME = "claim-ontology";
	public final static String LANGUAGE = "claim-language";
	
	public final static String ASKLOCATION = "ask-for-location";
	public final static String LOC_RTN = "location-return";
	
	private static Ontology theInstance = new ClaimOntology();
	
	public static Ontology getInstance(){
		return theInstance;
	}
	
	private ClaimOntology(){
		super(NAME, JADEManagementOntology.getInstance());
	}
	
	public static MessageTemplate template()
	{
		return JadeUtil.templateAssemble(
				MessageTemplate.MatchLanguage(LANGUAGE),
				MessageTemplate.MatchOntology(NAME)
				);
	}
	
}
