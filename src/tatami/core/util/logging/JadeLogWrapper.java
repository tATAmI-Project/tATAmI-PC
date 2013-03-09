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

import tatami.core.interfaces.Logger;

/**
 * INCOMPLETE
 * 
 * @author Andrei Olaru
 * 
 */
public class JadeLogWrapper extends Logger
{
	
	protected jade.util.Logger	theLog	= null;
	
	public JadeLogWrapper(String name)
	{
		theLog = jade.util.Logger.getJADELogger(name);
	}
	
	@Override
	public void setLevel(Level level)
	{
		
		// theLog.setLevel(jade.util.Logger.INFO); // TODO
	}
	
	public void addDestination(String format, OutputStream destination)
	{
		
		final String formatarg = format;
		/*
		 * theLog.addHandler(new StreamHandler(destination, new Formatter(){
		 * 
		 * @Override public String format(LogRecord record) { return formatarg; }
		 * 
		 * }));
		 */
	}
	
	@Override
	public void error(String message)
	{
		
		theLog.log(jade.util.Logger.INFO, message);
		
	}
	
	@Override
	public void warn(String message)
	{
		
		theLog.log(jade.util.Logger.WARNING, message);
		
	}
	
	@Override
	public void info(String message)
	{
		
		theLog.log(jade.util.Logger.INFO, message);
		
	}
	
	@Override
	public void trace(String message)
	{
		
		theLog.log(jade.util.Logger.FINER, message);
		
	}
	
	@Override
	public void addLocalDestination(OutputStream destination)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void addConsoleDestination(OutputStream destination)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void addReportDestination(OutputStream destination, Character separator)
	{
		// TODO Auto-generated method stub
		
	}
	
}
