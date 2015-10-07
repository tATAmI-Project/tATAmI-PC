package tatami.amilab;

import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import tatami.amilab.AmILabComponent.AmILabDataType;

/**
 * {@link AmILabBuffer} that allows more access to it's elements (like adding new ones or direct access to it's
 * underlying queues).
 * 
 * @author Claudiu-Mihai Toma
 *
 */
public class AmILabMutableBuffer extends AmILabBuffer
{
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
	public AmILabMutableBuffer(List<AmILabDataType> desiredTypes, Observable observableThread,
			LimitType desiredLimitType, long desiredLimit, boolean overwriteData, NotificationTarget notificationTarget)
	{
		super(desiredTypes, observableThread, desiredLimitType, desiredLimit, overwriteData, notificationTarget);
	}

	/**
	 * Returns the queue containing the given {@link AmILabDataType}.
	 * 
	 * @param queueType
	 *            - type of data the queue contains
	 * @return requested queue
	 */
	public Queue<Perception> getQueue(AmILabDataType queueType)
	{
		return buffer.get(queueType);
	}

	/**
	 * Adds perception to the correct queue.
	 * <p>
	 * TODO: Handle {@link NullPointerException}?
	 * 
	 * @param perception
	 *            - perception to be added at the end of it's queue.
	 * @return {@code true} if the operation is successful; {@code false} otherwise
	 */
	public boolean addElement(Perception perception)
	{
		return put(perception);
	}

	/**
	 * Gets all the perceptions.
	 * 
	 * @return all perceptions
	 */
	public Map<AmILabDataType, ConcurrentLinkedQueue<Perception>> getPerceptions()
	{
		return buffer;
	}
}
