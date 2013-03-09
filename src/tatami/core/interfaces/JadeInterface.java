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
package tatami.core.interfaces;

import tatami.core.util.Config;

/**
 * There should be at one {@link JadeInterface} instance per machine.
 * 
 * @author Andrei Olaru
 * @author with snippets from the code of Marius-Tudor Benea
 * 
 */
public interface JadeInterface
{
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
		 * @param _host
		 *            - the host of the main controller
		 * @return the config
		 */
		public JadeConfig setMainHost(String _host)
		{
			if(_host != null)
				this.host = _host;
			return this;
		}
		
		/**
		 * @param _port
		 *            - the port of the platform
		 * @return the config
		 */
		public JadeConfig setMainPort(String _port)
		{
			if(_port != null)
				this.port = _port;
			return this;
		}
		
		/**
		 * @param _localhost
		 *            - the host of the standard container to be created
		 * @return the config
		 */
		public JadeConfig setLocalHost(String _localhost)
		{
			if(_localhost != null)
				this.localhost = _localhost;
			return this;
		}
		
		/**
		 * @param _localport
		 *            - the port of the standard container to be created
		 * @return the config
		 */
		public JadeConfig setLocalPort(String _localport)
		{
			if(_localport != null)
				this.localport = _localport;
			return this;
		}
		
		public JadeConfig setPlatformID(String id)
		{
			if(id != null)
				this.platformID = id;
			return this;
		}
		
		public JadeConfig setMainContainerName(String name)
		{
			if(name != null)
				this.mainContainerName = name;
			return this;
		}
		
		public String getMainHost()
		{
			return host;
		}
		
		public String getMainPort()
		{
			return port;
		}
		
		public String getLocalHost()
		{
			return localhost;
		}
		
		public String getLocalPort()
		{
			return localport;
		}
		
		public String getPlatformID()
		{
			return platformID;
		}
		
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
	  * This is the actual constructing function. Call this after constructing, filling config, and setting favorite config params.
	  * @param configuration
	  * @return
	  */
	 public JadeInterface setConfig(JadeConfig configuration);
	 
	/**
	 * Creates the main container. On platforms that cannot contain the main container, it should return an IllegalOperationException.
	 * 
	 * @return this
	 */
	public JadeInterface startPlatform();
	
	/**
	 * Method used by createContainer methods in order to start a container, having the name of the container specified and supporting null arguments.
	 * 
	 * Not to be used for the main container.
	 * 
	 * @param containerName
	 *            - the name of the container
	 */
	public void startContainer(String containerName);
	
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
	 */
	public void addAgentToContainer(String containerName, String agentName, String agentClassName, Object[] agentArgs);
	
	/**
	 * @return the name of the main container, if any.
	 */
	public String getMainContainerName();
	
}
