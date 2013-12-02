package tatami.simulation;

import java.util.Map;

import tatami.pc.util.XML.XMLTree.XMLNode;

/**
 * Singleton class managing the simulation on a machine or on a set of machines (possibly all).
 * <p>
 * After the initializations in {@link Boot}, it handles the actual starting and management of agents, as well as
 * creating the events in the scenario timeline.
 * <p>
 * It normally offers a GUI or some other kind of UI for the said operations, that can exist outside of the actual agent
 * platform(s).
 * 
 * @author Andrei Olaru
 */
public class SimulationManager
{
	/**
	 * Creates a new instance, also starting the GUI, based on the map of platforms and their names, the map of agents
	 * and their names (agents are managed by {@link AgentManager} wrappers and the timeline.
	 * 
	 * @param platforms
	 *            - the {@link Map} of platform names and {@link PlatformLoader} instances that are currently started.
	 * @param allAgents
	 *            - the names and {@link AgentManager} wrappers of agents that are currently loaded.
	 * @param timeline
	 *            - the timeline of events, as {@link XMLNode} parsed from the scenario file.
	 */
	public SimulationManager(Map<String, PlatformLoader> platforms, Map<String, AgentManager> allAgents,
			XMLNode timeline)
	{
		
	}
	
}
