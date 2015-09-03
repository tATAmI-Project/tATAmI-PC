package scenario.examples.sclaim_tatami2.simpleScenarioF;

import java.util.Vector;

import tatami.core.agent.AgentComponent;
import tatami.core.agent.io.AgentActiveIO;

/**
 * Example component for testing agent I/O done following the {@link AgentActiveIO} model.
 * 
 * @author Andrei Olaru
 */
public class IOTester extends AgentComponent implements AgentActiveIO
{
	/**
	 * The serial UID.
	 */
	private static final long serialVersionUID = -3986631109726804753L;
	
	/**
	 * Default constructor.
	 */
	public IOTester()
	{
		super(AgentComponentName.TESTING_COMPONENT);
	}
	
	@Override
	public void doOutput(String portName, Vector<Object> arguments)
	{
		if(!portName.toLowerCase().equals("theoutput"))
			return;
		String out = "";
		for(Object arg : arguments)
			out += arg + " ";
		System.out.println("=== output received: " + out);
	}
	
	@Override
	public Vector<Object> getInput(String portName)
	{
		if(!portName.toLowerCase().equals("theinput"))
			return null;
		Vector<Object> ret = new Vector<Object>();
		ret.addElement("check");
		ret.addElement("bla");
		return ret;
	}
	
	@Override
	public void connectInput(String componentName, InputListener listener)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setDefaultListener(InputListener listener)
	{
		// TODO Auto-generated method stub
		
	}
	
}
