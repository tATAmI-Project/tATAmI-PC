/*******************************************************************************
 * Copyright (C) 2015 Andrei Olaru, Marius-Tudor Benea, Nguyen Thi Thuy Nga, Amal El Fallah Seghrouchni, Cedric Herpson.
 * 
 * This file is part of tATAmI-PC.
 * 
 * tATAmI-PC is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * tATAmI-PC is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with tATAmI-PC.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package tatami.simulation;

import java.util.HashMap;
import java.util.Set;

import net.xqhs.util.XML.XMLParser;
import net.xqhs.util.XML.XMLTree;
import net.xqhs.util.XML.XMLTree.XMLNode;
import net.xqhs.util.XML.XMLTree.XMLNode.XMLAttribute;
import net.xqhs.util.config.Config;
import net.xqhs.util.logging.UnitComponentExt;
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
    
    private static BootSettingsManager singleton = null;
	// /////////////////// scenario
	/**
	 * The default directory for scenarios.
	 */
	public static final String		SCENARIO_DIRECTORY	= "src-scenario/";
	/**
	 * The schema for scenario files.
	 */
	protected String				SCENARIO_SCHEMA		= "src-schema/scenarioSchema3.xsd";
	
	/**
	 * The name of the XML scenario file that contains the settings for the current simulation.
	 */
	protected String				scenarioFileName;
	
	/**
	 * The name of the local agent container. If the main container will be created on this machine, this will be the
	 * name of the main container.
	 * <p>
	 * This name may be used, for instance, to be able to specify all necessary settings through command line arguments
	 * and not use a whole scenario file just for the host information and the container name.
	 */
	protected String				localContainerName	= null;
	
	HashMap<String, String> configSettings;
	
	UnitComponentExt log = (UnitComponentExt) new UnitComponentExt().setUnitName("settings load").setLoggerType(
            PlatformUtils.platformLogType());
	
	private BootSettingsManager(){
	    configSettings = new HashMap<String, String>();
	    makeDefaults();
	}
	
	@Override
	public BootSettingsManager makeDefaults()
	{
		scenarioFileName = BootDefaultArguments.scenarioFileName;
		
		localContainerName = BootDefaultArguments.localContainerName;
		
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
		
		
		/*
		switch(programArguments.length)
		{
		default:
			log.warn("too many arguments; additional arguments ignored.");
			//$FALL-THROUGH$
		case 8:
			if(!"null".equals(programArguments[7]))
				localContainerName = programArguments[7];
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
		*/
		
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
			log.info(scenarioTree.toString());
			
			// TODO: make this jade-independent
			XMLNode configNode = (scenarioTree.getRoot().getNodeIterator("config").hasNext() ? scenarioTree.getRoot()
					.getNodeIterator("config").next() : null);
			
			
			
			if(configNode != null)
			{
			    String logAttributes = "";
			    for(XMLAttribute attribute : configNode.getAttributes()){
			        configSettings.put(attribute.getName(), attribute.getValue());
			        logAttributes += "(" + attribute.getName() + ", " + attribute.getValue() + "); ";
			    }
			    
			    log.info(logAttributes);
			    
				if(configNode.getAttributeValue("mainContainerName") != null)
					localContainerName = configNode.getAttributeValue("mainContainerName");
			}
		}
		
		
		log.info("local container: []", localContainerName == null ? "<null>" : localContainerName);
		
		log.doExit();
		lock();
		return scenarioTree;
	}
	
    public XMLTree load(String path) {
        XMLTree scenarioTree = null;
        
        log.info("loading scenario [" + path + "]");
        scenarioTree = XMLParser.validateParse(SCENARIO_SCHEMA, path);

        if (scenarioTree == null) {
            log.error("scenario parsing result is null.");
            return scenarioTree;
        }

        log.info("scenario: " + scenarioTree.toString());
        
        XMLNode configNode = (scenarioTree.getRoot().getNodeIterator("config").hasNext()
                ? scenarioTree.getRoot().getNodeIterator("config").next() : null);
        if (configNode != null) {
            String logAttributes = "";
            for(XMLAttribute attribute : configNode.getAttributes()){
                configSettings.put(attribute.getName(), attribute.getValue());
                logAttributes += "(" + attribute.getName() + ", " + attribute.getValue() + "); ";
            }
            
            log.info(logAttributes);
            
            if (configNode.getAttributeValue("mainContainerName") != null)
                localContainerName = configNode.getAttributeValue("mainContainerName");
        }
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
	 * @return the local container name
	 */
	public String getLocalContainerName()
	{
		return localContainerName;
	}
	
	
	public static BootSettingsManager getInst(){
	    if(singleton == null)
	        singleton = new BootSettingsManager();
	    return singleton;
	}
}
