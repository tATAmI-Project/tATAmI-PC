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
package tatami.core.agent.agent_type;

import tatami.core.agent.CompositeAgent;
import tatami.core.agent.CompositeAgentLoader;

/**
 * The agent loader is the interface to an instance that uses existing scenario information to completely specify
 * loading information for an agent.
 * 
 * @author Andrei Olaru
 */
public interface TatamiAgent extends Runnable
{
	/**
	 * Standard types of loaders for agents. The name of the loader is used in the agent description in the scenario
	 * file.
	 * 
	 * @author Andrei Olaru
	 */
	enum StandardAgentLoaderType {
		
		/**
		 * The agent is described by means of an .adf2 file. The ADF2 agent is a special kind of {@link CompositeAgent},
		 * with some pre-configured components. An S-CLAIM agent can be easily loaded as a COMPOSITE agent. This
		 * enumeration value is kept only for backwards compatibility of scenarios.
		 */
		ADF2(null), // TODO
		
		/**
		 * The agent is a {@link CompositeAgent} that is made up of various components.
		 */
		COMPOSITE(CompositeAgentLoader.class.getName()),
		
		EVENTBASED("eventbased"),
		
		/**
		 * The agent is described by means of traditional Java code. Currently unimplemented.
		 */
		JAVA(null), // TODO
		
		;
		
		/**
		 * The name of the loader.
		 */
		private String	name	= null;
		/**
		 * The fully qualified name of the class to instantiate when using the loader.
		 */
		private String	classN	= null;
		
		/**
		 * Creates a new loader type, giving it a name unrelated to the enumeration value.
		 * 
		 * @param loaderName
		 *            - the name of the loader.
		 * @param className
		 *            - the fully qualified name of the class to instantiate when starting the platform. The class
		 *            should implement {@link TatamiAgent}.
		 */
		private StandardAgentLoaderType(String loaderName, String className)
		{
			name = loaderName;
			classN = className;
		}
		
		/**
		 * Creates a new loader type, giving it a name that is the lower case version of the enumeration value.
		 * 
		 * @param className
		 *            - the fully qualified name of the class to instantiate when starting the platform. The class
		 *            should implement {@link TatamiAgent}.
		 */
		private StandardAgentLoaderType(String className)
		{
			name = super.toString().toLowerCase();
			classN = className;
		}
		
		/**
		 * @return the name of the class to instantiate in order to create this platform loader.
		 */
		public String getClassName()
		{
			return classN;
		}
		
		/**
		 * Overrides the inherited method to return the 'given name' of the loader.
		 */
		@Override
		public String toString()
		{
			return name;
		}
	}
	
	/**
	 * The name of the attribute containing the loader name in the XML file.
	 */
	static final String	NAME_ATTRIBUTE		= "name";
	/**
	 * The name of the attribute containing the class path of the {@link TatamiAgent} class, in the XML file.
	 */
	static final String	CLASSPATH_ATTRIBUTE	= "classpath";
	/**
	 * The default agent loader to use, if no other is specified.
	 */
	static final String	DEFAULT_LOADER	= StandardAgentLoaderType.COMPOSITE.toString();
	
	/**
	 * @return the the name of the agent loader, as used in the scenario file.
	 */
	public String getName();
}
