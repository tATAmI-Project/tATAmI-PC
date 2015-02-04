package testing.system_testing.default_components;

import java.util.Vector;

import net.xqhs.util.logging.LoggerSimple.Level;
import net.xqhs.util.logging.Unit;
import net.xqhs.util.logging.UnitComponent;
import tatami.core.agent.AgentComponent;
import tatami.core.agent.AgentComponent.AgentComponentName;
import tatami.core.agent.AgentEvent;
import tatami.core.agent.AgentEvent.AgentEventHandler;
import tatami.core.agent.AgentEvent.AgentEventType;
import tatami.core.agent.CompositeAgent;
import tatami.core.agent.parametric.AgentParameterName;
import tatami.core.agent.parametric.AgentParameters;
import tatami.core.agent.parametric.ParametricComponent;
import tatami.core.agent.visualization.AgentGui.AgentGuiBackgroundTask;
import tatami.core.agent.visualization.AgentGui.InputListener;
import tatami.core.agent.visualization.AgentGui.ResultNotificationListener;
import tatami.core.agent.visualization.VisualizableComponent;
import testing.system_testing.default_components.PC.TestGui;

/**
 * Tests the {@link VisualizableComponent} of a {@link CompositeAgent}. Creates a bare composite agent (without a
 * platform), and adds a parametric, a visual, and a test component to it. The test component intercepts agent events,
 * prints them, and also sends them to the visualization component. The agent is asked to exit soon after creation.
 * <p>
 * The testing component should print agent events both to the local log and to the log of the visualization component
 * (has the name of the agent).
 * <p>
 * A window for the agent is also created, with a button that instructs the agent to exit. The button changes label
 * after pressing.
 * 
 * @author Andrei Olaru
 */
public class VisualizableComponentTest extends Unit
{
	/**
	 * Main tester.
	 */
	public VisualizableComponentTest()
	{
		setUnitName("parametric component tester");
		li("starting...");
		
		String packageName = this.getClass().getPackage().getName();
		li("package name ", packageName);
		
		CompositeAgent agent = new CompositeAgent();
		final AgentParameters agentParameters = new AgentParameters();
		agentParameters.add(AgentParameterName.AGENT_NAME, "test agent");
		agentParameters.add(AgentParameterName.AGENT_PACKAGE, packageName);
		
		agent.addComponent(new ParametricComponent() {
			private static final long	serialVersionUID	= 1L;
			
			// pre-loading is only possible from the inside of the component or from the core package
			@Override
			protected void componentInitializer()
			{
				preload((ComponentCreationData) new ComponentCreationData().addObject(COMPONENT_PARAMETER_NAME,
						agentParameters), null, null);
			}
		});
		
		agent.addComponent(new VisualizableComponent() {
			private static final long	serialVersionUID	= 1L;
			
			@Override
			protected void componentInitializer()
			{
				super.componentInitializer();
				
				preload((ComponentCreationData) new ComponentCreationData().add(VisualizableComponent.GUI_PARAMETER_NAME,
						TestGui.class.getSimpleName()), null, null);
			}
		});
		
		agent.addComponent(new AgentComponent(AgentComponentName.TESTING_COMPONENT) {
			private static final long	serialVersionUID	= 1L;
			UnitComponent				locallog;
			
			@Override
			protected String getAgentName()
			{
				return super.getAgentName();
			}
			
			protected VisualizableComponent getVisualizable()
			{
				return (VisualizableComponent) getAgentComponent(AgentComponentName.VISUALIZABLE_COMPONENT);
			}
			
			@Override
			protected void postAgentEvent(AgentEvent event)
			{
				super.postAgentEvent(event);
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
						String eventMessage = "agent [" + getAgentName() + "] event: [" + event.getType().toString()
								+ "]";
						locallog.li(eventMessage);
						VisualizableComponent vis = getVisualizable();
						if(event.getType() == AgentEventType.AGENT_START)
							vis.getGUI().connectInput(TestGui.BUTTON_NAME, new InputListener() {
								@Override
								public void receiveInput(String componentName, Vector<Object> arguments)
								{
									if(componentName.equals(TestGui.BUTTON_NAME))
									{
										Vector<Object> args = new Vector<Object>();
										args.add("Exiting...");
										getVisualizable().getGUI().doOutput(TestGui.BUTTON_NAME.toString(), args);
										getVisualizable().getGUI().background(new AgentGuiBackgroundTask() {
											@Override
											public void execute(Object arg, ResultNotificationListener resultListener)
											{
												try
												{
													Thread.sleep(500);
												} catch(InterruptedException e)
												{
													e.printStackTrace();
												}
												postAgentEvent(new AgentEvent(AgentEventType.AGENT_STOP));
												doExit();
											}
										}, null, null);
									}
								}
							});
						if(vis != null)
							if(vis.getLog() != null)
								vis.getLog().info(eventMessage);
						if(event.getType() == AgentEventType.AGENT_STOP)
							locallog.doExit();
					}
				};
				for(AgentEventType eventType : AgentEventType.values())
					registerHandler(eventType, allEventHandler);
			}
		});
		
		agent.start();
	}
	
	@Override
	protected void doExit()
	{
		li("done.");
		super.doExit();
	}
	
	/**
	 * Main.
	 * 
	 * @param args
	 *            - not used.
	 */
	@SuppressWarnings("unused")
	public static void main(String args[])
	{
		new VisualizableComponentTest();
	}
}
