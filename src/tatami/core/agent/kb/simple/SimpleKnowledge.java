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
package tatami.core.agent.kb.simple;

import jade.util.leap.Serializable;

import java.util.ArrayList;
import java.util.List;

import tatami.core.interfaces.KnowledgeBase.KnowledgeDescription;


/**
 * Simplistic implementation of a {@link KnowledgeDescription}.
 * <p>
 * It represents a knowledge structure: it has knowledge type, and an {@link ArrayList} of String.
 * 
 * @author Nguyen Thi Thuy Nga
 * @author Andrei Olaru
 */
public class SimpleKnowledge implements KnowledgeDescription, Serializable
{
	@SuppressWarnings("javadoc")
	private static final long	serialVersionUID	= -3963198006784906653L;
	/**
	 * The type of the knowledge, or relation between the other elements.
	 */
	private String				knowledgeType		= "";
	/**
	 * The parts, or the elements between which the knowledge is defined.
	 */
	private List<String>		knowledgeParts		= new ArrayList<String>();
	
	/**
	 * CONSTRUCTOR
	 */
	public SimpleKnowledge()
	{
	}
	
	public SimpleKnowledge(String type, String... klFields)
	{
		knowledgeType = type;
		for(String field : klFields)
			knowledgeParts.add(field);
	}
	
	public SimpleKnowledge(String type, ArrayList<String> simpleKn)
	{
		knowledgeType = type;
		knowledgeParts = simpleKn;
	}
	
	public SimpleKnowledge(SimpleKnowledge kl)
	{
		this.knowledgeType = kl.knowledgeType;
		this.knowledgeParts.addAll(kl.knowledgeParts);
	}
	
	public boolean equals(SimpleKnowledge kl)
	{
		if(kl.knowledgeType.equals(this.knowledgeType))
		{
			if(kl.knowledgeParts.equals(this.knowledgeParts))
				return true;
		}
		return false;
	}
	
	/**
	 * Print the knowledge.
	 * 
	 * @return a textual representation of the piece of knowledge.
	 */
	public String printlnKnowledge()
	{
		String knowledge = knowledgeType.concat(" : ");
		for(int i = 0; i < knowledgeParts.size(); i++)
			knowledge = knowledge.concat(knowledgeParts.get(i) + " ");
		return knowledge;
	}
	
	@Override
	public String toString()
	{
		return printlnKnowledge();
	}
	
	// GET & SET
	/**
	 * get knowledge type
	 */
	public String getKnowledgeType()
	{
		return this.knowledgeType;
	}
	
	/**
	 * get knowledge content
	 */
	public List<String> getSimpleKnowledge()
	{
		return this.knowledgeParts;
	}
	
	public void setKnowledgeType(String _knowledgeType)
	{
		this.knowledgeType = _knowledgeType;
	}
	
	public void setSimpleKnowledge(List<String> _simpleKnowledge)
	{
		this.knowledgeParts = _simpleKnowledge;
	}
	
	public void setSimpleKnowledgeAtField(int index, String field)
	{
		this.knowledgeParts.set(index, field);
	}
	
	@Override
	public String getTextRepresentation()
	{
		return this.toString();
	}
}
