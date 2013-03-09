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
package tatami.pc.util.graphical;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;



import tatami.core.interfaces.Logger;
import tatami.core.util.logging.Log;

public class GCanvas extends JPanel implements MouseInputListener, MouseWheelListener
{
	private static final long						serialVersionUID	= 1L;
	
	Logger											log					= Log.getLogger("canvas");
	
	int												zoomLevel			= 0;
	float											zoom				= 1.0f;
	Point2D											lookingCenter		= new Point2D.Float(0, 0);
	
	Point											mouseDragStart		= new Point();
	
	Timer											theTime				= null;
	
	Set<GElement>									elements			= Collections.synchronizedSet(new HashSet<GElement>());
	Set<GElement>									toUpdate			= Collections.synchronizedSet(new HashSet<GElement>());
	
	protected Queue<GElement>						updatingMagnetics	= null;
	protected ArrayList<ArrayList<Set<GElement>>>	magneticsCells		= null;
	protected float									magneticCellSize	= 0;
	protected float									magneticsMinX		= 0;
	protected float									magneticsMaxX		= 0;
	protected float									magneticsMinY		= 0;
	protected float									magneticsMaxY		= 0;
	
	public GCanvas()
	{
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addMouseWheelListener(this);
	}
	
	@Override
	protected void paintChildren(Graphics graphics)
	{
		this.setBackground(Color.white);
		Graphics2D g = (Graphics2D)graphics;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		int gw = getWidth();
		int gh = getHeight();
		
		zoom = (float)Math.pow(2, (zoomLevel / 5f));
		
		AffineTransform xform = new AffineTransform();
		xform.translate(gw / 2, gh / 2);
		xform.scale(zoom, zoom);
		xform.translate(-lookingCenter.getX(), -lookingCenter.getY());
		g.setTransform(xform);
		
		int gridStep = 20;
		
		g.setStroke(new BasicStroke(1.0f / zoom));
		
		g.setColor(new Color(.9f, .9f, .9f));
		for(int i = -100; i < 100; i++)
		{
			// vertical grid lines
			g.drawLine(i * gridStep, -1000, i * gridStep, 1000);
			g.drawLine(-1000, i * gridStep, 1000, i * gridStep);
		}
		g.drawOval(-5, -5, 10, 10);
		
		g.setStroke(new BasicStroke(1f));
		g.setColor(Color.black);
		
		for(GElement el : elements)
			el.draw(g);
		
		g.setStroke(new BasicStroke(0.2f));
		g.setColor(new Color(1.0f, 0.7f, 0.7f));
		for(GElement el : elements)
			g.draw(el.getCurrentBox());
		if(magneticsCells != null)
		{
			int ny = magneticsCells.size();
			int nx = magneticsCells.get(0).size();
			for(int j = 0; j < ny; j++)
				for(int i = 0; i < nx; i++)
					g.draw(new Rectangle2D.Float(i * magneticCellSize + magneticsMinX, j * magneticCellSize + magneticsMinY, magneticCellSize, magneticCellSize));
		}
		g.setColor(Color.red);
		for(GElement el : elements)
			g.draw(new Line2D.Double(el.currentPosition.getX(), el.currentPosition.getY(), el.currentPosition.getX() + el.force.getX(), el.currentPosition.getY() + el.force.getY()));
		
		super.paintChildren(g);
	}
	
	public void startMagnetics()
	{
		updatingMagnetics = new LinkedBlockingQueue<GElement>();
		synchronized(updatingMagnetics)
		{
			for(GElement el : elements)
				if(el.isMagnetic)
					updatingMagnetics.offer(el);
		}
		updateMagnetics(null);
	}
	
	protected void setUpdateTimer()
	{
		if(theTime == null)
		{
			theTime = new Timer();
			theTime.schedule(new TimerTask() {
				@Override
				public void run()
				{
					Set<GElement> updates;
					synchronized(toUpdate)
					{
						updates = new HashSet<GElement>(toUpdate);
					}
					for(GElement el : updates)
					{
						if(el.update())
							toUpdate.remove(el);
					}
					repaint();
					if(toUpdate.isEmpty())
					{
						theTime.cancel();
						theTime = null;
					}
				}
			}, 0, 50);
		}
	}
	
	protected void addUpdatable(GElement el)
	{
		synchronized(toUpdate)
		{
			toUpdate.add(el);
		}
		if(!toUpdate.isEmpty())
			setUpdateTimer();
	}
	
	public void add(GElement element)
	{
		synchronized(elements)
		{
			elements.add(element);
		}
		addUpdatable(element);
	}
	
	public void remove(GElement gelement)
	{
		elements.remove(gelement);
	}
	
	@Override
	public void removeAll()
	{
		elements.clear();
	}
	
