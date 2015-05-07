package tatami.simulation;

import java.io.File;

import net.xqhs.util.XML.XMLParser;
import net.xqhs.util.XML.XMLTree;
import net.xqhs.util.XML.XMLTree.XMLNode;
import net.xqhs.util.config.Config;
import net.xqhs.util.logging.UnitComponentExt;
import net.xqhs.windowLayout.grid.GridLayoutIndications;
import tatami.core.util.platformUtils.PlatformUtils;

/**
 * This class manages settings for simulations. It handles loading these settings from various sources --
 * {@link BootDefaultArguments}, arguments given to the <code>main()</code> method in {@link Boot}, or settings
 * specified in the scenario file.
 * <p>
 * The precedence of values for settings is the following (latter values override former values):
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
	protected String				SCENARIO_SCHEMA		= "src-schema/scenarioSchema3.xsd";
	
	/**
	 * The name of the XML scenario file that contains the settings for the current simulation.
	 */
	protected String				scenarioFileName;
	
	// /////////////////// network configuration
	/**
	 * The main host of the current simulation. This may be a URL or an IP.
	 * <p>
	 * For Jade-based setups, it is the IP of the main container.
	 */
	protected String				mainHost;
	/**
	 * The port on the main host, at which the main elements of the simulation are available.
	 * <p>
	 * For Jade-based setups, it is the port of the main container.
	 */
	protected String				mainPort;
	/**
	 * An indication of the local host.
	 * <p>
	 * For Jade-based setups, it is the IP of the local container.
	 */
	protected String				localHost;
	/**
	 * The local port at which the local elements of the simulation are available.
	 * <p>
	 * For Jade-based setups, it is the port of the local container.
	 */
	protected String				localPort;
	/**
	 * The name of the local agent container. If the main container will be created on this machine, this will be the
	 * name of the main container.
	 * <p>
	 * This name may be used, for instance, to be able to specify all necessary settings through command line arguments
	 * and not use a whole scenario file just for the host information and the container name.
	 */
	protected String				localContainerName	= null;
	
	// /////////////////// visualization layout
	/**
	 * The width of the application layout. This is meant for PC platforms, but may be used in other situations as well.
	 */
	protected int					applicationLayoutWidth;
	/**
	 * The height of the application layout. This is meant for PC platforms, but may be used in other situations as
	 * well.
	 */
	protected int					applicationLayoutHeight;
	/**
	 * The window layout indications, for PC platforms.
	 */
	protected GridLayoutIndications	layout;
	
	@Override
	public BootSettingsManager makeDefaults()
	{
		scenarioFileName = BootDefaultArguments.scenarioFileName;
		
		mainHost = BootDefaultArguments.mainHost;
		mainPort = BootDefaultArguments.mainPort;
		localHost = BootDefaultArguments.localHost;
		localPort = BootDefaultArguments.localPort;
		
		localContainerName = BootDefaultArguments.localContainerName;
		
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
		
		UnitComponentExt log = (UnitComponentExt) new UnitComponentExt().setUnitName("settings load").setLoggerType(
				PlatformUtils.platformLogType());
		
		switch(programArguments.length)
		{
		default:
			log.warn("too many arguments; additional arguments ignored.");
			//$FALL-THROUGH$
		case 8:
			if(!"null".equals(programArguments[7]))
				localContainerName = programArguments[7];
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
		
		XMLTree scenarioTree = null;
		if(parseScenarioFile)
		{
			log.info("loading scenario [" + scenarioFileName + "]");
			scenarioTree = XMLParser.validateParse(SCENARIO_SCHEMA, scenarioFileName);
			
			if(scenarioTree == null)
			{
				log.error("scenario parsing result is null.");
				return scenarioTree;
			}
			
			log.info("scenario:");
			log.trace(scenarioTree.toString());
			
			// TODO: make this jade-independent
			XMLNode configNode = (scenarioTree.getRoot().getNodeIterator("config").hasNext() ? scenarioTree.getRoot()
					.getNodeIterator("config").next() : null);
			if(configNode != null)
			{
				if(configNode.getAttributeValue("mainHost") != null)
					mainHost = configNode.getAttributeValue("IPaddress");
				if(configNode.getAttributeValue("mainPort") != null)
					mainPort = configNode.getAttributeValue("port");
				if(configNode.getAttributeValue("localHost") != null)
					localHost = configNode.getAttributeValue("localIPaddress");
				if(configNode.getAttributeValue("localPort") != null)
					localPort = configNode.getAttributeValue("localPort");
				if(configNode.getAttributeValue("mainContainerName") != null)
					localContainerName = configNode.getAttributeValue("mainContainerName");
			}
		}
		
		log.info("network config: Main:[]:[] Local:[]:[]", mainHost, mainPort, localHost, localPort);
		log.info("local container: []", localContainerName);
		
		if(applicationLayoutWidth > 0)
			layout.indicateW(applicationLayoutWidth);
		if(applicationLayoutHeight > 0)
			layout.indicateH(applicationLayoutHeight);
		log.info("screen setup: []", applicationLayoutWidth + "x" + applicationLayoutHeight);
		
		log.doExit();
		lock();
		return scenarioTree;
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
	 * @return the mainContainerName
	 */
	public String getMainContainerName()
	{
		return localContainerName;
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
	public GridLayoutIndications getLayout()
	{
		return layout;
	}
}
