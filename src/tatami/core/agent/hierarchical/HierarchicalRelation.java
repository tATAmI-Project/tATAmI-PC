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
package tatami.core.agent.hierarchical;

import jade.util.leap.Serializable;

import java.util.ArrayList;

/**
 * This class represents hierarchical relationship between agents. 
 * An agent has a parent and a list of children
 * @author Nguyen Thi Thuy Nga
 * @version 1/6/11
 *
 */
public class HierarchicalRelation implements Serializable{
	private static final long	serialVersionUID	= 169308994859814231L;
	private String parent;
	private ArrayList<String> children;
	
	/**
	 * CONSTRUCTOR
	 * an empty relationship
	 */
	public HierarchicalRelation(){
		parent = null;
		children = new ArrayList<String>();
	}
	
	/**
	 * CONSTRUCTOR
	 * @param parent : initial parent of agent
	 */
	public HierarchicalRelation(String parent){
		this.parent = parent;
		this.children = new ArrayList<String>();
	}
	
	// GET & SET
	/**
	 * get agent's parent's name
	 */
	public String getParent(){
		return this.parent;
	}
	
	/**
	 * get agent's children's names
	 */
	public ArrayList<String> getChildren(){
		return this.children;
	}
	
	/**
	 * set agent's parent
	 */
	public void setParent(String parent){
		this.parent = parent;
	}
	/**
	 * add new child
	 */
	public void addChild(String aChild){
		this.children.add(aChild);
	}
	/**
	 * add a list of children
	 */
	public void addChildren(ArrayList<String> children){
		this.children.addAll(children);
	}
}
