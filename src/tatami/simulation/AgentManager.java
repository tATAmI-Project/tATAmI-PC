package tatami.simulation;

/**
 * An instance implementing this interface is a wrapper to an actual agent instance (that may be not yet created).
 * It offers methods for managing the lifecycle of an agent, for instance starting and stopping.
 * 
 * @author Andrei Olaru
 */
public interface AgentManager
{
	/**
	 * Starts the agent. If this goes well, from this moment on the agent should be executing normally.
	 * 
	 * @return <code>true</code> if the agent was started without error. <code>false</code> otherwise.
	 */
	public boolean start();
	
	/**
	 * Stops the agent. After this method succeeds, the agent should not be executing any more.
	 * 
	 * @return <code>true</code> if the agent was stopped without error. <code>false</code> otherwise.
	 */
	public boolean stop();
}