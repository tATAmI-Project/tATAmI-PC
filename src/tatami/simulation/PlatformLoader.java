package tatami.simulation;

import tatami.pc.util.XML.XMLTree.XMLNode;

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
		 */
		JADE(null), // TODO
		
		/**
		 * Agents will be created as new instances. This is the default setting.
		 */
		DEFAULT(DefaultPlatform.class.getName()), // TODO
		
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
	 *            the XML node containing the configuration of the platform.
	 * @return the instance itself.
	 */
	public PlatformLoader setConfig(XMLNode configuration);
	
	/**
	 * Starts the agent platform.
	 * 
	 * @return <code>true</code> if the platform was started successfully; <code>false</code> otherwise.
	 */
	public boolean start();
	
	/**
	 * Creates a new container, on this platform, on this machine.
	 * 
	 * @param containerName
	 *            - the name of the container to create.
	 * @return <code>true</code> if the container was created successfully.
	 */
	public boolean addContainer(String containerName);
	
}