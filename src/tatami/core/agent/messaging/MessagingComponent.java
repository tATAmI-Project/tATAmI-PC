package tatami.core.agent.messaging;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.xqhs.util.config.Config.ConfigLockedException;
import net.xqhs.util.logging.Debug.DebugItem;
import tatami.core.agent.AgentComponent;
import tatami.core.agent.AgentEvent;
import tatami.core.agent.AgentEvent.AgentEventHandler;
import tatami.core.agent.AgentEvent.AgentEventType;
import tatami.core.agent.CompositeAgent;
import tatami.core.util.platformUtils.PlatformUtils;

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
 * The communication is abstracted internally to the agent as exchanging messages between endpoints, where each endpoint
 * is identified by a {@link String} address composed of multiple elements separated by slashes (
 * {@link #ADDRESS_SEPARATOR}={@value #ADDRESS_SEPARATOR}). This means that an endpoint may be identified by a URI (if
 * the {@link MessagingComponent} implementation supports it), or, for instance, by an address of the type
 * "Agent1/Visualization" (this will work with Jade, where agents are addressable by name).
 * <p>
 * Messages are sent from one endpoint to another, and contain a {@link String}. Specific implementations may parse the
 * string for additional structure.
 * <p>
 * We make a difference between [complete] endpoints (or paths) and internal endpoints (or paths). An agent address
 * concatenated with an internal path should result in a complete path. Internal paths should begin with a slash.
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
	 * Debugging settings for messaging components.
	 * 
	 * @author Andrei Olaru
	 */
	public static enum MessagingDebug implements DebugItem {
		/**
		 * General messaging debugging switch.
		 */
		DEBUG_MESSAGING(true),
		
		;
		
		/**
		 * Activation state.
		 */
		boolean	isset;
		
		/**
		 * Default constructor.
		 * 
		 * @param set
		 *            - activation state.
		 */
		private MessagingDebug(boolean set)
		{
			isset = set;
		}
		
		@Override
		public boolean toBool()
		{
			return isset;
		}
	}
	
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
	public static final String						ADDRESS_SEPARATOR		= "/";
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
	 * invoked in no particular order. The endpoint is an internal path, rather than a complete path.
	 */
	protected Map<String, Set<AgentEventHandler>>	messageHandlers			= new HashMap<String, Set<AgentEventHandler>>();
	
	/**
	 * Default constructor.
	 */
	public MessagingComponent()
	{
		super(AgentComponentName.MESSAGING_COMPONENT);
		
		registerHandler(AgentEventType.AGENT_MESSAGE, new AgentEventHandler() {
			@Override
			public void handleEvent(AgentEvent event)
			{
				handleMessage(event);
			}
		});
	}
	
	/**
	 * Extending classes should call this method to defer to {@link MessagingComponent} the effort of packing message
	 * data into an {@link AgentEvent} and posting that event in the agent event queue.
	 * 
	 * @param source
	 *            - the source of the message, as a complete endpoint
	 * @param destination
	 *            - the internal destination of the message, as an internal endpoint
	 * @param content
	 *            - the content of the message
	 */
	protected void receiveMessage(String source, String destination, String content)
	{
		AgentEvent event = new AgentEvent(AgentEventType.AGENT_MESSAGE);
		try
		{
			event.addParameter(MessagingComponent.SOURCE_PARAMETER, source);
			event.addParameter(MessagingComponent.DESTINATION_PARAMETER, destination);
			event.addParameter(MessagingComponent.CONTENT_PARAMETER, content);
		} catch(ConfigLockedException e)
		{
			// should never happen.
			throw new IllegalStateException("Config locked:" + PlatformUtils.printException(e));
		}
		getAgentLog().dbg(MessagingDebug.DEBUG_MESSAGING, "Received message from [] to [] with content [].", source,
				destination, content);
		
		postAgentEvent(event);
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
		String destinationInternal = extractInternalAddress(event, "");
		if(destinationInternal.length() == 0)
			// if no internal path, make it the root path (a corect internal path).
			destinationInternal = "/";
		for(Map.Entry<String, Set<AgentEventHandler>> entry : messageHandlers.entrySet())
		{
			getAgentLog().dbg(MessagingDebug.DEBUG_MESSAGING, "Comparing: [] to declared []", destinationInternal,
					entry.getKey());
			if(destinationInternal.startsWith(entry.getKey()))
				// prefix matches
				for(AgentEventHandler receiver : entry.getValue())
				{
					getAgentLog().dbg(MessagingDebug.DEBUG_MESSAGING, "Dispatching to []", receiver);
					receiver.handleEvent(event);
				}
		}
	}
	
	/**
	 * Registers a new message receiver for the specified prefix (internal path). Messages to a target beginning with
	 * the prefix (after the prefix of the agent has been removed) will be delivered to this handler.
	 * <p>
	 * Multiple handlers may be registered for the same prefix.
	 * <p>
	 * WARNING: this implementation should not be confused with the method it overrides from {@link AgentComponent}.
	 * This is the method that actually registers the receiver with the messaging component. In other components (that
	 * do not extend {@link MessagingComponent}), the method will be used to relay calls either to the method in the
	 * {@link MessagingComponent} implementation or to the {@link CompositeAgent} instance.
	 * 
	 * @param receiver
	 *            - the message receiver, as an {@link AgentEventHandler} instance.
	 * @param prefixElements
	 *            - the prefix of the target, as elements of the internal path.
	 * @return always <code>true</code>, according to the meaning of the return value as given in the overridden method.
	 */
	@Override
	protected boolean registerMessageReceiver(AgentEventHandler receiver, String... prefixElements)
	{
		// TODO handle null for arguments
		String prefix = makeInternalPath(prefixElements);
		if(!messageHandlers.containsKey(prefix))
			messageHandlers.put(prefix, new HashSet<AgentEventHandler>());
		messageHandlers.get(prefix).add(receiver);
		return true;
	}
	
	/**
	 * The method creates a complete path by attaching the specified elements and placing slashes between them.
	 * <p>
	 * E.g. it produces targetAgent/element1/element2/element3
	 * 
	 * @param targetAgent
	 *            - the name of the searched agent.
	 * @param internalElements
	 *            - the elements in the internal path.
	 * @return the complete path/address.
	 */
	public String makePath(String targetAgent, String... internalElements)
	{
		return makePathHelper(getAgentAddress(targetAgent), internalElements);
	}
	
	/**
	 * The method creates an internal path by attaching the specified elements and placing slashes between them. The
	 * result begins with a slash.
	 * <p>
	 * E.g. it produces /element1/element2/element3
	 * 
	 * @param internalElements
	 *            - the elements in the path.
	 * @return the complete path/address.
	 */
	@SuppressWarnings("static-method")
	public String makeInternalPath(String... internalElements)
	{
		return makePathHelper(null, internalElements);
	}
	
	/**
	 * The method creates a complete path by attaching the specified elements to the address of this agent.
	 * <p>
	 * E.g. it produces thisAgent/element1/element2/element3
	 * 
	 * @param elements
	 *            - the elements in the path.
	 * @return the complete path/address.
	 */
	public String makeLocalPath(String... elements)
	{
		return makePathHelper(getAgentAddress(), elements);
	}
	
	/**
	 * Produces an address by assembling the start of the address with the rest of the elements. They will be separated
	 * by the address separator specified as constant.
	 * <p>
	 * Elements that are <code>null</code> will not be assembled in the path.
	 * <p>
	 * If the start is <code>null</code> the result will begin with a slash.
	 * 
	 * @param start
	 *            - start of the address.
	 * @param elements
	 *            - other elements in the address
	 * @return the resulting address.
	 */
	public static String makePathHelper(String start, String... elements)
	{
		String ret = (start != null) ? start : "";
		for(String elem : elements)
			if(elem != null)
				ret += ADDRESS_SEPARATOR + elem;
		return ret;
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
	 * The method produces the string address of an agent on the same platform, being provided with the agent name.
	 * <p>
	 * This address can subsequently be suffixed with more path elements by using {@link #makePath(String, String...)}.
	 * 
	 * @param agentName
	 *            - the name of the target agent.
	 * @return the string address of the target agent.
	 */
	public abstract String getAgentAddress(String agentName);
	
	/**
	 * The method extracts, from the complete endpoint path, the elements of the path, after the address of the agent
	 * itself and also eliminating specified prefix elements.
	 * 
	 * @param event
	 *            - the event to extract the address from.
	 * @param prefixElementsToRemove
	 *            - elements of the prefix to remove from the address.
	 * @return the elements that were extracted from the address, following the address of the agent and the specified
	 *         prefix elements; <code>null</code> if an error occurred (address not in the current agent or prefix
	 *         elements not part of the address in the specified order).
	 */
	public String[] extractInternalAddressElements(AgentEvent event, String... prefixElementsToRemove)
	{
		String prefix = makeInternalPath(prefixElementsToRemove);
		String rem = extractInternalAddress(event, prefix);
		String[] ret = rem.substring(1).split(ADDRESS_SEPARATOR);
		return ret;
	}
	
	/**
	 * The method extracts, from the complete endpoint path, the remaining internal address, after the address of the
	 * agent itself and also eliminating a specified prefix.
	 * 
	 * @param event
	 *            - the event to extract the address from.
	 * @param prefixToRemove
	 *            - prefix to remove from the address. The prefix must be an internal path (starting with slash, but not
	 *            ending with slash).
	 * @return the remaining internal address (starting with slash).
	 */
	public String extractInternalAddress(AgentEvent event, String prefixToRemove)
	{
		String address = (String) event.getParameter(DESTINATION_PARAMETER);
		if(!address.startsWith(getAgentAddress()))
			return null;
		String rem = address.substring(getAgentAddress().length());
		if(!rem.startsWith(prefixToRemove))
			return null;
		return rem.substring(prefixToRemove.length());
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
	 *            - the target (complete) endpoint of the message.
	 * @param source
	 *            - the source (internal) endpoint of the message.
	 * @param content
	 *            - the content of the message.
	 * @return <code>true</code> if the message was sent successfully.
	 */
	public abstract boolean sendMessage(String target, String source, String content);
}
