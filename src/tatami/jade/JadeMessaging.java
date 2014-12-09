package tatami.jade;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import net.xqhs.util.config.Config.ConfigLockedException;
import tatami.core.agent.AgentEvent;
import tatami.core.agent.AgentEvent.AgentEventHandler;
import tatami.core.agent.AgentEvent.AgentEventType;
import tatami.core.agent.CompositeAgent;
import tatami.core.agent.claim.ClaimComponent;
import tatami.core.agent.messaging.MessagingComponent;
import tatami.core.agent.visualization.VisualizableComponent;
import tatami.core.util.platformUtils.PlatformUtils;

/**
 * Implements messaging functionality, using the features offered by Jade.
 * 
 * @author Andrei Olaru
 */
public class JadeMessaging extends MessagingComponent
{
	/**
	 * The serial UID.
	 */
	private static final long	serialVersionUID	= 6948064285049451164L;
	
	@Override
	protected void parentChangeNotifier(CompositeAgent oldParent)
	{
		super.parentChangeNotifier(oldParent);
		
		registerHandler(AgentEventType.AGENT_START, new AgentEventHandler() {
			@Override
			public void handleEvent(AgentEvent event)
			{
				initialize();
			}
		});
	}
	
	/**
	 * Performs initialization: registers a behavior to receive messages received by the Jade agent.
	 */
	protected void initialize()
	{
		getWrapper().addBehaviour(new CyclicBehaviour() {
			private static final long	serialVersionUID	= 7120577948083587188L;
			
			@Override
			public void action()
			{
				ACLMessage message = myAgent.receive();
				if(message != null)
				{
					String receiver = myAgent.getLocalName();
					String source = message.getSender().getLocalName();
					String ontology = message.getOntology();
					String protocol = message.getProtocol();
					String conversation = message.getConversationId();
					String content = message.getContent();
					String destination = MessagingComponent.makePathHelper(receiver, ontology, protocol, conversation);
					
					AgentEvent event = new AgentEvent(AgentEventType.AGENT_MESSAGE);
					try
					{
						event.addParameter(MessagingComponent.SOURCE_PARAMETER, source);
						event.addParameter(MessagingComponent.DESTINATION_PARAMETER, destination);
						event.addParameter(MessagingComponent.CONTENT_PARAMETER, content);
					} catch(ConfigLockedException e)
					{
						// should never happen.
						if(getVisualizable() != null)
							getVisualizable().getLog().error("Config locked:" + PlatformUtils.printException(e));
					}
					if(getVisualizable() != null)
						getVisualizable().getLog().dbg(
								MessagingDebug.DEBUG_MESSAGING,
								"Received message from [" + source + "] to [" + destination + "] with content ["
										+ content + "].");
					
					/* check with which behavior does the message corresponde (if it does) */
					// FIXME
					if(content.startsWith("(") && getClaim() != null)
					{
						((ClaimComponent) getClaim()).matchStatement(source, content);
					}
					if(!content.contains("struct message"))
						postAgentEvent(event);
				}
				else
					block();
			}
		});
		
	}
	
	@Override
	public String getAgentAddress(String agentName, String containerName)
	{
		return agentName;
	}
	
	/**
	 * Relay for the overridden method to avoid warning.
	 */
	@Override
	protected void postAgentEvent(AgentEvent event)
	{
		super.postAgentEvent(event);
	}
	
	@Override
	public boolean sendMessage(String target, String source, String content)
	{
		String[] targetElements = target.split(ADDRESS_SEPARATOR, 4);
		int nElements = targetElements.length;
		if(nElements == 0)
			return false;
		ACLMessage message = new ACLMessage(ACLMessage.INFORM);
		message.addReceiver(new AID(targetElements[0], AID.ISLOCALNAME));
		if(nElements > 1)
			message.setOntology(targetElements[1]);
		if(nElements > 2)
			message.setProtocol(targetElements[2]);
		if(nElements > 3)
			message.setConversationId(targetElements[3]);
		message.setContent(content);
		if(getVisualizable() != null && getVisualizable().getLog() != null)
			getVisualizable().getLog().dbg(MessagingDebug.DEBUG_MESSAGING,
					"Sending message to [" + target + "] with content [" + content + "].");
		getWrapper().send(message);
		return true;
	}
	
	/**
	 * Retrieves the wrapping Jade agent.
	 * 
	 * @return the wrapper agent.
	 */
	protected JadeAgentWrapper getWrapper()
	{
		JadeAgentWrapper wrapper;
		try
		{
			wrapper = (JadeAgentWrapper) getPlatformLink();
		} catch(ClassCastException e)
		{
			if(getVisualizable() != null)
				getVisualizable().getLog().error(
						"Platform link is not a jade agent wrapper:" + PlatformUtils.printException(e));
			throw new IllegalStateException("Platform link is not a jade agent wrapper:"
					+ PlatformUtils.printException(e));
		}
		if(wrapper == null)
		{
			if(getVisualizable() != null && getVisualizable().getLog() != null)
				getVisualizable().getLog().error("Platform link is null.");
			throw new IllegalStateException("Platform link is null.");
		}
		return wrapper;
	}
	
	@Override
	public VisualizableComponent getVisualizable()
	{
		return super.getVisualizable();
	}
	
}
