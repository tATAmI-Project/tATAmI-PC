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
package tatami.core.agent.claim.parser;

/**
 * Structure returned by the parser for variables
 * 
 * @author tudor
 * 
 */
public class ClaimVariable extends ClaimConstruct
{
	private static final long		serialVersionUID	= 4863769255696708224L;
	
	/**
	 * the name of the variable
	 */
	private String					name;
	
	/**
	 * whether the variable is affectable or not
	 */
	private boolean					isAffectable;

	/**
	 * The behavior to which this construct belongs.
	 */
	private ClaimBehaviorDefinition	myBehavior;
	
	@Override
	public String toString()
	{
		if (isAffectable())
			return new String("??" + getName());
		else
			return new String("?" + getName());
//		return getName();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof ClaimVariable) && name.equals(((ClaimVariable)obj).getName()) && isAffectable==((ClaimVariable)obj).isAffectable();
	}
	
	@Override
	public int hashCode()
	{
		return name.hashCode();
	}
	
	/**
	 * static method that parses a string and returns a ClaimVariable with the name specified by the string.
	 * 
	 * @param str
	 *            - string to be parsed
	 */
	public static ClaimVariable parseString(String str)
	{
		String newStr = str.trim();
		while(newStr.startsWith("?"))
			newStr = newStr.substring(1);
		
		return new ClaimVariable(newStr);
	}
	
	public void setName(String variableName)
	{
		this.name = variableName;
	}
	
	public String getName()
	{
		return name;
	}
	
	public ClaimVariable(String variableName)
	{
		this(variableName,false);
	}
	
	public ClaimVariable(String variableName, boolean affectable)
	{
		super(ClaimConstructType.VARIABLE);
		setName(variableName);
		setAffectable(affectable);
	}
	
	
	
	public boolean isAffectable() {
		return isAffectable;
	}

	public void setAffectable(boolean isAffectable) {
		this.isAffectable = isAffectable;
	}

	public void setMyBehavior(ClaimBehaviorDefinition behavior)
	{
		this.myBehavior = behavior;
	}
	
	public ClaimBehaviorDefinition getMyBehavior()
	{
		return myBehavior;
	}
	
	/**
	 * Gets the complement of the current variable, according to the affectability
	 */
	public ClaimVariable getComplement()
	{
		ClaimVariable complement = new ClaimVariable(getName(), !isAffectable());
		return complement;
	}
}
