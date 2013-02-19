package testing.andrei;

import java.util.List;



import tatami.core.interfaces.Logger;
import tatami.core.util.logging.Log;
import tatami.pc.util.XML.XMLParser;
import tatami.pc.util.XML.XMLTree;
import tatami.pc.util.XML.XMLTree.XMLNode;





public class ScenarioLoadTest
{
	private static String unitName = "scenTestMain";
	private static String schemaNames[] = {"config/scenarioSchema.xsd"};
	private static String scenarioFileName = "scenario/phase1/scenario.xml";
	
	public static void main(String args[])
	{
		Logger log = Log.getLogger(unitName);
		log.trace("Hello World");
		
		// load the scenario
		
		XMLTree scenarioTree = XMLParser.validateParse(schemaNames, scenarioFileName);	// FIXME: validation throws one exception
		
		log.info("\n" + scenarioTree.toString());	// XMLTree.toString() prints out the whole tree, nicely indented 
		
		// do operations. e.g. find the list of agents to start in the first of the containers, and their type:
		// note! we suppose that the structure of the scenario XML tree is known
		
		List<XMLNode> agents = scenarioTree.getRoot()	// get the root
									.getNodeIterator("initial").next()	// get the first (and only) "initial" node
									.getNodeIterator("container").next()	// get the first "container" node
									.getNodes();	// get the nodes (we know that they are agents)
		for(XMLNode agentNode : agents)
		{
			String agentType = agentNode.getAttributeValue("type");
			String agentName = agentNode.getAttributeValue("name");
			log.info("[" + agentType + "] : [" + agentName + "]");
		}
			
		
		Log.exitLogger(unitName);
	}
	
}