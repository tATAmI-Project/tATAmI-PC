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
import java.util.Vector;

/**
 * Should be used to indicate to {@link WindowLayout} the preferences and restrictions regarding the desired layout of the windows. Should be able to
 * specify relative sizes of different types and instances of windows, avoiding bars at edges of the screen(s), order of the windows in the layout.
 * 
 * <p>
 * Indication functions return the indication itself so one can use chained calls (e.g. indicateCellsX(cellsX).indicateCellsY(cellsY); )
 * 
 * <p>
 * Currently supports: indicating relative sizes of windows (by means of total and specific numbers of x and y cells); avoiding bars on the left of
 * the main screen;
 * 
 * @author olarua
 * 
 */
public class LayoutIndications
{
	public static enum BarPosition {
		LEFT, TOP, RIGHT, BOTTOM
	}
	
	static class Bar
	{
		LayoutIndications.BarPosition	position;
		int								width;
		int								screen;
		
		public Bar(LayoutIndications.BarPosition pos, int width, int screen)
		{
			position = pos;
			this.width = width;
			this.screen = screen;
		}
		
		static int getBar(LayoutIndications.BarPosition side, Vector<LayoutIndications.Bar> bars, int screen)
		{
			int w = 0;
			for(LayoutIndications.Bar b : bars)
				if(b.position == side && b.screen == screen)
					w += b.width;
			return w;
		}
	}
	
	Integer								cellsX			= null;
	Integer								cellsY			= null;
	Integer								positionX		= null;
	Integer								positionY		= null;
	Vector<LayoutIndications.Bar>		bars			= new Vector<LayoutIndications.Bar>();
	HashMap<String, LayoutIndications>	windows			= new HashMap<String, LayoutIndications>();
	HashMap<String, LayoutIndications>	reservations	= new HashMap<String, LayoutIndications>();
	LayoutIndications					def				= null;
	
	/**
	 * No indications. Default indications will be used as per WindowLayout constructor and getWindow().
	 */
	public LayoutIndications()
	{
	}
	
	/**
	 * @param cellsX
	 *            total number of X cells
	 * @param cellsY
	 *            total number of Y cells
	 */
	public LayoutIndications(int cellsX, int cellsY)
	{
		this();
		this.indicateCellsX(cellsX).indicateCellsY(cellsY);
	}
	
	/**
	 * @param cells
	 *            total number of X cells
	 * @return the updated instance itself, for chained calls
	 */
	public LayoutIndications indicateCellsX(int cells)
	{
		cellsX = new Integer(cells);
		return this;
	}
	
	/**
	 * @param cells
	 *            total number of Y cells
	 * @return the updated instance itself, for chained calls
	 */
	public LayoutIndications indicateCellsY(int cells)
	{
		cellsY = new Integer(cells);
		return this;
	}
	
	/**
	 * @param position
	 *            - preferred position of the window, in cells.
	 * @return the updated instance itself, for chained calls
	 */
	public LayoutIndications indicatePositionX(int position)
	{
		positionX = new Integer(position);
		return this;
	}
	
	/**
	 * @param position
	 *            - preferred position of the window, in cells.
	 * @return the updated instance itself, for chained calls
	 */
	public LayoutIndications indicatePositionY(int position)
	{
		positionY = new Integer(position);
		return this;
	}
	
	/**
	 * @param type
	 *            the type of the window
	 * @param cellsX
	 *            X cells the window must have
	 * @param cellsY
	 *            Y cells the window must have
	 * @return the updated instance itself, for chained calls
	 */
	public LayoutIndications indicateWindowType(String type, int cellsX, int cellsY)
	{
		windows.put(type, new LayoutIndications().indicateCellsX(cellsX).indicateCellsY(cellsY));
		return this;
	}

	/**
	 * Makes a reservation for a window. The reserved space will have a size according to the type of the window. The reservation will be activate
	 * when an allocation will be requested with this type and name.
	 * 
	 * <p>
	 * The indications for the window type should have been done before, otherwise an exception is thrown.
	 * 
	 * @param type
	 *            - the type of the window
	 * @param reserveName
	 *            - the name of the window for which the space is reserved
	 * @param positionX
	 *            - position of the space, in cells, x component.
	 * @param positionY
	 *            - position of the space, in cells, y component.
	 * @return the updated instance itself, for chained calls
	 */
	public LayoutIndications indicateReservation(String type, String reserveName, int positionX, int positionY)
	{
		LayoutIndications indication = windows.get(type);
		if(indication == null)
			throw new IllegalArgumentException("indications for the window type [" + type + "] not found");
		reservations.put(WindowLayout.makeKey(type, reserveName), new LayoutIndications().indicatePositionX(positionX).indicatePositionY(positionY).indicateCellsX(indication.cellsX.intValue())
				.indicateCellsY(indication.cellsY.intValue()));
		return this;
	}
	
	/**
	 * @param pos
	 *            position of the bar to avoid
	 * @param width
	 *            width of the bar
	 * @param screen
	 *            the index of the screen on which the bar exists
	 * @return the updated instance itself, for chained calls
	 */
	public LayoutIndications indicateBar(LayoutIndications.BarPosition pos, int width, int screen)
	{
		bars.add(new Bar(pos, width, screen));
		return this;
	}
}
