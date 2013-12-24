package tatami.simulation;

import tatami.core.agent.CompositeAgent;
import tatami.core.agent.messaging.MessagingComponent;
import tatami.core.agent.visualization.VisualizableComponent;

/**
 * In order to be able to communicate with other agents in a platform, the {@link SimulationManager} keeps an agent on
 * each of the available platforms, in order to relay events and control messages to the agents.
 * 
 * @author Andrei Olaru
 */
public class SimulationLinkAgent extends CompositeAgent
{
	/**
	 * The serial UID.
	 */
	private static final long	serialVersionUID	= 9020113598000072111L;
	
	/**
	 * The name of this agent, as set by the owning {@link SimulationManager}.
	 */
	String						name				= null;
	
	/**
	 * Creates a new instance, with the specified name. The instance will have two components: a
	 * {@link VisualizableComponent} and a {@link MessagingComponent}.
	 * 
	 * @param agentName
	 *            - the name of the agent.
	 */
	public SimulationLinkAgent(String agentName)
	{
		name = agentName;
		addComponent(new VisualizableComponent());
		addComponent(new MessagingComponent());
	}
	
	@Override
	public String getAgentName()
	{
		return name;
	}
}
