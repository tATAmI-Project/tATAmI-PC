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
package testing.andrei.httpServer;

import java.awt.Color;
import java.awt.TextArea;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.JFrame;



import tatami.core.interfaces.Logger;
import tatami.core.util.logging.Log;
import tatami.core.util.logging.Unit;
import tatami.pc.util.logging.TextAreaLogDisplay;
import tatami.pc.util.windowLayout.WindowLayout;
import tatami.pc.util.windowLayout.WindowParameters;

/**
 * Provides access to the agent system by means of HTTP calls.
 * 
 * Uses a simple HTTP server (based on {@link NanoHTTPD}).
 * 
 * <br/>
 * <br/>
 * 
 * 
 * 
 * @author Andrei Olaru
 * 
 */
public class TestServer extends NanoHTTPD
{
	protected static final String	schemaFile	= "files/serverSchema.xml";
	
	protected Logger				log			= null;
	protected WindowLayout			layout		= null;
	
	int								nQuery		= 0;
	int								nQuery2		= 0;
	
	/**
	 * Builds (and, not complying to the {@link Unit} specification, also starts) the web server.
	 * 
	 * @param layout
	 *            the {@link WindowLayout} that should be called upon generation of the server management window.
	 * @throws IOException
	 */
	public TestServer(WindowLayout layout, int port) throws IOException
	{
		super(port);
		
		TextArea logText = new TextArea();
		log = Log.getLogger(getName(), new TextAreaLogDisplay(logText));
		
		this.layout = layout;
		createWindow(logText);
	}
	
	protected void createWindow(TextArea logText)
	{
		log.trace("building window.");
		JFrame window = new JFrame("WebServer");
		window.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e)
			{
				doexit();
			}
		});
		if(layout == null)
		{
			window.setLocation(-500, 0);// FIXME
			window.setSize(500, 500);
		}
		else
		{
			WindowParameters params = layout.getWindow("Server", null, null);
			window.setLocation(params.x().intValue(), params.y().intValue());
			window.setSize(params.w().intValue(), params.h().intValue());
		}
		window.setBackground(Color.gray);
		window.add(logText);
		// window.pack();
		
		log.trace("showing window...");
		window.setVisible(true);
	}
	
	/**
	 * Closes the web server and the window. There will be no more answer to requests on the specified port.
	 */
	public void doexit()
	{
		log.trace("web server closing...");
		this.stop();
		if(layout != null)
		{
			layout.dropWindow("Server", null);
			layout.doexit();
		}
		Log.exitLogger(getName());
		System.exit(0);
	}
	
	/**
	 * TODO:
	 * 
	 * @return
	 */
	@Override
	public Response serve(String uri, String method, Properties header, Properties parms, Properties files)
	{
		@SuppressWarnings("rawtypes")
		Enumeration e = header.propertyNames();
		String tolog = "Received [" + method + "] request for [" + uri + "] with" + (e.hasMoreElements() ? "" : " no") + " headers ";
		while(e.hasMoreElements())
		{
			String value = (String)e.nextElement();
			tolog += "[ " + value + " | " + header.getProperty(value) + " ]";
		}
		e = parms.propertyNames();
		tolog += " and" + (e.hasMoreElements() ? "" : " no") + " parameters ";
		while(e.hasMoreElements())
		{
			String value = (String)e.nextElement();
			tolog += "[ " + value + " | " + parms.getProperty(value) + " ]";
		}
		log.trace(tolog);
		
		if(method.equals("GET"))
		{
			if(uri.charAt(20) == 'L')
			{
				nQuery2++;
				if(nQuery2 < 5)
					return new Response(HTTP_OK, MIME_PLAINTEXT, "<userlocations>\n<userlocation><user>Bob</user><location>screen1</location></userlocation>\n<userlocation><user>Carol</user><location>screen2</location></userlocation>\n<userlocation><user>Alice</user><location>screen2</location></userlocation></userlocations>");
				else if(nQuery2 < 10)
					return new Response(HTTP_OK, MIME_PLAINTEXT, "<userlocations>\n<userlocation><user>Bob</user><location>screen1</location></userlocation>\n<userlocation><user>Carol</user><location>screen2</location></userlocation>\n<userlocation><user>Alice</user><location>screen1</location></userlocation></userlocations>");
				else
					return new Response(HTTP_OK, MIME_PLAINTEXT, "<userlocations>\n<userlocation><user>Bob</user><location>screen2</location></userlocation>\n<userlocation><user>Carol</user><location>screen2</location></userlocation>\n<userlocation><user>Alice</user><location>screen1</location></userlocation></userlocations>");
			}
			else
			{
				nQuery ++ ;
				if(nQuery < 2)
					return new Response(HTTP_OK, MIME_PLAINTEXT, "<users>\n</users>");
				else if(nQuery < 3)
					return new Response(HTTP_OK, MIME_PLAINTEXT, "<users>\n<user>Bob</user>\n<user>Carol</user>\n</users>");
				else if(nQuery < 5)
					return new Response(HTTP_OK, MIME_PLAINTEXT, "<users>\n<user>Bob</user>\n<user>Carol</user>\n<user>Paul</user></users>");
				else
					return new Response(HTTP_OK, MIME_PLAINTEXT, "<users>\n<user>Bob</user>\n<user>Carol</user>\n<user>Paul</user><user>Alice</user></users>");
			}
		}
		else
			return new Response(HTTP_OK, MIME_PLAINTEXT, "");
	}
	
	/**
	 * This server does not support the serving of files.
	 * 
	 * @return will always return HTTP_FORBIDDEN
	 */
	@Override
	public Response serveFile(String uri, Properties header, File homeDir, boolean allowDirectoryListing)
	{
		return new Response(HTTP_FORBIDDEN);
	}
	
	public String getName()
	{
		return this.getClass().getName();
	}
	
	@SuppressWarnings("unused")
	public static void main(String args[])
	{
		try
		{
			new TestServer(null, 8090);
		} catch(IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
