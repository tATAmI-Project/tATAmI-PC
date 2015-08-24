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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import net.xqhs.util.XML.XMLTree.XMLNode;
import net.xqhs.util.logging.Logger;
import tatami.amilab.AmILabBuffer.LimitType;
import tatami.amilab.util.SimpleKestrelClient;
import tatami.core.agent.AgentComponent;
import tatami.core.agent.AgentEvent;
import tatami.core.util.platformUtils.PlatformUtils;

/**
 * {@link AgentComponent} that gets data from AmILab.
 * <p>
 * FIXME: Write a bit more...
 * 
 * @author Claudiu-Mihai Toma
 */
public class AmILabComponent extends AgentComponent
{

	/**
	 * The serial UID.
	 */
	private static final long serialVersionUID = 7762026334280094146L;

	/**
	 * Server IP.
	 */
	public static final String KESTREL_MASTER_SERVER_IP = "172.16.7.143";

	/**
	 * Loopback IP.
	 */
	public static final String KESTREL_LOCAL_SERVER_IP = "127.0.0.1";

	/**
	 * Default Kestrel port.
	 */
	public static final int KESTREL_SERVER_PORT = 22133;

	/**
	 * Default Kestrel queue name.
	 */
	public static final String KESTREL_AMILAB_COMPONENT_QUEUE = "AMILAB_COMPONENT_QUEUE";

	/**
	 * Measurements queue.
	 */
	public static final String KESTREL_MEASUREMENTS_QUEUE = "measurements";

	/**
	 * The name of the parameter in the component parameter set that corresponds to the IP.
	 */
	private static final String IP = "IP";

	/**
	 * The name of the parameter in the component parameter set that corresponds to the name of the PORT.
	 */
	private static final String PORT = "port";

	/**
	 * The name of the parameter in the component parameter set that corresponds to the name of the queue name.
	 */
	private static final String QUEUE_NAME = "queue-name";

	/**
	 * Size of the internal buffer.
	 */
	private static final long INTERNAL_BUFFER_SIZE = 1;

	/**
	 * The Kestrel queue that is created on the AmILab Kestrel server. If this queue exists, it will be used.
	 */
	protected String kestrelQueueName;

	/**
	 * Kestrel client used to communicate with the server.
	 */
	protected SimpleKestrelClient kestrelClient;

	/**
	 * Internal buffer.
	 */
	protected AmILabMutableBuffer internalBuffer;

	/**
	 * Runnable that feeds the internal and external buffers.
	 */
	protected AmILabRunnable kestrelGatherer;

	/**
	 * Support thread for the {@link AmILabRunnable}.
	 */
	protected Thread supportThread;

	/**
	 * Enum that defines data types given by AmILab.
	 * 
	 * @author Claudiu-Mihai Toma
	 *
	 */
	public static enum AmILabDataType
	{
		/**
		 * RGB image
		 */
		IMAGE_RGB("image_rgb"),

		/**
		 * Depth image
		 */
		IMAGE_DEPTH("image_depth"),

		/**
		 * Skeleton
		 */
		SKELETON("skeleton"),

		;

		/**
		 * Name of the data type.
		 */
		private String type;

		/**
		 * Default constructor.
		 */
		private AmILabDataType()
		{
			type = null;
		}

		/**
		 * Constructor that sets the type.
		 * 
		 * @param dataType
		 */
		private AmILabDataType(String dataType)
		{
			type = dataType;
		}

		@Override
		public String toString()
		{
			return type;
		}
	}

	/**
	 * Default constructor.
	 */
	public AmILabComponent()
	{
		super(AgentComponentName.AMILAB_COMPONENT);
	}

	/**
	 * Gets data from Kestrel queue. If the queue is empty it returns {@code null}.
	 * 
	 * @return first element in the Kestrel queue
	 */
	public String get()
	{
		return kestrelClient.get(kestrelQueueName);
	}

	/**
	 * Gets specific data from Kestrel queue.
	 * 
	 * @param dataType
	 *            - type of data required
	 * @return data as JSON string
	 */
	public Perception get(AmILabDataType dataType)
	{
		return get(dataType, -1);
	}

	/**
	 * Gets specific data from Kestrel queue.
	 * 
	 * @param dataType
	 *            - type of data required
	 * @param wait
	 *            - {@code true} for blocking effect; {@code false} otherwise (can return {@code null})
	 * @return data as JSON string
	 */
	public Perception get(AmILabDataType dataType, boolean wait)
	{
		// Convert boolean to integer.
		int waitInt = wait ? -1 : 0;

		return get(dataType, waitInt);
	}

