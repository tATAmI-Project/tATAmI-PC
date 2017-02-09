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
package tatami.simulation;

import java.util.HashMap;

import net.xqhs.util.XML.XMLTree.XMLNode;
import tatami.core.platforms.PlatformDescriptor;

/**
 * The platform loader is the interface to the manager of an agent platform. It can load and manage agents. Agents are
 * loaded based on {@link AgentCreationData} instances.
 * <p>
 * The platform should not contain initializations in its constructor. Creating a new instance of the platform loader
 * should never generate any errors. The platform should be initialized inside the {@link #start()} method.
 * 
 * @author Andrei Olaru
 */
public abstract class PlatformLoader
{
	/**
	 * Standard types of platforms. The name of the platform is used in the agent description in the scenario file.
	 * 
	 * @author Andrei Olaru
	 */
	public enum StandardPlatformType {
		
		/**
		 * Agents will be loaded as Jade agents.
		 * <p>
		 * The platform class is inferred, as to avoid referring the class in case it is missing from the build.
		 */
		JADE,
		
		/**
		 * Same as default, but recommends a messaging component that routes messages based on agent names.
		 */
		LOCAL(LocalDeploymentPlatform.class.getName()),
		
		/**
		 * Agents will be created as new instances. This is the default setting.
		 */
		DEFAULT("defaultplatform"),
		
		/**
		 * 
		 */
		WEBSOCKET("tatami.websocket.WebSocketMessagingPlatform"),
		;
		
		/**
		 * The name of the platform.
		 */
		private String	name	= null;
								
		/**
		 * Creates a new platform type, giving it a name that is the lower case version of the enumeration value, and
		 * setting a default class name, considering the platform name as the package name and also as a prefix to the
		 * class name.
		 */
		private StandardPlatformType()
		{
			name = super.toString().toLowerCase();
		}
		
		/**
		 * Creates a new platform type, giving it a name that is the lower case version of the enumeration value.
		 * 
		 * @param className
		 *            - the fully qualified name of the class to instantiate when starting the platform. The class
		 *            should implement {@link PlatformLoader}.
		 */
		private StandardPlatformType(String className)
		{
			name = super.toString().toLowerCase();
		}
		
		/**
		 * Creates a new platform type, giving it a name unrelated to the enumeration value.
		 * 
		 * @param platformName
		 *            - the name of the platform.
		 * @param className
		 *            - the fully qualified name of the class to instantiate when starting the platform. The class
		 *            should implement {@link PlatformLoader}.
		 */
		private StandardPlatformType(String platformName, String className)
		{
			name = platformName;
		}
		
		/**
		 * Overrides the inherited method to return the 'given name' of the platform.
		 */
		@Override
		public String toString()
		{
			return name;
		}
	}
	
    
    public abstract void onStartAgent(String path);
    
    public abstract void onStopAgent(String path);
    
	
	/**
	 * The default platform to use, if no other is specified.
	 */
	static final StandardPlatformType	DEFAULT_PLATFORM	= StandardPlatformType.DEFAULT;
															
	/**
	 * The name of the attribute containing the platform name in the XML file.
	 */
	public static final String					NAME_ATTRIBUTE		= "name";
	
	HashMap<String, String> platformConfig;
	
	String mName;
	
															
	/**
	 * @return the name of the platform loader, as used in the scenario file.
	 */
	public String getName(){
	    return mName;
	}
	
	/**
	 * Configures the platform by passing the XML node in the scenario. The platform can extract the necessary settings.
	 * <p>
	 * The method should not perform any initializations that can fail. These should be done in the {@link #start()}
	 * method.
	 * 
	 * @param configuration
	 *            - the XML node containing the configuration of the platform.
	 * @param settings
	 *            - general application settings specified in the scenario file, program argumetns, etc.
	 * @return the instance itself.
	 */
	public abstract PlatformLoader setConfig(PlatformDescriptor platformDescriptor);
	
	/**
	 * Starts the agent platform.
	 * 
	 * @return <code>true</code> if the platform was started successfully; <code>false</code> otherwise.
	 */
	public abstract boolean start();
	
	/**
	 * Stops the agent platform.
	 * 
	 * @return <code>true</code> if the platform was stopped successfully; <code>false</code> otherwise.
	 */
	public abstract boolean stop();

}
