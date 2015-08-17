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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import tatami.amilab.AmILabComponent.AmILabDataType;

/**
 * Buffer that gets populated by an {@link AmILabRunnable}.
 * 
 * @author Claudiu-Mihai Toma
 *
 */
public class AmILabBuffer implements Observer
{
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
	 * The value of the actual limit. Will be treated differently for every {@link LimitType}.
	 */
	private long limit;

	/**
	 * Flag is {@code true} if old elements are overwritten.
	 */
	private boolean overwrite;

	/**
	 * Starting time.
	 */
	private long startingTime;

	/**
	 * Reference to observed thread.
	 */
	private Observable observedThread;

	/**
	 * Actual data container.
	 */
	protected Map<AmILabDataType, ConcurrentLinkedQueue<Perception>> buffer;

	/**
	 * Notification target.
	 */
	private NotificationTarget target;

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
	 * Constructs a buffer based on given specifications.
	 * 
	 * @param desiredTypes
	 *            - list of types to keep track of
	 * @param observableThread
	 *            - reference to observed thread
	 * @param desiredLimitType
	 *            - type of buffer
	 * @param desiredLimit
	 *            - numerical value of limit; the value for {@code UNLIMITED} must be {@code NO_LIMIT} ({@code -1})
	 * @param overwriteData
	 *            - {@code true} if data is continuously overwritten; {@code false} if the updating stops once the limit
	 *            is reached
	 *            <p>
	 *            This parameter has no effect on unlimited buffers.
	 * @param notificationTarget
	 *            - target to be notified
	 */
	public AmILabBuffer(List<AmILabDataType> desiredTypes, Observable observableThread, LimitType desiredLimitType,
			long desiredLimit, boolean overwriteData, NotificationTarget notificationTarget)
	{
		startingTime = System.currentTimeMillis();
		buffer = new HashMap<AmILabDataType, ConcurrentLinkedQueue<Perception>>();
		types = new ArrayList<AmILabDataType>();
		types.addAll(desiredTypes);
		observedThread = observableThread;
		limitType = desiredLimitType;
		overwrite = overwriteData;
		target = notificationTarget;

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
			buffer.put(type, new ConcurrentLinkedQueue<Perception>());
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
	 * @return {@code true} if the operation is successful; {@code false} otherwise
	 */
	protected boolean put(Perception perception)
	{
		switch (limitType)
		{
		case UNLIMITED:
			break;

		case SIZE:
			// Remove excess elements.
			while (getTotalSize() > limit)
				removeOldestEntry();

			// Overwrite an element.
			if (getTotalSize() >= limit)
				if (overwrite)
					removeOldestEntry();
				else
					return false;

			// Adding the last element.
			if (getTotalSize() + 1 >= limit)
				if (!overwrite)
					stopObserving();
			break;

		case SIZE_PER_TYPE:
			// Remove excess elements.
			while (getSizeForType(perception.getType()) > limit)
				getElement(perception.getType());

			// Overwrite an element.
			if (getSizeForType(perception.getType()) >= limit)
				if (overwrite)
					getElement(perception.getType());
				else
					return false;

			// Adding the last element.
			if (getTotalSize() + 1 >= limit * types.size())
				if (!overwrite)
					stopObserving();
			break;

		case TIME:
			if (!overwrite)
			{ // Perception is too old.
				if (perception.getTimestamp() - startingTime < 0)
					return false;
				else
					// Perception is too new.
					if (perception.getTimestamp() - startingTime > limit)
					stopObserving();
			} else
				// Remove old entries.
				for (Queue<Perception> queue : buffer.values())
					while (queue.peek() != null && (System.currentTimeMillis() - queue.peek().getTimestamp() > limit))
						queue.remove();
			break;

		case MEMORY_SIZE:
			break;

		default:
			break;
		}
		return buffer.get(perception.getType()).add(perception);
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
		return buffer.get(type).peek();
	}

	/**
	 * Gets element of given {@link AmILabDataType}. The element is removed from the queue.
	 * 
	 * @param type
	 *            - type of data
	 * @return first element; removes it from internal structure
	 */
	public Perception getElement(AmILabDataType type)
	{
		return buffer.get(type).poll();
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
	public long getTotalSize()
	{
		long totalSize = 0;
		for (AmILabDataType type : types)
			totalSize += buffer.get(type).size();
		return totalSize;
	}

	/**
	 * Gets size of a specific queue.
	 * 
	 * @param type
	 *            - type of the queue
	 * @return size of queue
	 */
	public long getSizeForType(AmILabDataType type)
	{
		return buffer.get(type).size();
	}

	/**
	 * Removes oldest perception (with the smallest timestamp).
	 */
	public void removeOldestEntry()
	{
		Perception perception = null;
		Queue<Perception> oldPerceptions = new LinkedList<Perception>();
		// Gather oldest perceptions (one per queue).
		for (Queue<Perception> queue : buffer.values())
		{
			perception = queue.poll();
			if (perception != null)
			{
				oldPerceptions.add(perception);
			}
		}

		// Determine oldest perception.
		Perception oldestPerception = oldPerceptions.poll();
		if (oldestPerception == null)
			return;

		while (!oldPerceptions.isEmpty())
		{
			perception = oldPerceptions.remove();
			if (oldestPerception.getTimestamp() > perception.getTimestamp())
				oldestPerception = perception;
		}

		// Remove oldest perception.
		buffer.get(oldestPerception.getType()).remove();
	}

	/**
	 * Cuts connection with the observable thread and notifies the notification target.
	 * <p>
	 * TODO: Update this comment.
	 */
	protected void stopObserving()
	{
		stopBuffer();
		if (target != null)
			target.notify(buffer);
	}

	/**
	 * The buffer stops updating.
	 */
	public void stopBuffer()
	{
		observedThread.deleteObserver(this);
	}

	/**
	 * Method called by the observable thread. This method has no effect if called by the user.
	 * 
	 * @param o
	 *            - the observable thread
	 * @param arg
	 *            - valid perception instance
	 */
	@Override
	public void update(Observable o, Object arg)
	{
		// This prevents the user from effectively calling this method.
		// TODO: Test this!
		if (!observedThread.equals(o))
			return;

		// Extract perception.
		Perception perception = (Perception) arg;

		// Check existence of perception type.
		if (buffer.get(perception.getType()) == null)
			return;

		// Add perception.
		put(perception);
	}
}
