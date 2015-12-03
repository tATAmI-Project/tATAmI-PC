package scenario.examples;

import java.io.Serializable;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

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
public class StateAgentTestComponent extends AgentComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Initial delay before the first ping message.
	 */
	protected static final long PING_INITIAL_DELAY = 0;
	/**
	 * Time between ping messages.
	 */
	protected static final long PING_PERIOD = 1000;

	/**
	 * Timer for pinging.
	 */
	transient Timer pingTimer = null;

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
	int mSubject;

	/**
	 * 
	 * @author Yonutix
	 *
	 */
	class MakeStep extends TimerTask implements Serializable{
		
		
		/**
		 * 
		 */
		StateAgentTestComponent mParent;
		
		/**
		 * 
		 * @param parent The parent of this object
		 */
		public MakeStep(StateAgentTestComponent parent){
			mParent = parent;
		}

		@Override
		public void run() {
			mSubject++;
			getAgentLog().lf("Incremented ", mSubject);
			//Time to move
			if( mSubject == 10){
				mParent.move("There");
			}
			
		}
	}

	/**
	 * 
	 */
	public StateAgentTestComponent() {
		super(AgentComponentName.TESTING_COMPONENT);
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

		if (getParent() != null) {
			thisAgent = getAgentName();
		}

		pingTimer = new Timer();

		Random r = new Random();

		mSubject = 8; //r.nextInt() % 10;
		
		mActive = true;
	}

	@Override
	protected void atSimulationStart(AgentEvent event) {
		super.atSimulationStart(event);
		pingTimer.schedule(new MakeStep(this), PING_INITIAL_DELAY, PING_PERIOD);
	}

	@Override
	protected void atAgentStop(AgentEvent event) {
		//AgentEvent event = new AgentEvent(AgentEventType.BEFORE_MOVE);
		super.atAgentStop(event);
		pingTimer.cancel();
		getAgentLog().lf("atAgentStop ");
	}
//	
//	@Override
//	protected void atBeforeAgentMove(AgentEvent event)
//	{
//		pingTimer.cancel();
//		mActive = false;
//	}
//	
//	@Override
//	protected void atAfterAgentMove(AgentEvent event)
//	{
//		pingTimer = new Timer();
//		pingTimer.schedule(new MakeStep(this), PING_INITIAL_DELAY, PING_PERIOD);
//	}
}
