package scenario.examples.composite.simpleA;

import net.xqhs.util.logging.Logger.Level;
import net.xqhs.util.logging.UnitComponent;
import tatami.core.agent.AgentComponent;
import tatami.core.agent.AgentEvent;
import tatami.core.agent.AgentEvent.AgentEventHandler;
import tatami.core.agent.AgentEvent.AgentEventType;
import tatami.core.agent.parametric.AgentParameterName;
import tatami.core.agent.parametric.ParametricComponent;

/**
 * An {@link AgentComponent} implementation that monitors all agent events.
 * 
 * @author Andrei Olaru
 */
public class TestingMonitorComponent extends AgentComponent
{
	/**
	 * The UID.
	 */
	private static final long	serialVersionUID	= 5214882018809437402L;
	/**
	 * The log.
	 */
	UnitComponent				locallog			= null;
	
	/**
	 * Default constructor
	 */
	public TestingMonitorComponent()
	{
		super(AgentComponentName.TESTING_COMPONENT);
	}
	
	@Override
	protected ParametricComponent getParametric()
	{
		return super.getParametric();
	}
	
	@Override
	protected void componentInitializer()
	{
		super.componentInitializer();
		
		locallog = (UnitComponent) new UnitComponent().setUnitName("monitoring").setLogLevel(Level.ALL);
		AgentEventHandler allEventHandler = new AgentEventHandler() {
			@Override
			public void handleEvent(AgentEvent event)
			{
				locallog.li("event: [" + event.getType().toString() + "]");
				ParametricComponent parametric = getParametric();
				if(parametric == null)
					locallog.li("\t parametric component is currently null");
				else
					locallog.li("\t parameter value: [" + parametric.parVal(AgentParameterName.AGENT_NAME) + "]");
				if(event.getType() == AgentEventType.AGENT_EXIT)
					locallog.doExit();
			}
		};
		for(AgentEventType eventType : AgentEventType.values())
			registerHandler(eventType, allEventHandler);
	}
}
