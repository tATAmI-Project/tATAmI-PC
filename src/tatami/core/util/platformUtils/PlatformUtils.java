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
package tatami.core.util.platformUtils;

import tatami.core.interfaces.Logger.LoggerType;

/**
 * Platform-related functionality. All functions should be static.
 * 
 * @author Andrei Olaru
 * 
 */
public class PlatformUtils
{
	/**
	 * This enums contains all supported platforms.
	 * 
	 * @author Andrei Olaru
	 */
	public static enum Platform {
		PC, ANDROID
	}
	
	public static Platform getPlatform()
	{
		if(System.getProperty("java.vm.name").equals("Dalvik"))
		{
			
			return Platform.ANDROID;
		}
		
		return Platform.PC;
	}
	
	// FIXME: should be split into WS registration and WS invocation.
	public static boolean platformSupportsWebServices()
	{
		return false;
	}
	
	public static LoggerType platformLogType()
	{
		switch(getPlatform())
		{
		case PC:
			return LoggerType.JAVA;
		case ANDROID:
			return LoggerType.JAVA;
		}
		return null;
	}
	
	public static Class<?> jadeInterfaceClass()
	{
		ClassLoader myClassLoader = ClassLoader.getSystemClassLoader();
		
		switch(getPlatform())
		{
		case PC:
		{
			Class<?> clazz = null;
			try
			{
				clazz = myClassLoader.loadClass("tatami.pc.util.jade.PCJadeInterface");
			} catch(ClassNotFoundException e)
			{
				e.printStackTrace();
			}
			return clazz;
		}
		case ANDROID:
		{
			Class<?> clazz = null;
			try
			{
				clazz = myClassLoader.loadClass("org.interfaces.AndroidJadeInterface");
			} catch(ClassNotFoundException e)
			{
				e.printStackTrace();
			}
			return clazz;
		}
		}
		return null;
	}
}
