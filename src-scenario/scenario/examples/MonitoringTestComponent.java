package scenario.examples;

import net.xqhs.util.logging.LoggerSimple.Level;
import net.xqhs.util.logging.UnitComponent;
import tatami.core.agent.AgentComponent;
import tatami.core.agent.AgentEvent;
import tatami.core.agent.AgentEvent.AgentEventHandler;
import tatami.core.agent.AgentEvent.AgentEventType;
import tatami.core.agent.CompositeAgent;
import tatami.core.agent.parametric.AgentParameterName;
import tatami.core.agent.parametric.ParametricComponent;
import tatami.core.agent.visualization.VisualizableComponent;

/**
 * An {@link AgentComponent} implementation that monitors all agent events.
 * 
 * @author Andrei Olaru
 */
public class MonitoringTestComponent extends AgentComponent
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
	public MonitoringTestComponent()
	{
		super(AgentComponentName.TESTING_COMPONENT);
	}
	
	@Override
	protected ParametricComponent getParametric()
	{
		return super.getParametric();
	}
	
	@Override
	protected VisualizableComponent getVisualizable()
	{
		return super.getVisualizable();
	}
	
	@Override
	protected String getAgentName()
	{
		return super.getAgentName();
	}
	
	@Override
	protected void componentInitializer()
	{
		super.componentInitializer();
		
		AgentEventHandler allEventHandler = new AgentEventHandler() {
			@Override
			public void handleEvent(AgentEvent event)
			{
				String eventMessage = "agent [" + getAgentName() + "] event: [" + event.getType().toString() + "]";
				locallog.li(eventMessage);
				ParametricComponent parametric = getParametric();
				if(parametric != null)
					locallog.li("\t parameter value: [" + parametric.parVal(AgentParameterName.AGENT_NAME) + "]");
				else
					locallog.li("\t parametric component is currently null");
				VisualizableComponent vis = getVisualizable();
				if(vis != null)
					if(vis.getLog() != null)
						vis.getLog().info(eventMessage);
				if(event.getType() == AgentEventType.AGENT_EXIT)
					locallog.doExit();
			}
		};
		for(AgentEventType eventType : AgentEventType.values())
			registerHandler(eventType, allEventHandler);
	}
	
	@Override
	protected void parentChangeNotifier(CompositeAgent oldParent)
	{
		super.parentChangeNotifier(oldParent);
		
		if(getParent() != null)
		{
			locallog = (UnitComponent) new UnitComponent().setUnitName("monitoring-" + getAgentName()).setLogLevel(
					Level.ALL);
			locallog.lf("testing started.");
		}
		else if(locallog != null)
		{
			locallog.doExit();
			locallog = null;
		}
	}
}
