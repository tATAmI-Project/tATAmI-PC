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

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.xml.bind.DatatypeConverter;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

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

	/**
	 * Default constructor.
	 */
	public AmILabControllerComponent()
	{
		super(AgentComponentName.TESTING_COMPONENT);
	}

	/**
	 * Extracts an image from a perception.
	 * 
	 * @param data
	 *            - an image depth perception
	 * @return the image as a byte array
	 */
	public static byte[] getImageBytes(Perception data)
	{
		HashMap<?, ?> parsedJson = null;
		try
		{
			parsedJson = new ObjectMapper().readValue(data.getData(), HashMap.class);
			if (!parsedJson.get("sensor_id").equals("daq-01"))
				return null;
			HashMap<?, ?> imageDepth = (HashMap<?, ?>) parsedJson.get("image_depth");
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

	@Override
	protected void atSimulationStart(AgentEvent event)
	{
		super.atSimulationStart(event);

		AmILabComponent amilab = (AmILabComponent) getAgentComponent(AgentComponentName.AMILAB_COMPONENT);

		Perception data = null;

		JFrame frame = new JFrame();
		frame.setSize(IMAGE_DEPTH_WIDTH, IMAGE_DEPTH_HEIGHT);
		frame.setVisible(true);

		JLabel label = new JLabel();
		frame.add(label);

		byte[] imageBytes = null;
		ImageIcon icon = null;

		amilab.startInternalBuffer();
		while (true)
		{
			data = amilab.get(AmILabDataType.IMAGE_DEPTH);
			imageBytes = getImageBytes(data);
			if (imageBytes == null)
				continue;
			icon = new ImageIcon(imageBytes);
			label.setIcon(icon);
		}
	}
}
