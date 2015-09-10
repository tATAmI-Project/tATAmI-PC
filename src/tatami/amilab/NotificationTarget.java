package tatami.amilab;

import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import tatami.amilab.AmILabComponent.AmILabDataType;

/**
 * Target to be notified by an {@link AmILabBuffer} when it gets full.
 * 
 * @author Claudiu-Mihai Toma
 *
 */
public interface NotificationTarget
{
	/**
	 * 
	 * @param bufferResult
	 *            - buffer to be transmitted
	 */
	public void notify(Map<AmILabDataType, ConcurrentLinkedQueue<Perception>> bufferResult);
}
