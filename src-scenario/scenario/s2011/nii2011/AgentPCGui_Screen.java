/*******************************************************************************
 * Copyright (C) 2013 Andrei Olaru, Marius-Tudor Benea, Nguyen Thi Thuy Nga, Amal El Fallah Seghrouchni, Cedric Herpson.
 * 
 * This file is part of tATAmI-PC.
 * 
 * tATAmI-PC is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * tATAmI-PC is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with tATAmI-PC.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package s2011.nii2011;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.TextArea;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;



import tatami.pc.agent.visualization.PCDefaultAgentGui;
import tatami.pc.util.graphical.GCanvas;

public class AgentPCGui_Screen extends PCDefaultAgentGui
{
	GCanvas						thecanvas	= null;
	Map<String, Set<String>>	opinions	= new HashMap<String, Set<String>>();
	
	TextArea opinionOutput = new TextArea("-");
	
	
	public AgentPCGui_Screen(AgentGuiConfig config)
	{
		super(config);
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 3;
		c.gridheight = 3;
		c.ipady = 200;
		c.weightx = c.weighty = 1;
		
		JPanel outputs = new JPanel(new FlowLayout());
		JLabel slide = new JLabel("-");
		outputs.add(slide);
		opinionOutput.setMinimumSize(new Dimension(100, 100));
		outputs.add(opinionOutput);
		
		window.add(outputs, c);
		components.put("slideName", slide);
		components.put("opinionOutput", opinionOutput);
		
		window.setVisible(true);
	}
	
	@Override
	public void doOutput(String oc, Vector<Object> output)
	{
		String user = (String)output.get(0);
		String type = (String)output.get(1);
		if(type.equals("remove"))
		{
			opinions.remove(user);
			makePrint();
		}
		else
		{
			String opin = (String)output.get(2);
			String opinion = type + ": " + opin;
			
			if(!opinions.containsKey(user))
				opinions.put(user, new HashSet<String>());
			
			opinions.get(user).add(opinion);
			
			makePrint();
		}
	}
	
	protected void makePrint()
	{
		String print = "";
		for(Map.Entry<String, Set<String>> usrop : opinions.entrySet())
		{
			print += usrop.getKey() + ": " + usrop.getValue().toString() + "\n";
		}
		opinionOutput.setText(print);
	}
}
