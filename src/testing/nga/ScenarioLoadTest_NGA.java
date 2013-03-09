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
package testing.nga;

import jade.core.AID;
import jade.core.Agent;
import jade.core.AgentState;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.util.ExtendedProperties;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import jade.wrapper.State;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


import tatami.core.agent.claim.parser.ClaimAgentDefinition;
import tatami.core.agent.claim.parser.Parser;
import tatami.core.agent.kb.Knowledge;
import tatami.core.agent.kb.simple.SimpleKnowledge;
import tatami.core.interfaces.Logger;
import tatami.core.util.logging.Log;
import tatami.pc.agent.visualization.VisualizationAgent;
import tatami.pc.util.XML.XMLParser;
import tatami.pc.util.XML.XMLTree;
import tatami.pc.util.XML.XMLTree.XMLNode;
import tatami.pc.util.windowLayout.LayoutIndications;
import tatami.pc.util.windowLayout.WindowLayout;
import tatami.pc.util.windowLayout.LayoutIndications.BarPosition;

/**
 * 
 * @author Nguyen Thi Thuy Nga
 * @version 3/6/11
 * 
 */

public class ScenarioLoadTest_NGA
{
	private static String		unitName			= "scenTestMain";
	protected static Logger		log					= Log.getLogger(unitName);
	
	private final static String	host				= "127.0.0.1";
	private final static int	port				= 8888;
	private final static String	packageName			= "phase2.ClaimAgent";					// name of package of all agent
	private final static String	LOAD_EVENT_AGENT	= "loadEvent";						// name of agent that loads events for simulation
																						
	private static String		schemaName			= "config/scenarioSchema.xsd";
	private static String		scenarioFileName	= "scenario/phase1/scenario.xml";
	
	private static Runtime		rt;
	
	private static String pathFolder = "./scenario/phase2/"; 
	
	public static void main(String args[])
	{
		// READ XML FILE
		
		log.trace("Hello World");
		
		// load the scenario
		
		WindowLayout.staticLayout = new WindowLayout(1200, 700, new LayoutIndications(10, 8)
			.indicateBar(BarPosition.LEFT, 70, 0)
			.indicateWindowType("agent", 4, 4)
			.indicateWindowType("system", 6, 4)
			, null);

		
		XMLTree scenarioTree = XMLParser.validateParse(schemaName, scenarioFileName);
		log.info(scenarioTree.toString());
		
		// do operations. e.g. find the list of agents to start in the first of the containers, and their type:
		// note! we suppose that the structure of the scenario XML tree is known
		
		// list of containers
		List<XMLNode> containers = scenarioTree.getRoot().getNodeIterator("initial").next().getNodes();
		//List<XMLNode> events = scenarioTree.getRoot().getNodeIterator("timeline").next().getNodes();
		
		// create the platform
		rt = Runtime.instance();
		/*****
		 * CREATE CONTAINERS AND AGENTS
		 */
		ContainerController mainController = createContainers(containers, rt);
		/****
		 * LOAD EVENTS (message)
		 */
		//loadEvent(events, mainController);
		Log.exitLogger(unitName);
	}
	
	/**
	 * Create containers and agents from XML tree return the reference to the main container of platform
	 * 
	 * @param containers
	 *            : list of xml nodes of tag container
	 * @param rt
	 *            : runtime of platform
	 * @return : reference to the main container of platform
	 */
	public static ContainerController createContainers(List<XMLNode> containers, Runtime rt)
	{
		
		ContainerController mainController = null; // return reference to the main controller of platform
		// properties to create containers
		ExtendedProperties pros = new ExtendedProperties();
		pros.setProperty("host", host);
		pros.setIntProperty("port", port);
		
		Collection<String> agentNames = new HashSet<String>();
		for(XMLNode containerNode : containers)
		{
			ContainerController containerRef = null;
			
			String containerType = containerNode.getAttributeValue("main");
			String containerName = containerNode.getAttributeValue("name");
			
			log.trace("new container " + containerName + "(" + containerType + ")");
			
			// if it is a main container
			if(containerType.equals("true"))
			{
				pros.setProperty(Profile.GUI, "true"); // set GUI
				Profile pMain = new ProfileImpl(pros);
				// create main container
				containerRef = rt.createMainContainer(pMain);
				mainController = containerRef;
			}
			else
			{
				pros.setProperty("name", containerName);
				pros.setProperty(Profile.MAIN, "false");
				Profile pContainer = new ProfileImpl(pros);
				// create agent container
				containerRef = rt.createAgentContainer(pContainer);
			}
			
			// list of agents of this container
			List<XMLNode> agents = containerNode.getNodes();
			Parser parser;
			// create agents
			for(XMLNode agentNode : agents)
			{
				// class and name
				String agentType = packageName;
				
				String agentClaimType = agentNode.getAttributeValue("type");
				//parse file
				String path = pathFolder.concat(agentClaimType).concat("Agent.adf2");
				parser = new Parser(path);
				ClaimAgentDefinition cad = parser.parse();
				
				String agentName = agentNode.getAttributeValue("name");
				
				//get parameters
				HashMap<String,String> parameters = new HashMap<String,String>();
				if(agentNode.getNodeIterator("parameter").next() != null){
					List<XMLNode> params = agentNode.getNodeIterator("parameter").next().getNodes();
					for(XMLNode param : params){
						String paramName = param.getAttributeValue("name");
						String paramValue = param.getAttributeValue("value");
						parameters.put(paramName, paramValue);
					}
				}
				// get knowledge
				
				ArrayList<SimpleKnowledge> knowledgeList = new ArrayList<SimpleKnowledge>();
				if(agentNode.getNodeIterator("knowledge").next() != null)
				{
					List<XMLNode> knowledge = agentNode.getNodeIterator("knowledge").next().getNodes();
					knowledgeList = getKnowledge(knowledge);
				}
				
				// create agent
				try
				{
					AgentController ag = containerRef.createNewAgent(agentName, agentType, new Object[] {cad, agentClaimType, parameters, knowledgeList });
					ag.start();
					agentNames.add(agentName);
				} catch(StaleProxyException e)
				{
					e.printStackTrace();
				}
			}
		}
		try
		{
			mainController.createNewAgent("visualizer", VisualizationAgent.class.getCanonicalName(), new Object[] {agentNames.toArray(new String[]{})}).start();
		} catch(StaleProxyException e)
		{
			e.printStackTrace();
		}
		return mainController;
	}
	
