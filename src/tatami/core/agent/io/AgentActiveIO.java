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
package tatami.core.agent.io;

import java.util.Vector;

/**
 * The interface extends {@link AgentIO} to add "active" inputs, i.e. input ports that are able to generate
 * notifications / invoke code when "activated". Such a notification would be sent to an {@link InputListener}
 * implementation that has previously been "connected" (via {@link #connectInput(String, InputListener)}) to the input
 * port.
 * <p>
 * The interface also specifies the existence of a default listener, to be called at the activation of active inputs
 * which have not been connected to an input listener. Implementing classes should be cautious in the case in which an
 * input is connected to the listener that is also the default listener, and then the default listener is changed --
 * this should not change the listener for the input that has been explicitly connected.
 * 
 * @author Andrei Olaru
 */
public interface AgentActiveIO extends AgentIO
{
	/**
	 * This interface should be implemented by classes that are able to receive notifications from 'active' inputs. Such
	 * notifications will be accompanied by some arguments describing the notification, beside the name of the port
	 * generating the notification.
	 * <p>
	 * For general implementations, it is recommended that long computations or blocking actions are not performed
	 * within the thread where {@link #receiveInput(String, Vector)} is called.
	 * 
	 * @author Andrei Olaru
	 */
	public interface InputListener
	{
		/**
		 * The method is invoked whenever an 'active input' port is activated.
		 * 
		 * @param portName
		 *            - the name of the I/O port invoking the method.
		 * @param arguments
		 *            - arguments accompanying the notification.
		 */
		public void receiveInput(String portName, Vector<Object> arguments);
	}
	
	/**
	 * Connects an I/O port, as active input, to an implementation of {@link InputListener}. The input will invoke the
	 * <code>receiveInput()</code> method of the implementation whenever it is the case.
	 * 
	 * @param componentName
	 *            - the name of the component
	 * @param listener
	 *            - the {@link InputListener} implementation to be invoked on activation.
	 */
	public void connectInput(String componentName, InputListener listener);
	
	/**
	 * Registers an implementation of {@link InputListener} to be used for all active inputs <b>not otherwise
	 * connected</b> via {@link #connectInput(String, InputListener)}. The inputs will invoke the
	 * <code>receiveInput()</code> method of the implementation whenever it is the case.
	 * 
	 * @param listener
	 *            - the {@link InputListener} implementation to be invoked on activation. <code>null</code> can be used.
	 */
	public void setDefaultListener(InputListener listener);
}
