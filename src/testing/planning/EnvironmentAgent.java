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
package testing.planning;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;
import testing.planning.graphplan.*;
import testing.planning.graphplan.parser.GParser;
import testing.planning.graphplan.parser.ParseException;
/**
 * 
 * @author Gerard Simons, Alex Garella
 *
 *	This class defines the environment as an environment agent. 
 *This class is a place holder for a real claim agent environment
 */
public class EnvironmentAgent
{
	private Vector<String> environment;
	private ArrayList<GoalAgent> registeredAgents;
	
	private TOperatorSet operators;

    private ObjectSet objects;
	
    /**
     * Constructor
     * @param operatorFilePath the file with the operators
     * @param factsFilePath the file with the facts
     */
	public EnvironmentAgent(String operatorFilePath, String factsFilePath)
	{
		environment = new Vector<String>();
		registeredAgents = new ArrayList<GoalAgent>();
		readEnvironmentFromFiles(operatorFilePath, factsFilePath);
	}
	
	 /**
     * Constructor
     */
	public EnvironmentAgent()
	{
		registeredAgents = new ArrayList<GoalAgent>();
		environment = new Vector<String>();
	}
	
    /**
     * The method which reads the two files
     * @param operatorFilePath the file with the operators
     * @param factsFilePath the file with the facts
     */
    public void readEnvironmentFromFiles(String operatorFile, String factsFile) 
    {
        // parse operators file
        GParser parser = null;
        try {
            parser = new GParser(new java.io.FileInputStream(operatorFile));
        } catch (java.io.FileNotFoundException e) {
            System.out.println("File " + operatorFile + " not found.");
            System.exit(0);
        }
        try {
            operators = parser.OpFile();
        } catch (ParseException ex) {
            System.out.println ("Error Parsing Operators");
            ex.printStackTrace();
            System.exit(0);
        }
        System.out.println("Total operators parsed:" + operators.size());
        // parse facts file
        try {
            parser.ReInit (new java.io.FileInputStream(factsFile));
        } catch (java.io.FileNotFoundException e) {
            System.out.println("JPlan Version 1.0:  File " + factsFile + " not found.");
            System.exit(0);
        }
        try {
            objects = parser.Objects();
            environment = parser.InitState().getLiterals();

        } catch (ParseException ex) {
            System.out.println ("Error Parsing Facts File");
            ex.printStackTrace();
            System.exit(0);
        }
        System.out.println("Total objects parsed:" + objects.size());
    }
    
    /**
     * This method applies an action to the environment, updates the environment states accordingly
     * and sends the new updated environment to all the registered agents
     * @param action the action to be performed
     * @return true if the action was performed succesfully
     */
	public boolean applyAction(Action action)
	{
		for(String preProp : action.getPreConditions().getLiterals())
		{
			if(!environment.contains(preProp)) return false;
		}
		for(String addProp : action.getAddEffects().getLiterals())
		{
			if(!environment.contains(addProp)) environment.add(addProp);
		}
		environment.removeAll(action.getDelEffects().getLiterals());
		for(GoalAgent registeredAgent : registeredAgents)
		{
			registeredAgent.updateEnvironment(environment);
		}
		return true;
	}
	
    /**
     * Register an agent
     * @param goalAgent the agent to be registered with this environment
     */
	public void registerGoalAgent(GoalAgent goalAgent)
	{
		if(!registeredAgents.contains(goalAgent)) registeredAgents.add(goalAgent);
		goalAgent.updateEnvironment(environment);
	}
	
    /**
     * String representation of the environment
     * @return a string representation of the environment
     */
	public String toString()
	{
		return "EnvironmentAgent\n Environment: " + environment + "\n Objects: " + objects + "\nOperators: " + operators;
	}
	
	/**
     * Returns the environment state
     * @return the state of the environment
     */
	public Vector<String> getEnvironmentState()
	{
		return environment;
	}
	
	/**
     * Return the Objectset
     * @return the objects
     */
	public ObjectSet getObjects()
	{
		return objects;
	}
	
	/**
     * Returns the Operators
     * @return the operators
     */
	public TOperatorSet getOperators()
	{
		return operators;
	}
	
	/**
	 * Main method for testing
     */
	public static void main(String[] args)
	{
		EnvironmentAgent testAgent = new EnvironmentAgent("src/testing/planning/environment/blocks/blocks.txt", "src/testing/planning/environment/blocks/blockfacts.txt");
		
		System.out.println(testAgent);
	}
}
