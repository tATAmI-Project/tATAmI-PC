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
package tatami.core.interfaces;

import java.io.Serializable;
import java.util.Collection;
import java.util.Vector;

import tatami.core.util.Config;
import tatami.core.util.platformUtils.PlatformUtils;

public interface AgentGui
{
	public enum DefaultComponent {
		AGENT_NAME, AGENT_LOG
	}
	
	public class AgentGuiConfig extends Config implements Serializable
	{
		private static final long				   serialVersionUID	= -5605500962863357519L;
		
		private static final String				 DEFAULT_WINDOW_TYPE = "agent";
		private static final String				 DEFAULT_AGENT_GUI   = "DefaultAgentGui";
		// FIXME should be elsewhere
		private static final String				 ROOT_PACKAGE		= "tatami";
		private static final String				 DEFAULT_GUI_PATH	= "agent.visualization";
		//private static final String				 ANDROID_GUI		 = "AndroidDefaultAgentGui";
		private static final PlatformUtils.Platform DEFAULT_PLATFORM	= PlatformUtils.Platform.PC;
		
		protected String overrideClassName = null;
		// FIXME: make protected or private
		public String							   guiClassName;								   // initialized
		// FIXME: make protected or private																							 // makeDefaults
		public String							   windowName		  = null;
		// FIXME: make protected or private
		public String							   windowType		  = DEFAULT_WINDOW_TYPE;
		
		public AgentGuiConfig()
		{
			super();
		}
		
		@Override
		public AgentGuiConfig makeDefaults()
		{
			setGuiClass(null, null);
			return this;
		}
		
		public AgentGuiConfig setWindowName(String name)
		{
			windowName = name;
			return this;
		}
		
		public AgentGuiConfig setWindowType(String type)
		{
			windowType = type;
			return this;
		}
		
		public AgentGuiConfig setClassNameOverride(String className)
		{
			overrideClassName = className;
			return this;
		}
		
		
		public AgentGuiConfig setGuiClass(String className, Collection<String> packages)
		{
			
			PlatformUtils.Platform platform = PlatformUtils.getPlatform();
			String defaultGuiPath = ROOT_PACKAGE + "." + platform.toString().toLowerCase() + "." + DEFAULT_GUI_PATH;
			guiClassName = null;
			if(overrideClassName != null)
				className = overrideClassName;
			if(className == null)
			{
				
				if(PlatformUtils.Platform.PC.equals(platform) || (platform == null))
					guiClassName = defaultGuiPath + "." + (platform != null ? platform : DEFAULT_PLATFORM)
							+ DEFAULT_AGENT_GUI;
				else
					guiClassName = defaultGuiPath + "." + platform + DEFAULT_AGENT_GUI;
				
			}
			else if(className.indexOf(defaultGuiPath) >= 0)
				guiClassName = className;
			else
			{
				for(String pack : packages)
				{
					String path = null;
					try
					{
						path = pack + "." + platform + "." + className;
						System.out.println("trying: [" + path + "]");
						Class.forName(path);
						guiClassName = path;
						break;
					} catch(ClassNotFoundException e)
					{
						System.out.println("not found: [" + path + "]");
						// do nothing; go forth
					}
				}
				if(guiClassName == null)
				{ // FIXME: why is there code duplication here?
					if(PlatformUtils.Platform.PC.equals(platform) || (platform == null))
						guiClassName = defaultGuiPath + "." + (platform != null ? platform : DEFAULT_PLATFORM)
								+ DEFAULT_AGENT_GUI;
					else
						guiClassName = defaultGuiPath + "." + platform + DEFAULT_AGENT_GUI;
				}
			}
			return this;
		}
	}
	
	public interface InputListener
	{
		public void receiveInput(String componentName, Vector<Object> arguments);
	}
	
	// public Component getComponent(String componentName);
	
	public void doOutput(String componentName, Vector<Object> arguments);
	
	public Vector<Object> getinput(String componentName);
	
	public void connectInput(String componentName, InputListener listener);
	
	public void close();
}
