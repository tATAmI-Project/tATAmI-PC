/*******************************************************************************
 * Copyright (C) 2015 Andrei Olaru, Marius-Tudor Benea, Nguyen Thi Thuy Nga, Amal El Fallah Seghrouchni, Cedric Herpson.
 * 
 * This file is part of tATAmI-PC.
 * 
 * tATAmI-PC is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * tATAmI-PC is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with tATAmI-PC.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package tatami.simulation;

import java.util.HashMap;
import java.util.Iterator;
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
import net.xqhs.windowLayout.WindowLayout;
import tatami.HMI.pub.HMIInterface;
import tatami.HMI.src.PC.AgentGui;
import tatami.core.agent.AgentComponent.AgentComponentName;
import tatami.core.agent.AgentEvent.AgentEventType;
import tatami.core.agent.io.AgentActiveIO;
import tatami.core.agent.io.AgentActiveIO.InputListener;
import tatami.core.agent.messaging.MessagingComponent;
import tatami.core.util.platformUtils.PlatformUtils;
import tatami.simulation.simulation_manager_builders.ISimulationManagerBuilder;

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
public class SimulationManager implements InputListener
{
	

	/**
	 * States of the simulation
	 * 
	 * @author Andrei Olaru
	 */
	public enum SimulationComponent {
		STEADY,

		RUNNING,
	}
	
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
	 * Name of the node in the scenario file that contains the event timeline to simulate.
	 */
	public static final String		TIMELINE_NODE					= "timeline";
	/**
	 * Delay before calling a System exit in case of a failed start.
	 */
	protected static final int			SYSTEM_ABORT_DELAY				= 1000;
	/**
	 * If set, a {@link System#exit(int)} will be called after everything has been theoretically closed (in the case of
	 * normal termination).
	 * <p>
	 * Ideally, this should be set to <code>false</code> and all the threads should exit normally.
	 */
	protected static final boolean		FORCE_SYSTEM_EXIT				= true;
	/**
	 * The log.
	 */
	UnitComponentExt					log								= null;
	
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
	
	AgentActiveIO hmi;
	
	ISimulationManagerBuilder mBuilder;
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
	/**
	 * public SimulationManager(Map<String, PlatformLoader> allPlatforms, Map<String, Boolean> allContainers,
            Set<AgentCreationData> allAgents, XMLNode timeline)
	 * @param allPlatforms
	 * @param allContainers
	 * @param allAgents
	 * @param timeline
	 */
	public SimulationManager(ISimulationManagerBuilder builder)
	{
		log = (UnitComponentExt) new UnitComponentExt().setUnitName("simulation").setLoggerType(
				PlatformUtils.platformLogType());
		mBuilder = builder;
		hmi = HMIInterface.INST.getHMI();
		hmi.connectInput("Simulation Manager", this);
		platforms = builder.getPlatform();
		containers = builder.getAllContainers();
		agents = builder.getAllAgents();
		/*
		if(builder.getTimeline() != null)
			events = builder.getTimeline().getNodes();
		else
			events = Collections.emptyList();
			*/
		// TODO: add agent graph and corresponding representation
	}
	
	/**
     * The method starts the platforms specified in the first parameter and adds to each platform the containers
     * corresponding to it, as indicated by the second parameter.
     * 
     * @param platforms
     *            - the {@link Map} of platform names and respective {@link PlatformLoader} instances.
     * @param platformContainers
     *            - the {@link Map} containing platform name &rarr; {@link Set} of the names of the containers to add to
     *            the platform.
     * @return the number of platforms successfully started.
     */
    protected int startPlatforms(Map<String, PlatformLoader> platforms, Map<String, Set<String>> platformContainers)
    {
        int platformsOK = 0;
        for(Iterator<PlatformLoader> itP = platforms.values().iterator(); itP.hasNext();)
        {
            PlatformLoader platform = itP.next();
            String platformName = platform.getName();
            if(!platform.start())
            {
                log.error("Platform [" + platformName + "] failed to start.");
                itP.remove();
                continue;
            }
            log.info("Platform [" + platformName + "] started.");
            platformsOK++;
        }
        return platformsOK;
    }
    
	
	public boolean start()
	{
		return startSystem();
	}
	
	/**
	 * Starts the whole agent system.
	 * 
	 * @return <code>true</code> in case of success.
	 */
	public boolean startSystem()
	{
		log.info("Simulation Manager started.");
		Map<String, Set<String>> platformContainers = mBuilder.getPlatformContainers();
		
		/*
		if (startPlatforms(platforms, platformContainers) <= 0) {
		    log.error("Simulation start failed.");
            for (PlatformLoader platform : platforms.values())
                if (!platform.stop())
                    log.error("Stopping platform [" + platform.getName() + "] failed");
		}
		*/
		
		
		// starts an agent on each platform
		if(!startSimulationAgents())
		{
			fullstop();
			return false;
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
					//gui.doOutput(SimulationComponent.START.toString(), PlatformUtils.toVector((Object) null));
					//gui.doOutput(SimulationComponent.PAUSE.toString(), PlatformUtils.toVector((Object) null));
					//gui.doOutput(SimulationComponent.TIME.toString(), PlatformUtils.toVector("no more events"));
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
	    /*
	    
		for(PlatformLoader platform : platforms.values()){
			String platformName = platform.getName();
			MessagingComponent msg = null;
			try
			{
				String msgrClass = platform.getRecommendedComponentClass(AgentComponentName.MESSAGING_COMPONENT);
				if(msgrClass == null)
					msgrClass = AgentComponentName.MESSAGING_COMPONENT.getClassName();
				msg = (MessagingComponent) PlatformUtils.loadClassInstance(this, msgrClass, new Object[0]);
			}
			catch(Exception e){
				log.error("Failed to create a messaging component for the simulation agent on platform []: []",
						platformName, PlatformUtils.printException(e));
			}
			if(msg != null){
				SimulationLinkAgent agent = new SimulationLinkAgent(SIMULATION_AGENT_NAME_PREFIX + platformName, msg);
				if(!platform.loadAgent("Neutral", agent)){
					log.error("Loading simulation agent on platform [" + platformName
							+ "] failed. Simulation cannot start.");
					agent.stop();
					return false;
				}
				if(!agent.start()){
					log.error("Starting simulation agent on platform [" + platformName
							+ "] failed. Simulation cannot start.");
					agent.stop();
					return false;
				}
				simulationAgents.put(platformName, agent);
			}
		}*/
		
		return true;
	}
	
	/**
	 * Creates all agents in the simulation.
	 */
	protected void createAgents()
	{
	    /*
		agentsCreated = true;
		
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
            AgentLoader loader = agentData.getAgentLoader();
            AgentManager manager = loader.load(agentData);
            if (manager != null)
                //if (platform.loadAgent(containerName, manager))
                    agentManagers.put(agentName, manager);
                //else
                 //   log.error("agent [" + agentName + "] failed to load on platform [" + platform.getName() + "]");
            else
                log.error("agent [" + agentName + "] failed to load");
			// TODO else: agents in remote containers
		}
		for(Entry<String, AgentManager> agent : agentManagers.entrySet())
		{
			if(agent.getValue().start())
				log.info("agent [" + agent.getKey() + "] started.");
			else
				log.error("agent [" + agent.getKey() + "] failed to start properly.");
		}
		
		// FIXME add await timeout
		// TODO what about agents on other machines?
		boolean stillStarting = true;
		while(stillStarting)
		{
			stillStarting = false;
			for(AgentManager agent : agentManagers.values())
			{
				if(!agent.isRunning())
					stillStarting = true;
			}
			try
			{
				Thread.sleep(50); // FIXME constant timeout in class
			} catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		// agents started
		for(String platformName : platforms.keySet())
		{
			SimulationLinkAgent simAgent = simulationAgents.get(platformName);
			if(simAgent != null)
				for(AgentCreationData agentData : agents)
					if(agentData.getPlatform().equals(platformName))
						simAgent.enrol(agentData);
		}
		*/
	}
	
	/**
	 * Broadcasts the specified event to all agents, via the simulation agents in the respective platforms.
	 * 
	 * @param event
	 *            - the event to broadcast.
	 */
	protected void signalAllAgents(AgentEventType event)
	{
		for(String platformName : platforms.keySet())
			if(simulationAgents.containsKey(platformName))
				simulationAgents.get(platformName).broadcast(event);
	}
	
	public boolean stop()
	{
		return stopSystem();
	}
	
	/**
	 * Stops the entire system.
	 * 
	 * @return <code>true</code> in case of success.
	 */
	public boolean stopSystem()
	{
		if(theTime != null)
			theTime.cancel();
		for(SimulationLinkAgent simAgent : simulationAgents.values())
			if(!simAgent.stop())
				log.error("Stopping agent [] failed.", simAgent.getAgentName());
		for(String platformName : platforms.keySet())
			if(!platforms.get(platformName).stop())
				log.error("Stopping platform [] failed.", platformName);
		//if(gui != null)
		//	gui.close();
		if(WindowLayout.staticLayout != null)
			WindowLayout.staticLayout.doexit();
		if(log != null)
			log.doExit();
		return true;
	}
	
	/**
	 * Stops the simulation manager and also exists the application.
	 * <p>
	 * This method should only be called in case of a failed start.
	 * <p>
	 * The system exit is delayed with {@value #SYSTEM_ABORT_DELAY}.
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
		}, SYSTEM_ABORT_DELAY);
		return result;
	}
	
	/**
	 * As this class implements {@link AgentManager} only for convenience (abusing), it is not expected to be linked to
	 * a platform "above" it, therefore the method will have no effect and always fail.
	 */
	/*
	@Override
	public boolean setPlatformLink(PlatformLink link)
	{
		return false;
	}*/
	
	/**
	 * As this class implements {@link AgentManager} only for convenience (abusing), one can consider it is always
	 * running.
	 */
	public boolean isRunning()
	{
		// TODO check if this makes sense
		return true;
	}
	
	public boolean isStopped()
	{
		// TODO check if this makes sense
		return false;
	}

	/**
	 * Input from the interface
	 */
    @Override
    public void receiveInput(String portName, Vector<Object> arguments) {
        if(portName.equals("GUI-START-PLATFORM")){
            if(platforms.containsKey(arguments.get(0).toString())){
                platforms.get(arguments.get(0).toString()).start();
            }
        }
        
        if(portName.equals("GUI-START-SIMULATION")){
            startSystem();
        }
    }
}
