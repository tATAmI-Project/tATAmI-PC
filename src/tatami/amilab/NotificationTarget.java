package tatami.amilab;

import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import tatami.amilab.AmILabComponent.AmILabDataType;

/**
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
