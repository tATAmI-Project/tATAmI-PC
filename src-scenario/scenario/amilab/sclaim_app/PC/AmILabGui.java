package scenario.amilab.sclaim_app.PC;

import java.awt.Label;
import java.util.Vector;

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
	 * The type of the window, to be given to WindowLayout.
	 */
	public static final String WINDOW_TYPE = "amilab-gui";

	/**
	 * Request message.
	 */
	public static final String REQUEST = "request";

	/**
	 * Creates and configures the GUI.
	 * 
	 * @param configuration
	 *            - the {@link AgentGuiConfig} specifying parameters such as window name and type.
	 */
	public AmILabGui(AgentGuiConfig configuration)
	{
		super(configuration);
		addComponent(REQUEST, new Label());
	}

	@Override
	protected void buildGUI()
	{
		label = new JLabel("", SwingConstants.CENTER);

		window.add(label);
		addComponent(MAIN_LABEL, label);
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

	/**
	 * Sends requests to all the clients.
	 */
	public void sendRequests()
	{
		inputConnections.get(REQUEST).receiveInput(REQUEST, new Vector<Object>());
	}
}
