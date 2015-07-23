package tatami.amilab;

import tatami.amilab.util.SimpleKestrelClient;
import tatami.core.agent.AgentComponent;

/**
 * {@link AgentComponent} that gets data from AmILab.
 * 
 * @author Claudiu-Mihai Toma
 */
//FIXME: Write a bit more...
public class AmILabComponent extends AgentComponent
{

	/**
	 * The serial UID.
	 */
	private static final long serialVersionUID = 7762026334280094146L;

	/**
	 * The Kestrel queue that is created on the AmILab Kestrel server. If this
	 * queue exists, it will be used.
	 */
	private String kestrelQueueName;

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
		 * @param type
		 */
		private AmILabDataType(String type)
		{
			this.type = type;
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
	 * @param kestrelQueueName
	 *            - name of the Kestrel queue used by this instance of
	 *            {@link AmILabComponent}
	 */
	public AmILabComponent(String serverIP, int serverPort, String kestrelQueueName)
	{
		super(AgentComponentName.AMILAB_COMPONENT);
		this.kestrelQueueName = kestrelQueueName;
		// FIXME: Check if connection is established.
		kestrelClient = new SimpleKestrelClient(serverIP, serverPort);
	}

	/**
	 * Gets data form Kestrel server.
	 * 
	 * @param dataType
	 *            - type of data required
	 * @return data as JSON string
	 */
	public String get(AmILabDataType dataType)
	{
		String data = null;
		String type = dataType.getType();

		do
		{
			data = kestrelClient.get(kestrelQueueName);
			System.out.println(data);
		} while (data == null || !data.contains(type));

		// TODO: Extract data from JSON
		return data;
	}

	/**
	 * Pushes a message to the Kestrel queue.
	 * 
	 * @param message
	 *            - message to be pushed
	 */
	//TODO: Relevant only for testing?
	public void set(String message)
	{
		kestrelClient.set(kestrelQueueName, message);
	}
}
