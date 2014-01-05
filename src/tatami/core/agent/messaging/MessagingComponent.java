package tatami.core.agent.messaging;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import tatami.core.agent.AgentComponent;
import tatami.core.agent.AgentEvent;
import tatami.core.agent.CompositeAgent;
import tatami.core.agent.AgentEvent.AgentEventHandler;
import tatami.core.agent.AgentEvent.AgentEventType;

/**
 * The messaging component should handle all communication between the agent and other agents. Note that the existence
 * of such a component in the {@link CompositeAgent} modifies its behavior: without any messaging component, received
 * messages are notified as agent events to all components of the agent; with it, messages are passed to the messaging
 * component, which then should route them to the appropriate receivers. // TODO: mention this in CompositeAgent.
 * <p>
 * The class should be extended by any component that offers to the agent services of communication with other agents.
 * Extending classes should override the <code>parentChangeNotifier</code> method to register an event handler for
 * {@link AgentEventType#AGENT_MESSAGE}.
 * <p>
 * The communication is abstracted as exchanging messages between endpoints, where each endpoint is identified by a
 * {@link String} address composed of multiple elements separated by slashes. This means that an endpoint may be
 * identified by a URI (if the {@link MessagingComponent} implementation supports it, or, for instance, by an address of
 * the type "Agent1/Visualization" (this will work with Jade, where agents are addressable by name).
 * <p>
 * Messages are sent from an endpoint to another, and contain a {@link String}. Specific implementations may parse the
 * string for additional structure.
 * <p>
 * The abstract class implements some basic methods for working with slash-delimited addresses, and offers the method
 * for registering message handlers.
 * <p>
 * TODO: make this not abstract by implementing a simple local messaging service.
 * 
 * @author Andrei Olaru
 */
public abstract class MessagingComponent extends AgentComponent
{
	/**
	 * An implementation of the interface must be able to receive messages received by the agent.
	 * 
	 * @author Andrei Olaru
	 */
	public interface MessageReceiver
	{
		/**
		 * Method invoked when a message is received that matches the registration of this instance.
		 * 
		 * @param source
		 *            - the source endpoint.
		 * @param target
		 *            - the target endpoint.
		 * @param content
		 *            - the content of the message.
		 */
		void receiveMessage(String source, String target, String content);
	}
	
	/**
	 * The serial UID.
	 */
	private static final long						serialVersionUID		= -7541956285166819418L;
	
	/**
	 * The string separating elements of an endpoint address.
	 */
	protected static final String					ADDRESS_SEPARATOR		= "/";
	/**
	 * The name of the parameter in an {@link AgentEvent} associated with a message, that corresponds to the target
	 * address of the message.
	 */
	public static final String						DESTINATION_PARAMETER	= "message address";
	/**
	 * The name of the parameter in an {@link AgentEvent} associated with a message, that corresponds to the content of
	 * the message.
	 */
	public static final String						CONTENT_PARAMETER		= "message content";
	/**
	 * The name of the parameter in an {@link AgentEvent} associated with a message, that corresponds to the source of
	 * the message.
	 */
	public static final String						SOURCE_PARAMETER		= "message source";
	
	/**
	 * The {@link Map} of {@link AgentEventHandler} instances that were registered with this component, associated with
	 * their respective endpoints. Multiple handlers may be registered with the same endpoint. These handlers will be
	 * invoked in no particular order.
	 */
	protected Map<String, Set<AgentEventHandler>>	messageHandlers			= new HashMap<String, Set<AgentEventHandler>>();
	
	/**
	 * Default constructor.
	 */
	public MessagingComponent()
	{
		super(AgentComponentName.MESSAGING_COMPONENT);
	}
	
	@Override
	protected void parentChangeNotifier(CompositeAgent oldParent)
	{
		super.parentChangeNotifier(oldParent);
		registerHandler(AgentEventType.AGENT_MESSAGE, new AgentEventHandler() {
			@Override
			public void handleEvent(AgentEvent event)
			{
				handleMessage(event);
			}
		});
	}
	
	/**
	 * Handles a message received by the agent. This method should be overridden for specific implementations.
	 * 
	 * @param event
	 *            - the event corresponding to the message.
	 */
	protected void handleMessage(AgentEvent event)
	{
		// FIXME: do checks
		String destination = (String) event.getParameter(DESTINATION_PARAMETER);
		for(Map.Entry<String, Set<AgentEventHandler>> entry : messageHandlers.entrySet())
		{
			if(getVisualizable() != null)
				getVisualizable().getLog().trace(
						"Comparing: [" + destination + "] to declared [" + entry.getKey() + "]");
			if(destination.startsWith(entry.getKey()))
				// prefix matches
				for(AgentEventHandler receiver : entry.getValue())
					receiver.handleEvent(event);
		}
	}
	
