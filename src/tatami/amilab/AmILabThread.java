package tatami.amilab;

import java.util.Observable;

import tatami.amilab.AmILabComponent.AmILabDataType;
import tatami.amilab.util.SimpleKestrelClient;

/**
 * Thread that populates buffers. Without buffers it dies.
 * 
 * @author Claudiu-Mihai Toma
 *
 */
class AmILabThread extends Observable implements Runnable
{
	/**
	 * The time used to reduce thread's CPU consumption.
	 */
	// TODO: Make it 50 (or something not zero).
	private static final int TIME_TO_SLEEP = 0;

	/**
	 * Holds the state of the thread.
	 */
	private boolean running;

	/**
	 * Kestrel client used to gather data.
	 */
	private SimpleKestrelClient kestrelClient;

	/**
	 * The Kestrel queue that is created on the AmILab Kestrel server. If this
	 * queue exists, it will be used.
	 */
	protected String kestrelQueueName;

	/**
	 * Constructor that requires the Kestrel client and queue.
	 * 
	 * @param client
	 *            - working Kestrel client
	 * @param queueName
	 *            - name of the queue
	 */
	public AmILabThread(SimpleKestrelClient client, String queueName)
	{
		running = false;
		kestrelClient = client;
		kestrelQueueName = queueName;
	}

	/**
	 * Stops the thread.
	 */
	public void stopThread()
	{
		running = false;
	}

	/**
	 * Checks if the thread is alive.
	 * 
	 * @return {@code true} if alive, {@code false} otherwise
	 */
	// FIXME: This may need a Thread.sleep().
	public boolean isAlive()
	{
		return running;
	}

	@Override
	public void run()
	{
		running = true;

		while (running)
		{
			if (countObservers() == 0)
			{
				stopThread();
				return;
			}

			try
			{
				Thread.sleep(TIME_TO_SLEEP);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}

			// Receive data from Kestrel queue, which resides on the Kestrel
			// server.
			String kestrelJSON;
			kestrelJSON = kestrelClient.get(kestrelQueueName);

			if (kestrelJSON == null)
				continue;

			// Get type of data.
			AmILabDataType dataType = null;
			for (AmILabDataType itDataType : AmILabDataType.values())
			{
				if (kestrelJSON.contains(itDataType.getType()))
				{
					dataType = itDataType;
					break;
				}
			}

			// Message has no known type or is corrupt.
			if (dataType == null)
				continue;

			// TODO: Extract information from JSON, maybe even deserialize.
			setChanged();
			notifyObservers(new Perception(dataType, 0, kestrelJSON));
		}
	}
}
