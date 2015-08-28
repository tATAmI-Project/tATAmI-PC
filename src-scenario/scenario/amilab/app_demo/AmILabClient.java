package scenario.amilab.app_demo;

import net.xqhs.util.XML.XMLTree.XMLNode;
import net.xqhs.util.logging.Logger;
import tatami.amilab.AmILabComponent;
import tatami.amilab.AmILabComponent.AmILabDataType;
import tatami.amilab.Perception;
import tatami.core.agent.AgentComponent;
import tatami.core.agent.AgentEvent;
import tatami.core.agent.AgentEvent.AgentEventHandler;
import tatami.core.agent.messaging.MessagingComponent;

/**
 * 
 * @author Claudiu-Mihai Toma
 *
 */
public class AmILabClient extends AgentComponent
{

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = -6787025838586743067L;

	/**
	 * Proximity request message.
	 */
	public static final String PROXIMITY_REQUEST = "PROXIMITY_REQUEST";

	/**
	 * Confirmation message. This agent is the closest one.
	 */
	public static final String CONFIRM = "CONFIRM";

	/**
	 * Decline message. This is not the closest agent.
	 */
	public static final String DECLINE = "DECLINE";

	/**
	 * AmILab path element.
	 */
	public static final String AMILAB_PATH_ELEMENT = "amilab";
	
	/**
	 * Client path element.
	 */
	public static final String CLIENT_PATH_ELEMENT = "client";
	
	/**
	 * Server path element.
	 */
	public static final String SERVER_PATH_ELEMENT = "server";
	
	/**
	 * Default constructor.
	 */
	public AmILabClient()
	{
		super(AgentComponentName.TESTING_COMPONENT);
	}

	/**
	 * State of the simulation.
	 */
	protected boolean simulationIsOn;

	/**
	 * State of this component. {@code true} if this component must do stuff.
	 */
	protected boolean active;

	/**
	 * Current distance to the subject.
	 */
	protected long proximity;

	/**
	 * Local {@link ProximityUpdater}.
	 */
	protected ProximityUpdater proximityUpdater;

	/**
	 * Support thread for the local {@link ProximityUpdater}.
	 */
	protected Thread proximityUpdaterThread;

	/**
	 * Reference to the {@link AmILabComponent}.
	 */
	protected AmILabComponent amilab;

	/**
	 * Runnable used to update the proximity.
	 * 
	 * @author Claudiu-Mihai Toma
	 *
	 */
	public class ProximityUpdater implements Runnable
	{
		/**
		 * The time used to reduce thread's CPU consumption.
		 */
		private static final int TIME_TO_SLEEP = 50;

		/**
		 * State of the current {@link ProximityUpdater}.
		 */
		private boolean alive;

		/**
		 * Default constructor. Sets the internal state.
		 */
		public ProximityUpdater()
		{
			alive = false;
		}

		@Override
		public void run()
		{
			Perception perception = null;

			alive = true;

			while (alive)
			{
				perception = getAmILabComponent().get(AmILabDataType.SKELETON);

				try
				{
					Thread.sleep(TIME_TO_SLEEP);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}

				long newProximity = processPerception(perception);

				if (newProximity < 0)
					continue;

				proximity = newProximity;
			}
		}

		/**
		 * Stops the {@link Thread}.
		 */
		public void stopThread()
		{
			alive = false;
		}

		/**
		 * Processes a skeleton {@link Perception} to obtain the current proximity.
		 * 
		 * @param perception
		 *            - {@link Perception} to be processed
		 * @return the value of the new proximity or {@code -1} if the perception is invalid
		 */
		private long processPerception(Perception perception)
		{
			// Perform checks and calculations.
			return -1;
		}
	}

	/**
	 * Getter for the {@link AmILabComponent}.
	 * 
	 * @return reference to the {@link AmILabComponent}
	 */
	public AmILabComponent getAmILabComponent()
	{
		return amilab;
	}

	/**
	 * Register a message handler for a client.
	 */
	protected void registerClientMessanger()
	{
		registerMessageReceiver(new AgentEventHandler()
		{
			@Override
			public void handleEvent(AgentEvent event)
			{
				String content = (String) event.getParameter(MessagingComponent.CONTENT_PARAMETER);

				getAgentLog().info("message with content [] received", content);

				if (content.equals(PROXIMITY_REQUEST))
				{
					sendReply(Long.toString(proximity), event);
				}
				if (content.equals(CONFIRM))
				{
					active = true;
				}
				if (content.equals(DECLINE))
				{
					active = false;
				}

			}
		}, AMILAB_PATH_ELEMENT, CLIENT_PATH_ELEMENT);
	}

	@Override
	protected boolean preload(ComponentCreationData parameters, XMLNode scenarioNode, Logger log)
	{
		if (!super.preload(parameters, scenarioNode, log))
			return false;

		active = false;
		proximity = Long.MAX_VALUE;
		amilab = (AmILabComponent) getAgentComponent(AgentComponentName.AMILAB_COMPONENT);
		proximityUpdater = new ProximityUpdater();
		proximityUpdaterThread = new Thread(proximityUpdater);

		return true;
	}

	@Override
	protected void atAgentStart(AgentEvent event)
	{
		super.atAgentStart(event);
		proximityUpdaterThread.start();

		registerClientMessanger();
	}

	@Override
	protected void atAgentStop(AgentEvent event)
	{
		super.atAgentStop(event);
		proximityUpdater.stopThread();
		simulationIsOn = false;
	}

	@Override
	protected void atSimulationStart(AgentEvent event)
	{
		super.atSimulationStart(event);

		while (simulationIsOn)
		{
			if (active)
			{
				System.out.println();
			} else
			{
				// stop doing stuff
			}
		}
	}

	@Override
	protected boolean sendReply(String content, AgentEvent replyTo)
	{
		return super.sendReply(content, replyTo);
	}

	@Override
	protected boolean sendMessageToEndpoint(String content, String sourceEndpoint, String targetEndpoint)
	{
		return super.sendMessageToEndpoint(content, sourceEndpoint, targetEndpoint);
	}

	@Override
	protected AgentComponent getAgentComponent(AgentComponentName name)
	{
		return super.getAgentComponent(name);
	}

	@Override
	protected void postAgentEvent(AgentEvent event)
	{
		super.postAgentEvent(event);
	}

	@Override
	protected Logger getAgentLog()
	{
		return super.getAgentLog();
	}
}