	/**
	 * Gets the address of the agent, as it is specific to the implementation.
	 * 
	 * @return the address of the parent agent, if any; <code>null</code> otherwise.
	 */
	public String getAgentAddress()
	{
		return getAgentName();
	}
	
	/**
	 * Registers a new message receiver for the specified prefix. Messages to a target beginning with the prefix (after
	 * the prefix of the agent has been removed) will be delivered to this handler.
	 * <p>
	 * WARNING: this implementation should not be confused with the method it overrides from {@link AgentComponent}.
	 * THis is the method that actually registers the receiver with the messaging component. In other components (that
	 * do not extend {@link MessagingComponent}), the method will be used to relay calls either to the method in the
	 * {@link MessagingComponent} implementation or to the {@link CompositeAgent} instance.
	 * 
	 * @param prefix
	 *            - the prefix of the target.
	 * @param receiver
	 *            - the message receiver, as an {@link AgentEventHandler} instance.
	 * @return always <code>true</code>, according to the meaning of the return value as given in the overridden method.
	 */
	@Override
	protected boolean registerMessageReceiver(String prefix, AgentEventHandler receiver)
	{
		if(!messageHandlers.containsKey(prefix))
			messageHandlers.put(prefix, new HashSet<AgentEventHandler>());
		messageHandlers.get(prefix).add(receiver);
		return true;
	}
	
	/**
	 * The method helps producing a local endpoint address, using the address of the agent and several given path
	 * elements.
	 * 
	 * @param elements
	 *            - the elements in the path.
	 * @return the full path/address.
	 */
	public String makePath(String... elements)
	{
		String localAdr = getAgentAddress();
		return makePathHelper((localAdr != null) ? localAdr : "", elements);
	}
	
	/**
	 * The method produces the string address of an agent on the same platform, being provided with the agent name and
	 * container. // FIXME: what if the container information is out of date.
	 * <p>
	 * This address can subsequently be suffixed with more path elements by using
	 * {@link #makePathHelper(String, String...)}.
	 * 
	 * @param agentName
	 *            - the name of the target agent.
	 * @param containerName
	 *            - the container of the target agent
	 * @return the string address of the target agent.
	 */
	public abstract String getAgentAddress(String agentName, String containerName);
	
	/**
	 * The method extracts, from the address of an endpoint inside this agent, the elements of the path, after the
	 * address of the agent itself and also eliminating specified prefix elements.
	 * 
	 * @param event
	 *            - the event to extract the address from.
	 * @param prefixToRemove
	 *            - elements of the prefix to remove from the address.
	 * @return the elements that were extracted from the address, following the address of the agent and the specified
	 *         prefix elements; <code>null</code> if an error occurred (address not in the current agent or prefix
	 *         elements not part of the address in the specified order).
	 */
	public String[] extractInternalAddress(AgentEvent event, String... prefixToRemove)
	{
		String address = (String) event.getParameter(DESTINATION_PARAMETER);
		if(!address.startsWith(getAgentAddress()))
			return null;
		String rem = address.substring(getAgentAddress().length());
		String[] elements = rem.split(ADDRESS_SEPARATOR);
		int i = 0;
		for(String prefix : prefixToRemove)
		{
			if(!prefix.equals(elements[i]))
				return null;
			i++;
		}
		String[] ret = Arrays.copyOfRange(elements, prefixToRemove.length, elements.length - 1);
		return ret;
	}
	
	/**
	 * The method extracts from the message event the content of the message.
	 * 
	 * @param event
	 *            - the event to extract the content from.
	 * @return the content of the message.
	 */
	@SuppressWarnings("static-method")
	public String extractContent(AgentEvent event)
	{
		String content = (String) event.getParameter(CONTENT_PARAMETER);
		return content;
	}
	
	/**
	 * Sends a message to another agent, according to the specific implementation.
	 * 
	 * @param target
	 *            - the target endpoint of the message.
	 * @param source
	 *            - the source endpoint of the message.
	 * @param content
	 *            - the content of the message.
	 * @return <code>true</code> if the message was sent successfully.
	 */
	public abstract boolean sendMessage(String target, String source, String content);
	
	/**
	 * Produces an address by assembling the start of the address with the rest of the elements. They will be separated
	 * by the address separator specified as constant.
	 * <p>
	 * Elements that are <code>null</code> will not be assembled in the path.
	 * 
	 * @param start
	 *            - start of the address.
	 * @param elements
	 *            - other elements in the address
	 * @return the resulting address.
	 */
	public static String makePathHelper(String start, String... elements)
	{
		String ret = start;
		for(String elem : elements)
			if(elem != null)
				ret += ADDRESS_SEPARATOR + elem;
		return ret;
	}
}
