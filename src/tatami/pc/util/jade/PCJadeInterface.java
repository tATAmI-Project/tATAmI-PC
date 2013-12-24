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
package tatami.pc.util.jade;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.util.ExtendedProperties;
import jade.util.leap.Properties;
import jade.wrapper.AgentContainer;
import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;

import java.util.HashMap;

import net.xqhs.util.logging.UnitComponentExt;
import net.xqhs.util.logging.Logger.Level;
import tatami.core.util.platformUtils.PlatformUtils;
import tatami.jade.JadeInterface;

/**
 * A class used in order to interact with Jade. One instance of this class corresponds to one JADE Platform.
 * 
 * @author Cedric Herpson
 * @author Tudor Benea
 * @author Andrei Olaru
 */
public class PCJadeInterface implements JadeInterface
{
	/**
	 * The default host name.
	 */
	private final static String					DEFAULT_HOST	= "localhost";
	/**
	 * The default port.
	 */
	private final static String					DEFAULT_PORT	= "1099";
	
	/**
	 * The log.
	 */
	private UnitComponentExt					log				= null;
	/**
	 * The configuration.
	 */
	protected JadeConfig						config			= null;
	/**
	 * the main agent container, if situated on this machine
	 */
	protected AgentContainer					mainContainer	= null;
	/**
	 * list of local containers
	 */
	protected HashMap<String, AgentContainer>	containerList	= new HashMap<String, AgentContainer>();
	
	/**
	 * Call this before <code>setConfig()</code>.
	 * 
	 * @param configuration
	 *            - the {@link tatami.jade.JadeInterface.JadeConfig} instance specifying the parameters for Jade.
	 * @return the config with the default parameters filled in..
	 */
	@Override
	public JadeConfig fillConfig(JadeConfig configuration)
	{
		return configuration.setLocalHost(DEFAULT_HOST).setLocalPort(DEFAULT_PORT).setMainHost(DEFAULT_HOST)
				.setMainPort(DEFAULT_PORT);
	}
	
	@Override
	public JadeInterface setConfig(JadeConfig configuration)
	{
		config = configuration;
		return this;
	}
	
	@Override
	public boolean startPlatform()
	{
		log = (UnitComponentExt) new UnitComponentExt().setUnitName("PCJadeInterface").setLogLevel(Level.ALL);
		ProfileImpl profile;
		Properties props = new ExtendedProperties();
		
		props.setProperty(Profile.GUI, "true");
		props.setProperty(Profile.MAIN, "true");
		
		props.setProperty(Profile.MAIN_HOST, config.getMainHost());
		props.setProperty(Profile.MAIN_PORT, config.getMainPort());
		props.setProperty(Profile.LOCAL_HOST, config.getLocalHost());
		props.setProperty(Profile.LOCAL_PORT, config.getLocalPort());
		
		if(config.getPlatformID() != null)
			props.setProperty(Profile.PLATFORM_ID, config.getPlatformID());
		
		if(config.getMainContainerName() != null)
			props.setProperty(Profile.CONTAINER_NAME, config.getMainContainerName());
		
		profile = new ProfileImpl(props);
		
		log.info("Launching [" + (config.getMainContainerName() != null ? config.getMainContainerName() : "unnamed")
				+ "] main container [" + profile + "]");
		mainContainer = Runtime.instance().createMainContainer(profile);
		try
		{
			containerList.put(mainContainer.getContainerName(), mainContainer);
		} catch(Exception e)
		{
			log.error("Failed to start platform (main container [" + config.getMainContainerName() + "]: "
					+ PlatformUtils.printException(e));
			return false;
		}
		return true;
	}
	
	/**
	 * Method used by createContainer methods in order to start a container, having the name of the container specified
	 * and supporting null arguments.
	 * 
	 * Not to be used for the main container.
	 * 
	 * @param containerName
	 *            - the name of the container
	 */
	@Override
	public boolean startContainer(String containerName)
	{
		ProfileImpl profile;
		Properties props = new ExtendedProperties();
		
		props.setProperty(Profile.MAIN, "false");
		
		props.setProperty(Profile.LOCAL_HOST, config.getLocalHost());
		props.setProperty(Profile.LOCAL_PORT, config.getLocalPort());
		props.setProperty(Profile.MAIN_HOST, config.getMainHost());
		props.setProperty(Profile.MAIN_PORT, config.getMainPort());
		
		if(containerName != null)
			props.setProperty(Profile.CONTAINER_NAME, containerName);
		
		profile = new ProfileImpl(props);
		
		log.info("Launching non-main container [" + (containerName != null ? containerName : "unnamed")
				+ "] with options [" + profile + "]");
		AgentContainer container = Runtime.instance().createAgentContainer(profile);
		try
		{
			containerList.put(container.getContainerName(), container);
		} catch(Exception e)
		{
			log.error("Failed to start container [" + containerName + "]: " + PlatformUtils.printException(e));
			return false;
		}
		return true;
	}
	
	@Override
	public boolean stopPlatform()
	{
		Runtime.instance().shutDown();
		log.doExit();
		log = null;
		return true;
	}
	
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
	@Override
	public boolean addAgentToContainer(String containerName, String agentName, String agentClassName, Object[] agentArgs)
	{
		try
		{
			(containerName != null ? containerList.get(containerName) : getMainContainer()).createNewAgent(agentName,
					agentClassName, agentArgs).start();
		} catch(Exception e)
		{
			log.error("Unable to add agent [" + agentName + "] to container [" + containerName + "]: "
					+ PlatformUtils.printException(e));
			return false;
		}
		return true;
	}
	
	/**
	 * @return the name of the main container, if any.
	 */
	@Override
	public String getMainContainerName()
	{
		if(mainContainer != null)
			try
			{
				return getMainContainer().getContainerName();
			} catch(ControllerException e)
			{
				e.printStackTrace();
			}
		return null;
	}
	
	/**
	 * @return the main agent container.
	 */
	public ContainerController getMainContainer()
	{
		return mainContainer;
	}
	
	/**
	 * @param containerName
	 *            - the name of the searched container.
	 * @return the container with the specified name.
	 */
	public ContainerController getContainer(String containerName)
	{
		return containerList.get(containerName);
	}
	
}
