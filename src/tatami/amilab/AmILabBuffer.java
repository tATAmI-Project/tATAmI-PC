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
import java.util.Queue;
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
	 * Used for unlimited buffers.
	 */
	public static final long NO_LIMIT = -1;

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
	 * Reference to observed thread.
	 */
	private Observable observedThread;

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
		 * Maximum number of elements.
		 */
		SIZE,

		/**
		 * Maximum number of elements for every type.
		 */
		SIZE_PER_TYPE,

		/**
		 * Time limit. Buffer is always active.
		 */
		TIME,

		/**
		 * TBD: Maximum memory size.
		 */
		MEMORY_SIZE,
	}

	/**
	 * Default constructor. Creates an unlimited buffer.
	 * 
	 * @param desiredTypes
	 *            - list of types to keep track of
	 * @param observableThread
	 *            - reference to observed thread
	 */
	public AmILabBuffer(List<AmILabDataType> desiredTypes, Observable observableThread)
	{
		this(desiredTypes, observableThread, LimitType.UNLIMITED, NO_LIMIT);
	}

	/**
	 * Constructs a buffer based on given specifications.
	 * 
	 * @param desiredTypes
	 *            - list of types to keep track of
	 * @param observableThread
	 *            - reference to observed thread
	 * @param desiredLimitType
	 *            - type of buffer
	 * @param desiredLimit
	 *            - numerical value of limit; the value for {@code UNLIMITED}
	 *            must be {@code NO_LIMIT} ({@code -1})
	 */
	public AmILabBuffer(List<AmILabDataType> desiredTypes, Observable observableThread, LimitType desiredLimitType,
			long desiredLimit)
	{
		types = new ArrayList<AmILabDataType>();
		types.addAll(desiredTypes);
		observedThread = observableThread;
		limitType = desiredLimitType;

		if (!(desiredLimit > 0 && !limitType.equals(LimitType.UNLIMITED)
				|| desiredLimit == NO_LIMIT && limitType.equals(LimitType.UNLIMITED)))
			throw new IllegalArgumentException("Forth argument [" + desiredLimit + "] is not a valid argument.");

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
		switch (limitType)
		{
		case UNLIMITED:
			break;

		case SIZE:
			// Adding the last element.
			if (getTotalSize() + 1 == limit)
				stopObserving();
			break;

		case SIZE_PER_TYPE:
			// Can add element to its queue?
			if (getSizeForType(perception.getType()) == limit)
				return;
			// Adding the last element.
			if (getTotalSize() + 1 == limit * types.size())
				stopObserving();
			break;

		case TIME:
			// Remove old entries.
			for (Queue<Perception> queue : values())
			{
				while (queue.peek() != null && (System.currentTimeMillis() - queue.peek().getTimestamp() > limit))
				{
					queue.poll();
				}
			}
			break;

		case MEMORY_SIZE:
			break;

		default:
			break;
		}
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

	/**
	 * Computes total size of structure.
	 * 
	 * @return total size of structure
	 */
	protected long getTotalSize()
	{
		long totalSize = 0;
		for (AmILabDataType type : types)
			totalSize += get(type).size();
		return totalSize;
	}

	/**
	 * Gets size of a specific queue.
	 * 
	 * @param type
	 *            - type of the queue
	 * @return size of queue
	 */
	protected long getSizeForType(AmILabDataType type)
	{
		return get(type).size();
	}

	/**
	 * Cuts connection with the observable thread.
	 */
	protected void stopObserving()
	{
		observedThread.deleteObserver(this);
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
