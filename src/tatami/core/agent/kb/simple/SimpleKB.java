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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import tatami.core.interfaces.KnowledgeBase;
import tatami.core.interfaces.KnowledgeBase.KnowledgeDescription;


/**
 * Class that implements a list of {@link KnowledgeDescription}, which should all be instances of
 * {@link SimpleKnowledge}.
 * 
 * @author Andrei Olaru
 */
public class SimpleKB extends ArrayList<KnowledgeDescription> implements KnowledgeBase
{
	@SuppressWarnings("javadoc")
	private static final long	serialVersionUID	= -3268657561088056482L;
	
	@Override
	public KnowledgeDescription getFirst(KnowledgeDescription pattern)
	{
		SimpleKnowledge patternS = (SimpleKnowledge) pattern;
		for(KnowledgeDescription kd : this)
		{
			if(matchPattern(patternS, (SimpleKnowledge) kd))
				return kd;
		}
		return null;
	}
	
	@Override
	public Collection<KnowledgeDescription> getAll(KnowledgeDescription pattern)
	{
		Collection<KnowledgeDescription> ret = new ArrayList<KnowledgeDescription>();
		SimpleKnowledge patternS = (SimpleKnowledge) pattern;
		for(KnowledgeDescription kd : this)
		{
			if(matchPattern(patternS, (SimpleKnowledge) kd))
				ret.add(kd);
		}
		return ret;
	}
	
	/**
	 * Matches two {@link SimpleKnowledge} descriptions, of which the pattern is allowed to contain null values (that
	 * match anything)
	 * 
	 * @param patternS
	 *            the pattern;
	 * @param targetS
	 *            the target to match against;
	 * @return true if the target matches the pattern.
	 */
	protected static boolean matchPattern(SimpleKnowledge patternS, SimpleKnowledge targetS)
	{
		// if the pieces of knowledge don't share the same type, fail
		if(!targetS.getKnowledgeType().equals(patternS.getKnowledgeType()))
			return false;
		// if the numer of elements is not the same, fail
		if(targetS.getSimpleKnowledge().size() != patternS.getSimpleKnowledge().size())
			return false;
		Iterator<String> patternIt = patternS.getSimpleKnowledge().iterator();
		for(String fieldT : targetS.getSimpleKnowledge())
		{
			String fieldP = patternIt.next();
			// if the field in the pattern is not null but it does not match the field in the target, fail
			if((fieldP != null) && (!fieldT.equals(fieldP)))
				return false;
		}
		return true;
	}
	
	@Override
	public boolean remove(KnowledgeDescription pattern)
	{
		SimpleKnowledge patternS = (SimpleKnowledge) pattern;
		for(KnowledgeDescription kd : this)
		{
			if(matchPattern(patternS, (SimpleKnowledge) kd))
			{
				super.remove(kd);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean removeAll(KnowledgeDescription pattern)
	{
		SimpleKnowledge patternS = (SimpleKnowledge) pattern;
		boolean ret = false;
		for(KnowledgeDescription kd : this)
		{
			if(matchPattern(patternS, (SimpleKnowledge) kd))
			{
				super.remove(kd);
				ret = true;
			}
		}
		return ret;
	}
	
}
