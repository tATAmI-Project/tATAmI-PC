package tatami.core.agent.parametric;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;


public class AgentParameters implements Serializable
{
	private static final long						serialVersionUID	= -6934932321274715286L;
	
	private final Set<Map.Entry<String, Object>>	parameterMap		= new HashSet<Map.Entry<String, Object>>();
	
	public AgentParameters add(String name, String value)
	{
		parameterMap.add(new AbstractMap.SimpleEntry<String, Object>(name, value));
		return this;
	}
	
	public AgentParameters add(AgentParameterName name, String value)
	{
		parameterMap.add(new AbstractMap.SimpleEntry<String, Object>(name.toString(), value));
		return this;
	}
	
	public AgentParameters addObject(String name, Object value)
	{
		parameterMap.add(new AbstractMap.SimpleEntry<String, Object>(name, value));
		return this;
	}
	
	public AgentParameters addObject(AgentParameterName name, Object value)
	{
		parameterMap.add(new AbstractMap.SimpleEntry<String, Object>(name.toString(), value));
		return this;
	}
	
	public String get(String name)
	{
		for(Map.Entry<String, Object> entry : parameterMap)
			if(entry.getKey().equals(name))
				return (String)entry.getValue();
		return null;
	}
	
	public String getValue(String name)
	{
		return get(name);
	}
	
	public Set<String> getValues(String name)
	{
		Set<String> ret = new HashSet<String>();
		for(Map.Entry<String, Object> entry : parameterMap)
			if(entry.getKey().equals(name))
				ret.add((String)entry.getValue());
		return ret;
	}
	
	public Object getObject(String name)
	{
		for(Map.Entry<String, Object> entry : parameterMap)
			if(entry.getKey().equals(name))
				return entry.getValue();
		return null;
	}
	
	public boolean isSet(String name)
	{
		for(Map.Entry<String, Object> entry : parameterMap)
			if(entry.getKey().equals(name))
				return true;
		return false;
	}
	
	/**
	 * @return a {@link Map} (ergo, no duplicate unregistered parameters allowed) with
	 *         parameters whose names are not in {@link AgentParameterName}.
	 */
	public Map<String, Object> getUnregisteredParameters()
	{
		Map<String, Object> ret = new HashMap<String, Object>();
		for(Map.Entry<String, Object> entry : parameterMap)
			if(AgentParameterName.getName(entry.getKey()) == null)
				ret.put(entry.getKey(), entry.getValue());
		return ret;
	}
}