package tatami.simulation;

import tatami.core.agent.CompositeAgent;

public interface IStateChangeListener {
	public void stateChanged(CompositeAgent agent);
}
