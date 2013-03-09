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

import java.io.ByteArrayInputStream;



import tatami.core.interfaces.Logger;
import tatami.core.interfaces.Logger.Level;
import tatami.core.util.graph.Graph;
import tatami.core.util.graph.GraphMatcher;
import tatami.core.util.graph.GraphPattern;
import tatami.core.util.graph.GraphPattern.EdgeP;
import tatami.core.util.graph.GraphPattern.NodeP;
import tatami.core.util.graph.representation.TextGraphRepresentation;
import tatami.core.util.logging.Log;
import tatami.core.util.logging.Unit;
import tatami.core.util.logging.Unit.UnitConfigData;

public class GraphMatcherTest
{
	private static String	unitName	= "graphMatcherTestMain";
	
	public static void main(String[] args)
	{
		Logger log = Log.getLogger(unitName);
		log.trace("Hello World");
		
		String input = "";
		input += "AIConf -> conftime;";
		input += "conftime -isa> interval;";
		input += "AIConf -> CFP;";
		input += "CFP -> AIConf;";
		input += "CFP -isa> document;";
		input += "CFP -contains> 05012011;";
		input += "05012011 -isa> date;";
		input += "CFP -contains> 30032011;";
		input += "30032011 -isa> date;";
		input += "AIConf -> 30032011;";
		input += "CFP -contains> conftime;";
		Graph G = Graph.readFrom(new ByteArrayInputStream(input.getBytes()), new UnitConfigData().setName("G").setLevel(Level.INFO).setLink(unitName));
		log.info(G.toString());
		
		TextGraphRepresentation.GraphConfig configT = new TextGraphRepresentation.GraphConfig(G).setLayout("\n", "\t", 2);
		configT.setName(Unit.DEFAULT_UNIT_NAME).setLink(unitName).setLevel(Level.ERROR);
		TextGraphRepresentation GRT = new TextGraphRepresentation(configT);
		log.info(GRT.displayRepresentation());
		
		GraphPattern GP = new GraphPattern(new UnitConfigData().setName("GP").setLevel(Level.INFO).setLink(unitName));
		NodeP nConf = new NodeP();
		NodeP nDeadline = new NodeP();
		NodeP nCFP = new NodeP();
		NodeP nArticle = new NodeP();
		NodeP nConfType = new NodeP("conference");
		NodeP nDocumentType = new NodeP("document");
		NodeP nDateType = new NodeP("date");
		GP.addNode(nConf);
		GP.addNode(nDeadline);
		GP.addNode(nCFP);
		GP.addNode(nArticle);
		GP.addNode(nConfType);
		GP.addNode(nDocumentType);
		GP.addNode(nDateType);
		GP.addEdge(new EdgeP(nConf, nConfType, "isa"));
		GP.addEdge(new EdgeP(nConf, nArticle, "article"));
		GP.addEdge(new EdgeP(nConf, nCFP, "CFP"));
		GP.addEdge(new EdgeP(nConf, nDeadline, "deadline"));
		GP.addEdge(new EdgeP(nDeadline, nDateType, "isa"));
		GP.addEdge(new EdgeP(nCFP, nDeadline, "contains"));
		GP.addEdge(new EdgeP(nArticle, nDocumentType, "isa"));
		GP.addEdge(new EdgeP(nCFP, nDocumentType, "isa"));
//		GraphPattern GP = GraphPattern.readFrom(new ByteArrayInputStream(input2.getBytes()), new UnitConfigData("GP").setLevel(Level.INFO).setLink(unitName));
		log.info(GP.toString());
		
		TextGraphRepresentation.GraphConfig configT2 = new TextGraphRepresentation.GraphConfig(GP).setLayout("\n", "\t", 2);
		configT2.setName(Unit.DEFAULT_UNIT_NAME).setLink(unitName).setLevel(Level.ERROR);
		TextGraphRepresentation GPRT = new TextGraphRepresentation(configT2);
		log.info(GPRT.displayRepresentation());
		
		new GraphMatcher(G, GP).doMatching();
		
		Log.exitLogger(unitName);
	}
	
}
