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

import tatami.core.util.Config;
import tatami.core.util.graph.GraphComponent;

public abstract class RepresentationElement
{
	public static class RepElementConfig extends Config
	{
		GraphRepresentation rootRepresentation;
		GraphComponent	  representedComponent = null;
		
		public RepElementConfig(GraphRepresentation root, GraphComponent component)
		{
			this.rootRepresentation = root;
			this.representedComponent = component;
		}
	}
	
	protected RepElementConfig config;
	
	public RepresentationElement(RepElementConfig conf)
	{
		this.config = conf;
	}
	
	public GraphRepresentation getRootRepresentation()
	{
		return config.rootRepresentation;
	}
	
	/**
	 * @return the {@link GraphComponent} represented by this {@link RepresentationElement}.
	 */
	protected GraphComponent representedComponent()
	{
		return config.representedComponent;
	}
}
