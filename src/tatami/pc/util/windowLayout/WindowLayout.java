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

import java.util.HashMap;
import java.util.Map;



import tatami.core.interfaces.Logger;
import tatami.core.util.logging.Log;
import tatami.pc.util.windowLayout.LayoutIndications.Bar;
import tatami.pc.util.windowLayout.LayoutIndications.BarPosition;

/**
 * Provides support for the automatic layout of windows.
 * 
 * Unimplemented: TODO: support for multiple screens TODO: avoiding bars on all sides
 * 
 * @author Andrei Olaru
 * 
 */
public class WindowLayout
{
	/**
	 * Internal class that manages the current allocation, by means of an array of cells, each cell having a pointer to the {@link WindowParameters}
	 * of the window that resides in the space this cell is part of.
	 * 
	 * @author Andrei Olaru
	 * 
	 */
	protected class Grid
	{
		int						w;
		int						h;
		
		WindowParameters[][]	allocation	= null;
		
		public Grid(int w, int h)
		{
			this.w = w;
			this.h = h;
			allocation = new WindowParameters[w][h];
		}
		
		WindowParameters findSpace(int wa, int ha)
		{
			for(int ia = 0; ia < w - wa + 1; ia++)
				for(int ja = 0; ja < h - ha + 1; ja++)
				{
					boolean ok = true;
					for(int i = ia; i < ia + wa; i++)
						for(int j = ja; j < ja + ha; j++)
							if(allocation[i][j] != null)
								ok = false;
					if(ok)
						return new WindowParameters(0, 0, 0, 0, ia, ja, wa, ha);
				}
			return null;
		}
		
		boolean canAllocate(WindowParameters params)
		{
			for(int i = params.xc.intValue(); i < params.xc.intValue() + params.wc.intValue(); i++)
				for(int j = params.yc.intValue(); j < params.yc.intValue() + params.hc.intValue(); j++)
					if(!((allocation[i][j] == null) || (allocation[i][j].isReservation && (params == allocation[i][j]))))
						return false;
			return true;
		}
		
		void allocate(WindowParameters params)
		{
			if(!canAllocate(params))
				throw new IllegalStateException();
			for(int i = params.xc.intValue(); i < params.xc.intValue() + params.wc.intValue(); i++)
				for(int j = params.yc.intValue(); j < params.yc.intValue() + params.hc.intValue(); j++)
					allocation[i][j] = params;
		}
		
		void deallocate(WindowParameters params)
		{
			for(int i = params.xc.intValue(); i < params.xc.intValue() + params.wc.intValue(); i++)
				for(int j = params.yc.intValue(); j < params.yc.intValue() + params.hc.intValue(); j++)
					if(allocation[i][j] != params)
						log.error("inconsistent allocation of window [" + makeKey(params.type, params.name) + "]");
					else
						allocation[i][j] = null;
			
		}
	}
	
	public static WindowLayout			staticLayout	= null;
	
	protected Logger					log				= null;
	
	Integer								startX			= null;
	Integer								startY			= null;
	Integer								globalWidth		= null;
	Integer								globalHeight	= null;
	Integer								gridStepX		= null;
	Integer								gridStepY		= null;
	
	LayoutIndications					indications		= null;
	Grid								grid			= null;
	HashMap<String, WindowParameters>	windows			= new HashMap<String, WindowParameters>();
	HashMap<String, WindowParameters>	reservations	= new HashMap<String, WindowParameters>();
	
	/**
	 * Constructs a new {@link WindowLayout} instance. It will manage the parameters (placement, size) for windows that are created vie getWindow and
	 * dropWindow.
	 * 
	 * @param globalWidth
	 *            : the area allocated to this layout; width.
	 * @param globalHeight
	 *            : the area allocated to this layout; width.
	 * @param ind
	 *            : a {@link LayoutIndications} structure that gives general indications for the layout (optional).
	 * @param mds
	 *            : a {@link MultipleDisplaySupport} structure that gives information on a multiple display setup (optional). Not Implemented.
	 */
	public WindowLayout(int globalWidth, int globalHeight, LayoutIndications ind, MultipleDisplaySupport mds)
	{
		log = Log.getLogger(this.getClass().getName());
		
		indications = (ind != null) ? ind : new LayoutIndications();
		
		this.globalWidth = new Integer((globalWidth != 0) ? globalWidth : 600);
		this.globalHeight = new Integer((globalHeight != 0) ? globalHeight : 400);
		
		startX = new Integer(Bar.getBar(BarPosition.LEFT, indications.bars, 0));
		startY = new Integer(Bar.getBar(BarPosition.TOP, indications.bars, 0));
		
		gridStepX = new Integer(globalWidth / ((indications.cellsX != null) ? indications.cellsX.intValue() : 4));
		gridStepY = new Integer(globalHeight / ((indications.cellsY != null) ? indications.cellsY.intValue() : 4));
		
		grid = new Grid(this.globalWidth.intValue() / gridStepX.intValue(), this.globalHeight.intValue() / gridStepY.intValue());
		
		for(Map.Entry<String, LayoutIndications> rez : ind.reservations.entrySet())
		{
			WindowParameters tentative = new WindowParameters(-1, -1, -1, -1, rez.getValue().positionX.intValue(), rez.getValue().positionY.intValue(), rez.getValue().cellsX.intValue(), rez
					.getValue().cellsY.intValue());
			tentative.isReservation = true;
			if(grid.canAllocate(tentative))
				grid.allocate(tentative);
			else
				tentative = grid.findSpace(tentative.wc.intValue(), tentative.hc.intValue());
			reservations.put(rez.getKey(), tentative);
		}
	}
	
