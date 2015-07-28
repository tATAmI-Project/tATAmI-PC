package tatami.amilab;

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
	public static final String KESTREL_AI_MAS_QUEUE = "AI_MAS_QUEUE";

	/**
	 * Measurements queue.
	 */
	public static final String KESTREL_MEASUREMENTS_QUEUE = "measurements";

	/**
	 * The Kestrel queue that is created on the AmILab Kestrel server. If this
	 * queue exists, it will be used.
	 */
	private String kestrelQueueName;

	/**
	 * Kestrel client used to communicate with the server.
	 */
	private SimpleKestrelClient kestrelClient;

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
		RGB_IMAGE,

		/**
		 * Depth image
		 */
		IMAGE_DEPTH("image_depth"),

		/**
		 * Skeleton
		 */
		SKELETON,

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
	 * Default constructor.
	 */
	public AmILabComponent()
	{
		super(AgentComponentName.AMILAB_COMPONENT);
		kestrelQueueName = KESTREL_AI_MAS_QUEUE;
		kestrelClient = new SimpleKestrelClient(KESTREL_LOCAL_SERVER_IP, KESTREL_SERVER_PORT);
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
		kestrelQueueName = queueName;
		// FIXME: Check if connection is established.
		kestrelClient = new SimpleKestrelClient(serverIP, serverPort);
	}

	/**
	 * Gets data from Kestrel queue. If the queue is empty it returns
	 * {@code null}.
	 * 
	 * @return first element in the Kestrel queue
	 */
	public String get()
	{
		String data = null;

		data = kestrelClient.get(kestrelQueueName);

		// TODO: Extract data from JSON
		return data;
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
		return get(dataType, true);
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

		String data = null;
		String type = dataType.getType();

		do
		{
			data = kestrelClient.get(kestrelQueueName);
		} while (wait && (data == null || !data.contains(type)));

		// TODO: Extract data from JSON
		return data.contains(type) ? data : null;
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
		if (wait == -1)
			return get(dataType);

		String data = null;
		String type = dataType.getType();
		long startingTime = System.currentTimeMillis();
		long currentTime;
		long currentWait;

		do
		{
			data = kestrelClient.get(kestrelQueueName);

			currentTime = System.currentTimeMillis();
			currentWait = currentTime - startingTime;
		} while (currentWait < wait && (data == null || !data.contains(type)));

		// TODO: Extract data from JSON
		return data.contains(type) ? data : null;
	}

	/**
	 * Pushes a message to the Kestrel queue.
	 * 
	 * @param message
	 *            - message to be pushed
	 */
	// TODO: Relevant only for testing?
	public void set(String message)
	{
		kestrelClient.set(kestrelQueueName, message);
	}

	/**
	 * Clears Kestrel queue.
	 */
	protected void clearQueue()
	{
		String data = null;
		do
		{
			data = get();
		} while (data != null);
	}
}
