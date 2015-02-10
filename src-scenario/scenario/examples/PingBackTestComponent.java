package scenario.examples;

import java.util.List;

import net.xqhs.util.XML.XMLTree.XMLNode;
import net.xqhs.util.logging.Logger;
import tatami.core.agent.AgentComponent;
import tatami.core.agent.AgentEvent;
import tatami.core.agent.AgentEvent.AgentEventHandler;
import tatami.core.agent.messaging.MessagingComponent;
import tatami.core.agent.CompositeAgent;

/**
 * An {@link AgentComponent} implementation that initially sends a message to another agent, it this agent is designated
 * as initiator.
 * <p>
 * Otherwise, it waits for a ping message, that it then sends back.
 * 
 * @author Andrei Olaru
 */
public class PingBackTestComponent extends AgentComponent
{
	/**
	 * The UID.
	 */
	private static final long		serialVersionUID			= 5214882018809437402L;
	/**
	 * The name of the component parameter that contains the id of the other agent.
	 */
	protected static final String	OTHER_AGENT_PARAMETER_NAME	= "other agent";
	/**
	 * The name of the component parameter that specifies if the agent is an initiator
	 */
	protected static final String	INITIATOR_PARAMETER_NAME	= "initiator";
	/**
	 * The internal address of the component.
	 */
	protected static final String	COMPONENT_ADDRESS			= "pingback";
	/**
	 * Cache for the name of this agent.
	 */
	String							thisAgent					= null;
	/**
	 * Cache for the name of the other agent.
	 */
	List<String>					otherAgents					= null;
	
	/**
	 * Default constructor
	 */
	public PingBackTestComponent()
	{
		super(AgentComponentName.TESTING_COMPONENT);
	}
	
	@Override
	protected boolean preload(ComponentCreationData parameters, XMLNode scenarioNode, Logger log)
	{
		if(!super.preload(parameters, scenarioNode, log))
			return false;
		otherAgents = getComponentData().getValues(OTHER_AGENT_PARAMETER_NAME);
		return true;
	}
	
	@Override
	protected void parentChangeNotifier(CompositeAgent oldParent)
	{
		super.parentChangeNotifier(oldParent);
		
		if(getParent() != null)
		{
			thisAgent = getAgentName();
		}
	}
	
	@Override
	protected void atAgentStart(AgentEvent startEvent)
	{
		super.atAgentStart(startEvent);
		
		registerMessageReceiver(new AgentEventHandler() {
			@Override
			public void handleEvent(AgentEvent event)
			{
				getAgentLog().info("Message received: ", event);
				String[] content = ((String) event.getParameter(MessagingComponent.CONTENT_PARAMETER)).split(" ");
				String replyContent = content[0] + " " + (Integer.parseInt(content[1]) + 1);
				String sender = (String) event.getParameter(MessagingComponent.SOURCE_PARAMETER);
				sendMessage(replyContent, getComponentEndpoint(COMPONENT_ADDRESS), sender);
			}
		}, COMPONENT_ADDRESS);
	}
	
	@Override
	protected Logger getAgentLog()
	{
		return super.getAgentLog();
	}
	
	@Override
	protected boolean sendMessage(String content, String sourceEndpoint, String targetAgent,
			String... targetPathElements)
	{
		return super.sendMessage(content, sourceEndpoint, targetAgent, targetPathElements);
	}
	
	@Override
	protected String getComponentEndpoint(String... pathElements)
	{
		return super.getComponentEndpoint(pathElements);
	}
	
	@Override
	protected void atSimulationStart(AgentEvent event)
	{
		super.atSimulationStart(event);
		
		if(Boolean.TRUE.toString().equals(getComponentData().getValue(INITIATOR_PARAMETER_NAME)))
			for(String otherAgent : otherAgents)
				sendMessage("ping 0", getComponentEndpoint(COMPONENT_ADDRESS), otherAgent, COMPONENT_ADDRESS);
	}
}