	public void initMagnetics()
	{
		// calculated best cell size
		boolean first = true;
		magneticCellSize = 0;
		int nMagneticElements = 0;
		Set<GElement> calcBase;
		synchronized(elements)
		{
			calcBase = new HashSet<GElement>(elements);
		}
		log.trace("all:" + calcBase);
		for(Iterator<GElement> it = calcBase.iterator(); it.hasNext();)
		{
			GElement el = it.next();
			if(!el.isMagnetic)
				it.remove();
			else
			{ // TODO some more filtering needed
				Rectangle2D box = el.getTargetBox();
				magneticCellSize += box.getHeight() + box.getWidth();
				nMagneticElements++;
				if(first)
				{
					first = false;
					magneticsMinX = (float)box.getMinX();
					magneticsMaxX = (float)box.getMaxX();
					magneticsMinY = (float)box.getMinY();
					magneticsMaxY = (float)box.getMaxY();
				}
				else
				{
					if(magneticsMinX > box.getMinX())
						magneticsMinX = (float)box.getMinX();
					if(magneticsMaxX < box.getMaxX())
						magneticsMaxX = (float)box.getMaxX();
					if(magneticsMinY > box.getMinY())
						magneticsMinY = (float)box.getMinY();
					if(magneticsMaxY < box.getMaxY())
						magneticsMaxY = (float)box.getMaxY();
				}
			}
		}
		if(nMagneticElements > 0)
			magneticCellSize /= (2 * nMagneticElements);
		magneticsMinX -= magneticCellSize;
		magneticsMaxX += magneticCellSize;
		magneticsMinY -= magneticCellSize;
		magneticsMaxY += magneticCellSize;
		log.info("medsize: " + magneticCellSize + " bounds: " + magneticsMinX + " " + magneticsMinY + " " + magneticsMaxX + " " + magneticsMaxY);
		
		// create cells
		int ncx = (int)((magneticsMaxX - magneticsMinX) / magneticCellSize) + 1;
		int ncy = (int)((magneticsMaxY - magneticsMinY) / magneticCellSize) + 1;
		magneticsCells = new ArrayList<ArrayList<Set<GElement>>>(ncy);
		for(int j = 0; j <= ncy; j++)
		{
			ArrayList<Set<GElement>> row = new ArrayList<Set<GElement>>(ncx);
			for(int i = 0; i <= ncx; i++)
				row.add(i, new HashSet<GElement>());
			magneticsCells.add(j, row);
		}
		
		// place all elements in cells
		for(GElement el : calcBase)
			if(el.isMagnetic)
			{ // TODO some more filtering needed
				Rectangle2D box = el.getTargetBox();
				int cxmin = (int)((box.getMinX() - magneticsMinX) / magneticCellSize);
				int cxmax = (int)((box.getMaxX() - magneticsMinX) / magneticCellSize);
				int cymin = (int)((box.getMinY() - magneticsMinY) / magneticCellSize);
				int cymax = (int)((box.getMaxY() - magneticsMinY) / magneticCellSize);
				
				// log.trace("adding: " + box + " " + cxmin + " " + cxmax + " " + cymin + " " + cymax);
				// log.trace("adding " + el + " " + cxmin + " " + cxmax + " " + cymin + " " + cymax);
				for(int i = cxmin; i <= cxmax; i++)
					for(int j = cymin; j <= cymax; j++)
						magneticsCells.get(j).get(i).add(el);
			}
		
		// see neighbors of the updater
		
	}
	
