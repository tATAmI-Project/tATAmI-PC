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
package tatami.core.agent.kb;

import jade.util.leap.Serializable;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents knowledge structure
 * it has knowledge type, a simple knowledge which is an arrayList of String. 
 * more complex knowledge should be represented by a graph 
 * @author Nguyen Thi Thuy Nga
 * @version 3/6/11
 *
 */
public class Knowledge implements Serializable{
	private static final long	serialVersionUID	= -3963198006784906653L;
	private String knowledgeType = "";
	private List<String> simpleKnowledge = new ArrayList<String>();
	
	/**
	 * CONSTRUCTOR
	 */
	public Knowledge(){
	}

	public Knowledge(String type, String ... klFields){
		knowledgeType = type;
		for(String field : klFields)
			simpleKnowledge.add(field);
	}
	public Knowledge(String type, ArrayList<String> simpleKn){
		knowledgeType = type;
		simpleKnowledge = simpleKn;
	}
	
	public Knowledge(Knowledge kl){
		this.knowledgeType = kl.knowledgeType;
		this.simpleKnowledge.addAll(kl.simpleKnowledge);
	}
	
	public boolean equals(Knowledge kl){
		if(kl.knowledgeType.equals(this.knowledgeType)){
			if(kl.simpleKnowledge.equals(this.simpleKnowledge))
				return true;
		}
		return false;
	}
	
	/**
	 * print knowledge
	 */
	public String printlnKnowledge(){
		String knowledge = knowledgeType.concat(" : ");
		for(int i = 0; i < simpleKnowledge.size(); i++)
			knowledge = knowledge.concat(simpleKnowledge.get(i) + " ");
		return knowledge;
	}
	
	@Override
	public String toString()
	{
		return printlnKnowledge();
	}
	
	//GET & SET
	/**
	 * get knowledge type
	 */
	public String getKnowledgeType(){
		return this.knowledgeType;
	}
	/**
	 * get knowledge content
	 */
	public List<String> getSimpleKnowledge(){
		return this.simpleKnowledge;
	}

	public void setKnowledgeType(String _knowledgeType) {
		this.knowledgeType = _knowledgeType;
	}

	public void setSimpleKnowledge(List<String> _simpleKnowledge) {
		this.simpleKnowledge = _simpleKnowledge;
	}
	
	public void setSimpleKnowledgeAtField(int index, String field){
		this.simpleKnowledge.set(index, field);
	}
}
