package tatami.simulation;

import java.io.File;

import tatami.pc.util.XML.XMLParser;
import tatami.pc.util.XML.XMLTree;
import tatami.pc.util.XML.XMLTree.XMLNode;
import tatami.pc.util.windowLayout.LayoutIndications;
import net.xqhs.util.config.Config;
import net.xqhs.util.logging.UnitComponentExt;

/**
 * This class manages settings for simulations. It handles loading these settings from various sources --
 * {@link BootDefaultArguments}, arguments given to the <code>main()</code> method in {@link Boot}, or settings
 * specified in the scenario file.
 * <p>
 * The precedence of values for settings is the following:
 * <ul>
 * <li>values given in {@link BootDefaultArguments};
 * <li>values given as arguments to method <code>main()</code> in {@link Boot};
 * <li>values given in the scenario file.
 * </ul>
 * Each setting may come from values only in some of the above state sources. The specific cases are mentioned in the
 * documentation of each setting.
 * 
 * @author Andrei Olaru
 */
public class BootSettingsManager extends Config
{
	// /////////////////// scenario
	/**
	 * The schema for scenario files.
	 */
	protected String			SCENARIO_SCHEMA		= "config/scenarioSchema2.xsd";
	
	/**
	 * The name of the XML scenario file that contains the settings for the current simulation.
	 */
	protected String			scenarioFileName;
	
	// /////////////////// network configuration
	/**
	 * The main host of the current simulation. This may be a URL or an IP.
	 * <p>
	 * For Jade-based setups, it is the IP of the main container.
	 */
	protected String			mainHost;
	/**
	 * The port on the main host, at which the main elements of the simulation are available.
	 * <p>
	 * For Jade-based setups, it is the port of the main container.
	 */
	protected String			mainPort;
	/**
	 * An indication of the local host.
	 * <p>
	 * For Jade-based setups, it is the IP of the local container.
	 */
	protected String			localHost;
	/**
	 * The local port at which the local elements of the simulation are available.
	 * <p>
	 * For Jade-based setups, it is the port of the local container.
	 */
	protected String			localPort;
	
	// /////////////////// jade-specific settings
	// TODO: make this file Jade-independent
	/**
	 * <code>true</code> if this scenario is based on Jade.
	 */
	protected boolean			loadJade			= false;
	/**
	 * The name of the main Jade container.
	 */
	protected String			mainContainerName	= null;
	/**
	 * <code>true</code> if the main container should be created on the local host.
	 */
	protected boolean			createMainContainer	= false;
	/**
	 * The name of the current Jade platform.
	 */
	protected String			jadePlatformName	= null;
	
	// /////////////////// visualization layout
	/**
	 * The width of the application layout. This is meant for PC platforms, but may be used in other situations as well.
	 */
	protected int				applicationLayoutWidth;
	/**
	 * The height of the application layout. This is meant for PC platforms, but may be used in other situations as
	 * well.
	 */
	protected int				applicationLayoutHeight;
	/**
	 * The window layout indications, for PC platforms.
	 */
	protected LayoutIndications	layout;
	
	@Override
	public BootSettingsManager makeDefaults()
	{
		scenarioFileName = BootDefaultArguments.scenarioFileName;
		
		mainHost = BootDefaultArguments.mainHost;
		mainPort = BootDefaultArguments.mainPort;
		localHost = BootDefaultArguments.localHost;
		localPort = BootDefaultArguments.localPort;
		
		applicationLayoutWidth = BootDefaultArguments.applicationLayoutWidth;
		applicationLayoutHeight = BootDefaultArguments.applicationLayoutHeight;
		layout = BootDefaultArguments.layout;
		
		return (BootSettingsManager) super.makeDefaults();
	}
	
