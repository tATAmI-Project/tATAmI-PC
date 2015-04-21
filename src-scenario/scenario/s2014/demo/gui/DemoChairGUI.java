package scenario.s2014.demo.gui;

import java.awt.Component;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.swing.JTextArea;

import scenario.s2014.demo.gui.Demo.PDAComponents;
import tatami.core.agent.visualization.AgentGui;
import tatami.core.agent.visualization.AgentGuiConfig;
import tatami.pc.util.windowLayout.WindowLayout;
import tatami.pc.util.windowLayout.WindowParameters;
import tatami.simulation.BootSettingsManager;

public class DemoChairGUI implements AgentGui {
 
	protected AgentGuiConfig				config				= null;
	protected WindowParameters				params				= null;
	protected DemoChair					window				= null;
	public Map<String, Component>		components			= null;
	
	public DemoChairGUI(AgentGuiConfig configuration) {
		
		config = configuration;
		components = new Hashtable<String, Component>();
		window = new DemoChair();
		window.setVisible(true);
	}
	
	@Override
	public void doOutput(String componentName, Vector<Object> arguments)
	{
		// do nothing
	}

	@Override
	public void connectInput(String componentName, InputListener input) {
		
		final InputListener listener = input;
		
		if((Demo.PDAComponents.JOIN.toString().toLowerCase()).equals(componentName.toLowerCase()))
		{
			window.joinListener = listener;
			System.out.println("connectInput " + componentName);
		}
		else
		{
			System.err.println("component [" + componentName + "] not found."); // FIXME: get a log from somewhere
		}
		
	}

	@Override
	public Vector<Object> getInput(String componentName)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close()
	{
		if(WindowLayout.staticLayout != null)
			WindowLayout.staticLayout.dropWindow(config.getWindowType(), config.getWindowName());
		window.dispose();
		window = null;
	}
	
	@Override
	public void background(AgentGuiBackgroundTask agentGuiBackgroundTask,
			Object argument, ResultNotificationListener resultListener) {
		// TODO Auto-generated method stub
		
	} 
}
