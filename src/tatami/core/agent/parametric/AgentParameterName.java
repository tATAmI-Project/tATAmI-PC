package tatami.core.agent.parametric;

import java.util.Collection;


/**
 * Here be all names of 'registered' parameters (see {@link AgentParameters}) used by any component of the agent.
 * <p>
 * Each parameter has an internal name - the constant in the enum - and the name in the scenario file - given in the
 * constructor of the enum constant.
 * <p>
 * It would be advised that these values be used when reading from the XML file.
 * <p>
 * Note about the javadoc: some links were not included in order to avoid imports outside of the <code>core</code>
 * package.
 * 
 * @author Andrei Olaru
 * 
 */
public enum AgentParameterName {
	
	// ///////// Simulation/Boot
	/**
	 * The class (for non-CLAIM agents) or the type (adf definition) (for CLAIM agents) of the agent.
	 * 
	 * Used by <code>simulation.Boot</code> (to load the agent) and <code>WSAgent</code> (to register the service under
	 * this type).
	 */
	AGENT_CLASS("class"),
	
	/**
	 * The {@link AgentLoader} to use for this agent.
	 */
	AGENT_LOADER("loader"),
	
	/**
	 * The {@link PlatformLoader} to use for this agent.
	 */
	AGENT_PLATFORM("platform"),
	
	/**
	 * Agent packages with classes that are relevant to this agent (GUIs, java functions, etc).
	 * 
	 * Used by {@link VisualizableAgent} (for the GUI) and {@link ClaimAgent} (for java function classes).
	 */
	AGENT_PACKAGE("agentPackage"),
	
	/**
	 * Adf Agent packages with classes that are relevant to this agent.
	 * 
	 * Used by {@link ClaimAgent} (for sclaim code).
	 */
	ADF_PACKAGE("adfPath"),
	
	/**
	 * Specifies the classes with java code to attach to the agent definition.
	 * 
	 * Used by <code>simulation.Boot</code> / <code>claim.ClaimComponent</code>.
	 */
	JAVA_CODE("java-code"),
	
	// ///////// BaseAgent
	/**
	 * The name of the agent.
	 * 
	 * Used by {@link BaseAgent}.
	 */
	AGENT_NAME("name"),
	
	/**
	 * Initial agent knowledge, represented as a text representation of the knowledge graph.
	 * 
	 * Used by {@link BaseAgent}.
	 */
	KNOWLEDGE("knowledge"),
	
	// ///////// VisualizableAgent
	/**
	 * The type of the window (used for <code>WindowLayout</code>).
	 * 
	 * Used by {@link VisualizableAgent}.
	 */
	WINDOW_TYPE("windowType"),
	
	/**
	 * The GUI class of the agent. See {@link VisualizableAgent} to see how this works.
	 * 
	 * Used by {@link VisualizableAgent}.
	 */
	GUI("GUI"),
	
	// ///////// HierarchicalAgent
	/**
	 * Equals <code>"true"</code> if the agent should not leave its container when the parent moves.
	 * 
	 * Used by {@link HierarchicalAgent}.
	 */
	FIXED("fixed"),
	
	/**
	 * The hierarchical parent (which the agent follows when the parent moves).
	 * 
	 * Used by {@link HierarchicalAgent}.
	 */
	HIERARCHICAL_PARENT("parent"),
	
	/**
	 * The initial location of the agent (where the agent should move to immediately after creation).
	 * 
	 * Is a {@link Location}.
	 * 
	 * Used by {@link HierarchicalAgent}.
	 */
	INITIAL_LOCATION("initialLocation"),
	
	// ////////// ClaimAgent
	/**
	 * The definition of the CLAIM agent.
	 * 
	 * Is a {@link ClaimAgentDefinition}.
	 * 
	 * Used by {@link ClaimAgent}.
	 */
	AGENT_DEFINITION("agentDefinition"),
	
	// //////// SimulationAgent
	/**
	 * Is a {@link JadeInterface}.
	 */
	JADE_INTERFACE("jadeInterface"),
	
	/**
	 * Is a {@link Collection} of <code>AgentCreationData</code>.
	 */
	AGENTS("agents"),
	
	/**
	 * Name of the Visualization Agent.
	 */
	VISUALIZATION_AGENT("visualizationAgent"),
	
	/**
	 * The timeline.
	 * 
	 * Is a <code>XMLNode</code>.
	 */
	TIMELINE("timeline"),
	
	;
	
	String	name	= null;
	
	/**
	 * @param parName
	 *            - the name of the parameter as will appear in the scenario file.
	 */
	private AgentParameterName(String parName)
	{
		name = parName;
	}
	
	@Override
	public String toString()
	{
		return name;
	}
	
	/**
	 * Retrieves the {@link AgentParameterName} instance that corresponds to the specified name.
	 * 
	 * @param name
	 *            - the name to search for (the name with which the instance was created).
	 * @return - the corresponding {@link AgentParameterName} instance.
	 */
	public static AgentParameterName getName(String name)
	{
		for(AgentParameterName parName : AgentParameterName.values())
			if(parName.toString().equals(name))
				return parName;
		return null;
	}
}