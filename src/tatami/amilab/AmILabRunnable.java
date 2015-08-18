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

import java.util.HashMap;
import java.util.Observable;

import org.codehaus.jackson.map.ObjectMapper;

import tatami.amilab.AmILabComponent.AmILabDataType;
import tatami.amilab.util.SimpleKestrelClient;

/**
 * Runnable that populates buffers. Without buffers it dies.
 * 
 * @author Claudiu-Mihai Toma
 *
 */
public class AmILabRunnable extends Observable implements Runnable
{
	/**
	 * The time used to reduce thread's CPU consumption.
	 */
	public static final int TIME_TO_SLEEP = 1 * 1000;

	/**
	 * Timestamp string found in JSONs.
	 */
	public static final String TIMESTAMP = "created_at";

	/**
	 * Data type string found in JSONs.
	 */
	public static final String DATA_TYPE = "type";

	/**
	 * Holds the state of the thread.
	 */
	private boolean running;

	/**
	 * Kestrel client used to gather data.
	 */
	private SimpleKestrelClient kestrelClient;

	/**
	 * The Kestrel queue that is created on the AmILab Kestrel server. If this queue exists, it will be used.
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
	public AmILabRunnable(SimpleKestrelClient client, String queueName)
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
	 * FIXME: This may need a Thread.sleep() because it may sometimes return {@code true} shortly after being stopped or
	 * {@code false} shortly after started.
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

			// Receive data from Kestrel queue, which resides on the Kestrel server.
			String kestrelJSON;
			kestrelJSON = kestrelClient.get(kestrelQueueName);

			// Create perception from raw JSON.
			Perception perception = createPerception(kestrelJSON);

			if (perception != null)
			{
				setChanged();
			}

			// Send perception to all buffers.
			notifyObservers(perception);

			// TODO: Sleep here?
			try
			{
				Thread.sleep(TIME_TO_SLEEP);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
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
			if (data.equals(itDataType.toString()))
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
	 * @return time of creation in Unix time; {@code -1} if timestamp could not be extracted
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
	 * @param JSON
	 *            - entry from an AmILab Kestrel queue
	 * @return new {@link Perception} object
	 */
	public static Perception createPerception(String JSON)
	{
		HashMap<?, ?> parsedJson = null;
		Long timestamp;
		String dataTypeString = null;
		AmILabDataType dataType = null;
		try
		{
			// Parse JSON into a tree. Intermediate nodes are HashMaps. Leafs are strings or numbers.
			parsedJson = new ObjectMapper().readValue(JSON, HashMap.class);
			timestamp = (Long) parsedJson.get(TIMESTAMP);
			dataTypeString = (String) parsedJson.get(DATA_TYPE);
			dataType = getTypeOfData(dataTypeString);
		} catch (Exception e)
		{
			return null;
		}

		if (dataType == null)
		{
			return null;
		}

		// TODO: Extract information from JSON, maybe even deserialize.
		return new Perception(dataType, timestamp.longValue(), JSON);
	}
}
