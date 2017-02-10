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

import java.util.Vector;

import net.xqhs.util.logging.LoggerSimple.Level;
import net.xqhs.util.logging.UnitComponentExt;
import net.xqhs.util.logging.logging.Logging;
import tatami.HMI.pub.HMIInterface;
import tatami.core.agent.io.AgentActiveIO;
import tatami.core.agent.io.AgentActiveIO.InputListener;
import tatami.core.util.platformUtils.PlatformUtils;
import tatami.simulation.simulation_manager_builders.SimulationManagerXMLBuilder;
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
	
	private static AgentActiveIO userInterface;
	
	/**
	 * The method handling main functionality of {@link Boot}.
	 * <p>
	 * 
	 * @param args
	 *            - the arguments received by the program.
	 */
	
    public void initSimulation(SimulationManagerXMLBuilder builder) {
        log.trace("Booting World.");
        
        simulation = new SimulationManager(builder);
        
/*
        if (!simulation.start()) {

        } else {
            log.error("No agent platforms loaded. Simulation will not start.");
        }
        */
    }
    
    
    public SimulationManagerXMLBuilder makeBuilder(String scenarioPath) {
        SimulationManagerXMLBuilder builder = null;
        if(scenarioPath == null){
            scenarioPath = BootDefaultArguments.scenarioFileName;
        }
        
        log.info("Loading scenario: " + scenarioPath);

        try {
            builder = new SimulationManagerXMLBuilder();
            builder.setGUI(userInterface);
            userInterface.connectInput("BOOT", this);
            builder.loadXML(scenarioPath);
            builder.buildPlatform();
            builder.buildArtefacts();
            builder.buildContainerAgents();

        } catch (SimulationManagerXMLBuilder.SimulationEzception exception) {
            log.error(exception.getMessage());
            return null;
        } catch (PlatformException exception) {
            log.warn(exception.getMessage());
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
		
		userInterface = HMIInterface.INST.getHMI();
		
		boot = new Boot();
		
		SimulationManagerXMLBuilder builder = boot.makeBuilder(BootDefaultArguments.scenarioFileName);
		
		boot.initSimulation(builder);
		
		//boot.build(builder);
	}
	
    @Override
    public void receiveInput(String portName, Vector<Object> arguments) {
        log.info("Receive input");
        if (portName.equals("GUI-LOAD_SCENARIO")) {
            SimulationManagerXMLBuilder builder = boot.makeBuilder(arguments.get(0).toString());
            boot.initSimulation(builder);
        }
    }
}
