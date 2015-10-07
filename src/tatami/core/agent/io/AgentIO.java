package tatami.core.agent.io;

import java.util.Vector;

/**
 * The class models simple I/O by considering a set of "I/O ports" (identified by name) which have values that can be
 * read and/or values that can be written.
 * <p>
 * Both input and output should not be blocking (see also {@link AgentActiveIO}.
 * <p>
 * It is recommended that each implementation contains an enumeration that contains the names of I/O ports. It is also
 * <b>strongly recommended</b> that the names of ports are always converted to lower case so that no casing issues
 * arise.
 * <p>
 * The class is meant to be implemented by agent components that are able to perform input/output with various external
 * systems. The interface should not be used for GUI (there exists <code>AgentGui</code> for that) nor for messaging
 * (there exists <code>MessagingComponent</code> for that.
 * 
 * @author Andrei Olaru
 */
public interface AgentIO
{
	/**
	 * Sends information to an I/O port.
	 * 
	 * @param portName
	 *            - the name of the port.
	 * @param arguments
	 *            - the information to transmit.
	 */
	public void doOutput(String portName, Vector<Object> arguments);
	
	/**
	 * Retrieves information from an I/O port. Implementations should not block to wait for input.
	 * 
	 * @param portName
	 *            - the name of the port.
	 * @return the received information.
	 */
	public Vector<Object> getInput(String portName);
	
}
