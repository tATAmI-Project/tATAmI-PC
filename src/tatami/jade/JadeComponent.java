package tatami.jade;

import tatami.core.agent.AgentComponent;
import tatami.core.util.platformUtils.PlatformUtils;

/**
 * Component that handles the interaction with the Jade platform.
 * 
 * @author Andrei Olaru
 */
public class JadeComponent extends AgentComponent
{
	/**
	 * The serial UID.
	 */
	private static final long	serialVersionUID	= -316069314442844313L;
	
	/**
	 * Default constructor.
	 */
	public JadeComponent()
	{
		super(AgentComponentName.JADE_COMPONENT);
	}
	
	/**
	 * Retrieves the wrapping Jade agent.
	 * 
	 * @return the wrapper agent.
	 */
	protected JadeAgentWrapper getWrapper()
	{
		JadeAgentWrapper wrapper;
		try
		{
			wrapper = (JadeAgentWrapper) getPlatformLink();
		} catch(ClassCastException e)
		{
			throw new IllegalStateException("Platform link is not a jade agent wrapper:"
					+ PlatformUtils.printException(e));
		}
		return wrapper;
	}

	/**
	 * Retrieves the name of the wrapping Jade agent.
	 * 
	 * @return the agent local name.
	 */
	public String getLocalName()
	{
		return getWrapper().getLocalName();
	}
}
