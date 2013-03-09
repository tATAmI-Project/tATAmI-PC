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

import jade.core.Agent;

/**
 * Implementing class provides the implementation for various operations necessary for accessing / registering web
 * services.
 * 
 * @author Andrei Olaru
 */
public interface WSAgentImplementationInterface
{
	/**
	 * Registers the agent as a web service.
	 * 
	 * @param agent
	 *            the {@link Agent} instance;
	 * @param agentName
	 *            the name of the agent;
	 * @param agentClass
	 *            the type of the agent (e.g. CLAIM agent class);
	 * @return <code>true</code> if the operation was successful.
	 */
	boolean registerService(Agent agent, String agentName, String agentClass);
	
	/**
	 * Unregisters the web service corresponding to the agent.
	 * 
	 * @param agent
	 *            the {@link Agent} instance;
	 * @return <code>true</code> if the operation was successful.
	 */
	boolean unregisterWS(Agent agent);
	
	/**
	 * Invokes a web service with a single, String, parameter, and a String return value.
	 * 
	 * // FIXME: make it work for objects
	 * 
	 * @param uri
	 *            the URI of the service;
	 * @param serviceName
	 *            the name of the service at that URI;
	 * @param message
	 *            the parameter / message;
	 * @return the result of the invocation.
	 */
	public String doAccess(String uri, String serviceName, String message);
	
	/**
	 * Invokes a web service using an HTTP connection (instead of using the WS plug-in).
	 * 
	 * @param uri
	 *            the URI to connect to;
	 * @param request
	 *            the request to send;
	 * @return the result of the invocation.
	 */
	public String doSimpleAccess(String uri, String request);
}
