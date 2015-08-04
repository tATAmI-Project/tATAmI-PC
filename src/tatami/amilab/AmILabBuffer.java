package tatami.amilab;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import tatami.amilab.AmILabComponent.AmILabDataType;

public class AmILabBuffer extends HashMap<AmILabDataType, ConcurrentLinkedQueue<Perception>>
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

	public void put(AmILabDataType type, Perception perception)
	{
		get(type).add(perception);
	}

	public Perception peekElement(AmILabDataType type)
	{
		return get(type).peek();
	}

	public Perception getElement(AmILabDataType type)
	{
		return get(type).poll();
	}
}
