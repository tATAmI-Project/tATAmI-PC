package tatami.simulation;

import net.xqhs.util.XML.XMLTree.XMLNode;

/**
 * The platform loader is the interface to the manager of an agent platform. It can load and manage agents. Agents are
 * loaded based on {@link AgentCreationData} instances.
 * <p>
 * The platform should not contain initializations in its constructor. Creating a new instance of the platform loader
 * should never generate any errors. The platform should be initialized inside the {@link #start()} method.
 * 
 * @author Andrei Olaru
 */
public interface PlatformLoader
{
	/**
	 * Standard types of platforms. The name of the platform is used in the agent description in the scenario file.
	 * 
	 * @author Andrei Olaru
	 */
	enum StandardPlatformType {
		
		/**
		 * Agents will be loaded as Jade agents.
		 * <p>
		 * The platform class is inferred, as to avoid referring the class in case it is missing from the build.
		 */
		JADE,
		
		/**
		 * Agents will be created as new instances. This is the default setting.
		 */
		DEFAULT(DefaultPlatform.class.getName()),
		
		;
		
		/**
		 * The name of the platform.
		 */
		private String	name	= null;
		/**
		 * The fully qualified name of the class to instantiate when starting the platform.
		 */
		private String	classN	= null;
		
		/**
		 * Creates a new platform type, giving it a name that is the lower case version of the enumeration value, and
		 * setting a default class name, considering the platform name as the package name and also as a prefix to the
		 * class name.
		 */
		private StandardPlatformType()
		{
			name = super.toString().toLowerCase();
			classN = "tatami." + name + "." + name.substring(0, 1).toUpperCase() + name.substring(1) + "PlatformLoader";
		}
		
		/**
		 * Creates a new platform type, giving it a name that is the lower case version of the enumeration value.
		 * 
		 * @param className
		 *            - the fully qualified name of the class to instantiate when starting the platform. The class
		 *            should implement {@link PlatformLoader}.
		 */
		private StandardPlatformType(String className)
		{
			name = super.toString().toLowerCase();
			classN = className;
		}
		
		/**
		 * Creates a new platform type, giving it a name unrelated to the enumeration value.
		 * 
		 * @param platformName
		 *            - the name of the platform.
		 * @param className
		 *            - the fully qualified name of the class to instantiate when starting the platform. The class
		 *            should implement {@link PlatformLoader}.
		 */
		private StandardPlatformType(String platformName, String className)
		{
			name = platformName;
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
		 * Overrides the inherited method to return the 'given name' of the platform.
		 */
		@Override
		public String toString()
		{
			return name;
		}
	}
	
	/**
	 * This interface should be implemented by classes facilitating the link between an agent and a platform (e.g. for
	 * using specific platform functionalities).
	 * <p>
	 * Instances of this interface will be passed to {@link AgentManager}s to be used for calls to the platform.
	 * <p>
	 * The implementing instance may be one per platform or there may be a specific instance for each agent.
	 * <p>
	 * It is advised that the platform link is used by the agent by beans of compatible agent components.
	 * 
	 * @author Andrei Olaru
	 */
	public static interface PlatformLink
	{
		// TODO
	}
	
	/**
	 * The default platform to use, if no other is specified.
	 */
	static final StandardPlatformType	DEFAULT_PLATFORM	= StandardPlatformType.DEFAULT;
	
	/**
	 * The name of the attribute containing the platform name in the XML file.
	 */
	static final String					NAME_ATTRIBUTE		= "name";
	/**
	 * The name of the attribute containing the class path of the {@link PlatformLoader} class, in the XML file.
	 */
	static final String					CLASSPATH_ATTRIBUTE	= "classpath";
	
	/**
	 * @return the name of the platform loader, as used in the scenario file.
	 */
	public String getName();
	
	/**
	 * Configures the platform by passing the XML node in the scenario. The platform can extract the necessary settings.
	 * <p>
	 * The method should not perform any initializations that can fail. These should be done in the {@link #start()}
	 * method.
	 * 
	 * @param configuration
	 *            - the XML node containing the configuration of the platform.
	 * @param settings
	 *            - general application settings specified in the scenario file, program argumetns, etc.
	 * @return the instance itself.
	 */
	public PlatformLoader setConfig(XMLNode configuration, BootSettingsManager settings);
	
	/**
	 * Starts the agent platform.
	 * 
	 * @return <code>true</code> if the platform was started successfully; <code>false</code> otherwise.
	 */
	public boolean start();
	
	/**
	 * Stops the agent platform.
	 * 
	 * @return <code>true</code> if the platform was stopped successfully; <code>false</code> otherwise.
	 */
	public boolean stop();
	
	/**
	 * Creates a new container, on this platform, on this machine.
	 * 
	 * @param containerName
	 *            - the name of the container to create.
	 * @return <code>true</code> if the container was created successfully.
	 */
	public boolean addContainer(String containerName);
	
	/**
	 * Loads the agent onto the platform. It also calls the method {@link AgentManager#setPlatformLink()} to create a
	 * link from the agent to the platform. The platform link may be the platform itself or an agent wrapper, depending
	 * on the specific platform.
	 * 
	 * @param containerName
	 *            - the name of the container in which to create the agent.
	 * @param agentManager
	 *            - the {@link AgentManager} handling the agent's lifecycle.
	 * @return <code>true</code> if the operation is successful (meaning a subsequent call to
	 *         {@link AgentManager#start()} should be able to start the agent.
	 */
	public boolean loadAgent(String containerName, AgentManager agentManager);
	
}