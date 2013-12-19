package tatami.simulation;

import net.xqhs.util.logging.Logger;
import tatami.core.agent.CompositeAgent;
import tatami.core.agent.CompositeAgentLoader;
import tatami.pc.util.XML.XMLTree.XMLNode;

/**
 * The agent loader is the interface to an instance that uses existing scenario information to completely specify
 * loading information for an agent.
 * 
 * @author Andrei Olaru
 */
public interface AgentLoader
{
	/**
	 * Standard types of loaders for agents. The name of the loader is used in the agent description in the scenario
	 * file.
	 * 
	 * @author Andrei Olaru
	 */
	enum StandardAgentLoaderType {
		
		/**
		 * The agent is described by means of an .adf2 file. The ADF2 agent is a special kind of {@link CompositeAgent},
		 * with some pre-configured components. An S-CLAIM agent can be easily loaded as a COMPOSITE agent. This
		 * enumeration value is kept only for backwards compatibility of scenarios.
		 */
		ADF2(null), // TODO
		
		/**
		 * The agent is a {@link CompositeAgent} that is made up of various components.
		 */
		COMPOSITE(CompositeAgentLoader.class.getName()),
		
		/**
		 * The agent is described by means of traditional Java code. Currently unimplemented.
		 */
		JAVA(null), // TODO
		
		;
		
		/**
		 * The name of the loader.
		 */
		private String	name	= null;
		/**
		 * The fully qualified name of the class to instantiate when using the loader.
		 */
		private String	classN	= null;
		
		/**
		 * Creates a new loader type, giving it a name unrelated to the enumeration value.
		 * 
		 * @param loaderName
		 *            - the name of the loader.
		 * @param className
		 *            - the fully qualified name of the class to instantiate when starting the platform. The class
		 *            should implement {@link AgentLoader}.
		 */
		private StandardAgentLoaderType(String loaderName, String className)
		{
			name = loaderName;
			classN = className;
		}
		
		/**
		 * Creates a new loader type, giving it a name that is the lower case version of the enumeration value.
		 * 
		 * @param className
		 *            - the fully qualified name of the class to instantiate when starting the platform. The class
		 *            should implement {@link AgentLoader}.
		 */
		private StandardAgentLoaderType(String className)
		{
			name = super.toString().toLowerCase();
			classN = className;
		}
		
		/**
		 * @return the name of the class to instantiate in order to create this platform loader.
		 */
		public String getClassName()
		{
			return classN;
		}
		
		/**
		 * Overrides the inherited method to return the 'given name' of the loader.
		 */
		@Override
		public String toString()
		{
			return name;
		}
	}
	
	/**
	 * The name of the attribute containing the loader name in the XML file.
	 */
	static final String	NAME_ATTRIBUTE		= "name";
	/**
	 * The name of the attribute containing the class path of the {@link AgentLoader} class, in the XML file.
	 */
	static final String	CLASSPATH_ATTRIBUTE	= "classpath";
	
	/**
	 * @return the the name of the agent loader, as used in the scenario file.
	 */
	public String getName();
	
	/**
	 * Configures the loader by passing the XML node in the scenario. The loader can extract the necessary settings.
	 * <p>
	 * The method should not perform any initializations that can fail. These should be done in the
	 * {@link #load(AgentCreationData)} method.
	 * 
	 * @param configuration
	 *            the XML node containing the configuration of the loader.
	 * @return the instance itself.
	 */
	public AgentLoader setConfig(XMLNode configuration);
	
	/**
	 * The method checks potential problems that could appear in the creation of an agent, as specified by the
	 * information in the argument. Potential problems relate, for example, to inexistent classes.
	 * <p>
	 * The method also may add information to the agent creation data, in order to speed up the loading process.
	 * <p>
	 * If the agent will surely not be able to load, <code>false</code> will be returned. For any non-fatal issues, the
	 * method should return <code>true</code> and output warnings in the specified log.
	 * 
	 * @param agentCreationData
	 *            - the {@link AgentCreationData}, as loaded by {@link Boot} from the scenario file.
	 * @param log
	 *            - the {@link Logger} in which to output any potential problems (as warnings or errors).
	 * @return <code>true</code> if no fatal issues were found; <code>false</code> otherwise.
	 */
	public boolean preload(AgentCreationData agentCreationData, Logger log);
	
	/**
	 * The method loads all the information necessary for the creation of an agent and returns an {@link AgentManager}
	 * instance used to manage the lifecycle of the loaded agent.
	 * 
	 * @param agentCreationData
	 *            - the {@link AgentCreationData}, as loaded by {@link Boot} from the scenario file.
	 * @return an {@link AgentManager} for the loaded agent.
	 */
	public AgentManager load(AgentCreationData agentCreationData);
}