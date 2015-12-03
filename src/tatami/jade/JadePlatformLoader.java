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
package tatami.jade;

import java.lang.reflect.InvocationTargetException;

import net.xqhs.util.XML.XMLTree.XMLNode;
import tatami.core.agent.CompositeAgent;
import tatami.core.agent.AgentComponent.AgentComponentName;
import tatami.core.util.platformUtils.PlatformUtils;
import tatami.jade.JadeInterface.JadeConfig;
import tatami.simulation.AgentManager;
import tatami.simulation.BootSettingsManager;
import tatami.simulation.PlatformLoader;

/**
 * Implementation of {@link PlatformLoader} using Jade (<a href = "http://jade.tilab.com">jade.tilab.com</a>) as an
 * underlying framework for agent communication and mobility.
 * <p>
 * For interfacing with Jade, the platform uses the older {@link JadeInterface} which can be implemented by specific
 * classes depending on the machine.
 * 
 * @author Andrei Olaru
 */
public class JadePlatformLoader implements PlatformLoader
{
	/**
	 * The interface with the actual Jade implementation.
	 */
	JadeInterface	jadeInterface;
	
	/**
	 * @throws SecurityException
	 *             -
	 * @throws IllegalArgumentException
	 *             -
	 * @throws NoSuchMethodException
	 *             -
	 * @throws ClassNotFoundException
	 *             -
	 * @throws InstantiationException
	 *             -
	 * @throws IllegalAccessException
	 *             -
	 * @throws InvocationTargetException
	 *             -
	 */
	public JadePlatformLoader() throws SecurityException, IllegalArgumentException, NoSuchMethodException,
			ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException
	{
		jadeInterface = (JadeInterface) PlatformUtils.loadClassInstance(this, PlatformUtils.jadeInterfaceClass());
	}
	
	@Override
	public String getName()
	{
		return StandardPlatformType.JADE.toString();
	}
	
	@Override
	public PlatformLoader setConfig(XMLNode configuration, BootSettingsManager settings)
	{
		JadeConfig config = new JadeConfig();
		jadeInterface.fillConfig(config);
		String mh = PlatformUtils.getParameterValue(configuration, "IPaddress");
		String mp = PlatformUtils.getParameterValue(configuration, "port");
		String lh = PlatformUtils.getParameterValue(configuration, "localIPaddress");
		String lp = PlatformUtils.getParameterValue(configuration, "localPort");
		String mc = PlatformUtils.getParameterValue(configuration, "mainContainerName");
		String id = PlatformUtils.getParameterValue(configuration, "platformID");
		if(mh != null)
			config.setMainHost(mh);
		if(mp != null)
			config.setMainPort(mp);
		if(lh != null)
			config.setLocalHost(lh);
		if(lp != null)
			config.setLocalPort(lp);
		if(mc != null)
			config.setMainContainerName(mc);
		if(id != null)
			config.setPlatformID(id);
		if(settings.getMainHost() != null)
			config.setMainHost(settings.getMainHost());
		if(settings.getMainPort() != null)
			config.setMainPort(settings.getMainPort());
		if(settings.getLocalHost() != null)
			config.setLocalHost(settings.getLocalHost());
		if(settings.getLocalPort() != null)
			config.setLocalPort(settings.getLocalPort());
		if(settings.getLocalContainerName() != null)
			config.setMainContainerName(settings.getLocalContainerName());
		jadeInterface.setConfig(config);
		return this;
	}
	
	@Override
	public boolean start()
	{
		return jadeInterface.startPlatform();
	}
	
	@Override
	public boolean stop()
	{
		return jadeInterface.stopPlatform();
	}
	
	@Override
	public boolean addContainer(String containerName)
	{
		return jadeInterface.startContainer(containerName);
	}
	
	@Override
	public boolean loadAgent(String containerName, AgentManager agentManager)
	{
		Object lock = new Object();
		boolean ret = false;
		synchronized(lock)
		{
			ret = jadeInterface.addAgentToContainer(containerName, agentManager.getAgentName(),
					JadeAgentWrapper.class.getName(), new Object[] { agentManager, lock });
			try
			{
				lock.wait(); // wait for setup to be completed.
			} catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		return ret;
	}
	
	@Override
	public String getRecommendedComponentClass(AgentComponentName componentName)
	{
		switch(componentName)
		{
		case MESSAGING_COMPONENT:
			return JadeMessagingComponent.class.getName();
		default:
			break;
		}
		return null;
	}

	@Override
	public void setParent(AgentManager agent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AgentManager getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onAgentStateChenged(CompositeAgent agent) {
		// TODO Auto-generated method stub
		
	}
}