	protected static String makeKey(String type, String name)
	{
		return type + ((name != null) ? (":" + name) : "");
	}
	
	/**
	 * Creates an allocation for a new window. The allocated space will remain associated with the window until dropWindow is called.
	 * 
	 * <p>
	 * Window name and type are just String used to identify the window internally, but two allocations with the same type and name cannot be created
	 * (the same allocation will be returned). Type can also help get indications specified when initializing the layout (via
	 * {@link LayoutIndications}.indicateWindowType.
	 * 
	 * <p>
	 * The function will always return a not-null instance. If no allocation can be made in the layout, a default window is returned.
	 * 
	 * @param type
	 *            the type of the window used to identify potential indications.
	 * @param name
	 *            the name of the window, used to identify it (together with type).
	 * @param indications
	 *            particular indications for this window; these override the indications for the global layout.
	 * @return the parameters for the newly allocated space, in the form of a {@link WindowParameters} instance.
	 */
	public WindowParameters getWindow(String type, String name, LayoutIndications indications)
	{
		int wc, hc;
		
		log.trace("finding window for " + type + ":" + name);
		
		if(windows.containsKey(makeKey(type, name)))
			return windows.get(makeKey(type, name));
		
		LayoutIndications ind = null;
		if(indications != null)
			ind = indications;
		else if(this.indications.windows.containsKey(type))
			ind = this.indications.windows.get(type);
		else
			ind = (this.indications.def != null) ? this.indications.def : new LayoutIndications();
		
		wc = (ind.cellsX != null) ? ind.cellsX.intValue() : 1;
		hc = (ind.cellsY != null) ? ind.cellsY.intValue() : 1;
		
		log.trace("building new window " + wc + " x " + hc);
		
		WindowParameters params = null;
		synchronized(windows)
		{
			if(reservations.containsKey(makeKey(type, name)))
				params = reservations.get(makeKey(type, name));
			else
				params = grid.findSpace(wc, hc);
			if(params != null)
			{
				params.x = new Integer(startX.intValue() + params.xc.intValue() * gridStepX.intValue());
				params.y = new Integer(startY.intValue() + params.yc.intValue() * gridStepY.intValue());
				params.width = new Integer(wc * gridStepX.intValue());
				params.height = new Integer(hc * gridStepY.intValue());
				params.type = type;
				params.name = name;
				
				windows.put(makeKey(type, name), params);
				grid.allocate(params);
				params.isReservation = false;
				
			}
		}
		if(params != null)
			log.trace("built: " + params);
		else
		{
			params = WindowParameters.defaultParameters();
			log.trace("returned default: " + params);
		}
		return params;
	}
	
	/**
	 * Deallocates the space for a window.
	 * 
	 * @param params
	 *            the parameters of the window (must be the same instance returned by getWindow())
	 */
	public void dropWindow(WindowParameters params)
	{
		boolean notfound = false;
		synchronized(windows)
		{
			if(!windows.containsKey(makeKey(params.type, params.name)))
				notfound = true;
			else
			{
				windows.remove(makeKey(params.type, params.name));
				grid.deallocate(params);
			}
		}
		if(notfound)
			log.error("window not found [" + makeKey(params.type, params.name) + "]");
		else
			log.trace("window " + makeKey(params.type, params.name) + " out");
	}
	
	/**
	 * Deallocates the space for a window.
	 * 
	 * @param type
	 * @param name
	 */
	public void dropWindow(String type, String name)
	{
		boolean notfound = false;
		synchronized(windows)
		{
			if(!windows.containsKey(makeKey(type, name)))
				notfound = true;
			else
				dropWindow(windows.get(makeKey(type, name)));
		}
		if(notfound)
			log.error("window not found [" + makeKey(type, name) + "]");
	}
	
	/**
	 * Removes all allocations, and shuts down the log. The instance is unusable after this call.
	 */
	public void doexit()
	{
		grid = null;
		windows = null;
		Log.exitLogger(this.getClass().getName());
	}
}
