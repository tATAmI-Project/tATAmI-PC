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

import java.util.List;
import java.util.Map;

import tatami.core.util.graph.Graph;
import tatami.core.util.graph.Node;
import tatami.core.util.graph.representation.GraphRepresentation.GraphConfig;

public class TextMultilevelGraphRepresentation extends MultilevelGraphRepresentation
{
	public TextMultilevelGraphRepresentation(List<Map<Node, Node>> nodeLevels, GraphConfig config)
	{
		super(nodeLevels, config);
	}
	
	@Override
	void processGraph()
	{
		super.processGraph();
		
		
	}
	
	@Override
	public Object displayRepresentation()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RepresentationElement getRepresentation()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
}
