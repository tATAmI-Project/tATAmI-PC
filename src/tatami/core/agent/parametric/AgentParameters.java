package tatami.core.agent.parametric;

import tatami.core.util.RegisteredParameterSet;

/**
 * An instance of this class stores the parameters of an agent (usually given at creation of the agent).
 * <p>
 * The class is underpinned by {@link RegisteredParameterSet}.
 * <p>
 * The entries can have as name / key one of {@link AgentParameterName} or can be a {@link String}, for the so-called
 * 'unregistered parameters'.
 * 
 * @author Andrei Olaru
 */
public class AgentParameters extends RegisteredParameterSet<AgentParameterName>
{
	/**
	 * The class UID.
	 */
	private static final long						serialVersionUID	= -6934932321274715286L;
}