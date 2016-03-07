/*******************************************************************************
 * Copyright (C) 2015 Andrei Olaru, Marius-Tudor Benea, Nguyen Thi Thuy Nga, Amal El Fallah Seghrouchni, Cedric Herpson.
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

import net.xqhs.graphs.context.ContextPattern;
import tatami.core.agent.AgentComponent;
import tatami.core.agent.kb.simple.SimpleKB;

/** Class describing the cognitive component of an agent.
 * @author Tudor
 *
 */
public class CognitiveComponent extends AgentComponent // TODO implement to be used as ContextComponent parent
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Constructor with no arguments.
	 */
	public CognitiveComponent()
	{
		super(AgentComponentName.COGNITIVE_COMPONENT);
	}
	
	@SuppressWarnings("javadoc")
	public void addPattern(@SuppressWarnings("unused") ContextPattern pattern)
	{
		// TODO
	}
}
