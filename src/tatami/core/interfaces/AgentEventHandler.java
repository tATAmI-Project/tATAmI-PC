package tatami.core.interfaces;

public interface AgentEventHandler
{
	public static enum AgentSequenceType {
		
		CONSTRUCTIVE,
		
		DESTRUCTIVE,
		
		UNORDERED,
	}
	
	public static enum AgentEventType {
		
		AGENT_START(AgentSequenceType.CONSTRUCTIVE),
		
		BEFORE_MOVE(AgentSequenceType.DESTRUCTIVE),
		
		AFTER_MOVE(AgentSequenceType.CONSTRUCTIVE),
		
		AGENT_EXIT(AgentSequenceType.DESTRUCTIVE),
		
		;
		
		protected AgentSequenceType	sequenceType;
		
		private AgentEventType(AgentSequenceType sequence)
		{
			sequenceType = sequence;
		}
		
		public AgentSequenceType getSequenceType()
		{
			return sequenceType;
		}
	}
	
	public void handleEvent();
}
