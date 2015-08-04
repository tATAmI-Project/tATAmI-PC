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
