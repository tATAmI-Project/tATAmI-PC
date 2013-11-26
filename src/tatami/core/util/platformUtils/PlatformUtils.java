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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import net.xqhs.util.logging.Log.LoggerType;

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
	
	/**
	 * Creates a new instance of a class that is now known at compile-time.
	 * 
	 * @param loadingClass
	 *            - an instance created with the class loader to use to create the new instance. Can be
	 *            <code>new Object()</code>.
	 * @param className
	 *            - the name of the class to instantiate.
	 * @param constructorArguments
	 *            - an object array specifying the arguments to pass to the constructor of the new instance. The types
	 *            of the objects in this array will be used to identify the constructor of the new instance.
	 * @return the newly created instance. If the creation fails, an exception will be surely thrown.
	 * @throws SecurityException
	 *             -
	 * @throws NoSuchMethodException
	 *             -
	 * @throws ClassNotFoundException
	 *             -
	 * @throws IllegalArgumentException
	 *             -
	 * @throws InstantiationException
	 *             -
	 * @throws IllegalAccessException
	 *             -
	 * @throws InvocationTargetException
	 *             -
	 */
	public static Object loadClassInstance(Object loadingClass, String className, Object... constructorArguments)
			throws SecurityException, NoSuchMethodException, ClassNotFoundException, IllegalArgumentException,
			InstantiationException, IllegalAccessException, InvocationTargetException
	{
		ClassLoader cl = null;
		cl = new ClassLoader(loadingClass.getClass().getClassLoader()) {
			// nothing to extend
		};
		Class<?>[] argumentTypes = new Class<?>[constructorArguments.length];
		int i = 0;
		for(Object obj : constructorArguments)
			argumentTypes[i++] = obj.getClass();
		Constructor<?> constructor = cl.loadClass(className).getConstructor(argumentTypes);
		Object ret = constructor.newInstance(constructorArguments);
		return ret;
	}
}
