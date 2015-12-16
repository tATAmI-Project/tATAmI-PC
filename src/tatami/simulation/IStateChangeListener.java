package tatami.simulation;

import java.io.Serializable;

import tatami.core.agent.CompositeAgent;

public interface IStateChangeListener extends Serializable{
	public void stateChanged(CompositeAgent agent);
}
