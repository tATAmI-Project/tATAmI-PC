package scenario.amilab.app_demo;

/**
 * Runnable that can be stopped.
 * 
 * @author Claudiu-Mihai Toma
 *
 */
public abstract class StoppableRunnable implements Runnable
{

	/**
	 * {@code true} when the runnable must stop;
	 */
	protected boolean stopFlag;

	/**
	 * Default constructor.
	 */
	public StoppableRunnable()
	{
		stopFlag = false;
	}

	/**
	 * Stops this runnable.
	 */
	public void stop()
	{
		stopFlag = true;
	}

}
