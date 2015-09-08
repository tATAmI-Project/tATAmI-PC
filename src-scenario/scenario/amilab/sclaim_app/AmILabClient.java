package scenario.amilab.sclaim_app;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.codehaus.jackson.map.ObjectMapper;

import net.xqhs.util.XML.XMLTree.XMLNode;
import net.xqhs.util.logging.Logger;
import scenario.amilab.sclaim_app.PC.AmILabGui;
import scenario.amilab.utils.StoppableRunnable;
import tatami.amilab.AmILabComponent;
import tatami.amilab.Perception;
import tatami.core.agent.AgentEvent;
import tatami.core.agent.io.AgentIO;
import tatami.core.agent.visualization.AgentGui;
import tatami.core.agent.visualization.VisualizableComponent;

/**
 * 
 * @author Claudiu-Mihai Toma
 *
 */
public class AmILabClient extends AmILabComponent implements AgentIO
{

	/**
	 * The serial UID.
	 */
	private static final long serialVersionUID = -7636346419364318356L;

	/**
	 * Name of the parameter for the daq to observe.
	 */
	private static final String SENSOR = "sensor";

	/**
	 * Proximity request message.
	 */
	public static final String PROXIMITY_REQUEST = "request";

	/**
	 * Get proximity message.
	 */
	public static final String GET_PROXIMITY = "getProximity";

	/**
	 * Numerical value for any invalid proximity.
	 */
	public static final long INVALID_PROXIMITY = -1;

	/**
	 * Max value for proximity.
	 */
	public static final long MAX_PROXIMITY = 999999;

	/**
	 * Confirmation message. This agent is the closest one.
	 */
	public static final String CONFIRM = "confirm";

	/**
	 * Decline message. This is not the closest agent.
	 */
	public static final String DECLINE = "decline";

	/**
	 * Update status message.
	 */
	public static final String UPDATE_STATUS = "updateStatus";

	/**
	 * A happy face XD
	 */
	public static final String HAPPY_FACE = "images/happy_face.png";

	/**
	 * A sad face :(
	 */
	public static final String SAD_FACE = "images/sad_face.jpg";

	/**
	 * Name of the data acquisition unit this client monitors.
	 */
	protected String daq;

	/**
	 * State of the simulation.
	 */
	protected boolean simulationIsOn;

	/**
	 * Status of this component. {@code true} if this component has the smallest proximity.
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
	 * Runnable that does all the logic
	 */
	protected StoppableRunnable clientRunnable;

	/**
	 * Thread that runs the server runnable.
	 */
	protected Thread clientThread;

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

	/**
	 * Returns an ImageIcon, or null if the path was invalid.
	 * 
	 * @param path
	 *            - path to the image
	 * @param description
	 *            - a description
	 * @return the image icon
	 */
	protected ImageIcon createImageIcon(String path, String description)
	{
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null)
		{
			return new ImageIcon(imgURL, description);
		}
		System.err.println("Couldn't find file: " + path);
		return null;
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
	protected boolean preload(ComponentCreationData parameters, XMLNode scenarioNode, List<String> agentPackages,
			Logger log)
	{
		if (!super.preload(parameters, scenarioNode, agentPackages, log))
			return false;

		daq = getComponentData().get(SENSOR);

		active = false;
		proximity = MAX_PROXIMITY;

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
					perception = get(AmILabDataType.SKELETON);

					try
					{
						Thread.sleep(TIME_TO_SLEEP);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}

					long newProximity = processPerception(perception);

					if (newProximity == INVALID_PROXIMITY)
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
					String crtDaq = (String) parsedJson.get("sensor_id");

					// If this entry is from another daq return the last known proximity.
					if (!crtDaq.equals(daq))
					{
						return INVALID_PROXIMITY;
					}

					HashMap<?, ?> skeleton = (HashMap<?, ?>) parsedJson.get("skeleton_3D");
					HashMap<?, ?> torso = (HashMap<?, ?>) skeleton.get("torso");
					double newProximity = ((Double) torso.get("Z")).doubleValue();

					newProximity *= DOUBLE_TO_LONG;

					return (long) newProximity;

				} catch (Exception e)
				{
					e.printStackTrace();
					return INVALID_PROXIMITY;
				}
			}

		};

		int resize = 300;
		onIcon = new ImageIcon(getScaledImage(createImageIcon(HAPPY_FACE, HAPPY_FACE).getImage(), resize, resize));
		offIcon = new ImageIcon(getScaledImage(createImageIcon(SAD_FACE, SAD_FACE).getImage(), resize, resize));

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

		gui = getGui();
		label = gui.getLabel();

		startInternalBuffer();
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
	public Vector<Object> getInput(String portName)
	{
		getAgentLog().trace("Getting proximity...");

		if (!portName.equals(GET_PROXIMITY))
			return null;
		Vector<Object> ret = new Vector<Object>();
		ret.addElement(new Long(proximity));

		getAgentLog().trace("Got proximity ", new Long(proximity));

		return ret;
	}

	@Override
	public void doOutput(String portName, Vector<Object> arguments)
	{
		if (!portName.equals(UPDATE_STATUS))
			return;

		String state = (String) arguments.get(0);

		getAgentLog().trace("Got response ", state);

		if (state.equals(CONFIRM))
			active = true;
		if (state.equals(DECLINE))
			active = false;
	}

}
