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
package tatami.pc.util.files;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

public class FileUtils
{
	public static String fileToString(String fileName)
	{
		String ret = null;
		try
		{
			InputStream in = new FileInputStream(fileName);
			ret = streamToString(in);
			in.close();
		} catch(FileNotFoundException e)
		{
			e.printStackTrace();
		} catch(IOException e)
		{
			e.printStackTrace();
		}
		return ret;
	}
	
	public static void stringToFile(String fileName, String string)
	{
		OutputStream out;
		try
		{
			out = new FileOutputStream(fileName, false);
			stringToStream(out, string);
		} catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	
	public static String streamToString(InputStream stream)
	{
		String ret = "";
		Scanner scanner = null;
		scanner = new Scanner(stream);
		while(scanner.hasNextLine())
			ret += scanner.nextLine();
		return ret;
	}
	
	public static void stringToStream(OutputStream stream, String string)
	{
		try
		{
			stream.write(string.getBytes());
		} catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
