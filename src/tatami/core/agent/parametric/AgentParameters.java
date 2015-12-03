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
package tatami.core.agent.parametric;

import java.io.Serializable;

import tatami.core.util.RegisteredParameterSet;

/**
 * An instance of this class stores the parameters of an agent (usually given at creation of the agent).
 * <p>
 * The class is underpinned by {@link RegisteredParameterSet}.
 * <p>
 * The entries can have as name / key one of {@link AgentParameterName} or can be a {@link String}, for the so-called
 * 'unregistered parameters'.
 * 
 * @author Andrei Olaru
 */
public class AgentParameters extends RegisteredParameterSet<AgentParameterName> implements Serializable
{
	/**
	 * The class UID.
	 */
	private static final long						serialVersionUID	= -6934932321274715286L;
}
