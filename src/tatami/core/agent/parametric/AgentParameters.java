package tatami.core.agent.parametric;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * An instance of class stores the parameters of an agent (usually given at creation of the agent).
 * <p>
 * The class is underpinned by a set of map entries, it is therefore possible to add multiple entries with the same key.
 * <p>
 * The entries can have as name / key one of {@link AgentParameterName} or can be a {@link String}, for the so-called
 * 'unregistered parameters'.
 * <p>
 * The values of the entries are {@link Object} instances, but they are usually perceived as {@link String} or
 * non-String. Specialized methods exist to retrieve String values.
 * 
 * @author Andrei Olaru
 */
public class AgentParameters implements Serializable
{
	/**
	 * The class UID.
	 */
	private static final long						serialVersionUID	= -6934932321274715286L;
	
	/**
	 * The set of map entries String -> Object.
	 */
	private final Set<Map.Entry<String, Object>>	parameterSet		= new HashSet<Map.Entry<String, Object>>();
	
	/**
	 * Adds a new ('unregistered') parameter entry.
	 * 
	 * @param name
	 *            - the name (key) of the entry.
	 * @param value
	 *            - the value associated with the name.
	 * @return the instance itself, for chained calls.
	 */
	public AgentParameters add(String name, String value)
	{
		parameterSet.add(new AbstractMap.SimpleEntry<String, Object>(name, value));
		return this;
	}
	
	/**
	 * Adds a new 'registered' parameter entry.
	 * 
	 * @param name
	 *            - the name of the entry, as an {@link AgentParameterName} instance.
	 * @param value
	 *            - the value of the entry.
	 * @return the instance itself, for chained calls.
	 */
	public AgentParameters add(AgentParameterName name, String value)
	{
		parameterSet.add(new AbstractMap.SimpleEntry<String, Object>(name.toString(), value));
		return this;
	}
	
	/**
	 * Adds a new ('unregistered') parameter entry. This version of the method supports any {@link Object} instance as
	 * value.
	 * 
	 * @param name
	 *            - the name (key) of the entry.
	 * @param value
	 *            - the value associated with the name.
	 * @return the instance itself, for chained calls.
	 */
	public AgentParameters addObject(String name, Object value)
	{
		parameterSet.add(new AbstractMap.SimpleEntry<String, Object>(name, value));
		return this;
	}
	
	/**
	 * Adds a new 'registered' parameter entry. This version of the method supports any {@link Object} instance as
	 * value.
	 * 
	 * @param name
	 *            - the name of the entry, as an {@link AgentParameterName} instance.
	 * @param value
	 *            - the value of the entry.
	 * @return the instance itself, for chained calls.
	 */
	public AgentParameters addObject(AgentParameterName name, Object value)
	{
		parameterSet.add(new AbstractMap.SimpleEntry<String, Object>(name.toString(), value));
		return this;
	}
	
	/**
	 * Retrieves the first value matching the given name. It is not guaranteed that other entries with the same name do
	 * not exist. If the first found value is not a {@link String}, an exception will be thrown.
	 * 
	 * @param name
	 *            - the name of the searched entry.
	 * @return the value of an entry with the given name.
	 */
	public String get(String name)
	{
		for(Map.Entry<String, Object> entry : parameterSet)
			if(entry.getKey().equals(name))
			{
				if(entry.getValue() instanceof String)
					return (String) entry.getValue();
				throw new IllegalStateException("Value cannot be converted to String");
			}
		return null;
	}
	
	/**
	 * Alias for the <code>get()</code> method.
	 * 
	 * @param name
	 *            - the name of the searched entry.
	 * @return the value of an entry with the given name.
	 */
	public String getValue(String name)
	{
		return get(name);
	}
	
	/**
	 * Retrieves all values matching the given name, as a {@link Set}. If any value is not a {@link String}, an
	 * exception will be thrown.
	 * 
	 * @param name
	 *            - the name to search for.
	 * @return a {@link Set} of values associated with the name.
	 */
	public Set<String> getValues(String name)
	{
		Set<String> ret = new HashSet<String>();
		for(Map.Entry<String, Object> entry : parameterSet)
			if(entry.getKey().equals(name))
			{
				if(entry.getValue() instanceof String)
					ret.add((String) entry.getValue());
				throw new IllegalStateException("Value cannot be converted to String");
			}
		return ret;
	}
	
	/**
	 * Retrieves the value associated with a name, as an {@link Object} instance.
	 * 
	 * @param name
	 *            - the name of the searched entry.
	 * @return the value associated with the name.
	 */
	public Object getObject(String name)
	{
		for(Map.Entry<String, Object> entry : parameterSet)
			if(entry.getKey().equals(name))
				return entry.getValue();
		return null;
	}
	
	/**
	 * Indicates whether an entry with the specified name exists.
	 * 
	 * @param name
	 *            - the name of the searched entry.
	 * @return - <code>true</code> if an entry with the specified name exists.
	 */
	public boolean isSet(String name)
	{
		for(Map.Entry<String, Object> entry : parameterSet)
			if(entry.getKey().equals(name))
				return true;
		return false;
	}
	
	/**
	 * Method to retrieve a map of those parameters which are 'unregistered', i.e. their name is not the name of an
	 * {@link AgentParameterName} instance. Multiple entries for the same key will be ignored.
	 * 
	 * @return a {@link Map} (ergo, no duplicate unregistered parameters allowed) with parameters whose names are not in
	 *         {@link AgentParameterName}.
	 */
	public Map<String, Object> getUnregisteredParameters()
	{
		Map<String, Object> ret = new HashMap<String, Object>();
		for(Map.Entry<String, Object> entry : parameterSet)
			if(AgentParameterName.getName(entry.getKey()) == null)
				ret.put(entry.getKey(), entry.getValue());
		return ret;
	}
}