package scenario.amilab;

import tatami.amilab.AmILabComponent;
import tatami.amilab.AmILabComponent.AmILabDataType;
import tatami.core.agent.AgentComponent;
import tatami.core.agent.AgentEvent;

/**
 * @author Claudiu-Mihai Toma
 *
 */
public class AmILabControllerComponent extends AgentComponent
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5909313431564468753L;

	/**
	 * Number of useless entries.
	 */
	private static final int USELESS_DATA_COUNT = 10000;

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

	/**
	 * 
	 */
	public AmILabControllerComponent()
	{
		super(AgentComponentName.TESTING_COMPONENT);
	}

	@Override
	protected void atSimulationStart(AgentEvent event)
	{
		super.atSimulationStart(event);

		AmILabComponent amilab = (AmILabComponent) getAgentComponent(AgentComponentName.AMILAB_COMPONENT);
		amilab.startInternalBuffer();

		for (int i = 0; i < USELESS_DATA_COUNT; i++)
		{
			amilab.set(USELESS_DATA);
		}

		amilab.set(DEPTH_DATA);

		System.out.println(amilab.get(AmILabDataType.IMAGE_DEPTH, WAIT));
		
		amilab.stopInternalBuffer();
	}
}
