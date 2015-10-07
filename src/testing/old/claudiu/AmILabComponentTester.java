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
package testing.old.claudiu;

import tatami.amilab.AmILabComponent;
import tatami.amilab.AmILabComponent.AmILabDataType;

/**
 * Tests the functionality of {@link AmILabComponent}
 * 
 * @author Claudiu-Mihai Toma
 *
 */
public class AmILabComponentTester
{
	/**
	 * Here goes nothing.
	 * 
	 * @param args
	 *            - main arguments
	 */
	public static void main(String[] args)
	{
		// Create component.
		AmILabComponent tester = new AmILabComponent(AmILabComponent.KESTREL_LOCAL_SERVER_IP,
				AmILabComponent.KESTREL_SERVER_PORT, AmILabComponent.KESTREL_AMILAB_COMPONENT_QUEUE);
		
		//Push some data.
		tester.set("a");
		tester.set("b");
		tester.set("image_depth {data}");
		
		//Test "get" method.
		System.out.println("data found: " + tester.get(AmILabDataType.IMAGE_DEPTH));
	}
}
