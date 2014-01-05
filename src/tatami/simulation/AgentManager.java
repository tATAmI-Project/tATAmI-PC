package tatami.simulation;

/**
 * An instance implementing this interface is a wrapper to an actual agent instance (that may be not yet created). It
 * offers methods for managing the lifecycle of an agent, for instance starting and stopping.
 * 
 * @author Andrei Olaru
 */
public interface AgentManager
{
	/**
	 * Starts the agent. If this goes well, from this moment on the agent should be executing normally.
	 * <p>
	 * The method must guarantee that once it has been started, it can immediately begin to receive events, even if
	 * those events will not be processed immediately.
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
	
	/**
	 * Creates a link from the agent to the platform, which will facilitate the invocation of specific platform
	 * functionality. The passed instance may be the platform itself, or some agent-specific instance, depending on the
	 * platform.
	 * <p>
	 * This method can usually by called only when the agent is not running.
	 * 
	 * @param link
	 *            - the link to the platform.
	 * @return <code>true</code> if the operation was successful. <code>false</code> otherwise.
	 */
	public boolean setPlatformLink(Object link);
	
	/**
	 * Retrieves the name of the agent managed by this instance.
	 * 
	 * @return the name of the agent.
	 */
	public String getAgentName();
}