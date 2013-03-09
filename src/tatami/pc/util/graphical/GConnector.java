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

import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class GConnector extends GElement
{
	public enum ArrowType {
		NONE, ARROW
	}
	
	public enum ConnectorType {
		DIRECT, VERTICAL, TOCONTAINER
	}
	
	final float		handleSize	= 3;
	
	ConnectorType	type;
	GElement		from		= null;
	GElement		to			= null;
	
	ArrowType		toArrow		= ArrowType.NONE;
	ArrowType		fromArrow	= ArrowType.NONE;
	
	public GConnector()
	{
		super();
		this.isMagnetic = false;
	}
	
	@Override
	protected void createShapes()
	{
	}
	
	public GConnector setFrom(GElement from)
	{
		this.from = from;
		from.addReferencingElement(this);
		return this;
	}
	
	public GConnector setTo(GElement to)
	{
		this.to = to;
		to.addReferencingElement(this);
		return this;
	}
	
	@Override
	protected void recalculateTarget()
	{
		if(!doDraw)
			return;
		size = handleSize * targetScaleFactor;
		if(to == null || from == null)
			return;
		double xF = from.getTargetBox().getCenterX();
		double yF = from.getTargetBox().getCenterY();
		double xT = to.getTargetBox().getCenterX();
		double yT = to.getTargetBox().getCenterY();
		
		double x = Math.min(xF, xT) + Math.abs(xF - xT) / 2;
		double y = Math.min(yF, yT) + Math.abs(yF - yT) / 2;
		
		targetPosition.setLocation(x, y);
		magneticPosition.setLocation(targetPosition);
		currentPosition.setLocation(magneticPosition);
		
		refreshReferencingElements();
		
		setUpdatable();
	}
	
	@Override
	protected void magneticUpdate(GElement magneticNeighbor)
	{
	}
	
	@Override
	protected Rectangle2D getBox(Point2D position, float scaleFactor)
	{
		float displayedsize = handleSize * scaleFactor;
		return new Rectangle2D.Float((float)position.getX() - displayedsize / 2, (float)position.getY() - displayedsize / 2, displayedsize, displayedsize);
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		if(!doDraw)
			return;
		double xF, xT, yF, yT;
		xF = from.getCurrentBox().getCenterX();
		yF = from.getCurrentBox().getCenterY();
		xT = to.getCurrentBox().getCenterX();
		yT = to.getCurrentBox().getCenterY();
		
		double x = Math.min(xF, xT) + Math.abs(xF - xT) / 2;
		double y = Math.min(yF, yT) + Math.abs(yF - yT) / 2;
		Point2D intersectFrom = from.intersectRay(new Point2D.Double(xT, yT));
		Point2D intersectTo = to.intersectRay(new Point2D.Double(xF, yF));
		
		g.draw(new Line2D.Double(intersectFrom, intersectTo));
		g.draw(new Ellipse2D.Double(x - size / 2, y - size / 2, size, size));
	}
}
