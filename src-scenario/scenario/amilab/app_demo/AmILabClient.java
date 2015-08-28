package scenario.amilab.app_demo;

import java.util.HashMap;

import org.codehaus.jackson.map.ObjectMapper;

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
	 * Updates the proximity.
	 */
	protected StoppableRunnable proximityUpdater;

	/**
	 * Support thread for the local {@link ProximityUpdater}.
	 */
	protected Thread proximityUpdaterThread;

	/**
	 * Reference to the {@link AmILabComponent}.
	 */
	protected AmILabComponent amilab;

	/**
	 * Runnable that does all the logic
	 */
	protected StoppableRunnable clientRunnable;

	/**
	 * Thread that runs the server runnable.
	 */
	protected Thread clientThread;

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

				getAgentLog().info("Client received message with content [] received", content);

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

		proximityUpdater = new StoppableRunnable()
		{
			/**
			 * The time used to reduce thread's CPU consumption.
			 */
			private static final int TIME_TO_SLEEP = 50;

			/**
			 * How many decimals to keep.
			 */
			private static final int DOUBLE_TO_LONG = 100;

			@Override
			public void run()
			{
				Perception perception = null;

				while (!stopFlag)
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
			 * Processes a skeleton {@link Perception} to obtain the current proximity.
			 * 
			 * @param perception
			 *            - {@link Perception} to be processed
			 * @return the value of the new proximity or {@code -1} if the perception is invalid
			 */
			private long processPerception(Perception perception)
			{
				// Perform checks and calculations.
				HashMap<?, ?> parsedJson = null;
				try
				{
					parsedJson = new ObjectMapper().readValue(perception.getData(), HashMap.class);
					HashMap<?, ?> skeleton = (HashMap<?, ?>) parsedJson.get("skeleton_3D");
					HashMap<?, ?> torso = (HashMap<?, ?>) skeleton.get("torso");
					double newProximity = ((Double) torso.get("Z")).doubleValue();

					newProximity *= DOUBLE_TO_LONG;

					return (long) newProximity;

				} catch (Exception e)
				{
					e.printStackTrace();
					return -1;
				}
			}

		};

		proximityUpdaterThread = new Thread(proximityUpdater);

		clientRunnable = new StoppableRunnable()
		{
			@Override
			public void run()
			{
				while (!stopFlag)
				{
					if (active)
					{
						getAgentLog().info("I'm active and I have proximity []", Long.toString(proximity));
					} else
					{
						// stop doing stuff
					}
				}
			}
		};

		clientThread = new Thread(clientRunnable);

		return true;
	}

	@Override
	protected void atSimulationStart(AgentEvent event)
	{
		super.atSimulationStart(event);

		amilab = (AmILabComponent) getAgentComponent(AgentComponentName.AMILAB_COMPONENT);
		
		registerClientMessanger();
		proximityUpdaterThread.start();
		clientThread.start();
	}

	@Override
	protected void atAgentStop(AgentEvent event)
	{
		super.atAgentStop(event);

		proximityUpdater.stop();
		clientRunnable.stop();
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
