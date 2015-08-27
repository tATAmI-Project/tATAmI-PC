package tatami.core.agent.messaging;

/**
 * A simple extension of {@link MessagingComponent}, which leaves only {@link #sendMessage(String, String, String)} as
 * an abstract method. This class is meant to be extended by messaging components in platforms that use agent names as
 * agent addresses (agents are addressed by name). Therefore, the address of the agent is always the same with its name.
 * <p>
 * It is also presumed that agent names do not contain slashes. An exception is thrown if the component is loaded in an
 * agent with a name containing a slash (or whatever {@link MessagingComponent#ADDRESS_SEPARATOR} is set to).
 * 
 * @author Andrei Olaru
 */
public abstract class NameBasedMessagingComponent extends MessagingComponent
{
	/**
	 * The serial UID.
	 */
	private static final long serialVersionUID = 13149367588469383L;
	
	/**
	 * The implementation considers agent addresses are the same with their names.
	 */
	@Override
	public String getAgentAddress(String agentName)
	{
		return agentName;
	}
	
	/**
	 * The implementation considers agent addresses are the same with their names.
	 */
	@Override
	public String getAgentNameFromAddress(String agentAddress)
	{
		return agentAddress;
	}
	
	/**
	 * This implementation presumes that the address / name of the agent does not contain any occurrence of
	 * {@link MessagingComponent#ADDRESS_SEPARATOR} (currently {@value MessagingComponent#ADDRESS_SEPARATOR}).
	 */
	@Override
	public String extractAgentAddress(String endpoint)
	{
		return endpoint.substring(0, endpoint.indexOf(ADDRESS_SEPARATOR));
	}
}
