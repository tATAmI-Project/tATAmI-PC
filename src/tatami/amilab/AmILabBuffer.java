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

/**
 * Buffer that gets populated by an {@link AmILabThread}.
 * 
 * @author Claudiu-Mihai Toma
 *
 */
public class AmILabBuffer extends HashMap<AmILabDataType, ConcurrentLinkedQueue<Perception>>implements Observer
{
	/**
	 * The serial UID.
	 */
	private static final long serialVersionUID = 3139672506480731805L;

	/**
	 * Zero
	 */
	private static final long ZERO = 0;

	/**
	 * Used for unlimited buffers.
	 */
	private static final long NO_LIMIT = -1;

	/**
	 * List of {@link AmILabDataType}s the buffer keeps track of.
	 */
	private List<AmILabDataType> types;

	/**
	 * The {@link LimitType} of this buffer.
	 */
	private LimitType limitType;

	/**
	 * The value of the actual limit. Will be treated differently for every
	 * {@link LimitType}.
	 */
	private long limit;

	/**
	 * Flag is {@code true} if old elements are overwritten.
	 */
	private boolean overwrite;

	/**
	 * Types of limits.
	 * 
	 * @author Claudiu-Mihai Toma
	 *
	 */
	public enum LimitType
	{
		/**
		 * No limits. Default value.
		 */
		UNLIMITED,

		/**
		 * Time limit. Buffer is always active.
		 */
		TIME,

		/**
		 * Maximum number of elements.
		 */
		SIZE,

		/**
		 * TBD: Maximum memory size.
		 */
		MEMORY_SIZE,

		/**
		 * Maximum number of elements for every type.
		 */
		SIZE_PER_TYPE,
	}

	/**
	 * Default constructor. Creates an unlimited buffer.
	 * 
	 * @param desiredTypes
	 *            - list of types to keep track of
	 */
	public AmILabBuffer(List<AmILabDataType> desiredTypes)
	{
		this(desiredTypes, LimitType.UNLIMITED, NO_LIMIT);
	}

	/**
	 * Constructs a buffer based on given specifications.
	 * 
	 * @param desiredTypes
	 *            - list of types to keep track of
	 * @param desiredLimitType
	 *            - type of buffer
	 * @param desiredLimit
	 *            - numerical value of limit; the value is irrelevant if the
	 *            buffer is unlimited
	 */
	public AmILabBuffer(List<AmILabDataType> desiredTypes, LimitType desiredLimitType, long desiredLimit)
	{
		types = new ArrayList<AmILabDataType>();
		types.addAll(desiredTypes);
		limitType = desiredLimitType;
		limit = desiredLimit;
		addQueues();
	}

	/**
	 * Adds queues for every specific type.
	 */
	private void addQueues()
	{
		for (AmILabDataType type : types)
		{
			put(type, new ConcurrentLinkedQueue<Perception>());
		}
	}

	/**
	 * Puts {@link Perception} in the internal structure.
	 * <p>
	 * TODO: Make private?
	 * <p>
	 * TODO: Here goes all the magic: switch case.
	 * 
	 * @param perception
	 *            - only {@link AmILabDataType}s that the buffer keeps track of
	 */
	public void put(Perception perception)
	{
		get(perception.getType()).add(perception);
	}

	/**
	 * Peeks element of given {@link AmILabDataType}.
	 * 
	 * @param type
	 *            - type of data
	 * @return first element; does not remove it from internal structure
	 */
	public Perception peekElement(AmILabDataType type)
	{
		return get(type).peek();
	}

	/**
	 * Gets element of given {@link AmILabDataType}.
	 * 
	 * @param type
	 *            - type of data
	 * @return first element; removes it from internal structure
	 */
	public Perception getElement(AmILabDataType type)
	{
		return get(type).poll();
	}

	/**
	 * Gets {@link LimitType} of this buffer.
	 * 
	 * @return {@link LimitType} of this buffer
	 */
	public LimitType getLimitType()
	{
		return limitType;
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
