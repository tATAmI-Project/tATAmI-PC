package tatami.amilab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Queue;

import tatami.amilab.AmILabBuffer.LimitType;
import tatami.amilab.util.SimpleKestrelClient;
import tatami.core.agent.AgentComponent;

/**
 * {@link AgentComponent} that gets data from AmILab.
 * 
 * @author Claudiu-Mihai Toma
 */
// FIXME: Write a bit more...
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
	 * The time used to reduce thread's CPU consumption.
	 */
	// TODO: Make it 50.
	private static final int TIME_TO_SLEEP = 0;

	/**
	 * The Kestrel queue that is created on the AmILab Kestrel server. If this
	 * queue exists, it will be used.
	 */
	// TODO: Remove this or just make another constructor.
	protected String kestrelQueueName;

	/**
	 * Kestrel client used to communicate with the server.
	 */
	protected SimpleKestrelClient kestrelClient;

	/**
	 * Internal buffer.
	 */
	protected AmILabBuffer internalBuffer;

	/**
	 * Thread that feeds the internal and external buffers.
	 */
	protected AmILabThread kestrelGatherer;

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
		RGB_IMAGE("image_rgb"),

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

		/**
		 * Type getter.
		 * 
		 * @return type of this instance
		 */
		public String getType()
		{
			return type;
		}
	}

	/**
	 * Thread that populates the internal buffer.
	 * 
	 * @author Claudiu-Mihai Toma
	 *
	 */
	// private class AmILabThread extends Observable implements Runnable
	private class AmILabThread extends Thread
	{
		/**
		 * Holds the state of the thread.
		 */
		private boolean running;

		/**
		 * Default constructor.
		 */
		public AmILabThread()
		{
			running = true;
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
		 * 
		 * @return {@code true} if alive, {@code false} otherwise
		 */
		/*
		 * public boolean isAlive() { return running; }
		 */

		@Override
		public void run()
		{
			while (running)
			{
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

				if (kestrelJSON == null)
					continue;

				// Get type of data.
				AmILabDataType dataType = null;
				for (AmILabDataType itDataType : AmILabDataType.values())
				{
					if (kestrelJSON.contains(itDataType.getType()))
					{
						dataType = itDataType;
						break;
					}
				}

				// Message has no known type or is corrupt.
				if (dataType == null)
					continue;

				// TODO: Extract information from JSON, maybe even deserialize.
				internalBuffer.put(new Perception(dataType, 0, kestrelJSON));

				//notifyObservers(new Perception(dataType, 0, kestrelJSON));
			}
		}
	}

	/**
	 * Default constructor.
	 */
	public AmILabComponent()
	{
		this(KESTREL_LOCAL_SERVER_IP, KESTREL_SERVER_PORT, KESTREL_AMILAB_COMPONENT_QUEUE);
	}

	/**
	 * Constructor that requires the Kestrel queue name as well as server IP and
	 * port.
	 * 
	 * @param serverIP
	 *            - server IP address as string
	 * @param serverPort
	 *            - server port
	 * @param queueName
	 *            - name of the Kestrel queue used by this instance of
	 *            {@link AmILabComponent}
	 */
	public AmILabComponent(String serverIP, int serverPort, String queueName)
	{
		super(AgentComponentName.AMILAB_COMPONENT);
		// Set up connection.
		kestrelQueueName = queueName;
		// FIXME: Check if connection is established.
		kestrelClient = new SimpleKestrelClient(serverIP, serverPort);

		// Start internal buffer and thread.
		List<AmILabDataType> types = new ArrayList<AmILabDataType>(Arrays.asList(AmILabDataType.values()));
		internalBuffer = new AmILabBuffer(types, LimitType.UNLIMITED);
		kestrelGatherer = new AmILabThread();
	}

	/**
	 * Gets data from Kestrel queue. If the queue is empty it returns
	 * {@code null}.
	 * 
	 * @return first element in the Kestrel queue
	 */
	// TODO: Relevant only for testing.
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
	public String get(AmILabDataType dataType)
	{
		return get(dataType, -1);
	}

	/**
	 * Gets specific data from Kestrel queue.
	 * 
	 * @param dataType
	 *            - type of data required
	 * @param wait
	 *            - {@code true} for blocking effect; {@code false} otherwise
	 *            (can return {@code null})
	 * @return data as JSON string
	 */
	public String get(AmILabDataType dataType, boolean wait)
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
	 *            - amount of milliseconds to wait for a queue element (can
	 *            return {@code null}); {@code -1} for blocking effect
	 * @return data as JSON string
	 */
	public String get(AmILabDataType dataType, long wait)
	{
		// Set up parameter for infinite wait case.
		boolean infiniteWait = (wait == -1) ? true : false;

		if (wait < 0 && wait != -1)
			throw new IllegalArgumentException("Second argument [" + wait + "] is not a valid argument.");

		Queue<Perception> dataQueue = internalBuffer.get(dataType);
		String data = null;
		long startingTime = System.currentTimeMillis();
		long currentTime;
		long currentWait;

		// Try to get data within the time limit.
		do
		{
			currentTime = System.currentTimeMillis();
			currentWait = currentTime - startingTime;
		} while ((currentWait < wait || infiniteWait) && dataQueue.isEmpty());

		if (dataQueue.isEmpty())
			return null;

		data = dataQueue.peek().getData();

		return data;
	}

	/**
	 * Pushes a message to the Kestrel queue.
	 * 
	 * @param message
	 *            - message to be pushed
	 */
	// TODO: Relevant only for testing.
	public void set(String message)
	{
		kestrelClient.set(kestrelQueueName, message);
	}

	/**
	 * Clears Kestrel queue.
	 */
	// TODO: Relevant only for testing.
	public void clearQueue()
	{
		String data = null;
		do
		{
			data = get();
		} while (data != null);
	}

	/**
	 * Stops the internal thread.
	 */
	// TODO: Relevant only for testing.
	public void startInternalThread()
	{
		kestrelGatherer.start();
	}

	/**
	 * Stops the internal thread.
	 */
	// TODO: Relevant only for testing.
	public void stopInternalThread()
	{
		kestrelGatherer.stopThread();
	}

	/**
	 * Starts the internal buffer.
	 */
	public void startInternalBuffer()
	{
		if (!kestrelGatherer.isAlive())
			startInternalThread();
	}

	/**
	 * Starts the internal buffer.
	 */
	public void stopInternalBuffer()
	{
		if (kestrelGatherer.isAlive())
			stopInternalThread();
	}
}
