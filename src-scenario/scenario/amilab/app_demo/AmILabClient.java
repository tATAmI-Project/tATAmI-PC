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

				// Perform checks and calculations.
				// proximity = newProximity;
			}
		}

		/**
		 * Stops the {@link Thread}.
		 */
		public void stopThread()
		{
			alive = false;
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
	 * Register a message handler.
	 */
	protected void registerMessanger()
	{
		registerMessageReceiver(new AgentEventHandler()
		{
			@Override
			public void handleEvent(AgentEvent event)
			{
				// receive
				String content = (String) event.getParameter(MessagingComponent.CONTENT_PARAMETER);
				String sender = (String) event.getParameter(MessagingComponent.SOURCE_PARAMETER);
				String receiver = (String) event.getParameter(MessagingComponent.DESTINATION_PARAMETER);

				// reply
				// sendMessage("reply", receiver, sender);
			}
		}, "application", "amilab");
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
	}

	@Override
	protected void atAgentStop(AgentEvent event)
	{
		super.atAgentStop(event);
		proximityUpdater.stopThread();
	}

	@Override
	protected void atSimulationStart(AgentEvent event)
	{
		super.atSimulationStart(event);

		registerMessanger();

		while (simulationIsOn)
		{
			if (active)
			{
				// do stuff if it wasn't already doing it
			} else
			{
				// stop doing stuff
			}
		}
	}

}