	/**
	 * The method loads all available values from the specified sources.
	 * <p>
	 * The only given source is the arguments the program has received, as the name of the scneario file will be decided
	 * by this method. If it is instructed through the parameter, the scenario file is parsed, producing an additional
	 * source of setting values.
	 * <p>
	 * The <code>load()</code> method can be called only once. It is why all sources must be given in a single call to
	 * <code>load()</code>.
	 * <p>
	 * Therefore, if it is desired to pick <i>any</i> settings from the scenario file, the <code>boolean</code> argument
	 * should be set to <code>true</code>.
	 * 
	 * @param programArguments
	 *            - the arguments passed to the application, exactly as they were passed.
	 * @param parseScenarioFile
	 *            - if <code>true</code>, the scenario file will be parsed to obtain the setting values placed in the
	 *            scenario; also, the {@link XMLTree} instance resulting from the parsing will be returned.
	 * @return if the <code>parseScenarioFile</code> argument was <code>true</code> and the parsing was successful, the
	 *         resulting XML tree is returned; <code>null</code> otherwise.
	 * 
	 * @throws ConfigLockedException
	 *             - if load is called more than once.
	 */
	public XMLTree load(String programArguments[], boolean parseScenarioFile) throws ConfigLockedException
	{
		locked();
		
		UnitComponentExt log = (UnitComponentExt) new UnitComponentExt().setUnitName("settings load");
		
		switch(programArguments.length)
		{
		default:
			log.warn("too many arguments; additional arguments ignored.");
			//$FALL-THROUGH$
		case 7:
			applicationLayoutHeight = Integer.parseInt(programArguments[6]);
			//$FALL-THROUGH$
		case 6:
			if(programArguments.length == 6)
				log.warn("incorrect number of arguments");
			applicationLayoutWidth = Integer.parseInt(programArguments[5]);
			//$FALL-THROUGH$
		case 5:
			try
			{
				if(Integer.parseInt(programArguments[4]) >= 0)
					localPort = programArguments[4];
			} catch(NumberFormatException e1)
			{
				log.error("unable to parse value for local port; value not retained.");
			}
			//$FALL-THROUGH$
		case 4:
			if(!"null".equals(programArguments[3]))
				localHost = programArguments[3];
			//$FALL-THROUGH$
		case 3:
			try
			{
				if(Integer.parseInt(programArguments[2]) >= 0)
					mainPort = programArguments[2];
			} catch(NumberFormatException e1)
			{
				log.error("unable to parse value for main port; value not retained.");
			}
			//$FALL-THROUGH$
		case 2:
			if(!"null".equals(programArguments[1]))
				mainHost = programArguments[1];
			//$FALL-THROUGH$
		case 1:
			if(!"default".equals(programArguments[0]) && new File(programArguments[0]).exists())
				scenarioFileName = programArguments[0];
			else
				log.error("file [" + programArguments[0] + "] not found.");
			//$FALL-THROUGH$
		case 0:
		}
		
		if(parseScenarioFile)
		{
			log.info("loading scenario [" + scenarioFileName + "]");
			XMLTree scenarioTree = XMLParser.validateParse(SCENARIO_SCHEMA, scenarioFileName);
			
			if(scenarioTree == null)
			{
				log.error("scenario parsing result is null.");
				return scenarioTree;
			}
			
			log.info("scenario:");
			log.info(scenarioTree.toString());
			
			// TODO: make this jade-independent
			XMLNode jadeConfigNode = (scenarioTree.getRoot().getNodeIterator("jadeConfig").hasNext() ? scenarioTree
					.getRoot().getNodeIterator("jadeConfig").next() : null);
			if(jadeConfigNode != null)
			{
				loadJade = true;
				if(jadeConfigNode.getAttributeValue("IPaddress") != null)
					mainHost = jadeConfigNode.getAttributeValue("IPaddress");
				if(jadeConfigNode.getAttributeValue("port") != null)
					mainPort = jadeConfigNode.getAttributeValue("port");
				if(jadeConfigNode.getAttributeValue("localIPaddress") != null)
					localHost = jadeConfigNode.getAttributeValue("localIPaddress");
				if(jadeConfigNode.getAttributeValue("localPort") != null)
					localPort = jadeConfigNode.getAttributeValue("localPort");
				if(jadeConfigNode.getAttributeValue("platformID") != null)
					jadePlatformName = jadeConfigNode.getAttributeValue("platformID");
				if(jadeConfigNode.getAttributeValue("mainContainerName") != null)
				{
					mainContainerName = jadeConfigNode.getAttributeValue("mainContainerName");
					createMainContainer = true;
				}
				if((jadeConfigNode.getAttributeValue("isMain") != null)
						&& jadeConfigNode.getAttributeValue("isMain").equals(new Boolean(true).toString()))
					createMainContainer = true;
			}
		}
		
		log.doExit();
		lock();
		return null;
	}

	/**
	 * @return the scenarioFileName
	 */
	public String getScenarioFileName()
	{
		return scenarioFileName;
	}

	/**
	 * @return the mainHost
	 */
	public String getMainHost()
	{
		return mainHost;
	}

	/**
	 * @return the mainPort
	 */
	public String getMainPort()
	{
		return mainPort;
	}

	/**
	 * @return the localHost
	 */
	public String getLocalHost()
	{
		return localHost;
	}

	/**
	 * @return the localPort
	 */
	public String getLocalPort()
	{
		return localPort;
	}

	/**
	 * @return the loadJade
	 */
	public boolean doLoadJade()
	{
		return loadJade;
	}

	/**
	 * @return the mainContainerName
	 */
	public String getMainContainerName()
	{
		return mainContainerName;
	}

	/**
	 * @return the createMainContainer
	 */
	public boolean doCreateMainContainer()
	{
		return createMainContainer;
	}

	/**
	 * @return the jadePlatformName
	 */
	public String getJadePlatformName()
	{
		return jadePlatformName;
	}

	/**
	 * @return the applicationLayoutWidth
	 */
	public int getApplicationLayoutWidth()
	{
		return applicationLayoutWidth;
	}

	/**
	 * @return the applicationLayoutHeight
	 */
	public int getApplicationLayoutHeight()
	{
		return applicationLayoutHeight;
	}

	/**
	 * @return the layout
	 */
	public LayoutIndications getLayout()
	{
		return layout;
	}
}
