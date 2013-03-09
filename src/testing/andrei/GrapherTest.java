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
package testing.andrei;

import java.util.Random;


import tatami.core.interfaces.Logger;
import tatami.core.interfaces.Logger.Level;
import tatami.core.util.graph.Edge;
import tatami.core.util.graph.Graph;
import tatami.core.util.graph.Node;
import tatami.core.util.graph.representation.TextGraphRepresentation;
import tatami.core.util.logging.Log;
import tatami.core.util.logging.Unit;
import tatami.core.util.logging.Unit.UnitConfigData;
import tatami.pc.util.files.FileUtils;

public class GrapherTest
{
	private static String	unitName	= "grapherTestMain";
	
	private static String	testDir		= "playground/graphplay/";
	
	public static void main(String args[])
	{
		Unit thisUnit = new Unit(new UnitConfigData().setName(unitName).setLevel(Level.ALL));
		Logger log = thisUnit.getLog();
		log.trace("Hello World");
		
		// generate graph
		Graph G = new Graph();
		
		// Random rand = new Random(675346416);
		// int nNodes = 7;
		// int nEdges = 10;
		long seed = System.currentTimeMillis();
		// seed = 1307714367060L; // buggy // fixed
		// seed = 1310205248694L; // buggy with 6 nodes, 8 edges // fixed
		// seed = 1310208355983L; // buggy // fixed
		// seed = 1310475944100L; // buggy for 9 nodes, 8 edges // fixed
		log.trace("seed was " + seed);
		Random rand = new Random(seed);
		int nNodes = 9;
		int nEdges = 8;
		Node nodes[] = new Node[nNodes];
		
		for(int i = 0; i < nNodes; i++)
		{
			nodes[i] = new Node(Character.toString((char)('A' + i)));
			G.addNode(nodes[i]);
		}
		// *
		for(int i = 0; i < nEdges; i++)
		{
			int from = rand.nextInt(nNodes);
			int to = rand.nextInt(nNodes);
			while((from == to) || (nodes[from].outList().contains(nodes[to])))
				to = rand.nextInt(nNodes);
			G.addEdge(new Edge(nodes[from], nodes[to], null)); // Character.toString((char)('a' + i))
		}
		/*
		 * /
		 * 
		 * // static tests: G.addEdge(new Edge(nodes[4], nodes[0], null)); G.addEdge(new Edge(nodes[4], nodes[3], null)); G.addEdge(new Edge(nodes[3], nodes[5],
		 * null)); G.addEdge(new Edge(nodes[0], nodes[6], null)); G.addEdge(new Edge(nodes[6], nodes[5], null)); G.addEdge(new Edge(nodes[3], nodes[1], null));
		 * G.addEdge(new Edge(nodes[1], nodes[2], null)); G.addEdge(new Edge(nodes[2], nodes[7], null)); G.addEdge(new Edge(nodes[7], nodes[8], null));
		 * G.addEdge(new Edge(nodes[5], nodes[8], null)); //
		 */
		// ////
		// G.addEdge(new Edge(nodes[2], new Node("hello"), null)); // node hello is outside G
		// new Edge(nodes[2], nodes[3], "hello_edge"); // edge is outside G (but nodes are not)
		// //
		// G.addEdge(new Edge(nodes[0], nodes[1], null));
		// G.addEdge(new Edge(nodes[0], nodes[4], null));
		// G.addEdge(new Edge(nodes[1], nodes[0], null));
		// G.addEdge(new Edge(nodes[1], nodes[2], null));
		// G.addEdge(new Edge(nodes[2], nodes[3], null));
		// G.addEdge(new Edge(nodes[5], nodes[1], null));
		// G.addEdge(new Edge(nodes[5], nodes[4], null));
		// G.addEdge(new Edge(nodes[4], nodes[5], null));
		// G.addEdge(new Edge(nodes[4], nodes[0], null));
		
		// print out the graph
		
		/*
		 * log.info(G.toString()); Grapher grapher = new Grapher(G).config("\n", "\t", 1); log.info("\n\n" + grapher.toString() + "\n"); //
		 */
		// test reading
		// TextGraphRepresentation GRa = new TextGraphRepresentation(new TextGraphRepresentation.GraphConfig(G).setLayout("", " ", -1));
		// log.info(GRa.toString());
		//
		// Graph GR = TextGraphRepresentation.readRepresentation(GRa.toString(), null, new Unit.UnitConfigData().setName(null));
		// TextGraphRepresentation GRR = new TextGraphRepresentation(new TextGraphRepresentation.GraphConfig(GR).setLayout("", " ", -1));
		// log.info("\n\n" + GR.toString() + "\n");
		// log.info("\n\n" + GRR.toString() + "\n");
		// log.info(GRR.toString().equals(GRa.toString()) ? "===OK" : "===NOPE");
		
		/*
		 * // String input = "hello - world \n world -> big \n whoa - is for > hello"; String input = "Albert -in> London ;Albert -isa> User \n" +
		 * "Schedule -of> Albert \n" + "attend -part-of>Schedule \n" + "flight -part-of> attend \n" + "flight -from>LHR \n" + "flight -to> CDG \n" +
		 * "LHR -in> London \n" + "CDG -in> Paris \n" + "CNAM -in> Paris \n" + "AI Conf -venue> CNAM \n" + "AI Conf -isa> Activity \n" +
		 * "attend -isa> Activity \n" + "flight -isa> Activity \n" + "attend -> AI Conf \n" + "LHR -isa> airport \n" + "CDG -isa> airport \n"; Graph G2 =
		 * Graph.readFrom(new ByteArrayInputStream(input.getBytes())); log.info(G2.toString()); TextGraphRepresentation G2R = new
		 * TextGraphRepresentation((GraphConfig)new TextGraphRepresentation.GraphConfig(G2).setLayout("\n", "\t", 2).setName(null).setLevel(Level.ALL));
		 * log.info("\n\n" + G2R.toString() + "\n");
		 * 
		 * // testing representation reading TextGraphRepresentation G2Ra = new TextGraphRepresentation(new
		 * TextGraphRepresentation.GraphConfig(G2).setLayout("", " ", -1)); log.info(G2Ra.toString());
		 * 
		 * Graph G2Rev = TextGraphRepresentation.readRepresentation(G2Ra.toString(), null, new Unit.UnitConfigData().setName(Unit.DEFAULT_UNIT_NAME));
		 * TextGraphRepresentation G2RR = new TextGraphRepresentation(new TextGraphRepresentation.GraphConfig(G2Rev).setLayout("\n", "\t", 2)); log.info("\n\n"
		 * + G2Rev.toString() + "\n"); log.info("\n\n" + G2RR.toString() + "\n");
		 * 
		 * //
		 */
		
		/*
		 * String input = ""; // input += "UniversityUPMCAgent -resides-on> AdministrationContainer;"; // input +=
		 * "SchedulerUPMCAgent -resides-on> AdministrationContainer;"; // input += "CourseCSAgent -resides-on> AdministrationContainer;"; // input +=
		 * "Room04Agent -resides-on> RoomContainer;"; // input += "AliceAgent -resides-on> AliceContainer;"; input +=
		 * "Room04Agent-has-parent>UniversityUPMCAgent;"; input += "AliceAgent -has-parent> CourseCSAgent;"; // input +=
		 * "CourseCSAgent -has-parent> UniversityUPMCAgent;"; input += "CourseCSAgent -has-parent> Room04Agent;"; input +=
		 * "SchedulerUPMCAgent -has-parent> UniversityUPMCAgent;"; Graph G3 = Graph.readFrom(new ByteArrayInputStream(input.getBytes()));//, new
		 * UnitConfigData("G3").setLevel(Level.INFO).setLink(unitName)); log.info(G3.toString());
		 * 
		 * TextGraphRepresentation.GraphConfig configT = new TextGraphRepresentation.GraphConfig(G3).setLayout("\n", "\t", 2);
		 * configT.setName(Unit.DEFAULT_UNIT_NAME).setLink(unitName).setLevel(Level.ERROR); // configT.setBackwards(); TextGraphRepresentation G3RT = new
		 * TextGraphRepresentation(configT); log.info(G3RT.displayRepresentation());
		 * 
		 * // test reading Graph G2RTRev = TextGraphRepresentation.readRepresentation(G3RT.toString(), null, new
		 * Unit.UnitConfigData().setName(Unit.DEFAULT_UNIT_NAME)); TextGraphRepresentation G2RTR = new TextGraphRepresentation(new
		 * TextGraphRepresentation.GraphConfig(G2RTRev).setLayout("\n", "\t", 2)); log.info("\n\n" + G2RTRev.toString() + "\n"); log.info("\n\n" +
		 * G2RTR.toString() + "\n");
		 * 
		 * 
		 * // G3RT.update(); // log.info(G3RT.displayRepresentation());
		 * 
		 * /* GraphicalGraphRepresentation.GraphConfig configX = new GraphicalGraphRepresentation.GraphConfig(G3);
		 * configX.setBackwards().setName(Unit.DEFAULT_UNIT_NAME).setLink(unitName).setLevel(Level.INFO); GraphicalGraphRepresentation G3RX = new
		 * GraphicalGraphRepresentation(configX); log.info(G3RX.displayRepresentation());
		 * 
		 * // String containers[] = {"AdministrationContainer", "RoomContainer", "AliceContainer"}; // // Map<Node, Node> agentLevel = new HashMap<Graph.Node,
		 * Graph.Node>(); // for(Edge edge : G3.getEdges()) // if(edge.getName() == "resides-on") // agentLevel.put(edge.getFrom(), edge.getTo()); // //
		 * Map<Node, Node> containersLevel = new HashMap<Graph.Node, Graph.Node>(); // for(String containerName : containers) //
		 * containersLevel.put(G3.getNodesNamed(containerName).iterator().next(), null); // // LinkedList<Map<Node,Node>> levels = new
		 * LinkedList<Map<Node,Node>>(); // levels.add(agentLevel); // levels.add(containersLevel); // MultilevelGraphRepresentation G3R = new
		 * TextMultilevelGraphRepresentation(G3, levels, null); // // log.info("\n\n" + G3R.toString() + "\n");
		 * 
		 * JFrame acc = new JFrame(unitName); acc.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); acc.setLocation(100, 100); acc.setSize(800, 500);
		 * acc.add(G3RX.displayRepresentation()); acc.setVisible(true);
		 * 
		 * //
		 */
		
		// transform linear representation files into .dot files
		String files[] = new String[] { "alice", "aliceP", "conf", "confP", "book", "bookP" };
		String extension_in = ".txt";
		String extension_out = ".dot.txt";
		for(String file : files)
		{
			Graph graph = TextGraphRepresentation.readRepresentation(FileUtils.fileToString(testDir + file + extension_in)); //, null, new UnitConfigData().setName(Unit.DEFAULT_UNIT_NAME).setLevel(Level.ALL));
			TextGraphRepresentation graphR = new TextGraphRepresentation(new TextGraphRepresentation.GraphConfig(graph).setLayout("\n", "\t", 5));
			log.info("\n\n" + graph.toString() + "\n");
			log.info("\n\n" + graphR.toString() + "\n");
			FileUtils.stringToFile(testDir + file + extension_out, graph.toDot());
		}
		
		Log.exitLogger(unitName);
	}
}
