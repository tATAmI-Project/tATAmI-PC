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
package tatami.core.util.logging;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import tatami.core.interfaces.Logger;

/**
 * A {@link Logger}-implementing wrapper of the Java {@link java.util.logging.Logger}.
 * 
 * @author Andrei Olaru
 */
public class JavaLogWrapper extends Logger
{
	/**
	 * The wrapped instance of Java {@link java.util.logging.Logger}.
	 */
	protected java.util.logging.Logger	theLog		= null;
	/**
	 * The list of handlers for the log. It is needed to be able to <code>flush</code> their content.
	 */
	protected ArrayList<Handler>		handlers	= new ArrayList<Handler>();
	
	/**
	 * Constructs a new instance.
	 * 
	 * @param name
	 *            the name of the logger
	 */
	public JavaLogWrapper(String name)
	{
		theLog = java.util.logging.Logger.getLogger(name);
		theLog.setUseParentHandlers(false);
	}
	
	/**
	 * Flushes all handlers. Should be called after every log entry, as the OutputStreamWriter of the logger does not
	 * flush automatically.
	 */
	protected void flush()
	{
		for(Handler h : handlers)
			h.flush();
	}
	
	@Override
	public void setLevel(Level level)
	{
		theLog.setLevel(java.util.logging.Level.parse(level.toString()));
	}
	
	@Override
	public void error(String message)
	{
		theLog.log(java.util.logging.Level.INFO, message);
		flush();
	}
	
	@Override
	public void warn(String message)
	{
		theLog.log(java.util.logging.Level.WARNING, message);
		flush();
	}
	
	@Override
	public void info(String message)
	{
		theLog.log(java.util.logging.Level.INFO, message);
		flush();
	}
	
	@Override
	public void trace(String message)
	{
		theLog.log(java.util.logging.Level.FINER, message);
		flush();
	}
	
	@Override
	public void addLocalDestination(OutputStream destination)
	{
		Handler h = new StreamHandler(destination, new SimpleFormatter() {
			@Override
			public synchronized String format(LogRecord record)
			{
				return record.getLevel().toString() + "\t" + record.getMessage() + "\n";
			}
		});
		handlers.add(h);
		theLog.addHandler(h);
	}
	
	@Override
	public void addConsoleDestination(final OutputStream destination)
	{
		Handler h = new StreamHandler(destination, new SimpleFormatter() {
			@Override
			public synchronized String format(LogRecord record)
			{
				return record.getLevel().toString() + "\t" + record.getLoggerName() + ":\t" + record.getMessage()
						+ "\n";
			}
		});
		h.setLevel(theLog.getLevel());
		handlers.add(h);
		theLog.addHandler(h);
	}
	
	@Override
	public void addReportDestination(OutputStream destination, final Character separator)
	{
		Handler h = new StreamHandler(destination, new SimpleFormatter() {
			@Override
			public synchronized String format(LogRecord record)
			{
				return separator.toString()
						+ new SimpleDateFormat("HH:mm:ss:ssss").format(new Long(record.getMillis()))
						+ record.getLevel().toString() + "[" + record.getLoggerName() + "]\t" + record.getMessage()
						+ separator;
			}
		});
		handlers.add(h);
		theLog.addHandler(h);
	}
}
