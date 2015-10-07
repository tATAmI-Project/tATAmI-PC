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
