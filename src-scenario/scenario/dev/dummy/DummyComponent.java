package scenario.dev.dummy;

import net.xqhs.util.logging.Logger;
import net.xqhs.util.logging.LoggerSimple.Level;
import net.xqhs.util.logging.UnitComponent;
import tatami.core.agent.AgentComponent;
import tatami.core.agent.AgentEvent;
import tatami.core.agent.CompositeAgent;


/**
 * 
 * @author Yonutix
 *
 */
public class DummyComponent extends AgentComponent {

	/**
	 * The log.
	 */
	transient UnitComponent locallog = null;

	/**
	 * Cache for the name of this agent.
	 */
	String thisAgent = null;

	/**
	 * 
	 */
	public DummyComponent() {
		super(AgentComponentName.DUMMY_COMPONENT);
	}

	@Override
	public Logger getAgentLog() {
		return super.getAgentLog();
	}

	@Override
	protected String getAgentName() {
		return super.getAgentName();
	}

	@Override
	protected AgentComponent getAgentComponent(AgentComponentName name) {
		return super.getAgentComponent(name);
	}

	@Override
	protected void componentInitializer() {
		super.componentInitializer();
	}

	@Override
	protected void parentChangeNotifier(CompositeAgent oldParent) {
		super.parentChangeNotifier(oldParent);
		if (getParent() != null) {
			locallog = (UnitComponent) new UnitComponent().setUnitName(
					"state_testing-" + getAgentName()).setLogLevel(Level.ALL);
			locallog.lf("testing started.");
		} else if (locallog != null) {
			locallog.doExit();
			locallog = null;
		}
	}

	@Override
	protected void atAgentStart(AgentEvent event) {
		super.atAgentStart(event);
		System.out.println("Agent started");

		if (getParent() != null) {
			thisAgent = getAgentName();
		}
		
		mActive = true;
	}

	@Override
	protected void atSimulationStart(AgentEvent event) {
		super.atSimulationStart(event);
	}

	@Override
	protected void atAgentStop(AgentEvent event) {
		//AgentEvent event = new AgentEvent(AgentEventType.BEFORE_MOVE);
		super.atAgentStop(event);
	}

	protected void atAgentResume(AgentEvent event) {
	}
}
