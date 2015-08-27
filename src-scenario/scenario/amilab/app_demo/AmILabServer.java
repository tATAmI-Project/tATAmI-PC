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

				getAgentLog().info("message with content [] received", content);

				MessagingComponent msg = (MessagingComponent) getAgentComponent(AgentComponentName.MESSAGING_COMPONENT);

				String sender = msg
						.extractAgentAddress((String) event.getParameter(MessagingComponent.SOURCE_PARAMETER));

				if (responses.add(sender))
				{
					Long senderProximity = new Long(content);
					proximities.put(sender, senderProximity);
				}
			}
		}, AMILAB_PATH_ELEMENT, SERVER_PATH_ELEMENT);
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

	@Override
	protected boolean preload(ComponentCreationData parameters, XMLNode scenarioNode, Logger log)
	{
		if (!super.preload(parameters, scenarioNode, log))
			return false;

		clients = new ArrayList<String>();
		responses = new HashSet<String>();
		proximities = new HashMap<String, Long>();

		return true;
	}

	@Override
	protected void atSimulationStart(AgentEvent event)
	{
		super.atSimulationStart(event);

		while (simulationIsOn)
		{
			// Send requests to all clients.
			for (String client : clients)
			{
				sendMessage(PROXIMITY_REQUEST, getComponentEndpoint(AMILAB_PATH_ELEMENT, SERVER_PATH_ELEMENT), client,
						AMILAB_PATH_ELEMENT, CLIENT_PATH_ELEMENT);
			}

			while (!receivedAllAnswers())
			{
				try
				{
					Thread.sleep(50);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}

			// Determine closest client.
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

			// Announce the closest client first.
			sendMessage(PROXIMITY_REQUEST, getComponentEndpoint(AMILAB_PATH_ELEMENT, SERVER_PATH_ELEMENT),
					closestClient, AMILAB_PATH_ELEMENT, CLIENT_PATH_ELEMENT);

			// Send negative feedback to the other clients.
			proximities.remove(closestClient);
			for (String client : proximities.keySet())
			{
				sendMessage(DECLINE, getComponentEndpoint(AMILAB_PATH_ELEMENT, SERVER_PATH_ELEMENT), client,
						AMILAB_PATH_ELEMENT, CLIENT_PATH_ELEMENT);

			}

			responses.clear();
			proximities.clear();

			try
			{
				Thread.sleep(1000);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

}
