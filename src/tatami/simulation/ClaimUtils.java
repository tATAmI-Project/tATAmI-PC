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
package tatami.simulation;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import tatami.core.agent.claim.parser.ClaimAgentDefinition;
import tatami.core.agent.claim.parser.Parser;
import tatami.core.interfaces.Logger;

/**
 * The purpose of this class is to hold functions that are necessary for the simulation, but are related to CLAIM
 * agents.
 * 
 * @author Andrei Olaru
 */
public class ClaimUtils
{
	
	protected static Map<String, ClaimAgentDefinition>	claimDefinitions	= new HashMap<String, ClaimAgentDefinition>();
	protected static boolean							cachingEnabled		= false;
	
	/**
	 * //TODO: to document
	 * 
	 * @param agentClass
	 * @param javaCodeAttachments
	 * @param adfPaths
	 * @param agentPackages
	 * @param log
	 * @return
	 */
	public static ClaimAgentDefinition fillCAD(String agentClass, Collection<String> javaCodeAttachments,
			Collection<String> adfPaths, Collection<String> agentPackages, Logger log)
	{
		ClaimAgentDefinition cad = null;
		// should not cache for now: some parameters and java code attachments might differ even
		// if the class is the same. PLUS: the cad contains the symbol table.
		if(ClaimUtils.claimDefinitions.containsKey(agentClass) && ClaimUtils.cachingEnabled)
			cad = ClaimUtils.claimDefinitions.get(agentClass);
		else
		{
			for(String adfPath : adfPaths)
			{
				String path = adfPath + "/" + agentClass + ".adf2";
				log.trace("trying adf path [" + path + "]");
				File f = new File(path);
				if(f.exists())
				{
					cad = new Parser(path).parse();
					log.trace("agent definition loaded for agent class [" + agentClass + "]");
					ClaimUtils.claimDefinitions.put(agentClass, cad);
					break;
				}
			}
		}
		
		if(cad != null)
		{
			// attach java code
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
							path = agentPackagesIt.next() + "." + className;
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
		else
			log.error("agent definition not found for agent class [" + agentClass + "]");
		
		return cad;
	}
	
}
