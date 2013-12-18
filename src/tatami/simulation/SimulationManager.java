package tatami.simulation;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import net.xqhs.util.logging.UnitComponentExt;
import tatami.core.agent.visualization.AgentGui;
import tatami.core.agent.visualization.AgentGui.InputListener;
import tatami.core.agent.visualization.AgentGuiConfig;
import tatami.core.util.platformUtils.PlatformUtils;
import tatami.pc.util.XML.XMLTree.XMLNode;

/**
 * Singleton class managing the simulation on a machine or on a set of machines (possibly all).
 * <p>
 * After the initializations in {@link Boot}, it handles the actual starting and management of agents, as well as
 * creating the events in the scenario timeline.
 * <p>
 * It normally offers a GUI or some other kind of UI for the said operations, that can exist outside of the actual agent
 * platform(s).
 * <p>
 * Although not an agent of any platform, the {@link SimulationManager} can be viewed as an agent> it implements
 * {@link AgentManager} and it features a GUI based on {@link AgentGui}.
 * <p>
 * This implementation presumes that {@link java.util.Timer} and related classes {@link TimerTask} are available on the
 * execution platform.
 * 
 * @author Andrei Olaru
 */
public class SimulationManager implements AgentManager
{
	/**
	 * Components of the simulation manager GUI.
	 * 
	 * @author Andrei Olaru
	 */
	public enum SimulationComponent {
		/**
		 * Button to create the agents.
		 */
		CREATE,
		
		/**
		 * Button to start simulation.
		 */
		START,
		
		/**
		 * Field showing the simulation time.
		 */
		TIME,
		
		/**
		 * Button to pause simulation.
		 */
		PAUSE,
		
		/**
		 * Button to destroy all agents in the simulation.
		 */
		CLEAR,
		
		/**
		 * Button to exit all platforms completely and close the application.
		 */
		EXIT,
	}
	
	/**
	 * Window type for the simulation manager window.
	 */
	private static final String							WINDOW_TYPE				= "systemSmall";
	/**
	 * Window name for the simulation manager window.
	 */
	private static final String							WINDOW_NAME				= "simulation";
	/**
	 * The name of the attribute indicating the time of the event.
	 */
	protected static final String						EVENT_TIME_ATTRIBUTE	= "time";
	/**
	 * The log.
	 */
	UnitComponentExt									log						= null;
	/**
	 * The GUI.
	 */
	AgentGui											gui						= null;
	
	/**
	 * Name and {@link PlatformLoader} for all platforms to be started.
	 */
	Map<String, PlatformLoader>							platforms;
	/**
	 * Name and locality indication (container is created locally or remotely) for all containers.
	 */
	protected Map<String, Boolean>						containers				= null;
	/**
	 * Name and {@link AgentManager} instance for all agents to be started.
	 */
	Map<String, Map<String, Map<String, AgentManager>>>	agents;
	/**
	 * The list of events in the simulation, as specified by the scenario file.
	 */
	List<XMLNode>										events					= new LinkedList<XMLNode>();
	/**
	 * Current time, in 1/10 seconds.
	 */
	long												time					= 0;
	/**
	 * Indicates whether the simulation is currently paused.
	 */
	boolean												isPaused				= false;
	/**
	 * Indicates whether agents have been created.
	 */
	boolean												agentsCreated			= false;
	/**
	 * The {@link Timer} for simulation time and also the display in the GUI.
	 */
	Timer												theTime					= null;
	
	/**
	 * Creates a new instance, also starting the GUI, based on the map of platforms and their names, the map of agents
	 * and their names (agents are managed by {@link AgentManager} wrappers and the timeline.
	 * 
	 * @param allPlatforms
	 *            - the {@link Map} of platform names and {@link PlatformLoader} instances that are currently started.
	 * @param allContainers
	 *            - the map of container names and information whether the container is created locally or remotely.
	 * @param platformContainersAgents
	 *            - for each platform, the platform name and the list of names and {@link AgentManager} wrappers of
	 *            agents that will be loaded on the platform.
	 * @param timeline
	 *            - the timeline of events, as {@link XMLNode} parsed from the scenario file.
	 */
	public SimulationManager(Map<String, PlatformLoader> allPlatforms, Map<String, Boolean> allContainers,
			Map<String, Map<String, Map<String, AgentManager>>> platformContainersAgents, XMLNode timeline)
	{
		log = (UnitComponentExt) new UnitComponentExt().setUnitName("simulation");
		platforms = allPlatforms;
		containers = allContainers;
		agents = platformContainersAgents;
		if(timeline != null)
			events = timeline.getNodes();
		else
			events = Collections.emptyList();
	}
	
	@Override
	public boolean start()
	{
		try
		{
			AgentGuiConfig config = new AgentGuiConfig().setWindowType(WINDOW_TYPE).setWindowName(WINDOW_NAME);
			gui = (AgentGui) PlatformUtils.loadClassInstance(this, PlatformUtils.getSimulationGuiClass(), config);
		} catch(Exception e)
		{
			log.error("Unable to create simulation GUI. Simulation stops here." + PlatformUtils.printException(e));
			return false;
		}
		log.info("Simulation Manager started.");
		
		if(!setupGui())
			return false;
		
		return true;
	}
	
	@Override
	public boolean stop()
	{
		// TODO
		return false;
	}
	
