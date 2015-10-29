package tatami.core.agent.movement;

import tatami.core.agent.AgentComponent;
import tatami.core.agent.AgentEvent;
import tatami.core.agent.AgentEvent.AgentEventHandler;
import tatami.core.agent.AgentEvent.AgentEventType;
import tatami.core.agent.CompositeAgent;

public class MovementComponent extends AgentComponent {
	
	
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
	 * The name of the parameter in an {@link AgentEvent} associated with a message, that corresponds to the target
	 * address of the message.
	 */
	public static final String						DESTINATION_PARAMETER	= "message address";
	
	protected MovementComponent(CompositeAgent parent) {
		super(AgentComponentName.MESSAGING_COMPONENT);

		registerHandler(AgentEventType.AGENT_MESSAGE, new AgentEventHandler() {
			@Override
			public void handleEvent(AgentEvent event) {
				handleMessage(event);
			}
		});
	}
	
	protected void handleMessage(AgentEvent event)
	{
		if(event == null)
		{
			if(getAgentLog() != null)
				getAgentLog().error("Event is null.");
			return;
		}
		
		String source = (String) event.getParameter(SOURCE_PARAMETER);
		
		String content = (String) event.getParameter(CONTENT_PARAMETER);
		
		String destination = (String) event.getParameter(DESTINATION_PARAMETER);
		
		if(content.equals("move")){
			//Forward this message to the platform which will set the state of the agent to transient
		}
		
		
	}

}
