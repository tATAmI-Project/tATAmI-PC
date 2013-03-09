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
package tatami.core.util.graph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import tatami.core.util.graph.representation.GraphRepresentation;
import tatami.core.util.graph.representation.RepresentationElement;

public abstract class GraphComponent
{
	protected Set<RepresentationElement> representations = new HashSet<RepresentationElement>();
	
	public void addRepresentation(RepresentationElement repr)
	{
		representations.add(repr);
	}
	
	public Collection<RepresentationElement> getRepresentations()
	{
		return representations;
	}
	
	public RepresentationElement getFirstRepresentationForPlatform(GraphRepresentation representation)
	{
		Collection<RepresentationElement> filtered = getRepresentationsForPlatform(representation);
		if(filtered.isEmpty())
			return null;
		return filtered.iterator().next();
	}
	
	protected Collection<RepresentationElement> getRepresentationsForPlatform(GraphRepresentation representation)
	{
		Collection<RepresentationElement> ret = new HashSet<RepresentationElement>();
		for(RepresentationElement repr : representations)
			if(repr.getRootRepresentation() == representation)
				ret.add(repr);
		return ret;
	}
}