	/**
	 * Sets up the functions of the buttons together with functionality related to simulation time.
	 * 
	 * @return <code>true</code> if setup is successfull.
	 */
	protected boolean setupGui()
	{
		gui.connectInput(SimulationComponent.CREATE.toString(), new InputListener() {
			@Override
			public void receiveInput(String componentName, Vector<Object> arguments)
			{
				createAgents();
			}
		});
		gui.connectInput(SimulationComponent.CLEAR.toString(), new InputListener() {
			@Override
			public void receiveInput(String componentName, Vector<Object> arguments)
			{
				// TODO send exit event
			}
		});
		gui.connectInput(SimulationComponent.EXIT.toString(), new InputListener() {
			@Override
			public void receiveInput(String componentName, Vector<Object> arguments)
			{
				// TODO do exit
				PlatformUtils.systemExit(0);
			}
		});
		
		if(events.isEmpty())
		{
			gui.doOutput(SimulationComponent.START.toString(), PlatformUtils.toVector((Object) null));
			gui.doOutput(SimulationComponent.PAUSE.toString(), PlatformUtils.toVector((Object) null));
			gui.doOutput(SimulationComponent.TIME.toString(), PlatformUtils.toVector("no events"));
		}
		else
		{
			gui.connectInput(SimulationComponent.START.toString(), new InputListener() {
				@Override
				public void receiveInput(String componentName, Vector<Object> arguments)
				{
					if(!agentsCreated)
						createAgents();
					if(!events.isEmpty())
					{
						log.info("starting simulation. next event at "
								+ (Integer.parseInt(events.get(0).getAttributeValue("time"))));
						startTimers();
					}
				}
			});
			
			gui.connectInput(SimulationComponent.PAUSE.toString(), new InputListener() {
				@Override
				public void receiveInput(String componentName, Vector<Object> arguments)
				{
					if(events.isEmpty())
					{
						gui.doOutput(SimulationComponent.TIME.toString(), PlatformUtils.toVector("no more events"));
						
						if(!isPaused)
						{
							theTime.cancel();
						}
					}
					else if(isPaused)
					{
						log.info("simulation restarting, next event in "
								+ (Integer.parseInt(events.get(0).getAttributeValue("time")) - time * 100));
						startTimers();
					}
					else
					{
						theTime.cancel();
						log.info("simulation stopped at " + time * 100 + ", next event was in "
								+ (Integer.parseInt(events.get(0).getAttributeValue("time")) - time * 100));
					}
					isPaused = !isPaused;
				}
			});
		}
		return true;
	}
	
	/**
	 * Starts the timers associated with the displayed time and the time to the next event.
	 */
	protected void startTimers()
	{
		theTime = new Timer();
		theTime.schedule(new TimerTask() {
			@Override
			public void run()
			{
				time++;
				
				String display = "___" + (int) (time / 600) + ":" + (int) ((time % 600) / 10) + "." + (time % 10)
						+ "___";
				gui.doOutput(SimulationComponent.TIME.toString(),
						new Vector<Object>(Arrays.asList(new Object[] { display })));
				
				int nextEvent = (events.isEmpty() ? 0 : Integer.parseInt(events.get(0).getAttributeValue(
						EVENT_TIME_ATTRIBUTE)));
				while(!events.isEmpty() && (nextEvent <= time * 100))
				{ // there is an event to do
					XMLNode event = events.remove(0);
					log.trace("processing new event");
					
					for(XMLNode task : event.getNodes())
					{
						log.info("task: " + task.getName());
					}
					
					nextEvent = (events.isEmpty() ? 0 : Integer.parseInt(events.get(0).getAttributeValue(
							EVENT_TIME_ATTRIBUTE)));
				}
				if(!events.isEmpty())
					log.info("next event at " + nextEvent);
				else
				{
					log.info("no more events");
					gui.doOutput(SimulationComponent.START.toString(), PlatformUtils.toVector((Object) null));
					gui.doOutput(SimulationComponent.PAUSE.toString(), PlatformUtils.toVector((Object) null));
					gui.doOutput(SimulationComponent.TIME.toString(), PlatformUtils.toVector("no more events"));
					theTime.cancel();
				}
			}
		}, 0, 100);
	}
	
	/**
	 * Creates all agents in the simulation.
	 */
	protected void createAgents()
	{
		// load agents on their respective platforms
		Map<String, AgentManager> allAgents = new HashMap<String, AgentManager>();
		for(Entry<String, Map<String, Map<String, AgentManager>>> platformEntry : agents.entrySet())
		{
			// TODO checks
			PlatformLoader platform = platforms.get(platformEntry.getKey());
			for(Map.Entry<String, Map<String, AgentManager>> containerEntry : platformEntry.getValue().entrySet())
			{
				String containerName = containerEntry.getKey();
				boolean localContainer = containers.get(containerName).booleanValue();
				if(localContainer)
					for(Map.Entry<String, AgentManager> agentEntry : containerEntry.getValue().entrySet())
					{
						String agentName = agentEntry.getKey();
						AgentManager agent = agentEntry.getValue();
						if(platform.loadAgent(containerName, agent))
						{
							allAgents.put(agentName, agent);
						}
						else
							log.error("agent [" + agentName + "] failed to load on platform [" + platform.getName()
									+ "]");
					}
				else
					; // TODO agents in remote containers
			}
		}
		for(Entry<String, AgentManager> agent : allAgents.entrySet())
		{
			if(agent.getValue().start())
				log.info("agent [" + agent.getKey() + "] started.");
			else
				log.error("agent [" + agent.getKey() + "] failed to start properly.");
		}
	}
	
	@Override
	public boolean setPlatformLink()
	{
		return false;
	}
}
