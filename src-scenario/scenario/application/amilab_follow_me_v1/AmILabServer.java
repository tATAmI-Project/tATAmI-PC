package scenario.application.amilab_follow_me_v1;

import java.util.TimerTask;

import tatami.core.agent.AgentEvent;

/**
 * 
 * The server is a special kind of client that is capable of sending periodic requests for proximity to all the clients.
 * 
 * @author Claudiu-Mihai Toma
 *
 */
public class AmILabServer extends AmILabClient
{

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1406553420405779596L;

	/**
	 * How often to send pings.
	 */
	private static final int PING_PERIOD = 500;

	/**
	 * Send requests to all clients.
	 */
	protected void sendRequests()
	{
		gui.sendRequests();
	}

	@Override
	protected void atSimulationStart(AgentEvent event)
	{
		super.atSimulationStart(event);

		// Periodically send pings.
		timer.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				sendRequests();
			}
		}, NO_DELAY, PING_PERIOD);
	}
}
