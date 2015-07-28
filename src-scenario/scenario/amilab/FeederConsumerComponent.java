package scenario.amilab;

import tatami.amilab.AmILabComponent;
import tatami.core.agent.AgentEvent;

/**
 * Tester for {@link AmILabComponent}.
 * 
 * @author Claudiu-Mihai Toma
 */
public class FeederConsumerComponent extends AmILabComponent
{

	/**
	 * The serial UID.
	 */
	private static final long serialVersionUID = 7753773976224362270L;

	/**
	 * Number of useless entries.
	 */
	private static final int USELESS_DATA_COUNT = 5000;

	/**
	 * Useless data.
	 */
	private static final String USELESS_DATA = "useless data";

	/**
	 * Depth data.
	 */
	private static final String DEPTH_DATA = "image_depth data";

	/**
	 * Waiting time.
	 */
	private static final long WAIT = 5000;

	@Override
	protected void atSimulationStart(AgentEvent event)
	{
		super.atSimulationStart(event);

		clearQueue();

		for (int i = 0; i < USELESS_DATA_COUNT; i++)
		{
			set(USELESS_DATA);
		}

		set(DEPTH_DATA);

		System.out.println(get(AmILabDataType.IMAGE_DEPTH, WAIT));
	}
}
