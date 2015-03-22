package tatami.core.agent;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.xqhs.util.XML.XMLTree.XMLNode;
import net.xqhs.util.logging.Logger;
import net.xqhs.util.logging.LoggerSimple.Level;
import net.xqhs.util.logging.UnitComponentExt;
import tatami.core.agent.AgentComponent.AgentComponentName;
import tatami.core.agent.claim.ClaimComponent;
import tatami.core.agent.parametric.AgentParameterName;
import tatami.core.agent.parametric.AgentParameters;
import tatami.core.util.platformUtils.PlatformUtils;
import tatami.sclaim.constructs.basic.ClaimAgentDefinition;
import tatami.simulation.AgentCreationData;
import tatami.simulation.AgentLoader;
import tatami.simulation.AgentManager;
import tatami.simulation.ClaimUtils;
import tatami.simulation.PlatformLoader;

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
	 * The name of the parameter in the {@link AgentParameters} list that corresponds to a component entry.
	 */
	private static final String	COMPONENT_PARAMETER_NAME	= "agent_component";
	
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
	public boolean preload(AgentCreationData agentCreationData, PlatformLoader platformLoader, Logger log)
	{
		String logPre = agentCreationData.getAgentName() + ":"; // FIXME: use a subordinate log for each preload.
		Iterator<XMLNode> componentIt = agentCreationData.getNode().getNodeIterator(COMPONENT_NODE_NAME);
		List<Map.Entry<String, Object>> componentData = new ArrayList<Map.Entry<String, Object>>();
		while(componentIt.hasNext())
		{
			XMLNode componentNode = componentIt.next();
			String componentName = componentNode.getAttributeValue(COMPONENT_NAME_ATTRIBUTE);
			System.out.println(" ---------- bbb " + componentName);
			
			// get component class
			String componentClass = componentNode.getAttributeValue(COMPONENT_CLASS_ATTRIBUTE);
			if(componentClass == null)
			{
				AgentComponentName component = AgentComponentName.toComponentName(componentName);
				if(component != null)
				{
					if(platformLoader != null)
					{
						String recommendedClass = platformLoader.getRecommendedComponentClass(component);
						if(recommendedClass != null)
							componentClass = recommendedClass;
					}
					if(componentClass == null)
						componentClass = component.getClassName();
				}
				else
				{
					log.error(logPre + "Component [" + componentName
							+ "] unknown and component class not specified. Component will not be available.");
					continue;
				}
			}
			if(componentClass == null)
			{
				log.error(logPre + "Component class not specified for component [" + componentName
						+ "]. Component will not be available.");
				continue;
			}
			
			// TODO: also check parameters
			if(PlatformUtils.classExists(componentClass)) {
				log.trace(logPre + "component [" + componentName + "] can be loaded");
			}
			else
			{
				log.error(logPre + "Component class [" + componentName + " | " + componentClass
						+ "] not found; it will not be loaded.");
				continue;
			}
			
			// TODO emma maybe change Object to String as before
			// get component parameters
			// FIXME pentru mutat initializarea componentei
			Set<Map.Entry<String, Object>> componentParameters = new HashSet<Map.Entry<String, Object>>();
			Iterator<XMLNode> paramsIt = componentNode.getNodeIterator(PARAMETER_NODE_NAME);
			while(paramsIt.hasNext())
			{
				XMLNode param = paramsIt.next();
				componentParameters.add(new AbstractMap.SimpleEntry<String, Object>(param
						.getAttributeValue(PARAMETER_NAME), param.getAttributeValue(PARAMETER_VALUE)));
			}
			
			/* create the associated ClaimAgentDefinition */
			if (AgentComponentName.S_CLAIM_COMPONENT.toString().toLowerCase().contains(componentName)) {
				ClaimAgentDefinition cad = ClaimUtils.fillCAD(agentCreationData.getParameters().get(AgentParameterName.AGENT_CLASS.toString()), 
															  agentCreationData.getParameters().getValues(AgentParameterName.JAVA_CODE.toString()), 
															  agentCreationData.getParameters().getValues(AgentParameterName.AGENT_PACKAGE.toString()), 
															  (UnitComponentExt)log);
				componentParameters.add(new AbstractMap.SimpleEntry<String, Object>(AgentParameterName.AGENT_CLASS.toString(), cad));
			}
			
			Object argument = null;
			if(AgentComponentName.PARAMETRIC_COMPONENT.componentName().equals(componentName)) {
				argument = agentCreationData.getParameters();
				System.out.println(" ---- pe prima");
			}
			else if(!componentParameters.isEmpty()) {
				argument = componentParameters;
				System.out.println(" ------ pe a doua");
			}
						 
			componentData.add(new AbstractMap.SimpleEntry<String, Object>(componentClass, argument));
		}
		
		agentCreationData.getParameters().addObject(COMPONENT_PARAMETER_NAME, componentData);
		return true;
	}
	
	@Override
	public AgentManager load(AgentCreationData agentCreationData)
	{
		UnitComponentExt log = (UnitComponentExt) new UnitComponentExt()
				.setUnitName("agent " + agentCreationData.getAgentName() + " loader").setLogLevel(Level.ALL)
				.setLoggerType(PlatformUtils.platformLogType());
		CompositeAgent agent = new CompositeAgent();
		
		@SuppressWarnings("unchecked")
		// FIXME
		List<Map.Entry<String, Object>> componentData = (List<Entry<String, Object>>) agentCreationData.getParameters()
				.getObject(COMPONENT_PARAMETER_NAME);
		for(Object compObj : componentData)
		{
			@SuppressWarnings("unchecked")
			// FIXME
			Entry<String, Object> compEntry = (Entry<String, Object>) compObj;
			String componentClass = compEntry.getKey();
			Object argument = compEntry.getValue();
			
			try
			{
				if(argument != null)
					agent.addComponent((AgentComponent) PlatformUtils.loadClassInstance(this, componentClass, argument));
				else
					agent.addComponent((AgentComponent) PlatformUtils.loadClassInstance(this, componentClass,
							new Object[0]));
				log.trace("component [" + componentClass + "] loaded for agent [" + agentCreationData.getAgentName()
						+ "].");
			} catch(Exception e)
			{
				log.error("Component [" + componentClass + "] failed to load; it will not be available for agent ["
						+ agentCreationData.getAgentName() + "]:" + PlatformUtils.printException(e));
			}
		}
		log.trace("agent [" + agentCreationData.getAgentName() + "] loaded.");
		
		/* start ClaimComponent if any */
		if (agent.hasComponent(AgentComponentName.S_CLAIM_COMPONENT)) {
			((ClaimComponent) agent.getComponent(AgentComponentName.S_CLAIM_COMPONENT)).setup();
		}
		log.doExit();
		return agent;
	}
}
