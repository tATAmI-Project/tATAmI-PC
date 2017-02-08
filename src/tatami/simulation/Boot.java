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
package tatami.simulation;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import net.xqhs.util.logging.LoggerSimple.Level;
import net.xqhs.util.logging.UnitComponentExt;
import net.xqhs.util.logging.logging.Logging;
import tatami.core.agent.io.AgentActiveIO.InputListener;
import tatami.core.util.platformUtils.PlatformUtils;
import tatami.simulation.simulation_manager_builders.SimulationManagerXMLBuilder;
import tatami.simulation.simulation_manager_builders.SimulationManagerXMLBuilder.AgentLoaderException;
import tatami.simulation.simulation_manager_builders.SimulationManagerXMLBuilder.PlatformException;

/**
 * The Boot singleton class manages the startup of the multi-agent system. It manages settings, it loads the scenario,
 * loads the agent definitions (agents are actually created later).
 * <p>
 * After performing all initializations, it creates a {@link SimulationManager} instance that manages the actual
 * simulation.
 * 
 * @author Andrei Olaru
 * @author Nguyen Thi Thuy Nga
 */
public class Boot implements InputListener
{
	/**
	 * The log of the class.
	 */
	static protected UnitComponentExt	log	= (UnitComponentExt) new UnitComponentExt().setUnitName("boot").setLoggerType(
											PlatformUtils.platformLogType());
	
	private SimulationManager simulation = null;
	
	private static Boot boot = null;
	
	/**
	 * The method handling main functionality of {@link Boot}.
	 * <p>
	 * 
	 * @param args
	 *            - the arguments received by the program.
	 */
    public void boot(SimulationManagerXMLBuilder builder) {
        log.trace("Booting World.");

        simulation = new SimulationManager(builder);
/*
        if (!simulation.start()) {

        } else {
            log.error("No agent platforms loaded. Simulation will not start.");
        }
        */
    }
    
    public static SimulationManagerXMLBuilder makeBuilder(String[] args) {
        SimulationManagerXMLBuilder builder = null;

        try {
            builder = new SimulationManagerXMLBuilder(args);
            builder.getGUI().connectInput("CORE", boot);
            builder.buildGUI();
            builder.buildPlatform();
            builder.buildAgentLoaders();
            builder.buildContainerAgents();
            
            //builder.buildAgentPackages();
        } catch (SimulationManagerXMLBuilder.SimulationEzception exception) {
            log.error(exception.getMessage());
            return null;
        } catch (PlatformException exception) {
            log.warn(exception.getMessage());
        } catch (AgentLoaderException exception) {
            log.error(exception.getMessage());
            return null;
        }
        
        return builder;
    }

	
	
	/**
	 * Main method. It calls {@link Boot#boot(String[])} with the arguments received by the program.
	 * 
	 * @param args
	 *            - the arguments received by the program.
	 */
	public static void main(String[] args)
	{
		Logging.getMasterLogging().setLogLevel(Level.ALL);
		boot = new Boot();
		loadInitialData(args);
	}
	
	public static void loadInitialData(String[] args){
	    
        SimulationManagerXMLBuilder builder = makeBuilder(args);
        boot.boot(builder);
	}

    @Override
    public void receiveInput(String portName, Vector<Object> arguments) {
        if(portName.equals("GUI")){
            if(arguments.get(0).equals("LOAD SCENARIO")){
                
                System.out.println("Load scenario");
                String[] args = new String[1];
                args[0] = arguments.get(0).toString();
                loadInitialData(args);
            }
        }
    }
}
