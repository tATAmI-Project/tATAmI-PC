/*******************************************************************************
 * Copyright (C) 2013 Andrei Olaru, Marius-Tudor Benea, Nguyen Thi Thuy Nga, Amal El Fallah Seghrouchni, Cedric Herpson.
 * 
 * This file is part of tATAmI-PC.
 * 
 * tATAmI-PC is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * tATAmI-PC is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with tATAmI-PC.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package tatami.core.agent.claim;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.xqhs.util.logging.Logger;
import tatami.sclaim.constructs.basic.ClaimAgentDefinition;
import tatami.sclaim.parser.Parser;

/**
 * This class handles the loading of .adf2 files to {@link ClaimAgentDefinition} instances and the attachment of java
 * code to the S-Claim agent definitions.
 * <p>
 * Ideally, loaded definitions should be cached so that the same file would be parsed only once, even if used by
 * multiple agents.
 * 
 * @author Andrei Olaru
 */
public class ClaimLoader
{
	/**
	 * The map of S-Claim agent agent definitions, indexed by the name of the S-CLaim class.
	 */
	protected static Map<String, ClaimAgentDefinition>	claimDefinitions	= new HashMap<String, ClaimAgentDefinition>();
	/**
	 * Specifies if chaching should be enabled (see {@link ClaimLoader}).
	 */
	protected static boolean							cachingEnabled		= false;
	/**
	 * The source folder for scenario files. FIXME this should be specified elsewhere?
	 */
	final protected static String						SOURCE_FOLDER		= "src-scenario";
	
	/**
	 * Loads an S-Claim Agent Definition instance, or returns a pre-cached one (according to the settings).
	 * <p>
	 * The returned {@link ClaimAgentDefinition} instance comes complete with code attachments.
	 * 
	 * @param agentClass
	 *            - the S-CLaim class to search for (same as the name of the .adf2 file)
	 * @param javaCodeAttachments
	 *            - a list of Java code attachments as specified in the scenario file.
	 * @param agentPackages
	 *            - the list of agent packages, specified in the scenario file, in which to search for the .adf2 file.
	 * @param log
	 *            - a {@link Logger} instance to use for logging messages.
	 * @return the created (or cached) {@link ClaimAgentDefinition} instance, on <code>null</code> if the loading fails.
	 */
	public static ClaimAgentDefinition fillCAD(String agentClass, Collection<String> javaCodeAttachments,
			Collection<String> agentPackages, Logger log)
	{
		ClaimAgentDefinition cad = null;
		// should not cache for now: some parameters and java code attachments might differ even
		// if the class is the same. PLUS: the cad contains the symbol table.
		if(ClaimLoader.claimDefinitions.containsKey(agentClass) && ClaimLoader.cachingEnabled)
			cad = ClaimLoader.claimDefinitions.get(agentClass);
		else
		{
			for(String adfPath : agentPackages)
			{
				String path = SOURCE_FOLDER + "/" + adfPath.replace('.', '/') + "/" + agentClass + ".adf2";
				log.trace("trying adf path [" + path + "]");
				File f = new File(path);
				if(f.exists())
				{
					cad = new Parser(path).parse();
					log.trace("agent definition loaded for agent class [" + agentClass + "]");
					ClaimLoader.claimDefinitions.put(agentClass, cad);
					break;
				}
			}
		}
		
		// FIXME load code attachments per cad, not per agent.
		
		if(cad == null)
		{
			log.error("agent definition not found for agent class [" + agentClass + "]");
			return null;
		}
		
		// attach java code
		if(javaCodeAttachments != null)
		{
			for(String className : javaCodeAttachments)
			{
				Class<?> attachment = null;
				
				if(className.indexOf(".") < 0)
				{ // just the class name; search in agent package
					Iterator<String> agentPackagesIt = agentPackages.iterator();
					boolean found = false;
					String path = null;
					while(agentPackagesIt.hasNext() && !found)
						try
						{
							path = agentPackagesIt.next().replace("/", ".") + "." + className;
							log.trace("trying code attachment path [" + path + "]");
							attachment = Class.forName(path);
							found = true;
						} catch(ClassNotFoundException e)
						{
							// do nothing; go forth
						}
					if(!found && path == null)
						log.trace("no agent packages defined.");
				}
				else
				{ // complete class name
					try
					{
						log.trace("trying code attachment path [" + className + "]");
						attachment = Class.forName(className);
					} catch(ClassNotFoundException e)
					{
						// TODO
					}
				}
				
				if(attachment != null)
				{
					cad.addCodeAttachement(attachment);
					log.info("attached java code " + attachment.toString());
				}
				else
					log.error("code attachment [" + className + "] not found.");
			}
		}
		
		return cad;
	}
	
}