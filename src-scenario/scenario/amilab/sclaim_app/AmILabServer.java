package scenario.amilab.sclaim_app;

import java.util.List;

import net.xqhs.util.XML.XMLTree.XMLNode;
import net.xqhs.util.logging.Logger;
import scenario.amilab.utils.StoppableRunnable;
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
	 * Runnable that does all the logic
	 */
	protected StoppableRunnable serverRunnable;

	/**
	 * Thread that runs the server runnable.
	 */
	protected Thread serverThread;

	/**
	 * Send requests to all clients.
	 */
	protected void sendRequests()
	{
		gui.sendRequests();
	}

	@Override
	protected boolean preload(ComponentCreationData parameters, XMLNode scenarioNode, List<String> agentPackages,
			Logger log)
	{
		if (!super.preload(parameters, scenarioNode, agentPackages, log))
			return false;

		serverRunnable = new StoppableRunnable()
		{
			/**
			 * The time between the "pings".
			 */
			private static final int TIME_TO_SLEEP = 5000;

			@Override
			public void run()
			{
				while (!stopFlag)
				{
					System.out.println("Sending ping...");
					sendRequests();
					System.out.println("Sent ping.");

					try
					{
						Thread.sleep(TIME_TO_SLEEP);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}
		};

		serverThread = new Thread(serverRunnable);

		return true;
	}

	@Override
	protected void atSimulationStart(AgentEvent event)
	{
		super.atSimulationStart(event);

		serverThread.start();
	}

	@Override
	protected void atAgentStop(AgentEvent event)
	{
		super.atAgentStop(event);

		serverRunnable.stop();
	}

}
