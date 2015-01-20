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
package tatami.jade;

import net.xqhs.util.config.Config;

/**
 * There should be at one {@link JadeInterface} instance per machine.
 * 
 * @author Andrei Olaru
 * @author with snippets from the code of Marius-Tudor Benea
 * 
 */
public interface JadeInterface
{
	/**
	 * Configuration of Jade.
	 * 
	 * @author Andrei Olaru
	 */
	public class JadeConfig extends Config
	{
		/**
		 * the host of the main container
		 */
		private String	host				= null;
		/**
		 * the port of the main container
		 */
		private String	port				= null;
		/**
		 * the host of the standard container to be created
		 */
		private String	localhost			= null;
		/**
		 * the port of the standard container to be created
		 */
		private String	localport			= null;
		
		/**
		 * the ID of the platform; used only for the main instance;
		 */
		private String	platformID			= null;
		
		/**
		 * the name of the main container; used only for the main instance;
		 */
		private String	mainContainerName	= null;
		
		/**
		 * @param mainHost
		 *            - the host of the main controller
		 * @return the configuration itself
		 */
		public JadeConfig setMainHost(String mainHost)
		{
			if(mainHost != null)
				host = mainHost;
			return this;
		}
		
		/**
		 * @param mainPort
		 *            - the port of the platform
		 * @return the configuration itself
		 */
		public JadeConfig setMainPort(String mainPort)
		{
			if(mainPort != null)
				port = mainPort;
			return this;
		}
		
		/**
		 * @param thisHost
		 *            - the host of the standard container to be created
		 * @return the configuration itself
		 */
		public JadeConfig setLocalHost(String thisHost)
		{
			if(thisHost != null)
				localhost = thisHost;
			return this;
		}
		
		/**
		 * @param thisPort
		 *            - the port of the standard container to be created
		 * @return the configuration itself
		 */
		public JadeConfig setLocalPort(String thisPort)
		{
			if(thisPort != null)
				localport = thisPort;
			return this;
		}
		
		/**
		 * @param id
		 *            - the ID of the Jade platform.
		 * @return the configuration itself
		 */
		public JadeConfig setPlatformID(String id)
		{
			if(id != null)
				platformID = id;
			return this;
		}
		
		/**
		 * @param name
		 *            - the name of the main container.
		 * @return the configuration itself
		 */
		public JadeConfig setMainContainerName(String name)
		{
			if(name != null)
				mainContainerName = name;
			return this;
		}
		
		/**
		 * @return the main host
		 */
		public String getMainHost()
		{
			return host;
		}
		
		/**
		 * @return the main port
		 */
		public String getMainPort()
		{
			return port;
		}
		
		/**
		 * @return the local host
		 */
		public String getLocalHost()
		{
			return localhost;
		}
		
		/**
		 * @return the local port
		 */
		public String getLocalPort()
		{
			return localport;
		}
		
		/**
		 * @return the platform ID
		 */
		public String getPlatformID()
		{
			return platformID;
		}
		
		/**
		 * @return the main container name
		 */
		public String getMainContainerName()
		{
			return mainContainerName;
		}
	}
	
	/**
	 * Fills in the configuration settings that are default for the platform.
	 * 
	 * @param configuration
	 *            : the configuration object to fill
	 * @return the same object as the argument
	 */
	public JadeConfig fillConfig(JadeConfig configuration);
	
	/**
	 * This is the actual constructing function. Call this after constructing, filling the configuration, and setting
	 * favorite parameters.
	 * 
	 * @param configuration
	 *            - the {@link Config} instance containing the configuration.
	 * @return the instance itself.
	 */
	public JadeInterface setConfig(JadeConfig configuration);
	
	/**
	 * Creates the main container. On platforms that cannot contain the main container, it should return an
	 * IllegalOperationException.
	 * 
	 * @return <code>true</code> if the operation succeeded; <code>false</code> otherwise.
	 */
	public boolean startPlatform();
	
	/**
	 * Stops the Jade platform.
	 * 
	 * @return <code>true</code> if the operation succeeded; <code>false</code> otherwise.
	 */
	public boolean stopPlatform();
	
	/**
	 * Method used by createContainer methods in order to start a container, having the name of the container specified
	 * and supporting null arguments.
	 * 
	 * Not to be used for the main container.
	 * 
	 * @param containerName
	 *            - the name of the container
	 * @return <code>true</code> if the operation succeeded; <code>false</code> otherwise.
	 */
	public boolean startContainer(String containerName);
	
	/**
	 * Method which creates and starts an agent in a container specified by its name
	 * 
	 * @param containerName
	 *            - the name of the container; null for the main container - should be on the same machine
	 * @param agentName
	 *            - the name of the agent
	 * @param agentClassName
	 *            - the class of the agent, including the package
	 * @param agentArgs
	 *            - array of Objects, representing the arguments of the agent
	 * @return <code>true</code> if the operation succeeded; <code>false</code> otherwise.
	 */
	public boolean addAgentToContainer(String containerName, String agentName, String agentClassName, Object[] agentArgs);
	
	/**
	 * @return the name of the main container, if any.
	 */
	public String getMainContainerName();
	
}
