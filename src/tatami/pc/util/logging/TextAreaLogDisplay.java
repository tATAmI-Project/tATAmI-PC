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
package tatami.pc.util.logging;

import java.awt.TextArea;

import tatami.core.util.logging.Log.DisplayEntity;


/**
 * DO NOT USE THIS EXCEPT FOR TESTING.
 * 
 * @author Andrei Olaru
 */
public class TextAreaLogDisplay implements DisplayEntity
{
	TextArea ta;
	
	public TextAreaLogDisplay(TextArea textArea)
	{
		ta = textArea;
		ta.setText("");
	}
	
	@Override
	public void output(String string)
	{
		ta.setText(string);
		ta.append(".");
		ta.repaint();
	}
}
