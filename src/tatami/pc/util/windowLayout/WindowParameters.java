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
package tatami.pc.util.windowLayout;

import java.awt.Frame;

/**
 * Specifies all parameters necessary for a window to be created. The parameters are calculated according to the allocation made by the
 * {@link WindowLayout}.
 * 
 * <p>
 * Also contains other non-public data to characterize the window, used by {@link WindowLayout}.
 * 
 * <p>
 * From the exterior, it can only be used for reading parameters when creating the window.
 * 
 * @author Andrei Olaru
 * 
 */
public class WindowParameters
{
	// features of the window, in pixels
	protected Integer	x				= null;
	protected Integer	y				= null;
	protected Integer	width			= null;
	protected Integer	height			= null;
	
	// same, but in terms of cells, not pixels.
	protected Integer	xc				= null;
	protected Integer	yc				= null;
	protected Integer	wc				= null;
	protected Integer	hc				= null;
	
	// window can be identified by type and name
	protected String	type			= null;
	protected String	name			= null;
	
	// only used internally, to mark a reservation
	protected boolean	isReservation	= false;
	
	/**
	 * @return x coordinate of top left corner, in pixels.
	 */
	public Integer x()
	{
		return x;
	}
	
	/**
	 * @return y coordinate of top left corner, in pixels.
	 */
	public Integer y()
	{
		return y;
	}
	
	/**
	 * @return width of the window, in pixels.
	 */
	public Integer w()
	{
		return width;
	}
	
	/**
	 * @return height of the window, in pixels.
	 */
	public Integer h()
	{
		return height;
	}
	
	/**
	 * @return the name of the window characterized by these parameters
	 */
	public String name()
	{
		return name;
	}
	
	/**
	 * Provided for convenient setting of a window. It sets the location, size and title (optional) of the window given as parameter.
	 * 
	 * @param window
	 *            : the window to set
	 */
	public void setWindow(Frame window, boolean setTitle)
	{
		window.setLocation(x().intValue(), y().intValue());
		window.setSize(w().intValue(), h().intValue());
		if(setTitle)
			window.setTitle((type != null ? type : "") + ((type != null && name != null) ? ": " : "") + (name != null ? name : ""));
	}
	
	protected WindowParameters(int x, int y, int width, int height, int xc, int yc, int wc, int hc)
	{
		this(x, y, width, height, xc, yc, wc, hc, null, null);
	}
	
	protected WindowParameters(int x, int y, int width, int height, int xc, int yc, int wc, int hc, String type, String name)
	{
		this.x = new Integer(x);
		this.y = new Integer(y);
		this.width = new Integer(width);
		this.height = new Integer(height);
		this.xc = new Integer(xc);
		this.yc = new Integer(yc);
		this.wc = new Integer(wc);
		this.hc = new Integer(hc);
		this.type = type;
		this.name = name;
	}
	
	@Override
	public String toString()
	{
		return "window[" + x + "," + y + "." + width + "x" + height + "/" + xc + "," + yc + "." + wc + "x" + hc + "]";
	}
	
	/**
	 * WARNING: use only as a fallback in case a {@link WindowLayout} is not available!!
	 * 
	 * <p>
	 * Will initialize only x, y, w, h.
	 * 
	 * @return - the newly allocated {@link WindowParameters} instance.
	 */
	public static WindowParameters defaultParameters()
	{
		return new WindowParameters(0, 0, 300, 300, -1, -1, -1, -1);
	}
}
