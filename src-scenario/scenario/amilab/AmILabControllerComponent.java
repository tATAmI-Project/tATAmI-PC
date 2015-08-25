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
package scenario.amilab;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.xml.bind.DatatypeConverter;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import sun.security.krb5.internal.ccache.CredentialsCache;
import tatami.amilab.AmILabComponent;
import tatami.amilab.AmILabComponent.AmILabDataType;
import tatami.amilab.Perception;
import tatami.core.agent.AgentComponent;
import tatami.core.agent.AgentEvent;

/**
 * @author Claudiu-Mihai Toma
 *
 */
public class AmILabControllerComponent extends AgentComponent
{
	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 5909313431564468753L;

	private static final int	IMAGE_DEPTH_WIDTH	= 640;
	private static final int	IMAGE_DEPTH_HEIGHT	= 480;
	private static final double	RESIZE_FACTOR		= 0.73;
	private static final int	NB_CAMERAS			= 4;

	private int crtCamera;

	/**
	 * Default constructor.
	 */
	public AmILabControllerComponent()
	{
		super(AgentComponentName.TESTING_COMPONENT);
	}

	@Override
	protected void atSimulationStart(AgentEvent event)
	{
		super.atSimulationStart(event);

		// TODO: AmILabRunnable sleep time!
		AmILabComponent amilab = (AmILabComponent) getAgentComponent(AgentComponentName.AMILAB_COMPONENT);

		Perception data = null;

		JFrame frame = new JFrame();
		frame.setLayout(new FlowLayout());
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		frame.setVisible(true);

		List<JLabel> labels = new ArrayList<JLabel>();

		for (int i = 0; i < NB_CAMERAS; i++)
		{
			JLabel label = new JLabel();
			frame.add(label);

			labels.add(label);
		}

		byte[] imageBytes = null;
		ImageIcon icon = null;
		ImageIcon scaledIcon = null;

		amilab.startInternalBuffer();
		while (true)
		{
			data = amilab.get(AmILabDataType.IMAGE_DEPTH);
			// data = amilab.get(AmILabDataType.IMAGE_RGB);
			imageBytes = getImageBytes(data);
			if (imageBytes == null)
				continue;
			icon = new ImageIcon(imageBytes);
			scaledIcon = new ImageIcon(getScaledImage(icon.getImage(), (int) (RESIZE_FACTOR * IMAGE_DEPTH_WIDTH),
					(int) (RESIZE_FACTOR * IMAGE_DEPTH_HEIGHT)));

			labels.get(crtCamera - 1).setIcon(scaledIcon);
		}
	}

	public static Image getScaledImage(Image srcImg, int w, int h)
	{
		BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = resizedImg.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(srcImg, 0, 0, w, h, null);
		g2.dispose();
		return resizedImg;
	}

	/**
	 * Extracts an image from a perception.
	 * 
	 * @param data
	 *            - an image depth perception
	 * @return the image as a byte array
	 */
	public byte[] getImageBytes(Perception data)
	{
		HashMap<?, ?> parsedJson = null;
		try
		{
			parsedJson = new ObjectMapper().readValue(data.getData(), HashMap.class);
			String sensorId = (String) parsedJson.get("sensor_id");
			crtCamera = Integer.parseInt(sensorId.substring(sensorId.length() - 1));

			if (crtCamera > NB_CAMERAS)
				return null;

			HashMap<?, ?> imageDepth = (HashMap<?, ?>) parsedJson.get(AmILabDataType.IMAGE_DEPTH.toString());
			// HashMap<?, ?> imageDepth = (HashMap<?, ?>) parsedJson.get(AmILabDataType.IMAGE_RGB.toString());
			String image = (String) imageDepth.get("image");
			return DatatypeConverter.parseBase64Binary(image);
		} catch (JsonParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
