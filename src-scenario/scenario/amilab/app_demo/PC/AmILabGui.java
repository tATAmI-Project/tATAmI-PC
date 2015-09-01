package scenario.amilab.app_demo.PC;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import tatami.core.agent.visualization.AgentGuiConfig;
import tatami.pc.agent.visualization.PCDefaultAgentGui;

/**
 * Gui used for the AmILab application.
 * 
 * @author Claudiu-Mihai Toma
 *
 */
public class AmILabGui extends PCDefaultAgentGui
{
	/**
	 * Label that will display images.
	 */
	private JLabel label;

	/**
	 * Name of the main label.
	 */
	public static final String MAIN_LABEL = "main_label";

	/**
	 * Creates and configures the GUI.
	 * 
	 * @param configuration
	 *            - the {@link AgentGuiConfig} specifying parameters such as window name and type.
	 */
	public AmILabGui(AgentGuiConfig configuration)
	{
		super(configuration);
	}

	@Override
	protected void buildGUI()
	{
		label = new JLabel("", SwingConstants.CENTER);

		int size = 350;
		window.setSize(size, size);
		// window.setLayout(new FlowLayout());
		window.add(label);
		components.put(MAIN_LABEL, label);
	}

	@Override
	protected void placeWindow()
	{
		// WindowParameters protected constructors. How to set my custom dimensions?

		// Without the super call the windows does not have a name
		// super.placeWindow();
	}

	/**
	 * Getter for the main label of this GUI.
	 * 
	 * @return the label
	 */
	public JLabel getLabel()
	{
		return label;
	}
}
