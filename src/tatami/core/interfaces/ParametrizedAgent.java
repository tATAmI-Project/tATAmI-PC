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
package tatami.core.interfaces;

import jade.core.Location;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import tatami.core.agent.BaseAgent;
import tatami.core.agent.claim.ClaimAgent;
import tatami.core.agent.claim.parser.ClaimAgentDefinition;
import tatami.core.agent.hierarchical.HierarchicalAgent;
import tatami.core.agent.visualization.VisualizableAgent;

public interface ParametrizedAgent
{
	/**
	 * Here be all names of parameters used by any layer of the agent.
	 * <p>
	 * Each parameter has an internal name - the constant in the enum - and the name in the scenario - given in the
	 * constructor of the enum constant.
	 * <p>
	 * It would be advised that these values be used when reading from the XML file.
	 * <p>
	 * Note about the javadoc: some links were not included in order to avoid imports outside of the <code>core</code>
	 * package.
	 * 
	 * @author Andrei Olaru
	 * 
	 */
	public enum AgentParameterName {
		
		// ///////// Simulation/Boot
		/**
		 * The class (for non-CLAIM agents) or the type (adf definition) (for CLAIM agents) of the agent.
		 * 
		 * Used by <code>simulation.Boot</code> (to load the agent) and <code>WSAgent</code> (to register the service
		 * under this type).
		 */
		AGENT_CLASS("class"),
		
		/**
		 * Specifies the classes with java code to attach to the agent definition.
		 * 
		 * Used by <code>simulation.Boot</code> / {@link ClaimAgent}.
		 */
		JAVA_CODE("java-code"),
		
		// ///////// BaseAgent
		/**
		 * The name of the agent.
		 * 
		 * Used by {@link BaseAgent}.
		 */
		AGENT_NAME("name"),
		
		/**
		 * Initial agent knowledge, represented as a text representation of the knowledge graph.
		 * 
		 * Used by {@link BaseAgent}.
		 */
		KNOWLEDGE("knowledge"),
		
		// ///////// VisualizableAgent
		/**
		 * The type of the window (used for <code>WindowLayout</code>).
		 * 
		 * Used by {@link VisualizableAgent}.
		 */
		WINDOW_TYPE("windowType"),
		
		/**
		 * The GUI class of the agent. See {@link VisualizableAgent} to see how this works.
		 * 
		 * Used by {@link VisualizableAgent}.
		 */
		GUI("GUI"),
		
		/**
		 * Agent packages with classes that are relevant to this agent (GUIs, java functions, etc).
		 * 
		 * Used by {@link VisualizableAgent} (for the GUI) and {@link ClaimAgent} (for java function classes).
		 */
		AGENT_PACKAGE("agentPackage"),
		
		// ///////// HierarchicalAgent
		/**
		 * Equals <code>"true"</code> if the agent should not leave its container when the parent moves.
		 * 
		 * Used by {@link HierarchicalAgent}.
		 */
		FIXED("fixed"),
		
		/**
		 * The hierarchical parent (which the agent follows when the parent moves).
		 * 
		 * Used by {@link HierarchicalAgent}.
		 */
		HIERARCHICAL_PARENT("parent"),
		
		/**
		 * The initial location of the agent (where the agent should move to immediately after creation).
		 * 
		 * Is a {@link Location}.
		 * 
		 * Used by {@link HierarchicalAgent}.
		 */
		INITIAL_LOCATION("initialLocation"),
		
		// ////////// ClaimAgent
		/**
		 * The definition of the CLAIM agent.
		 * 
		 * Is a {@link ClaimAgentDefinition}.
		 * 
		 * Used by {@link ClaimAgent}.
		 */
		AGENT_DEFINITION("agentDefinition"),
		
		// //////// SimulationAgent
		/**
		 * Is a {@link JadeInterface}.
		 */
		JADE_INTERFACE("jadeInterface"),
		
		/**
		 * Is a {@link Collection} of <code>AgentCreationData</code>.
		 */
		AGENTS("agents"),
		
		/**
		 * Name of the Visualization Agent.
		 */
		VISUALIZTION_AGENT("visualizationAgent"),
		
		/**
		 * The timeline.
		 * 
		 * Is a <code>XMLNode</code>.
		 */
		TIMELINE("timeline"),
		
		;
		
		String	name	= null;
		
		private AgentParameterName(String parName)
		{
			name = parName;
		}
		
		@Override
		public String toString()
		{
			return name;
		}
		
		public static AgentParameterName getName(String name)
		{
			for(AgentParameterName parName : AgentParameterName.values())
				if(parName.toString().equals(name))
					return parName;
			return null;
		}
	}
	
	public static class AgentParameters implements Serializable
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
					return (String) entry.getValue();
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
					ret.add((String) entry.getValue());
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
		 * @return a {@link Map} (ergo, no duplicate unregistered parameters allowed) with parameters whose names are
		 *         not in {@link AgentParameterName}.
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
	
	String parVal(AgentParameterName name);
	
	Collection<String> parVals(AgentParameterName name);
	
	Object parObj(AgentParameterName name);
	
	boolean hasPar(AgentParameterName name);
	
	Map<String, Object> getUnregisteredParameters();
}
