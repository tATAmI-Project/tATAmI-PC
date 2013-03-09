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
package tatami.core.util.graph.representation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import tatami.core.util.graph.Edge;
import tatami.core.util.graph.Graph;
import tatami.core.util.graph.Node;
import tatami.core.util.graph.representation.GraphRepresentation.GraphConfig;

/**
 * 
 * @author Andrei Olaru
 * 
 */
public abstract class MultilevelGraphRepresentation extends GraphRepresentation
{
	/**
	 * A {@link List} in which each item corresponds to a level. An item is a {@link Map} that represents the member function for nodes of the current level, as
	 * (node, node-on-the-next-level-this-node-belongs-to). The last level should have all values <code>null</code>.
	 */
	List<Map<Node, Node>>	levelNodes	= null;
	/**
	 * A {@link List} in which each item at position corresponds to a level. The first element of the list is an empty {@link Map} (for easier reading of code).
	 * 
	 * An item is a {@link Map} that links a {@link Node} from the level above to a subgraph at the current level which corresponds to that Node.
	 */
	List<Map<Node, Graph>>	theLevels	= null;
	
	/**
	 * @param nodeLevels
	 *            : a {@link List} in which each item corresponds to a level.
	 * 
	 *            An item is a {@link Map} that represents the member function for nodes of the current level, as (node,
	 *            node-on-the-next-level-this-node-belongs-to).
	 * 
	 *            Albeit redundant, the last level should exist have all values <code>null</code> (they will not be considered).
	 */
	public MultilevelGraphRepresentation(List<Map<Node, Node>> nodeLevels, GraphConfig config)
	{
		super(config);
		
		levelNodes = new Vector<Map<Node, Node>>(nodeLevels);
		
		processGraph();
	}
	
	@Override
	void processGraph()
	{
		theLevels = new Vector<Map<Node, Graph>>(levelNodes.size());
		for(Map<Node, Node> level : levelNodes)
		{
			Map<Node, Graph> graphs = new HashMap<Node, Graph>();
			theLevels.add(graphs);
			
			// put the nodes in corresponding graphs
			for(Map.Entry<Node, Node> belongsTo : level.entrySet())
			{
				if(!graphs.containsKey(belongsTo.getValue()))
					graphs.put(belongsTo.getValue(), new Graph());// TODO unitName + ":" + belongsTo.getValue(), unitName));
				graphs.get(belongsTo.getValue()).addNode(belongsTo.getKey());
			}
			// put in-graph edges in the graphs
			for(Graph graph : graphs.values())
				for(Edge edge : theGraph.getEdges())
				{
					if(graph.contains(edge.getTo()) && graph.contains(edge.getFrom()))
						graph.addEdge(edge);
				}
		}
		
	}
}
