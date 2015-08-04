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
		Perception perception = (Perception) arg;
		put(perception);
	}
}
