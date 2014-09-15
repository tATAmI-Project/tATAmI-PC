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

public class DemoMainOutputGUI implements AgentGui {
 
	protected AgentGuiConfig				config				= null;
	protected WindowParameters				params				= null;
	protected DemoMainOutput				window				= null;
	public Map<String, Component>		components			= null;
	private static String outputDisplay	= "MAINOUTPUT";
	
	public DemoMainOutputGUI(AgentGuiConfig configuration) {
		
		config = configuration;
		components = new Hashtable<String, Component>();
		window = new DemoMainOutput();
		
		components.put(outputDisplay, window.jTextArea1);
		window.setVisible(true);
	}
	
	@Override
	public void doOutput(String componentName, Vector<Object> arguments)
	{
		String compName = componentName.toUpperCase();
		Component component;
		
		if(compName.compareTo(Demo.PDAComponents.CLEAR.toString()) == 0){
			
			component = components.get(outputDisplay);
			if(component != null && component instanceof JTextArea){
				JTextArea ta = (JTextArea)component;
				ta.setText(null);
			}
			component = components.get(outputDisplay);
			if(component != null && component instanceof JTextArea){
				JTextArea ta = (JTextArea)component;
				ta.setText(null);
			}
		}
		else {
			if(!components.containsKey(compName))
			{
				System.err.println("component [" + componentName + "] not found."); // FIXME: get a log from somewhere
				return;
			}
			
			component = components.get(compName);
			if(component instanceof JTextArea)
			{
				if(arguments.size() > 0)
				{
					JTextArea ta = (JTextArea)component;
					for (Object arg : arguments)
						ta.append((String)arg + " ");
					ta.append("\n");
					ta.repaint();
				}
			}
		}
	}

	@Override
	public void connectInput(String componentName, InputListener input) {
		// do nothing here
	}

	@Override
	public Vector<Object> getinput(String componentName)
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