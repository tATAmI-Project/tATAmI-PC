package testing.system_testing.default_components.PC;

import java.awt.TextArea;

import javax.swing.JButton;

import tatami.core.agent.visualization.AgentGuiConfig;
import tatami.pc.agent.visualization.PCDefaultAgentGui;

/**
 * Simple GUI with one button.
 * 
 * @author Andrei Olaru
 */
public class TestGui extends PCDefaultAgentGui
{
	/**
	 * Name for the button component.
	 */
	public static final String	BUTTON_NAME	= "thebutton";
	
	/**
	 * creates a new GUI.
	 * 
	 * @param configuration
	 *            - the configuration
	 */
	public TestGui(AgentGuiConfig configuration)
	{
		super(configuration);
	}
	
	@Override
	protected void buildGUI()
	{
		JButton theButton = new JButton("Press to exit");
		addComponent(BUTTON_NAME, theButton);
		window.add(theButton);
		
		addComponent(DefaultComponent.AGENT_LOG.toString(), new TextArea()); // will not be used
	}
}
