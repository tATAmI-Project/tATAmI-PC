package tatami.jade;

import jade.core.Agent;
import net.xqhs.util.logging.Logger.Level;
import net.xqhs.util.logging.Unit;
import net.xqhs.util.logging.UnitComponentExt;
import tatami.core.util.platformUtils.PlatformUtils;
import tatami.simulation.AgentManager;

/**
 * A wrapper for an {@link AgentManager} instance, that wraps it inside a Jade {@link Agent}.
 * <p>
 * When started as a Jade agent, the wrapper is supposed to receive one argument: the {@link AgentManager} instance.
 * 
 * @author Andrei Olaru
 */
public class JadeAgentWrapper extends Agent
{
	/**
	 * The serial UID.
	 */
	private static final long	serialVersionUID	= 3762116064764191232L;
	/**
	 * The log.
	 */
	UnitComponentExt			log;
	/**
	 * The wrapped agent.
	 */
	AgentManager				agent;
	
	@Override
	protected void setup()
	{
		super.setup();
		
		log = (UnitComponentExt) new UnitComponentExt().setUnitName(Unit.DEFAULT_UNIT_NAME).setLogLevel(Level.ALL);
		
		Object[] args = getArguments();
		if(args.length < 2)
		{
			log.error("Not enough arguments.");
			doExit();
		}
		try
		{
			agent = (AgentManager) args[0];
		} catch(ClassCastException e)
		{
			log.error("Agent argument not correct: " + PlatformUtils.printException(e));
			doExit();
		}
		if(agent.getAgentName() != null)
			log.setUnitName(agent.getAgentName() + "-JadeWrapper");
		log.info("Wrapper is up.");
		// System.out.println("Agent ["+agent.getAgentName()+"] has state ["+((CompositeAgent)agent).state+"].");
		if(!agent.setPlatformLink(this))
			log.error("Setting platform link failed");
		
		Object lock = args[1];
		synchronized(lock)
		{
			lock.notifyAll(); // notify the setup is completed
		}
	}
	
	/**
	 * Performs the exit procedure for the wrapper, together with the wrapped agent.
	 */
	protected void doExit()
	{
		if(agent != null)
		{
			agent.stop();
			agent = null;
		}
		if(log != null)
			log.doExit();
		doDelete();
	}
}
