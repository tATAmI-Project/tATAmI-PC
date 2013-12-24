package tatami.simulation;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import net.xqhs.util.XML.XMLTree.XMLNode;
import net.xqhs.util.logging.UnitComponentExt;
import tatami.core.agent.visualization.AgentGui;
import tatami.core.agent.visualization.AgentGui.AgentGuiBackgroundTask;
import tatami.core.agent.visualization.AgentGui.InputListener;
import tatami.core.agent.visualization.AgentGui.ResultNotificationListener;
import tatami.core.agent.visualization.AgentGuiConfig;
import tatami.core.util.platformUtils.PlatformUtils;
import tatami.pc.util.windowLayout.WindowLayout;

/**
 * Singleton class managing the simulation, visualization and agent control on a machine or on a set of machines
 * (possibly all).
 * <p>
 * After the initializations in {@link Boot}, it handles the actual starting and management of agents, as well as
 * creating the events in the scenario timeline.
 * <p>
 * It receives updates from all agents regarding logging messages and parent and location changes.
 * <p>
 * The link with agents uses the agent's VisualizationComponent (or equivalent for non-CompositeAgent instances) and
 * corresponding Vocabulary.
 * <p>
 * It normally offers a GUI or some other kind of UI for the said operations, that can exist outside of the actual agent
 * platform(s).
 * <p>
 * Although not an agent of any platform, the {@link SimulationManager} can be viewed as an agent; for convenience, it
 * implements {@link AgentManager} and it features a GUI based on {@link AgentGui}.
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
	public static final String			WINDOW_TYPE						= "system";
	/**
	 * Window name for the simulation manager window.
	 */
	public static final String			WINDOW_NAME						= "simulation";
	/**
	 * The name of the attribute indicating the time of the event.
	 */
	protected static final String		EVENT_TIME_ATTRIBUTE			= "time";
	/**
	 * The prefix to the name of a simulation agent. The rest of the name will be the name of the platform onto which it
	 * resides.
	 */
	protected static final String		SIMULATION_AGENT_NAME_PREFIX	= "SimAgent-";
	/**
	 * The log.
	 */
	UnitComponentExt					log								= null;
	/**
	 * The GUI.
	 */
	AgentGui							gui								= null;
	
	/**
	 * Name and {@link PlatformLoader} for all platforms to be started.
	 */
	Map<String, PlatformLoader>			platforms;
	/**
	 * Name and locality indication (container is created locally or remotely) for all containers.
	 */
	protected Map<String, Boolean>		containers						= null;
	/**
	 * {@link AgentCreationData} instances for all agents to be started.
	 */
	Set<AgentCreationData>				agents;
	/**
	 * A map that holds for each platform (identified by name) a simulation agent.
	 */
	Map<String, SimulationLinkAgent>	simulationAgents				= new HashMap<String, SimulationLinkAgent>();
	/**
	 * The list of events in the simulation, as specified by the scenario file.
	 */
	List<XMLNode>						events							= new LinkedList<XMLNode>();
	/**
	 * Current time, in 1/10 seconds.
	 */
	long								time							= 0;
	/**
	 * Indicates whether the simulation is currently paused.
	 */
	boolean								isPaused						= false;
	/**
	 * Indicates whether agents have been created.
	 */
	boolean								agentsCreated					= false;
	/**
	 * The {@link Timer} for simulation time and also the display in the GUI.
	 */
	Timer								theTime							= null;
	
	/**
	 * Creates a new instance, also starting the GUI, based on the map of platforms and their names, the map of agents
	 * and their names (agents are managed by {@link AgentManager} wrappers and the timeline.
	 * 
	 * @param allPlatforms
	 *            - the {@link Map} of platform names and {@link PlatformLoader} instances that are currently started.
	 * @param allContainers
	 *            - the map of container names and information whether the container is created locally or remotely.
	 * @param allAgents
	 *            - a {@link Set} of {@link AgentCreationData} instances, describing all agents to be loaded.
	 * @param timeline
	 *            - the timeline of events, as {@link XMLNode} parsed from the scenario file.
	 */
	public SimulationManager(Map<String, PlatformLoader> allPlatforms, Map<String, Boolean> allContainers,
			Set<AgentCreationData> allAgents, XMLNode timeline)
	{
		log = (UnitComponentExt) new UnitComponentExt().setUnitName("simulation");
		platforms = allPlatforms;
		containers = allContainers;
		agents = allAgents;
		if(timeline != null)
			events = timeline.getNodes();
		else
			events = Collections.emptyList();
		// TODO: add agent graph and corresponding representation
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
		{
			fullstop();
			return false;
		}
		
		// starts an agent on each platform
		if(!startSimulationAgents())
		{
			fullstop();
			return false;
		}
		
		return true;
	}
	
	/**
	 * Stops the simulation manager and also exists the application.
	 * 
	 * @return
	 */
	protected boolean fullstop()
	{
		boolean result = stop();
		new Timer().schedule(new TimerTask() {
			@Override
			public void run()
			{
				PlatformUtils.systemExit(0);
			}
		}, 1000);
		return result;
	}
	
	@Override
	public boolean stop()
	{
		if(theTime != null)
			theTime.cancel();
		for(SimulationLinkAgent simAgent : simulationAgents.values())
			if(!simAgent.stop())
				log.error("Stopping agent [" + simAgent.getAgentName() + "] failed.");
		if(gui != null)
			gui.close();
		if(WindowLayout.staticLayout != null)
			WindowLayout.staticLayout.doexit();
		if(log != null)
			log.doExit();
		return true;
	}
	
	/**
	 * Sets up the functions of the buttons together with functionality related to simulation time.
	 * 
	 * @return <code>true</code> if setup is successful.
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
				gui.background(new AgentGuiBackgroundTask() {
					@Override
					public void execute(Object arg, ResultNotificationListener resultListener)
					{
						stop();
						resultListener.receiveResult(null);
					}
				}, null, new ResultNotificationListener() {
					@Override
					public void receiveResult(Object result)
					{
						PlatformUtils.systemExit(0);
					}
				});
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
	 * Starts the {@link SimulationLinkAgent} instances for all platforms.
	 * 
	 * @return <code>true</code> if the operation succeeded; <code>false</code> otherwise.
	 */
	protected boolean startSimulationAgents()
	{
		for(PlatformLoader platform : platforms.values())
		{
			String platformName = platform.getName();
			SimulationLinkAgent agent = new SimulationLinkAgent(SIMULATION_AGENT_NAME_PREFIX + platformName);
			if(!platform.loadAgent(null, agent))
			{
				log.error("Loading simulation agent on platform [" + platformName
						+ "] failed. Simulation cannot start.");
				agent.stop();
				return false;
			}
			if(!agent.start())
			{
				log.error("Starting simulation agent on platform [" + platformName
						+ "] failed. Simulation cannot start.");
				agent.stop();
				return false;
			}
			simulationAgents.put(platformName, agent);
		}
		return true;
	}
	
	/**
	 * Creates all agents in the simulation.
	 */
	protected void createAgents()
	{
		// load agents on their respective platforms
		Map<String, AgentManager> agentManagers = new HashMap<String, AgentManager>();
		for(AgentCreationData agentData : agents)
		{
			String agentName = agentData.getAgentName();
			if(!platforms.containsKey(agentData.getPlatform()))
			{
				log.error("Platform [" + agentData.getPlatform() + "] for agent [" + agentName + "] not found.");
				continue;
			}
			PlatformLoader platform = platforms.get(agentData.getPlatform());
			String containerName = agentData.getDestinationContainer();
			boolean localContainer = !agentData.isRemote();
			if(localContainer)
			{
				AgentLoader loader = agentData.getAgentLoader();
				AgentManager manager = loader.load(agentData);
				if(manager != null)
					if(platform.loadAgent(containerName, manager))
						agentManagers.put(agentName, manager);
					else
						log.error("agent [" + agentName + "] failed to load on platform [" + platform.getName() + "]");
				else
					log.error("agent [" + agentName + "] failed to load");
			}
			// TODO else: agents in remote containers
		}
		for(Entry<String, AgentManager> agent : agentManagers.entrySet())
		{
			if(agent.getValue().start())
				log.info("agent [" + agent.getKey() + "] started.");
			else
				log.error("agent [" + agent.getKey() + "] failed to start properly.");
		}
	}
	
	/**
	 * As this class implements {@link AgentManager} only for convenience (abusing), it is not expected to be linked to
	 * a platform "above" it, therefore the method will have no effect and always fail.
	 */
	@Override
	public boolean setPlatformLink()
	{
		return false;
	}
	
	/**
	 * As this class implements {@link AgentManager} only for convenience (abusing), it does not have an agent name.
	 */
	@Override
	public String getAgentName()
	{
		return null;
	}
}
