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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

public class GContainer extends GElement
{
	final float CONTENT_DRAW_SCALE_THERSHOLD = .5f;
	
	float			width					= 10;
	float			height					= 10;
	
	ReferenceType	containerReferenceType	= ReferenceType.CENTER;
	
	public GContainer()
	{
		super();
		this.isMagnetic = false;
	}
	
	public GContainer setSize(float w, float h)
	{
		width = w;
		height = h;
		
		createShapes();
		
		return this;
	}
	
	public GContainer setReferenceType(ReferenceType type)
	{
		this.containerReferenceType = type;
		return this;
	}
	
	@Override
	public GContainer setScaleDelta(int amount)
	{
		super.setScaleDelta(amount);
		if(targetScaleFactor < CONTENT_DRAW_SCALE_THERSHOLD)
			for(GElement sub : referencingElements)
				sub.setDrawable(false);
		else
			for(GElement sub : referencingElements)
				sub.setDrawable(true);
		return this;
	}
	
	/**
	 * becomes setter
	 */
	@Override
	public GContainer addReferencingElement(GElement el)
	{
		super.addReferencingElement(el);
		el.setReference(this, containerReferenceType);
		return this;
	}
	
	@Override
	protected void createShapes()
	{
		float cornersize = Math.min(width, height) / 10;
		shapes.clear();
		shapes.add(new RoundRectangle2D.Float(-width / 2, -height / 2, width, height, cornersize, cornersize));
	}
	
	@Override
	public Rectangle2D getBox(Point2D position, float scaleFactor)
	{
		float x = (float)position.getX();
		float y = (float)position.getY();
		return new Rectangle2D.Float(x - width * scaleFactor / 2, y - height * scaleFactor / 2, width * scaleFactor, height * scaleFactor);
	}
}