	/**
	 * Load events for simulation from XML tree
	 * 
	 * @param events
	 *            : list of XML nodes of tag event
	 * @param cagentType.concat(".").concat(
	 *            : containerController for creating agent that loads events for simulation
	 */
	public static void loadEvent(List<XMLNode> events, ContainerController c)
	{
		
		// Create agent for loading events
		try
		{
			AgentController loadEventAG = c.createNewAgent(LOAD_EVENT_AGENT, LoadEventAgent.class.getName(), new Object[] {});
			loadEventAG.start();
			
			// get events
			for(XMLNode eventNode : events)
			{
				// get event sendMessage
				List<XMLNode> sendMessEvs = eventNode.getNodeIterator("sendMessage").next().getNodes();
				if(sendMessEvs.size() > 0)
				{
					/**
					 * Create message for each event
					 */
					// get receiver of the message
					String receiver = (String)sendMessEvs.get(0).getValue();
					// get ontology of the message
					String ontology = (String)sendMessEvs.get(1).getValue();
					// get content of the message
					List<XMLNode> contents = eventNode.getNodeIterator("sendMessage").next().getNodeIterator("content").next().getNodes();
					ArrayList<SimpleKnowledge> kl = getKnowledge(contents);
					
					// create Message
					ACLMessage eventMess = new ACLMessage(ACLMessage.INFORM);
					eventMess.addReceiver(new AID(receiver, AID.ISLOCALNAME));
					eventMess.addReplyTo(new AID(receiver, AID.ISLOCALNAME));
					eventMess.setOntology(ontology);
					try
					{
						eventMess.setContentObject(kl);
					} catch(IOException e)
					{
						e.printStackTrace();
					}
					
					// put Event into object queue of load events agent
					State state = loadEventAG.getState();
					while(!(state.getCode() == AgentState.getInstance(Agent.AP_IDLE).getValue()))
					{
						try
						{
							Thread.sleep(100);
						} catch(InterruptedException e)
						{
							e.printStackTrace();
						}
						state = loadEventAG.getState();
					}
					
					loadEventAG.putO2AObject(eventMess, AgentController.SYNC);
				}
			}
			
		} catch(StaleProxyException e1)
		{
			e1.printStackTrace();
		}
	}
	
	/**
	 * Get Knowledge from XML tree //store in an ArrayList
	 * 
	 * @param knowledge
	 *            : list of xml nodes of tag knowledge
	 */
	public static ArrayList<SimpleKnowledge> getKnowledge(List<XMLNode> knowledge)
	{
		ArrayList<SimpleKnowledge> knowledgeList = new ArrayList<SimpleKnowledge>(); // list of knowledge relations
		SimpleKnowledge kl = new SimpleKnowledge();
		// go through each knowledge relation
		for(XMLNode knowledgeNode : knowledge)
		{
			// knowledge content
			ArrayList<String> simpleKnowledge = new ArrayList<String>();
			// knowledge type
			String knowledgeType = knowledgeNode.getAttributeValue("relationType") == null ? " " : knowledgeNode.getAttributeValue("relationType");
			List<XMLNode> relationNodes = knowledgeNode.getNodes();
			for(XMLNode node : relationNodes)
				simpleKnowledge.add((String)node.getValue());
			kl.setKnowledgeType(knowledgeType);
			kl.setSimpleKnowledge(simpleKnowledge);
			knowledgeList.add(new SimpleKnowledge(kl));
		}
		return knowledgeList;
	}
}
