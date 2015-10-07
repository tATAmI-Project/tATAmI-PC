/*******************************************************************************
 * Copyright (C) 2015 Andrei Olaru, Marius-Tudor Benea, Nguyen Thi Thuy Nga, Amal El Fallah Seghrouchni, Cedric Herpson.
 * 
 * This file is part of tATAmI-PC.
 * 
 * tATAmI-PC is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * tATAmI-PC is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with tATAmI-PC.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
