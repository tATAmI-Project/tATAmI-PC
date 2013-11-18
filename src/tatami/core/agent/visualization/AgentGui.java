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
package tatami.core.agent.visualization;

import java.util.Vector;

/**
 * This class models the GUI of an agent (and a GUI in general) so as to make it platform-independent.
 * <p>
 * It models the GUI as a set of components that can perform input and/or output. Components are logical units that can
 * contain one or more controls and that can correspond to non-disjoint sets of controls.
 * <p>
 * There are three types of functionalities that can be associated to a component:
 * <ul>
 * <li>output -- the component is able to change appearance according to set of {@link Object} instances;
 * <li>'passive' input -- the component is able to store information entered by the user (or collected from the user),
 * that can be retrieved as a set of {@link Object} instances.
 * <li>'active' input -- the component is able to notify an {@link InputListener} instance about the user's activity,
 * also transmitting a set of {@link Object} instances along with the notification.
 * </ul>
 * Basic examples of the functionalities are as follows: output in a text field; get passive input from a text field;
 * receive active input / notifications when a button is pressed.
 * <p>
 * Components in the GUI are identified by {@link String} names. It is strongly recommended that the names are stored as
 * constants in enumerations. For instance, default components exists in a GUI, which get their names from the
 * {@link DefaultComponent} enumeration.
 * 
 * @author Andrei Olaru
 */
public interface AgentGui
{
	/**
	 * Default components of a GUI.
	 * 
	 * @author Andrei Olaru
	 */
	public enum DefaultComponent {
		/**
		 * The name of the agent.
		 */
		AGENT_NAME,
		
		/**
		 * The log of the agent (as an output component).
		 */
		AGENT_LOG
	}
	
	/**
	 * This interface should be implemented by classes that are able to receive notifications from 'active' inputs. Such
	 * notifications will be accompanied by some arguments describing the notification, beside the name of the component
	 * generating the notification.
	 * 
	 * @author Andrei Olaru
	 */
	public interface InputListener
	{
		/**
		 * The method is invoked whenever an 'active input' component is activated.
		 * 
		 * @param componentName
		 *            - the name of the component invoking the method.
		 * @param arguments
		 *            - arguments accompanying the notification.
		 */
		public void receiveInput(String componentName, Vector<Object> arguments);
	}
	
	/**
	 * Sends information to a component meant to convey that information to the GUI.
	 * 
	 * @param componentName
	 *            - the name of the component.
	 * @param arguments
	 *            - the information to transmit.
	 */
	public void doOutput(String componentName, Vector<Object> arguments);
	
	/**
	 * Retrieves information from a component. The information is expected to come from the GUI.
	 * 
	 * @param componentName
	 *            - the name of the component.
	 * @return the received information.
	 */
	public Vector<Object> getinput(String componentName);
	
	/**
	 * Connects a component, as active input, to an implementation of {@link InputListener}. The input will invoke the
	 * <code>receiveInput()</code> method of the implementation whenever it is the case.
	 * 
	 * @param componentName
	 *            - the name of the component
	 * @param listener
	 *            - the {@link InputListener} implementation to be invoked on activation.
	 */
	public void connectInput(String componentName, InputListener listener);
	
	/**
	 * Instructs the GUI to unload, effectively closing the GUI.
	 */
	public void close();
}
