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
package tatami.amilab;

import java.io.IOException;
import java.util.HashMap;
import java.util.Observable;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import tatami.amilab.AmILabComponent.AmILabDataType;
import tatami.amilab.util.SimpleKestrelClient;

/**
 * Thread that populates buffers. Without buffers it dies.
 * 
 * @author Claudiu-Mihai Toma
 *
 */
public class AmILabThread extends Observable implements Runnable
{
	/**
	 * The time used to reduce thread's CPU consumption.
	 * <p>
	 * TODO: Make it 50 (or something not zero).
	 */
	private static final int TIME_TO_SLEEP = 0;

	/**
	 * Default timestamp.
	 * <p>
	 * TODO: Add correct timestamp.
	 */
	public static final int DEFAULT_TIMESTAMP = 0;

	/**
	 * Timestamp string found in JSONs.
	 */
	public static final String TIMESTAMP = "created_at";

	/**
	 * Holds the state of the thread.
	 */
	private boolean running;

	/**
	 * Kestrel client used to gather data.
	 */
	private SimpleKestrelClient kestrelClient;

	/**
	 * The Kestrel queue that is created on the AmILab Kestrel server. If this
	 * queue exists, it will be used.
	 */
	protected String kestrelQueueName;

	/**
	 * Constructor that requires the Kestrel client and queue.
	 * 
	 * @param client
	 *            - working Kestrel client
	 * @param queueName
	 *            - name of the queue
	 */
	public AmILabThread(SimpleKestrelClient client, String queueName)
	{
		running = false;
		kestrelClient = client;
		kestrelQueueName = queueName;
	}

	/**
	 * Stops the thread.
	 */
	public void stopThread()
	{
		running = false;
	}

	/**
	 * Checks if the thread is alive.
	 * <p>
	 * FIXME: This may need a Thread.sleep().
	 * 
	 * @return {@code true} if alive, {@code false} otherwise
	 */
	public boolean isAlive()
	{
		return running;
	}

	@Override
	public void run()
	{
		running = true;

		while (running)
		{
			if (countObservers() == 0)
			{
				stopThread();
				return;
			}

			try
			{
				Thread.sleep(TIME_TO_SLEEP);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}

			// Receive data from Kestrel queue, which resides on the Kestrel
			// server.
			String kestrelJSON;
			kestrelJSON = kestrelClient.get(kestrelQueueName);

			// Get type of data.
			AmILabDataType dataType = getTypeOfData(kestrelJSON);

			// Message has no known type or is corrupt.
			if (dataType == null)
				continue;

			Perception perception = null;

			// TODO: Extract information from JSON, maybe even deserialize. Make
			// it a static method.
			setChanged();
			notifyObservers(createPerception(dataType, kestrelJSON));
		}
	}

	/**
	 * Gets type of data.
	 * 
	 * @param data
	 *            - JSON string from the Kestrel queue
	 * @return type of given data
	 */
	public static AmILabDataType getTypeOfData(String data)
	{
		if (data == null)
			return null;

		AmILabDataType dataType = null;
		for (AmILabDataType itDataType : AmILabDataType.values())
		{
			if (data.contains(itDataType.getType()))
			{
				dataType = itDataType;
				break;
			}
		}

		return dataType;
	}

	/**
	 * Gets timestamp from a JSON.
	 * 
	 * @param JSON
	 *            - entry from an AmILab Kestrel queue
	 * @return time of creation in Unix time; {@code -1} if timestamp could not be
	 *         extracted
	 */
	public static long getTimestamp(String JSON)
	{
		HashMap<?, ?> parsedJson = null;
		long timestamp;
		try
		{
			parsedJson = new ObjectMapper().readValue(JSON, HashMap.class);
			timestamp = Long.parseLong((String) parsedJson.get(TIMESTAMP));
		} catch (Exception e)
		{
			return -1;
		}

		return timestamp;
	}

	/**
	 * Creates a perception from given parameters.
	 * 
	 * @param dataType
	 *            - type of data
	 * @param JSON
	 *            - entry from an AmILab Kestrel queue
	 * @return new {@link Perception} object
	 */
	public static Perception createPerception(AmILabDataType dataType, String JSON)
	{
		long timestamp = getTimestamp(JSON);
		if (timestamp == -1)
		{
			return null;
		}
		return new Perception(dataType, timestamp, JSON);
	}
}
