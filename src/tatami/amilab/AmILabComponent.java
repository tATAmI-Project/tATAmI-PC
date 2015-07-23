package tatami.amilab;

import tatami.core.agent.AgentComponent;

/**
 * Component that gets data from AmILab.
 * 
 * @author Claudiu-Mihai Toma
 */
public class AmILabComponent extends AgentComponent {

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
	 * Constructor that requires the Kestrel queue name.
	 * 
	 * @param kestrelQueueName
	 *            - name of the Kestrel queue used by this instance of
	 *            {@link AmILabComponent}
	 */

	public static enum AmILabDataType {
		/**
		 * RGB image
		 */
		RGB_IMAGE,

		/**
		 * Depth image
		 */
		DEPTH_IMAGE,

		/**
		 * Skeleton
		 */
		SKELETON,
	}

	protected AmILabComponent(String kestrelQueueName) {
		super(AgentComponentName.AMILAB_COMPONENT);
		this.kestrelQueueName = kestrelQueueName;
	}

	/**
	 * 
	 * @param dataType
	 * @return
	 */
	@SuppressWarnings("static-method")
	public String get(AmILabDataType dataType){
		return null;
	}

}
