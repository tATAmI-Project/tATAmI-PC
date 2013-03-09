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

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class GLabel extends GElement
{
	String				labelText	= null;
	
	Font				font		= null;
	FontRenderContext	fontctx		= null;
	
	@Override
	public GLabel setCanvas(GCanvas canvas)
	{
		super.setCanvas(canvas);
		font = canvas.getFont().deriveFont(Font.PLAIN, 5);
		fontctx = canvas.getFontMetrics(font).getFontRenderContext();
		createShapes();
		return this;
	}

	public GLabel setText(String labelText)
	{
		this.labelText = labelText;
		createShapes();
		recalculateTarget();
		return this;
	}

	public GLabel setParent(GElement labeled)
	{
		labeled.addReferencingElement(this);
		setReference(labeled, ReferenceType.CENTER);
		return this;
	}

	@Override
	public GLabel setScaleDelta(int amount)
	{
		super.setScaleDelta(amount);
		recalculateTarget();
		return this;
	}

	@Override
	protected void createShapes()
	{
		if(font == null || fontctx == null || labelText == null)
			return;
		GlyphVector v = font.createGlyphVector(fontctx, labelText);
		shapes.clear();
		shapes.add(v.getOutline());
	}
	
	@Override
	protected boolean contains(Point2D pt)
	{
		if(!doDraw)
			return false;
		if(shapes == null)
			return false;
		for(Shape s : shapes)
		{
			AffineTransform t = new AffineTransform();
			t.translate(currentPosition.getX(), currentPosition.getY());
			t.scale(currentScaleFactor, currentScaleFactor);
			if(t.createTransformedShape(s).getBounds2D().contains(pt))
				return true;
		}
		return false;
	}
	
	@Override
	protected void drawShapes(Graphics2D g)
	{
		for(Shape s : shapes)
		{
			g.fill(s);
//			Rectangle2D bounds = s.getBounds2D();
//			g.draw(bounds);
//			g.draw(getTargetBox());
		}
	}
	
	@Override
	protected void recalculateTarget()
	{
		if(!doDraw)
			return;
		if(shapes.isEmpty())
			return;
		Shape text = shapes.iterator().next();
		if(layoutReference != null)
			targetPosition.setLocation(getReferencePoint().getX() - text.getBounds2D().getMaxX() / 2 * targetScaleFactor, getReferencePoint().getY() + text.getBounds2D().getHeight() + layoutReference.getTargetBox().getHeight() / 2);
		else
			targetPosition.setLocation(new Point2D.Float());
		magneticPosition.setLocation(targetPosition);
		setUpdatable();
		refreshReferencingElements();
		if(isMagnetic)
			updateMagnetics();
		setUpdatable();
	}
	
	@Override
	protected Rectangle2D getBox(Point2D position, float scaleFactor)
	{
		Shape text = shapes.iterator().next();
		float w = (float)text.getBounds2D().getWidth() * scaleFactor;
		float h = (float)text.getBounds2D().getHeight() * scaleFactor;
		float x = (float)position.getX();
		float y = (float)position.getY();
		return new Rectangle2D.Float(x, y - h, w, h);
	}
	
	@Override
	public String toString()
	{
		Rectangle2D box = this.getTargetBox();
		return "GLabel[" + labelText + "|" + box.getMinX() + " " + box.getMaxX() + " " + box.getMinY() + " " + box.getMaxY() + "]";
	}
}
