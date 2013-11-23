package tatami.core.agent.messaging;

import java.util.HashMap;
import java.util.Map;

import tatami.core.agent.AgentComponent;
import tatami.core.agent.AgentEvent.AgentEventHandler;
import tatami.core.agent.CompositeAgent;

public class MessagingComponent extends AgentComponent
{
	private static final long					serialVersionUID	= -7541956285166819418L;
	
	protected Map<String, AgentEventHandler>	messageHandlers		= new HashMap<String, AgentEventHandler>();
	
	protected MessagingComponent(CompositeAgent parent)
	{
		super(AgentComponentName.MESSAGING_COMPONENT);
		
		// TODO: register this as message/event receiver
	}
	
	public boolean registerMessageReceiver(String prefix, AgentEventHandler handler)
	{
		// FIXME: check collisions
		messageHandlers.put(prefix, handler);
		return true;
	}
	
	public String makePath(String... prefixElements)
	{
		String prefix = "";
		for(String elem : prefixElements)
			prefix += elem + "/";
		return prefix;
	}
	
	public String[] extractAddress(String... prefixToRemove)
	{
		String address = ""; // TODO: address extraction
		
		return null;
	}
	
	public String extractContent(Object eventData)
	{
		// TODO
		return null;
	}
	
	public boolean sendMessage(String target, String path, String content)
	{
		// TODO Auto-generated method stub
		return false;
		
	}
}
