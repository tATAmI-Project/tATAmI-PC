package tatami.core.agent;

/**
 * The interface should be implemented by a class that can handle agent events for an agent
 * component. Each {@link AgentComponent} instance is able to register, for each event, an event
 * handler.
 * <p>
 * The class also contains enumerations relevant to event handling: event types (
 * {@link AgentEventType}) and types of sequences for events ({@link AgentSequenceType}).
 * 
 * @author Andrei Olaru
 */
public interface AgentEventHandler
{
	/**
	 * The sequence type of an agent event specifies the order in which components should be
	 * notified of the event.
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
		AGENT_EXIT(AgentSequenceType.DESTRUCTIVE),
		
		/**
		 * Event occurs when the agent must move to a different machine.
		 */
		BEFORE_MOVE(AgentSequenceType.DESTRUCTIVE),
		
		/**
		 * Event occurs when the agent has just moved to a different machine.
		 */
		AFTER_MOVE(AgentSequenceType.CONSTRUCTIVE),
		
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
	 * The method is invoked whenever the event is posted to the {@link CompositeAgent} the
	 * component is part of.
	 * <p>
	 * The handlers in various components will be invoked (through the method in
	 * {@link AgentComponent}) in the order specified by the {@link AgentSequenceType} associated
	 * with the event.
	 * 
	 * @param eventType
	 *            - the type of the event that occurred.
	 * @param eventData
	 *            - data that is associated with the event.
	 */
	public void handleEvent(AgentEventType eventType, Object eventData);
}
