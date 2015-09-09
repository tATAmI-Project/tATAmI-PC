package scenario.amilab.sclaim_app;

import java.util.TimerTask;

import tatami.core.agent.AgentEvent;

/**
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
				System.out.println("Sending ping...");
				sendRequests();
				System.out.println("Sent ping.");
			}
		}, NO_DELAY, PING_PERIOD);
	}
}
