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
package tatami.core.agent.webServices;

import java.lang.reflect.Constructor;

import tatami.core.agent.visualization.VisualizableAgent;
import tatami.core.util.platformUtils.PlatformUtils;

/**
 * 
 * @author Nguyen Thi Thuy Nga
 * @author Andrei Olaru
 * 
 */
public class WSAgent extends VisualizableAgent
{
	@SuppressWarnings("javadoc")
	private static final long		serialVersionUID		= 5210112206747363495L;
	
	/**
	 * QUEST: this should be put somewhere else?
	 */
	private static final String		WS_IMPLEMENTATION_CLASS	= "tatami.pc.agent.webService.WSAgentImplementation";
	
	/**
	 * Marks if hte service has already been registered.
	 */
	boolean							serviceRegistered		= false;
	
	/**
	 * The implementation of the WS-related operations.
	 */
	WSAgentImplementationInterface	implementation			= null;
	
	@Override
	public void setup()
	{
		super.setup();
		
		if(PlatformUtils.platformSupportsWebServices())
		{
			ClassLoader cl = new ClassLoader(getClass().getClassLoader()) {
				// nothing to extend
			};
			
			try
			{
				Constructor<?> cons = cl.loadClass(WS_IMPLEMENTATION_CLASS).getConstructor();
				implementation = (WSAgentImplementationInterface) cons.newInstance();
			} catch(Exception e)
			{
				log.error("constructing a WS implementation failed: ", e);
			}
		}
	}
	
	/**
	 * Registers the agent as a web service.
	 */
	protected void registerWSBehavior()
	{
		if(implementation == null)
			return;
		
		if(!serviceRegistered)
		{
			if(implementation.registerService(this, getAgentName(), parVal(AgentParameterName.AGENT_CLASS)))
			{
				log.info(getAgentName() + " has registered service " + getAgentName());
				serviceRegistered = true;
			}
			else
				log.error("Problem during DF registration");
		}
	}
	
	@Override
	protected void takeDown()
	{
		// Unregister from the DF
		if(serviceRegistered)
			if(!implementation.unregisterWS(this))
				log.error("Error in unregistering from DF");
		super.takeDown();
	}
	
	/**
	 * Accesses a web service.
	 * 
	 * @param uri
	 *            the URI of the service;
	 * @param serviceName
	 *            the name of the service at that URI;
	 * @param message
	 *            the parameter / message;
	 * @return the result of the invocation.
	 */
	public String doAccess(String uri, String serviceName, String message)
	{
		if(implementation != null)
		{
			String result = implementation.doAccess(uri, serviceName, message);
			getLog().info("returned value: [" + result + "]");
			return result;
		}
		
		return null;
	}
	
	/**
	 * Invokes a web service using an HTTP connection (instead of using the WS plug-in).
	 * 
	 * @param uri
	 *            the URI to connect to;
	 * @param request
	 *            the request to send;
	 * @return the result of the invocation.
	 */
	public String doSimpleAccess(String uri, String request)
	{
		if(implementation != null)
		{
			String result = implementation.doSimpleAccess(uri, request);
			getLog().info("simple access returned value: [" + result + "]");
			return result;
		}
		return null;
	}
}
