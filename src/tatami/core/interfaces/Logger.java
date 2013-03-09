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

import java.io.OutputStream;

import tatami.core.util.logging.Log;

/**
 * Use this interface to implement any [wrapper of a] logging structure that is returned by {@link Log}.
 * 
 * @author Andrei Olaru
 * 
 */
public abstract class Logger // FIXME to rename to LoggerWrapper
{
	/**
	 * Supported logger types. These will extend the {@link Logger} class.
	 * 
	 * @author Andrei Olaru
	 * 
	 */
	public static enum LoggerType {
		LOG4J("tatami.pc.util.logging.Log4JWrapper"),
		
		JADE("tatami.core.util.logging.JadeLogWrapper"),
		
		JAVA("tatami.core.util.logging.JavaLogWrapper")
		
		;
		
		String	className;
		
		private LoggerType(String className)
		{
			this.className = className;
		}
		
		public String getClassName()
		{
			return className;
		}
	}
	
	/**
	 * Indicates the level of the log. Mimics {@link org.apache.log4j.Level}.
	 * 
	 * @author Andrei Olaru
	 * 
	 */
	public enum Level {
		
		OFF,
		
		/**
		 * unused in this project
		 */
		FATAL,
		
		ERROR,
		
		WARN,
		
		/**
		 * unused in this project
		 */
		INFO,
		
		/**
		 * unused in this project
		 */
		DEBUG,
		
		TRACE,
		
		ALL,
	}
	
	public abstract void setLevel(Level level);
	
	// /**
	// * @param format
	// * : a pattern, in a format that is potentially characteristic to the wrapper
	// * @param destination
	// * : a destination stream
	// */
	// public abstract void addDestination(String format, OutputStream destination);
	
	/**
	 * Adds a destination for logging messages, configured for a local output (e.g. in a TextArea) that is dedicated
	 * only to this log.
	 * 
	 * @param destination
	 *            the destination of the output
	 */
	public abstract void addLocalDestination(OutputStream destination);
	
	/**
	 * Adds a destination for logging messages, configured for console output (that may contain other logs).
	 * 
	 * @param destination
	 *            the destination of the output
	 */
	public abstract void addConsoleDestination(OutputStream destination);
	
	/**
	 * Adds a destination for logging messages, configured for console output (that may contain other logs). The
	 * destination is automatically set to <code>System.out</code>
	 */
	public void addConsoleDestination()
	{
		addConsoleDestination(System.out);
	}
	
	/**
	 * Adds a destination for logging messages, configured for remote output. The remote output may centralize logs that
	 * come from a large number of entities.
	 * <p>
	 * IMPORTANT: it is required that the log messages begin with their timestamp, so that they are sortable
	 * chronologically.
	 * 
	 * @param destination
	 *            the destination of the output
	 * @param separator
	 *            a {@link Character} that is placed at the beginning and end of each message. It is strongly
	 *            recommended that the same character is used across all logs that report to a destination (and the
	 *            character is known by the destination)
	 */
	public abstract void addReportDestination(OutputStream destination, Character separator);
	
	public abstract void error(String message);
	
	public abstract void warn(String message);
	
	public abstract void info(String message);
	
	public abstract void trace(String message);
	
	public void error(String message, Object... objects)
	{
		error(compose(message, objects));
	}
	
	public void warn(String message, Object... objects)
	{
		warn(compose(message, objects));
	}
	
	public void info(String message, Object... objects)
	{
		info(compose(message, objects));
	}
	
	public void trace(String message, Object... objects)
	{
		trace(compose(message, objects));
	}
	
	protected static String compose(String message, Object[] objects)
	{
		String ret = message;
		for(Object object : objects)
			ret += "," + object.toString();
		return ret;
	}
}