	/**
	 * Gets specific data from Kestrel queue.
	 * 
	 * @param dataType
	 *            - type of data required
	 * @param wait
	 *            - amount of milliseconds to wait for a queue element (can return {@code null}); {@code -1} for
	 *            blocking effect
	 * @return data as JSON string
	 */
	public Perception get(AmILabDataType dataType, long wait)
	{
		// Set up parameter for infinite wait case.
		boolean infiniteWait = (wait == -1);

		if (wait < 0 && wait != -1)
			throw new IllegalArgumentException("Second argument [" + wait + "] is not a valid argument.");

		Queue<Perception> dataQueue;
		// Support inactive internal buffer.
		if (internalBuffer != null)
			dataQueue = internalBuffer.getQueue(dataType);
		else
			dataQueue = new LinkedList<Perception>();

		String data = null;
		long startingTime = System.currentTimeMillis();
		long currentTime;
		long currentWait;

		// Try to get data within the time limit.
		do
		{
			currentTime = System.currentTimeMillis();
			currentWait = currentTime - startingTime;

			if (internalBuffer == null)
			{
				data = get();
				Perception perception = AmILabRunnable.createPerception(data);
				if (perception != null && dataType.equals(perception.getType()))
					dataQueue.add(perception);
				try
				{
					Thread.sleep(AmILabRunnable.TIME_TO_SLEEP);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}

		} while ((currentWait < wait || infiniteWait) && dataQueue.isEmpty());

		if (dataQueue.isEmpty())
			return null;

		return dataQueue.peek();
	}

	/**
	 * Pushes a message to the Kestrel queue.
	 * <p>
	 * TODO: Relevant only for testing.
	 * 
	 * @param message
	 *            - message to be pushed
	 */
	public void set(String message)
	{
		kestrelClient.set(kestrelQueueName, message);
	}

	/**
	 * Clears Kestrel queue.
	 * <p>
	 * TODO: Relevant only for testing.
	 */
	public void clearQueue()
	{
		String data = null;
		do
		{
			data = get();
		} while (data != null);
	}

	/**
	 * Starts the internal thread.
	 */
	protected void startInternalThread()
	{
		supportThread = new Thread(kestrelGatherer);
		supportThread.start();
	}

	/**
	 * Stops the internal thread. The user MUST call this if any kind of buffers are used.
	 */
	public void stopInternalThread()
	{
		kestrelGatherer.stopThread();
	}

	/**
	 * Checks if the internal thread is alive.
	 * <p>
	 * TODO: Public is relevant only for testing. Make it protected!
	 * 
	 * @return state of internal thread
	 */
	public boolean isInternalThreadAlive()
	{
		return kestrelGatherer.isAlive();
	}

	/**
	 * Starts the internal buffer.
	 */
	public void startInternalBuffer()
	{
		resetInternalBuffer();
		kestrelGatherer.addObserver(internalBuffer);

		if (!kestrelGatherer.isAlive())
			startInternalThread();
	}

	/**
	 * Stops the internal buffer.
	 */
	public void stopInternalBuffer()
	{
		kestrelGatherer.deleteObserver(internalBuffer);
		internalBuffer = null;
	}

	/**
	 * Checks the state of the internal buffer.
	 * 
	 * @return {@code true} if the internal buffer is alive; {@code false} otherwise
	 */
	public boolean isInternalBufferAlive()
	{
		return internalBuffer != null;
	}

	/**
	 * Sets (resets) internal buffer.
	 */
	protected void resetInternalBuffer()
	{
		List<AmILabDataType> types = new ArrayList<AmILabDataType>(Arrays.asList(AmILabDataType.values()));
		internalBuffer = startCyclicMutableBuffer(types, LimitType.SIZE_PER_TYPE, INTERNAL_BUFFER_SIZE);
	}

	/**
	 * {@code enum} used by the
	 * {@link AmILabComponent#createBuffer(AmILabBufferType, List, LimitType, long, NotificationTarget) createBuffer}
	 * helper method.
	 * 
	 * @author Claudiu-Mihai Toma
	 *
	 */
	private enum AmILabBufferType
	{
		/**
		 * Simple buffer.
		 */
		BUFFER,

		/**
		 * Simple buffer with setters.
		 */
		MUTABLE_BUFFER,

		/**
		 * Cyclic buffer.
		 */
		CYCLIC_BUFFER,

		/**
		 * Cyclic buffer with setters.
		 */
		CYCLIC_MUTABLE_BUFFER
	}

	/**
	 * Creates a buffer based on the specifications. This is a helper method.
	 * 
	 * @param bufferType
	 *            - type of the buffer
	 * @param desiredTypes
	 *            - list of types to keep track of
	 * @param desiredLimitType
	 *            - type of buffer
	 * @param desiredLimit
	 *            - numerical value of limit; the value for {@code UNLIMITED} must be {@code NO_LIMIT} ({@code -1})
	 * @param notificationTarget
	 *            - target to be notified
	 * @return the buffer; note that it may not be full
	 */
	private AmILabBuffer createBuffer(AmILabBufferType bufferType, List<AmILabDataType> desiredTypes,
			LimitType desiredLimitType, long desiredLimit, NotificationTarget notificationTarget)
	{
		AmILabBuffer buffer = null;

		switch (bufferType)
		{
		case BUFFER:
			buffer = new AmILabBuffer(desiredTypes, kestrelGatherer, desiredLimitType, desiredLimit, false,
					notificationTarget);
			break;

		case MUTABLE_BUFFER:
			buffer = new AmILabMutableBuffer(desiredTypes, kestrelGatherer, desiredLimitType, desiredLimit, false,
					notificationTarget);
			break;

		case CYCLIC_BUFFER:
			buffer = new AmILabBuffer(desiredTypes, kestrelGatherer, desiredLimitType, desiredLimit, true, null);
			break;

		case CYCLIC_MUTABLE_BUFFER:
			buffer = new AmILabMutableBuffer(desiredTypes, kestrelGatherer, desiredLimitType, desiredLimit, true, null);
			break;
		}

		if (buffer == null)
			return null;

		kestrelGatherer.addObserver(buffer);

		if (!isInternalThreadAlive())
			startInternalThread();

		return buffer;
	}

	/**
	 * 
	 * @param desiredTypes
	 *            - list of types to keep track of
	 * @param desiredLimitType
	 *            - type of buffer
	 * @param desiredLimit
	 *            - numerical value of limit; the value for {@code UNLIMITED} must be {@code NO_LIMIT} ({@code -1})
	 * @param notificationTarget
	 *            - target to be notified
	 * @return the buffer; note that it may not be full
	 */
	public AmILabBuffer startBuffer(List<AmILabDataType> desiredTypes, LimitType desiredLimitType, long desiredLimit,
			NotificationTarget notificationTarget)
	{
		return createBuffer(AmILabBufferType.BUFFER, desiredTypes, desiredLimitType, desiredLimit, notificationTarget);
	}

	/**
	 * 
	 * @param desiredTypes
	 *            - list of types to keep track of
	 * @param desiredLimitType
	 *            - type of buffer
	 * @param desiredLimit
	 *            - numerical value of limit; the value for {@code UNLIMITED} must be {@code NO_LIMIT} ({@code -1})
	 * @param notificationTarget
	 *            - target to be notified
	 * @return the buffer; note that it may not be full
	 */
	public AmILabMutableBuffer startMutableBuffer(List<AmILabDataType> desiredTypes, LimitType desiredLimitType,
			long desiredLimit, NotificationTarget notificationTarget)
	{
		return (AmILabMutableBuffer) createBuffer(AmILabBufferType.MUTABLE_BUFFER, desiredTypes, desiredLimitType,
				desiredLimit, notificationTarget);
	}

	/**
	 * 
	 * @param desiredTypes
	 *            - list of types to keep track of
	 * @param desiredLimitType
	 *            - type of buffer
	 * @param desiredLimit
	 *            - numerical value of limit; the value for {@code UNLIMITED} must be {@code NO_LIMIT} ({@code -1})
	 * @return the buffer; note that it may not be full
	 */
	public AmILabBuffer startCyclicBuffer(List<AmILabDataType> desiredTypes, LimitType desiredLimitType,
			long desiredLimit)
	{
		return createBuffer(AmILabBufferType.CYCLIC_BUFFER, desiredTypes, desiredLimitType, desiredLimit, null);
	}

	/**
	 * 
	 * @param desiredTypes
	 *            - list of types to keep track of
	 * @param desiredLimitType
	 *            - type of buffer
	 * @param desiredLimit
	 *            - numerical value of limit; the value for {@code UNLIMITED} must be {@code NO_LIMIT} ({@code -1})
	 * @return the buffer; note that it may not be full
	 */
	public AmILabMutableBuffer startCyclicMutableBuffer(List<AmILabDataType> desiredTypes, LimitType desiredLimitType,
			long desiredLimit)
	{
		return (AmILabMutableBuffer) createBuffer(AmILabBufferType.CYCLIC_MUTABLE_BUFFER, desiredTypes,
				desiredLimitType, desiredLimit, null);
	}

	@Override
	protected boolean preload(ComponentCreationData parameters, XMLNode scenarioNode, Logger log)
	{
		if (!super.preload(parameters, scenarioNode, log))
			return false;

		try
		{
			kestrelQueueName = getComponentData().get(QUEUE_NAME);
			// Set up connection.
			kestrelClient = new SimpleKestrelClient(getComponentData().get(IP),
					Integer.parseInt(getComponentData().get(PORT)));
			// Test connection.
			kestrelClient.peek(kestrelQueueName);
		} catch (Exception e)
		{
			log.error("Load AmILabComponent failed: " + PlatformUtils.printException(e));
			return false;
		}

		// Make preparations for internal thread.
		kestrelGatherer = new AmILabRunnable(kestrelClient, kestrelQueueName);

		// Initialize internal buffer.
		internalBuffer = null;

		return true;
	}

	@Override
	protected void atAgentStop(AgentEvent event)
	{
		super.atAgentStop(event);
		stopInternalThread();
	}
}
