package tatami.core.interfaces;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import tatami.core.agent.CompositeAgent;
import tatami.core.interfaces.AgentEventHandler.AgentEventType;

public abstract class AgentComponent implements Serializable
{
	
	public static enum AgentComponentName {
		PARAMETRIC_COMPONENT,
		
		VISUALIZABLE_COMPONENT,
		
		COGNITIVE_COMPONENT,
		
		MESSAGING_COMPONENT,
		
		MOVEMENT_COMPONENT,
		
		WEBSERVICE_COMPONENT,
		
		HIERARCHICAL_COMPONENT,
		
		S_CLAIM_COMPONENT,
		
		JADE_COMPONENT,
		
		;
		
		private static final String	AGENT_COMPONENT_CLASS_SUFFIX	= "Component";
		private static final String	AGENT_COMPONENT_PACKAGE_ROOT	= "tatami.core.agent";
		
		String						componentPackage;
		String						componentClass;
		
		private AgentComponentName(String packagename, String classname)
		{
			// FIXME: check that package and class exist
			componentPackage = packagename;
			componentClass = classname;
		}
		
		private AgentComponentName()
		{
			// FIXME: check that package and class exist
			String compName = this.name().split("_")[0].toLowerCase();
			componentPackage = AGENT_COMPONENT_PACKAGE_ROOT + "." + compName;
			componentClass = compName.substring(0, 1).toUpperCase() + compName.substring(1)
					+ AGENT_COMPONENT_CLASS_SUFFIX;
		}
	}
	
	protected AgentComponentName						componentName	= null;
	protected CompositeAgent							parentAgent;
	protected Map<AgentEventType, AgentEventHandler>	eventHandlers	= new HashMap<AgentEventHandler.AgentEventType, AgentEventHandler>();
	
	protected AgentComponent(CompositeAgent parent, AgentComponentName name)
	{
		parentAgent = parent;
		componentName = name;
	}
	
	protected void registerHandler(AgentEventType event, AgentEventHandler handler)
	{
		// TODO: do checks
		eventHandlers.put(event, handler);
	}
}
