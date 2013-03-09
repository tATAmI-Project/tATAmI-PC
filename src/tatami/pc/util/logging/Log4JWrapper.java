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
package tatami.pc.util.logging;

import java.io.OutputStream;

import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;

import tatami.core.interfaces.Logger;

/**
 * A {@link Logger}-implementing wrapper of the Log4J {@link org.apache.log4j.Logger}.
 * 
 * @author Andrei Olaru
 */
public class Log4JWrapper extends Logger
{
	/**
	 * The wrapped instance of log4j {@link org.apache.log4j.Logger}.
	 */
	protected org.apache.log4j.Logger	theLog	= null;
	
	/**
	 * Constructs a new instance.
	 * 
	 * @param name
	 *            the name of the log
	 */
	public Log4JWrapper(String name)
	{
		theLog = org.apache.log4j.Logger.getLogger(name);
	}
	
	@Override
	public void setLevel(Level level)
	{
		theLog.setLevel(org.apache.log4j.Level.toLevel(level.toString()));
	}
	
	/**
	 * Adds a destination using a given pattern format.
	 * 
	 * @param format
	 *            the format
	 * @param destination
	 *            the destination stream
	 */
	protected void addDestination(String format, OutputStream destination)
	{
		theLog.addAppender(new WriterAppender(new PatternLayout(format), destination));
	}
	
	@Override
	public void addLocalDestination(OutputStream destination)
	{
		// level, message
		addDestination("%-5p \t %m%n", destination);
	}
	
	@Override
	public void addConsoleDestination(OutputStream destination)
	{
		// priority (level), name, message, line break
		addDestination("%-5p [" + theLog.getName() + "]:\t %m%n", destination);
	}
	
	@Override
	public void addReportDestination(OutputStream destination, Character separator)
	{
		// date level name message (no new line)
		addDestination(separator + "%d{HH:mm:ss:SSSS} %-5p [" + theLog.getName() + "]:\t %m" + separator, destination);
	}
	
	@Override
	public void error(String message)
	{
		theLog.error(message);
	}
	
	@Override
	public void warn(String message)
	{
		theLog.warn(message);
	}
	
	@Override
	public void info(String message)
	{
		theLog.info(message);
	}
	
	@Override
	public void trace(String message)
	{
		theLog.trace(message);
	}
	
}
