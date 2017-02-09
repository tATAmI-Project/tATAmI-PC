/*******************************************************************************
 * Copyright (C) 2015 Andrei Olaru, Marius-Tudor Benea, Nguyen Thi Thuy Nga, Amal El Fallah Seghrouchni, Cedric Herpson.
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

import tatami.core.platforms.PlatformDescriptor;

/**
 * THe default platform for running agents. It is a minimal platform, offering no facilities.
 * <p>
 * Loading agents on the platform will practically have no effect on the agents.
 * 
 * @author Andrei Olaru
 */
public class DefaultPlatform extends PlatformLoader
{
	
	@Override
	public String getName()
	{
		return StandardPlatformType.DEFAULT.toString();
	}
	
	@Override
	public PlatformLoader setConfig(PlatformDescriptor configuration)
	{
		// do nothing.
		return this;
	}
	
	@Override
	public boolean start()
	{
		// does nothing.
		return true;
	}
	
	@Override
	public boolean stop()
	{
		// does nothing.
		return true;
	}

    @Override
    public void onStartAgent(String path) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onStopAgent(String path) {
        // TODO Auto-generated method stub
        
    }
}
