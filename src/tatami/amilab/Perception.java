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
package tatami.amilab;

import tatami.amilab.AmILabComponent.AmILabDataType;

public class Perception
{
	private AmILabDataType	_type;
	private long			_timestamp;
	private String			_data;

	public Perception(AmILabDataType type, long timestamp, String data)
	{
		_type = type;
		_timestamp = timestamp;
		_data = data;
	}

	public AmILabDataType getType()
	{
		return _type;
	}

	public void setType(AmILabDataType type)
	{
		_type = type;
	}

	public long getTimestamp()
	{
		return _timestamp;
	}

	public void setTimestamp(long timestamp)
	{
		_timestamp = timestamp;
	}

	public String getData()
	{
		return _data;
	}

	public void setData(String data)
	{
		_data = data;
	}
}
