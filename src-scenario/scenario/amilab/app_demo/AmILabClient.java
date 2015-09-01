package scenario.amilab.app_demo;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.codehaus.jackson.map.ObjectMapper;

import net.xqhs.util.XML.XMLTree.XMLNode;
import net.xqhs.util.logging.Logger;
import scenario.amilab.app_demo.PC.AmILabGui;
import tatami.amilab.AmILabComponent;
import tatami.amilab.AmILabComponent.AmILabDataType;
import tatami.amilab.Perception;
import tatami.core.agent.AgentComponent;
import tatami.core.agent.AgentEvent;
import tatami.core.agent.AgentEvent.AgentEventHandler;
import tatami.core.agent.messaging.MessagingComponent;
import tatami.core.agent.visualization.AgentGui;
import tatami.core.agent.visualization.VisualizableComponent;

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
	 * A happy face XD
	 */
	public static final String HAPPY_FACE = "images/happy_face.png";

	/**
	 * A sad face :(
	 */
	// public static final String SAD_FACE = "images/sad_face.ico";
	// public static final String SAD_FACE = "images/sad_face.jpeg";
	// public static final String SAD_FACE = "images/sad_face.png";
	public static final String SAD_FACE = "images/sad_face.jpg";

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
	 * The {@link AgentGui} from the {@link VisualizableComponent}.
	 */
	protected AmILabGui gui;

	/**
	 * Main label.
	 */
	protected JLabel label;

	/**
	 * Image when active.
	 */
	protected ImageIcon onIcon;

	/**
	 * Image when inactive.
	 */
	protected ImageIcon offIcon;

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

				getAgentLog().info("Client received []", content);

				if (content.equals(PROXIMITY_REQUEST))
				{
					sendReply(Long.toString(proximity), event);
					getAgentLog().info("Client sent []", Long.toString(proximity));
					return;
				}
				if (content.equals(CONFIRM))
				{
					active = true;
					return;
				}
				if (content.equals(DECLINE))
				{
					active = false;
					return;
				}

			}
		}, AMILAB_PATH_ELEMENT, CLIENT_PATH_ELEMENT);
	}

	/**
	 * Gets the visualizable component of this agent.
	 * 
	 * @return the visualizable component
	 */
	protected VisualizableComponent getVisualizable()
	{
		return (VisualizableComponent) getAgentComponent(AgentComponentName.VISUALIZABLE_COMPONENT);
	}

	/**
	 * Gets the gui.
	 * 
	 * @return the gui
	 */
	protected AmILabGui getGui()
	{
		return (AmILabGui) getVisualizable().getGUI();
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected ImageIcon createImageIcon(String path, String description)
	{
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null)
		{
			return new ImageIcon(imgURL, description);
		} else
		{
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	/**
	 * Scales an {@link Image} based on given parameters.
	 * 
	 * @param srcImg
	 *            - image to be scaled
	 * @param width
	 *            - required width
	 * @param height
	 *            - required height
	 * @return scaled {@link Image}
	 */
	public static Image getScaledImage(Image srcImg, int width, int height)
	{
		BufferedImage resizedImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = resizedImg.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(srcImg, 0, 0, width, height, null);
		g2.dispose();
		return resizedImg;
	}

	@Override
	protected boolean preload(ComponentCreationData parameters, XMLNode scenarioNode, List<String> agentPackages, Logger log)
	{
		if (!super.preload(parameters, scenarioNode, agentPackages, log))
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

		int resize = 300;
		onIcon =  new ImageIcon(getScaledImage(createImageIcon(HAPPY_FACE, HAPPY_FACE).getImage(), resize, resize));
		offIcon =  new ImageIcon(getScaledImage(createImageIcon(SAD_FACE, SAD_FACE).getImage(), resize, resize));

		proximityUpdaterThread = new Thread(proximityUpdater);

		clientRunnable = new StoppableRunnable()
		{
			/**
			 * The time between feedbacks.
			 */
			private static final int TIME_TO_SLEEP = 500;

			@Override
			public void run()
			{
				while (!stopFlag)
				{
					if (active)
					{
						// getAgentLog().info("I'm active and I have proximity []", Long.toString(proximity));
						label.setIcon(onIcon);
					} else
					{
						// getAgentLog().info("I'm inactive and I have proximity []", Long.toString(proximity));
						label.setIcon(offIcon);
					}

					try
					{
						Thread.sleep(TIME_TO_SLEEP);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
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
		gui = getGui();
		label = gui.getLabel();

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
