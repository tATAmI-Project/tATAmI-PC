/*******************************************************************************
 * Copyright (C) 2015 Andrei Olaru, Marius-Tudor Benea, Nguyen Thi Thuy Nga, Amal El Fallah Seghrouchni, Cedric Herpson.
 * 
 * This file is part of tATAmI-PC.
 * 
 * tATAmI-PC is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * tATAmI-PC is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with tATAmI-PC.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package tatami.amilab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentLinkedQueue;

import tatami.amilab.AmILabComponent.AmILabDataType;

public class AmILabBuffer extends HashMap<AmILabDataType, ConcurrentLinkedQueue<Perception>>implements Observer
{
	private static final int ZERO_ELEMENTS = 0;

	private List<AmILabDataType> types;

	private LimitType limitType;

	private int numberOfElements;

	private boolean overwrite;

	public enum LimitType
	{
		UNLIMITED, TIME, SIZE, MEMORY_SIZE, SIZE_PER_TYPE,
	}

	public AmILabBuffer(List<AmILabDataType> desiredTypes, LimitType desiredLimitType)
	{
		types = new ArrayList<AmILabDataType>();
		types.addAll(desiredTypes);
		limitType = desiredLimitType;
		numberOfElements = ZERO_ELEMENTS;
		addQueues();
	}

	private void addQueues()
	{
		for (AmILabDataType type : types)
		{
			put(type, new ConcurrentLinkedQueue<Perception>());
		}
	}

	public void put(Perception perception)
	{
		get(perception.getType()).add(perception);
	}

	public Perception peekElement(AmILabDataType type)
	{
		return get(type).peek();
	}

	public Perception getElement(AmILabDataType type)
	{
		return get(type).poll();
	}

	@Override
	public void update(Observable o, Object arg)
	{
		// Extract perception.
		Perception perception = (Perception) arg;
		
		// Check existence of perception type.
		if (get(perception.getType()) == null)
			return;

		// Add perception.
		put(perception);
	}
}
