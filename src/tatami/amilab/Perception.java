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

/**
 * Wrapper for the raw data obtained from the Kestrel queue.
 * 
 * @author Claudiu-Mihai Toma
 *
 */
public class Perception
{
	/**
	 * Type of this perception.
	 */
	private AmILabDataType _type;

	/**
	 * The timestamp of this perception.
	 */
	private long _timestamp;

	/**
	 * The actual data received from the Kestrel queue. It is in JSON format.
	 */
	private String _data;

	/**
	 * Basic constructor that needs all the information about the perception.
	 * 
	 * @param type
	 *            - type of this perception
	 * @param timestamp
	 *            - the timestamp of this perception
	 * @param data
	 *            - JSON from a Kestrel queue
	 */
	public Perception(AmILabDataType type, long timestamp, String data)
	{
		_type = type;
		_timestamp = timestamp;
		_data = data;
	}

	/**
	 * Type getter.
	 * 
	 * @return the type of this perception
	 */
	public AmILabDataType getType()
	{
		return _type;
	}

	/**
	 * Type setter.
	 * 
	 * @param type
	 *            - the type of this perception
	 */
	public void setType(AmILabDataType type)
	{
		_type = type;
	}

	/**
	 * Timestamp getter.
	 * 
	 * @return the timestamp of this perception
	 */
	public long getTimestamp()
	{
		return _timestamp;
	}

	/**
	 * Timestamp setter.
	 * 
	 * @param timestamp
	 *            - the timestamp of this perception
	 */
	public void setTimestamp(long timestamp)
	{
		_timestamp = timestamp;
	}

	/**
	 * Data getter.
	 * 
	 * @return JSON from a Kestrel queue
	 */
	public String getData()
	{
		return _data;
	}

	/**
	 * Data setter.
	 * 
	 * @param data
	 *            - JSON from a Kestrel queue
	 */
	public void setData(String data)
	{
		_data = data;
	}
}
