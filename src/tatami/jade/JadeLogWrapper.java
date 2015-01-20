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
package tatami.jade;

import java.io.OutputStream;

import net.xqhs.util.logging.LoggerSimple.Level;
import net.xqhs.util.logging.logging.LogWrapper;


/**
 * INCOMPLETE
 * 
 * @author Andrei Olaru
 * 
 */
public class JadeLogWrapper extends LogWrapper
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
	public void l(Level level, String message)
	{
		switch(level)
		{
		case ERROR:
			theLog.log(jade.util.Logger.SEVERE, message);
			break;
		case INFO:
			theLog.log(jade.util.Logger.INFO, message);
			break;
		case TRACE:
			theLog.log(jade.util.Logger.FINER, message);
			break;
		case WARN:
			theLog.log(jade.util.Logger.WARNING, message);
			break;
		default:
			// should not get here
			break;
		}
		
	}

	@Override
	public void exit()
	{
		// TODO Auto-generated method stub
		
	}
	
}
