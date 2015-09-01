package scenario.amilab.app_demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.xqhs.util.XML.XMLTree.XMLNode;
import net.xqhs.util.logging.Logger;
import tatami.core.agent.AgentEvent;
import tatami.core.agent.AgentEvent.AgentEventHandler;
import tatami.core.agent.messaging.MessagingComponent;

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
	private static final long serialVersionUID = 4167394821635837982L;

	/**
	 * Clients parameter string.
	 */
	private static final String CLIENTS = "clients";

	/**
	 * Client separator.
	 */
	private static final String CLIENT_SEPARATOR = " ";

	/**
	 * List of client agents.
	 */
	protected List<String> clients;

	/**
	 * Set of the responses from periodical proximity requests.
	 */
	protected Set<String> responses;

	/**
	 * Proximities for every client;
	 */
	protected Map<String, Long> proximities;

	/**
	 * Runnable that does all the logic
	 */
	protected StoppableRunnable serverRunnable;

	/**
	 * Thread that runs the server runnable.
	 */
	protected Thread serverThread;

	/**
	 * Register a message handler for a server.
	 */
	protected void registerServerMessanger()
	{
		registerMessageReceiver(new AgentEventHandler()
		{
			@Override
			public void handleEvent(AgentEvent event)
			{
				String content = (String) event.getParameter(MessagingComponent.CONTENT_PARAMETER);

				MessagingComponent msg = (MessagingComponent) getAgentComponent(AgentComponentName.MESSAGING_COMPONENT);

				String sender = msg
						.extractAgentAddress((String) event.getParameter(MessagingComponent.SOURCE_PARAMETER));

				getAgentLog().info("Server received [] from []", content, sender);

				if (responses.add(sender))
				{
					Long senderProximity = new Long(content);
					proximities.put(sender, senderProximity);
				}
			}
		}, AMILAB_PATH_ELEMENT, SERVER_PATH_ELEMENT);
	}

	/**
	 * Send requests to all clients.
	 */
	protected void sendRequests()
	{
		for (String client : clients)
		{
			sendMessage(PROXIMITY_REQUEST, getComponentEndpoint(AMILAB_PATH_ELEMENT, SERVER_PATH_ELEMENT), client,
					AMILAB_PATH_ELEMENT, CLIENT_PATH_ELEMENT);
		}
	}

	/**
	 * Receive answers from all clients.
	 */
	protected void receiveAnswers()
	{
		while (!receivedAllAnswers())
		{
			// getAgentLog().info("Server received [] answers and expects all []",
			// Integer.toString(this.proximities.size()), Integer.toString(clients.size()));
			try
			{
				Thread.sleep(50);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		getAgentLog().info("Server received [] from [] answers", Integer.toString(this.proximities.size()),
				Integer.toString(clients.size()));
	}

	/**
	 * Check if all the clients responded.
	 * 
	 * @return {@code true} if all the clients have responded; {@code false} otherwise
	 */
	protected boolean receivedAllAnswers()
	{
		return responses.size() == clients.size();
	}

	/**
	 * Determine closest client.
	 * 
	 * @return name of the closest client
	 */
	protected String getClosestClient()
	{
		String closestClient = null;
		long smallestProximity = Long.MAX_VALUE;

		for (Map.Entry<String, Long> client : proximities.entrySet())
		{
			long clientProximity = client.getValue().longValue();

			if (clientProximity <= smallestProximity)
			{
				closestClient = client.getKey();
				smallestProximity = clientProximity;
			}
		}
		return closestClient;
	}

	/**
	 * Send responses to all the clients.
	 * 
	 * @param closestClient
	 *            - name of the closesc client
	 */
	protected void sendResponses(String closestClient)
	{
		// Announce the closest client first.
		sendMessage(CONFIRM, getComponentEndpoint(AMILAB_PATH_ELEMENT, SERVER_PATH_ELEMENT), closestClient,
				AMILAB_PATH_ELEMENT, CLIENT_PATH_ELEMENT);

		// Send negative feedback to the other clients.
		proximities.remove(closestClient);
		for (String client : proximities.keySet())
		{
			sendMessage(DECLINE, getComponentEndpoint(AMILAB_PATH_ELEMENT, SERVER_PATH_ELEMENT), client,
					AMILAB_PATH_ELEMENT, CLIENT_PATH_ELEMENT);

		}

		responses.clear();
		proximities.clear();
	}

	@Override
	protected boolean preload(ComponentCreationData parameters, XMLNode scenarioNode, List<String> agentPackages, Logger log)
	{
		if (!super.preload(parameters, scenarioNode, agentPackages, log))
			return false;

		clients = new ArrayList<String>();
		responses = new HashSet<String>();
		proximities = new HashMap<String, Long>();

		String[] clientsParam = getComponentData().get(CLIENTS).split(CLIENT_SEPARATOR);

		for (String client : clientsParam)
		{
			clients.add(client);
		}

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
					sendRequests();
					receiveAnswers();

					String closestClient = getClosestClient();

					sendResponses(closestClient);

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

		registerServerMessanger();
		serverThread.start();
	}

	@Override
	protected void atAgentStop(AgentEvent event)
	{
		super.atAgentStop(event);

		serverRunnable.stop();
	}

}