	protected void updateMagnetics(GElement updating)
	{
		if(updatingMagnetics == null)
			return;
		if(updating != null)
		{
			boolean existing = false;
			boolean mustreturn = false;
			synchronized(updatingMagnetics)
			{
				if(!updatingMagnetics.isEmpty()) // updating is already in progress in another call
					mustreturn = true;
				if(updatingMagnetics.contains(updating))
					existing = true;
				else
					updatingMagnetics.offer(updating);
			}
			if(existing)
				log.trace("element " + updating + " already in queue");
			else
				log.trace("element " + updating + " inserted in queue");
			if(mustreturn)
				return;
		}
		
		while(!updatingMagnetics.isEmpty())
		{
			GElement el = null;
			synchronized(updatingMagnetics)
			{
				el = updatingMagnetics.poll();
			}
			Set<GElement> concerned = new HashSet<GElement>();
			
			// locate
			Rectangle2D box = el.getTargetBox();
			int cxmin, cxmax, cymin, cymax;
			// (re-)initialize?
			if((magneticsCells == null) || (box.getMinX() < magneticsMinX) || (box.getMaxX() > magneticsMaxX) || (box.getMinY() < magneticsMinY) || (box.getMaxY() > magneticsMaxY))
			{
				log.trace("must init magnetics");
				initMagnetics();
				cxmin = (int)((box.getMinX() - magneticsMinX) / magneticCellSize);
				cxmax = (int)((box.getMaxX() - magneticsMinX) / magneticCellSize);
				cymin = (int)((box.getMinY() - magneticsMinY) / magneticCellSize);
				cymax = (int)((box.getMaxY() - magneticsMinY) / magneticCellSize);
				String logbox = "boxes: \n";
				int i = 0, j = 0;
				for(ArrayList<Set<GElement>> row : magneticsCells)
				{
					i = 0;
					for(Set<GElement> cell : row)
					{
						logbox += (cell.isEmpty() ? "" : "\n") + "(" + j + "," + i + "):" + cell;
						i++;
					}
					j++;
				}
				log.trace(logbox);
			}
			else
			{
				cxmin = (int)((box.getMinX() - magneticsMinX) / magneticCellSize);
				cxmax = (int)((box.getMaxX() - magneticsMinX) / magneticCellSize);
				cymin = (int)((box.getMinY() - magneticsMinY) / magneticCellSize);
				cymax = (int)((box.getMaxY() - magneticsMinY) / magneticCellSize);
				// update cell matrix
				int x = 0, y = 0;
				for(ArrayList<Set<GElement>> row : magneticsCells)
				{
					x = 0;
					for(Set<GElement> cell : row)
					{
						if(cell.contains(el))
						{
							if(!((cxmin <= x) && (x <= cxmax) && (cymin <= y) && (y <= cymax)))
								cell.remove(el);
						}
						else if((cxmin <= x) && (x <= cxmax) && (cymin <= y) && (y <= cymax))
							cell.add(el);
						x++;
					}
					y++;
				}
				
			}
			log.trace("updating magnetics for " + el.toString());
			
			for(int i = cxmin; i <= cxmax; i++)
				for(int j = cymin; j <= cymax; j++)
					concerned.addAll(magneticsCells.get(j).get(i));
			
			concerned.remove(el);
			log.trace("in the same box: " + concerned.toString());
			// call updates for concerned cells
			for(GElement toUpdate : concerned)
				toUpdate.magneticUpdate(el);
		}
		
		if(magneticsCells != null && !magneticsCells.isEmpty())
		{
			String logbox = "boxes: \n";
			int i = 0, j = 0;
			for(ArrayList<Set<GElement>> row : magneticsCells)
			{
				i = 0;
				for(Set<GElement> cell : row)
				{
					logbox += (cell.isEmpty() ? "" : "\n") + "(" + j + "," + i + "):" + cell;
					i++;
				}
				j++;
			}
			log.trace(logbox);
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e)
	{
		mouseDragStart = new Point();
		for(GElement el : elements)
			if(el instanceof GContainer)
			{
				el.setMoveTo(reverseTransform(e.getPoint()));
				return;
			}
	}
	
	protected Point2D reverseTransform(Point screen)
	{
		Point2D canvas = new Point2D.Float();
		
		int x = (int)screen.getX();
		int y = (int)screen.getY();
		
		x -= getWidth() / 2;
		y -= getHeight() / 2;
		
		float xf = x / zoom;
		float yf = y / zoom;
		
		xf += lookingCenter.getX();
		yf += lookingCenter.getY();
		
		canvas.setLocation(xf, yf);
		return canvas;
	}
	
	@Override
	public void mouseEntered(MouseEvent e)
	{
	}
	
	@Override
	public void mouseExited(MouseEvent e)
	{
	}
	
	@Override
	public void mousePressed(MouseEvent e)
	{
		mouseDragStart = new Point(e.getPoint());
	}
	
	@Override
	public void mouseReleased(MouseEvent e)
	{
		if(!mouseDragStart.equals(e.getPoint()))
		{
			int dx = mouseDragStart.x - e.getPoint().x;
			int dy = mouseDragStart.y - e.getPoint().y;
			
			float dxf = dx / zoom;
			float dyf = dy / zoom;
			
			lookingCenter.setLocation(lookingCenter.getX() + dxf, lookingCenter.getY() + dyf);
			repaint();
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e)
	{
		if(!mouseDragStart.equals(e.getPoint()))
		{
			int dx = mouseDragStart.x - e.getPoint().x;
			int dy = mouseDragStart.y - e.getPoint().y;
			
			float dxf = dx / zoom;
			float dyf = dy / zoom;
			
			lookingCenter.setLocation(lookingCenter.getX() + dxf, lookingCenter.getY() + dyf);
			mouseDragStart = new Point(e.getPoint());
			repaint();
		}
	}
	
	@Override
	public void mouseMoved(MouseEvent ev)
	{
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		int onmask = InputEvent.CTRL_DOWN_MASK;
		int offmask = 0;
		if((e.getModifiersEx() & (onmask | offmask)) == onmask)
		{
			GElement chosen = null;
			for(GElement el : elements)
				if(el.contains(reverseTransform(e.getPoint())))
				{
					if((chosen == null) || (chosen.referencingElements.contains(el)))
						chosen = el;
				}
			if(chosen != null)
				chosen.setScaleDelta(e.getWheelRotation());
		}
		else
		{
			if(Math.abs(zoomLevel - e.getWheelRotation()) < 20)
				zoomLevel -= e.getWheelRotation();
			repaint();
		}
	}
	
	public void setZoom(int level)
	{
		zoomLevel = level;
		repaint();
	}
	
	public void resetLook()
	{
		lookingCenter.setLocation(new Point2D.Float());
	}
}
