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
package testing.andrei;

import java.awt.TextArea;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;



import tatami.core.interfaces.Logger;
import tatami.core.util.logging.Log;
import tatami.pc.util.graphical.GCanvas;
import tatami.pc.util.logging.TextAreaLogDisplay;
import tatami.pc.util.windowLayout.LayoutIndications;
import tatami.pc.util.windowLayout.WindowLayout;
import tatami.pc.util.windowLayout.WindowParameters;
import tatami.pc.util.windowLayout.LayoutIndications.BarPosition;

public class WindowAndGrapherTest
{
	private static String	unitName	= "window+grapherTestMain";
	TextArea				logText		= null;
	Logger					log			= null;
	WindowLayout			layout		= null;
	
	@SuppressWarnings("unused")
	public static void main(String args[])
	{
		new WindowAndGrapherTest();
	}
	
	public WindowAndGrapherTest()
	{
		logText = new TextArea(".hello.");
		log = Log.getLogger(unitName, new TextAreaLogDisplay(logText));
		log.trace("Hello World");
		
		layout = new WindowLayout(1200, 700, new LayoutIndications(8, 8).indicateBar(BarPosition.LEFT, 70, 0).indicateWindowType("System", 4, 1).indicateWindowType("accessory", 8, 7), null); // example indications
		
		JFrame window = new JFrame();
		buildWindow(window, layout);
		
	}
	
	protected void doExit()
	{
		layout.doexit();
		Log.exitLogger(unitName);
		System.exit(0);
	}
	
	protected void buildWindow(JFrame window, WindowLayout layout)
	{
		log.trace("building window.");
		window = new JFrame(unitName);
		window.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e)
			{
				super.windowClosing(e);
				log.info("exiting....");
				doExit();
			}
		});
		WindowParameters params = layout.getWindow("System", unitName, null);
		
		params.setWindow(window, true);
		
		window.add(logText);
		
		log.trace("showing window...");
		window.setVisible(true);
		
		JFrame acc = new JFrame(unitName);
//		acc.setLayout(new VerticalBagLayout());
		layout.getWindow("accessory", "acc", null).setWindow(acc, true);
//		acc.add(new Label("hello. i am an accessory window."));
		GCanvas canvas = new GCanvas();
		acc.add(canvas);
		acc.setVisible(true);
	}
	
}
