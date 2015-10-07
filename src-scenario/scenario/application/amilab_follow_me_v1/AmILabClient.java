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
package scenario.application.amilab_follow_me_v1;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.codehaus.jackson.map.ObjectMapper;

import net.xqhs.util.XML.XMLTree.XMLNode;
import net.xqhs.util.logging.Logger;
import scenario.application.amilab_follow_me_v1.PC.AmILabGui;
import tatami.amilab.AmILabComponent;
import tatami.amilab.Perception;
import tatami.core.agent.AgentEvent;
import tatami.core.agent.io.AgentIO;
import tatami.core.agent.visualization.AgentGui;
import tatami.core.agent.visualization.VisualizableComponent;

/**
 * 
 * This client is a special kind of {@link AmILabComponent} that constantly updates the subject's proximity. It also
 * listens for proximity requests (which are responded to with the latest proximity) and updates the GUI according with
 * the response of the server.
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
	 * New dimension of image.
	 */
	private final int resize = 300;

	/**
	 * Name of the data acquisition unit this client monitors.
	 */
	protected String daq;

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
	 * Timer to run tasks periodically.
	 */
	protected Timer timer;

	/**
	 * No delay.
	 */
	protected static final int NO_DELAY = 0;

	/**
	 * How often to update the proximity.
	 */
	private static final int PROXIMITY_PERIOD = 50;

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
		URL imgURL = getClass().getResource(path);

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

		proximity = MAX_PROXIMITY;

		timer = new Timer();

		onIcon = new ImageIcon(getScaledImage(createImageIcon(HAPPY_FACE, HAPPY_FACE).getImage(), resize, resize));
		offIcon = new ImageIcon(getScaledImage(createImageIcon(SAD_FACE, SAD_FACE).getImage(), resize, resize));

		return true;
	}

	@Override
	protected void atSimulationStart(AgentEvent event)
	{
		super.atSimulationStart(event);

		gui = getGui();
		label = gui.getLabel();
		label.setIcon(offIcon);

		startInternalBuffer();

		// Update the proximity.
		timer.schedule(new TimerTask()
		{
			/**
			 * How many decimals to keep.
			 */
			private static final int DOUBLE_TO_LONG = 100;

			/**
			 * Numerical value for any invalid proximity.
			 */
			public static final long INVALID_PROXIMITY = -1;

			/**
			 * JSON sensor id.
			 */
			public static final String SENSOR_ID = "sensor_id";

			/**
			 * JSON 3d skeleton.
			 */
			public static final String SKELETON_3D = "skeleton_3D";

			/**
			 * JSON torso.
			 */
			public static final String TORSO = "torso";

			/**
			 * JSON z axis.
			 */
			public static final String Z_AXIS = "Z";

			@Override
			public void run()
			{
				Perception perception = get(AmILabDataType.SKELETON, false);

				long newProximity = processPerception(perception);

				if (newProximity == INVALID_PROXIMITY)
					return;

				proximity = newProximity;
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
					String crtDaq = (String) parsedJson.get(SENSOR_ID);

					// If this entry is from another daq return the last known proximity.
					if (!crtDaq.equals(daq))
					{
						return INVALID_PROXIMITY;
					}

					HashMap<?, ?> skeleton = (HashMap<?, ?>) parsedJson.get(SKELETON_3D);
					HashMap<?, ?> torso = (HashMap<?, ?>) skeleton.get(TORSO);
					double newProximity = ((Double) torso.get(Z_AXIS)).doubleValue();

					newProximity *= DOUBLE_TO_LONG;

					return (long) newProximity;

				} catch (Exception e)
				{
					return INVALID_PROXIMITY;
				}
			}
		}, NO_DELAY, PROXIMITY_PERIOD);
	}

	@Override
	protected void atAgentStop(AgentEvent event)
	{
		super.atAgentStop(event);

		timer.cancel();
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
			label.setIcon(onIcon);
		if (state.equals(DECLINE))
			label.setIcon(offIcon);
	}

}
