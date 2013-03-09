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
package tatami.pc.agent.webService;

import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.FIPAManagementOntology;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.webservice.dynamicClient.DynamicClient;
import jade.webservice.dynamicClient.WSData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.StringTokenizer;

import tatami.core.agent.webServices.WSAgentImplementationInterface;
import tatami.core.agent.webServices.WebServiceOntology;

/**
 * Implements the operation needed for registering the agent as a web service and allowing the agent to access web
 * services.
 * 
 * //FIXME: treat exceptions better, return them to WSAgent.
 * 
 * @author Andrei Olaru
 */
public class WSAgentImplementation implements WSAgentImplementationInterface
{
	@Override
	public boolean registerService(Agent agent, String agentName, String agentClass)
	{
		// Prepare a DFAgentDescription
		DFAgentDescription dfad = new DFAgentDescription();
		dfad.setName(agent.getAID());
		dfad.addLanguages(WebServiceOntology.LANGUAGE);
		dfad.addProtocols(FIPANames.InteractionProtocol.FIPA_REQUEST);
		
		ServiceDescription sd;
		sd = new ServiceDescription();
		sd.addLanguages(WebServiceOntology.LANGUAGE);
		sd.addProtocols(FIPANames.InteractionProtocol.FIPA_REQUEST);
		sd.setType(agentClass);
		
		// WSIG properties
		sd.addProperties(new Property("wsig", "true"));
		sd.addProperties(new Property("name", agentName));
		sd.setName(agentName);
		
		// Ontology
		Ontology onto = WebServiceOntology.getInstance();
		sd.addOntologies(onto.getName());
		
		dfad.addServices(sd);
		
		// Register codec/onto
		agent.getContentManager().registerLanguage(new SLCodec());
		agent.getContentManager().registerOntology(FIPAManagementOntology.getInstance());
		agent.getContentManager().registerOntology(onto);
		
		// DF registration
		try
		{
			DFService.register(agent, dfad);
			return true;
		} catch(Exception e)
		{
			return false;
			// doDelete();
		}
	}
	
	@Override
	public boolean unregisterWS(Agent agent)
	{
		try
		{
			DFService.deregister(agent);
		} catch(FIPAException e)
		{
			return false;
		}
		return true;
	}
	
	@Override
	public String doAccess(String uri, String serviceName, String message)
	{
		try
		{
			String fulladdress = uri;
			// Get an instance of DynamicClient
			DynamicClient dc = new DynamicClient();
			
			// Initialize DynamicClient for displayService webservice by url
			// String uri = "http://localhost:8080/wsig/ws/";
			fulladdress = fulladdress.concat(serviceName);
			fulladdress = fulladdress.concat("?WSDL");
			dc.initClient(new URI(fulladdress));
			
			// Example of invocation of an operation getAgentInfo
			// ---------------------------------------------------------------------------------------
			WSData input = new WSData();
			input.setParameter(WebServiceOntology.OPERATION_ARGUMENT, message);
			// Invoke the operation
			WSData output = dc.invoke(WebServiceOntology.RECEIVE_OPERATION, input);
			String result = output.getParameterString(WebServiceOntology.RECEIVE_OPERATION + "Return");
			return result;
			
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public String doSimpleAccess(String uri, String request)
	{
		String ret = null;
		try
		{
			StringTokenizer tokker = new StringTokenizer(request);
			String method = tokker.nextToken();
			String path = tokker.nextToken();
			
			URL url = new URL(uri + path);
			
			// make connection
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			
			// set mode
			connection.setRequestMethod(method);
			if(!method.equals("GET"))
				connection.setDoOutput(true);
			connection.setAllowUserInteraction(false);
			
			// send query
			if(!method.equals("GET"))
			{
				PrintStream ps = new PrintStream(connection.getOutputStream());
				ps.print(request);
				ps.close();
			}
			// get result
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line = null;
			while((line = br.readLine()) != null)
			{
				if(ret == null)
					ret = line;
				else
					ret += line + "\n";
			}
			br.close();
		} catch(MalformedURLException e)
		{
			e.printStackTrace();
		} catch(IOException e)
		{
			e.printStackTrace();
		}
		
		return ret;
	}
	
}
