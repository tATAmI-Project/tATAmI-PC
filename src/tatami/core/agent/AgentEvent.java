package tatami.core.agent;

import java.util.HashMap;
import java.util.Map;

import net.xqhs.util.config.Config;

/**
 * The class stores an agent event, characterized by its type and, optionally, a set of parameters that have names.
 * 
 * @author andreiolaru
 */
public class AgentEvent extends Config
{
	
	/**
	 * The enumeration specified the full set of agent-internal events that can occur.
	 * 
	 * @author Andrei Olaru
	 */
	public static enum AgentEventType {
		
		/**
		 * Event occurs when the agent starts and the components need to be initialized.
		 */
		AGENT_START(AgentSequenceType.CONSTRUCTIVE),
		
		/**
		 * Event occurs when the agent must be destroyed and components need to close.
		 */
		AGENT_STOP(AgentSequenceType.DESTRUCTIVE),
		
		/**
		 * Event occurs when the agent must move to a different machine.
		 */
		BEFORE_MOVE(AgentSequenceType.DESTRUCTIVE),
		
		/**
		 * Event occurs when the agent has just moved to a different machine.
		 */
		AFTER_MOVE(AgentSequenceType.CONSTRUCTIVE),
		
		/**
		 * Event occurs when the agent has received a message. Events of this type will only get posted when no
		 * messaging component exists; otherwise, message routing will be handled by the messaging component.
		 */
		AGENT_MESSAGE(AgentSequenceType.UNORDERED),
		/**
		 * Event occurs when an active input (see AgentGui) is activated by the user and the agent must react to it.
		 */
		GUI_INPUT(AgentSequenceType.UNORDERED),
		
		/**
		 * Event occurs when the start of the simulation is requested by the user.
		 */
		SIMULATION_START(AgentSequenceType.UNORDERED),
		
		/**
		 * Event occurs when the simulation is paused by the user.
		 */
		SIMULATION_PAUSE(AgentSequenceType.UNORDERED),
		
		;
		
		/**
		 * The sequence type that is characteristic to the event.
		 */
		protected AgentSequenceType	sequenceType;
		
		/**
		 * The constructor assigns a sequence type to the event type.
		 * 
		 * @param sequence
		 *            - the sequence type, as a {@link AgentSequenceType} instance.
		 */
		private AgentEventType(AgentSequenceType sequence)
		{
			sequenceType = sequence;
		}
		
		/**
		 * @return the type of sequence associated with the event type.
		 */
		public AgentSequenceType getSequenceType()
		{
			return sequenceType;
		}
	}
	
	/**
	 * The sequence type of an agent event specifies the order in which components should be notified of the event.
	 * 
	 * @author Andrei Olaru
	 */
	public static enum AgentSequenceType {
		
		/**
		 * The components should be invoked in the order they were added.
		 */
		CONSTRUCTIVE,
		
		/**
		 * The components should be invoked in inverse order as to that in which they were added.
		 */
		DESTRUCTIVE,
		
		/**
		 * The components can be invoked in any order.
		 */
		UNORDERED,
	}
	
	/**
	 * The interface should be implemented by a class that can handle agent events for an agent component. Each
	 * {@link AgentComponent} instance is able to register, for each event, an event handler.
	 * <p>
	 * The class also contains enumerations relevant to event handling: event types ( {@link AgentEventType}) and types
	 * of sequences for events ({@link AgentSequenceType}).
	 * 
	 * @author Andrei Olaru
	 */
	public interface AgentEventHandler
	{
		/**
		 * The method is invoked whenever the event is posted to the {@link CompositeAgent} the component is part of.
		 * <p>
		 * The handlers in various components will be invoked (through the method in {@link AgentComponent}) in the
		 * order specified by the {@link AgentSequenceType} associated with the event.
		 * 
		 * @param event
		 *            - the event that occurred.
		 */
		public void handleEvent(AgentEvent event);
	}
	
	/**
	 * The type of the event. Is one of {@link AgentEventType}.
	 */
	AgentEventType		type;
	
	/**
	 * The parameters of the event, associated with their name.
	 */
	Map<String, Object>	parameters	= new HashMap<String, Object>();
	
	/**
	 * Creates a new agent event.
	 * 
	 * @param eventType
	 *            - the type of the event.
	 */
	public AgentEvent(AgentEventType eventType)
	{
		setType(eventType);
	}
	
	/**
	 * Getter for the type of the event.
	 * 
	 * @return the type of the event.
	 */
	public AgentEventType getType()
	{
		return type;
	}
	
	/**
	 * Sets the type of the event.
	 * 
	 * @param eventType
	 *            - the type of the event.
	 */
	private void setType(AgentEventType eventType)
	{
		type = eventType;
	}
	
	/**
	 * Adds a parameter to the event.
	 * 
	 * @param name
	 *            - the name associated with the parameter.
	 * @param value
	 *            - the value of the parameter.
	 * @return the previous value associated with the name, if any; <code>null</code> otherwise.
	 * @throws ConfigLockedException
	 *             if the method is called after the event has been posted (underlying {@link Config} instance is
	 *             locked).
	 */
	public Object addParameter(String name, Object value) throws ConfigLockedException
	{
		locked();
		return parameters.put(name, value);
	}
	
	/**
	 * Retrieves a parameter of the event.
	 * 
	 * @param name
	 *            - the name associated with the parameter.
	 * @return the value associated with the name.
	 */
	public Object getParameter(String name)
	{
		return parameters.get(name);
	}
	
	@Override
	public String toString()
	{
		return type + ":" + parameters.toString();
	}
	
}
