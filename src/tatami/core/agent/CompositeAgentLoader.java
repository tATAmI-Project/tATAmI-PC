package tatami.core.agent;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.xqhs.util.logging.Logger.Level;
import net.xqhs.util.logging.UnitComponentExt;
import tatami.core.agent.AgentComponent.AgentComponentName;
import tatami.core.util.platformUtils.PlatformUtils;
import tatami.pc.util.XML.XMLTree.XMLNode;
import tatami.simulation.AgentCreationData;
import tatami.simulation.AgentLoader;
import tatami.simulation.AgentManager;

/**
 * Agent loader for agents based on {@link CompositeAgent}.
 * 
 * @author Andrei Olaru
 */
public class CompositeAgentLoader implements AgentLoader
{
	/**
	 * Name of XML nodes in the scenario representing components.
	 */
	private static final String	COMPONENT_NODE_NAME			= "component";
	/**
	 * The name of the attribute representing the name of the component in the component node.
	 */
	private static final String	COMPONENT_NAME_ATTRIBUTE	= "name";
	/**
	 * The name of the attribute representing the class of the component in the component node. The class may not be
	 * specified, it the component is standard and its class is specified by the corresponding
	 * {@link AgentComponentName} entry.
	 */
	private static final String	COMPONENT_CLASS_ATTRIBUTE	= "classpath";
	/**
	 * The name of nodes containing component parameters.
	 */
	private static final String	PARAMETER_NODE_NAME			= "parameter";
	/**
	 * The name of the attribute of a parameter node holding the name of the parameter.
	 */
	private static final String	PARAMETER_NAME				= "name";
	/**
	 * The name of the attribute of a parameter node holding the value of the parameter.
	 */
	private static final String	PARAMETER_VALUE				= "value";
	
	/**
	 * The constructor does not do any initializations.
	 */
	public CompositeAgentLoader()
	{
		// nothing to do.
	}
	
	@Override
	public String getName()
	{
		return StandardAgentLoaderType.COMPOSITE.toString();
	}
	
	@Override
	public AgentLoader setConfig(XMLNode configuration)
	{
		// no configuration to load
		return this;
	}
	
	@Override
	public AgentManager load(AgentCreationData agentCreationData)
	{
		UnitComponentExt log = (UnitComponentExt) new UnitComponentExt().setUnitName(
				"agent " + agentCreationData.getAgentName() + " loader").setLogLevel(Level.ALL);
		CompositeAgent agent = new CompositeAgent();
		Iterator<XMLNode> componentIt = agentCreationData.getNode().getNodeIterator(COMPONENT_NODE_NAME);
		while(componentIt.hasNext())
		{
			XMLNode componentNode = componentIt.next();
			String componentName = componentNode.getAttributeValue(COMPONENT_NAME_ATTRIBUTE);
			
			// get component class
			String componentClass = componentNode.getAttributeValue(COMPONENT_CLASS_ATTRIBUTE);
			if(componentClass == null)
				try
				{
					componentClass = AgentComponentName.valueOf(componentName).getClassName();
				} catch(Exception e)
				{
					log.error("Component [" + componentName
							+ "] unknown and component class not specified. Component will not be available.");
					continue;
				}
			if(componentClass == null)
			{
				log.error("Component class not specified for component [" + componentName
						+ "]. Component will not be available.");
				continue;
			}
			
			// get component parameters
			Set<Map.Entry<String, String>> componentParameters = new HashSet<Map.Entry<String, String>>();
			Iterator<XMLNode> paramsIt = componentNode.getNodeIterator(PARAMETER_NODE_NAME);
			while(paramsIt.hasNext())
			{
				XMLNode param = paramsIt.next();
				componentParameters.add(new AbstractMap.SimpleEntry<String, String>(param
						.getAttributeValue(PARAMETER_NAME), param.getAttributeValue(PARAMETER_VALUE)));
			}
			
			try
			{
				Object argument = null;
				if(AgentComponentName.PARAMETRIC_COMPONENT.toString().equals(componentName))
					argument = agentCreationData.getParameters();
				else if(!componentParameters.isEmpty())
					argument = componentParameters;
				if(argument != null)
					agent.addComponent((AgentComponent) PlatformUtils.loadClassInstance(this, componentClass, argument));
				else
					agent.addComponent((AgentComponent) PlatformUtils.loadClassInstance(this, componentClass,
							new Object[0]));
				log.trace("component [" + componentName + "] loaded for agent [" + agentCreationData.getAgentName()
						+ "].");
			} catch(Exception e)
			{
				log.error("Component [" + componentName + "] failed to load; it will not be available:"
						+ PlatformUtils.printException(e));
			}
		}
		// log.info("agent [" + agentCreationData.getAgentName() + "] loaded.");
		log.doExit();
		
		return agent;
	}
}
