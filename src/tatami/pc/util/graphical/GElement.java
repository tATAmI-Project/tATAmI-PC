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
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Set;

import tatami.core.util.logging.Unit;


public class GElement extends Unit
{
	public enum ReferenceType {
		CENTER, TOPLEFT, BOTTOMLEFT
	}
	
	
	final float		UPDATE_SPEED		= 0.5f;
	final float		SCALE_UPDATE_SPEED	= UPDATE_SPEED;
	final float		THRESHOLD			= 0.001f;
	final double	MAGNETIC_THRESHOLD	= 0.1f;
	final double	MAGNETIC_DAMPENING	= 0.5f;
	
	// relations
	GCanvas			uplink				= null;
	/**
	 * the reference {@link GElement} relative to which the <code>layoutPosition</code> is set.
	 */
	GElement		layoutReference		= null;
	/**
	 * specifies to what part of the reference {@link GElement} the layout is relative to; as in to its center, to its top left corner, etc
	 */
	ReferenceType	layoutReferenceType	= null;
	/**
	 * the {@link GElement} instances having this instance as <code>layoutReference</code>
	 */
	Set<GElement>	referencingElements	= new HashSet<GElement>();
	
	// positioning & parameters
	/**
	 * displayed GCanvas(uplink)-relative position; calculated to smoothly reach <code>magneticPosition</code>
	 */
	Point2D			currentPosition		= new Point2D.Float();
	/**
	 * relative to the uplink; adjusted by <code>magnetic()</code>
	 */
	Point2D			magneticPosition	= new Point2D.Float();
	/**
	 * relative to the uplink
	 */
	Point2D			targetPosition		= new Point2D.Float();
	/**
	 * relative to the reference; set by the exterior by means of <code>setMoveTo()</code>
	 */
	Point2D			layoutPosition		= new Point2D.Float();
	
	float			targetScaleFactor	= 1.0f;
	float			currentScaleFactor	= 1.0f;
	int				scaleLevel			= 0;						// (target)
																	
	// Magnetic support
	Set<GElement>	magneticNeighbors	= new HashSet<GElement>();
	boolean			isMagnetic			= false;
	
	// shaping, drawing
	boolean			doDraw				= true;
	float			size				= 10;
	Set<Shape>		shapes				= new HashSet<Shape>();
	
	// debugging
	Object			represented			= null;
	Point2D			force				= new Point2D.Float();
	
	public GElement()
	{
		super(new UnitConfigData().setName(DEFAULT_UNIT_NAME));
		createShapes();
	}
	
	/**
	 * setter; external or relationship with the reference
	 * 
	 * @param ref
	 *            : the layout reference {@link GElement}
	 * @param type
	 *            : the type of the reference (says how to calculate the reference point)
	 * @return the element itself, for chained calls
	 */
	public GElement setReference(GElement ref, ReferenceType type)
	{
		this.layoutReferenceType = type;
		this.layoutReference = ref;
		recalculateTarget();
		return this;
	}
	
	/**
	 * setter; external; sets the layout position (relative to the <code>layoutReference</code> for this element
	 * 
	 * @param layoutPosition
	 * @return
	 */
	public GElement setMoveTo(Point2D layoutPosition)
	{
		this.layoutPosition.setLocation(layoutPosition);
		recalculateTarget();
		return this;
	}
	
	/**
	 * setter; external; sets the amount to scale the element and affects the <i>target</i>
	 * 
	 * @param amount
	 * @return the element itself, for chained calls
	 */
	public GElement setScaleDelta(int amount)
	{
		scaleLevel -= amount;
		targetScaleFactor = (float)Math.pow(2, (scaleLevel / 5f));
		recalculateTarget();
		
		for(GElement sub : referencingElements)
			if((this instanceof GContainer) || !(sub instanceof GConnector))
				sub.setScaleDelta(amount);
		
		return this;
	}
	
	/**
	 * Setter; sets relation with the <code>uplink</code> canvas
	 * 
	 * @param canvas
	 *            : the {@link GCanvas}
	 * @return the element itself, for chained calls
	 */
	public GElement setCanvas(GCanvas canvas)
	{
		if(uplink != null)
			uplink.remove(this);
		uplink = canvas;
		canvas.add(this);
		return this;
	}
	
	/**
	 * relationship with uplink (to uplink); set itself to be called by the canvas updating mechanism
	 */
	protected void setUpdatable()
	{
		if(uplink != null)
			uplink.addUpdatable(this);
	}
	
	/**
	 * relationship with reference; instructs whether to draw this element or not (for the case where the container is displayed too small)
	 * 
	 * @param doDraw
	 * @return the element itself, for chained calls
	 */
	protected GElement setDrawable(boolean doDraw)
	{
		this.doDraw = doDraw;
		for(GElement sub : referencingElements)
			sub.setDrawable(this.doDraw);
		if(this.doDraw)
			recalculateTarget();
		return this;
	}
	
	public GElement setRepresented(Object repr)
	{
		this.represented = repr;
		return this;
	}
	
	/**
	 * relationship with uplink; called by the update mechanism of the canvas to instruct the recalculation of <i>current</i> to move towards <i>target</i>
	 * 
	 * @return <code>true</code> if <i>current</i> has reached <i>target</i> (i.e. no more updates needed)
	 */
	protected boolean update()
	{
		if(!doDraw)
			return true;
		double dx = magneticPosition.getX() - currentPosition.getX();
		double dy = magneticPosition.getY() - currentPosition.getY();
		float ds = targetScaleFactor - currentScaleFactor;
		if(Math.abs(dx) > THRESHOLD || Math.abs(dy) > THRESHOLD || Math.abs(ds) > THRESHOLD)
		{
			currentPosition.setLocation(currentPosition.getX() + dx * UPDATE_SPEED, currentPosition.getY() + dy * UPDATE_SPEED);
			currentScaleFactor += ds * SCALE_UPDATE_SPEED;
			// refreshReferencingElements();
			return false;
		}
		else
			return true;
	}
	
	/**
	 * refreshes the element's shapes;
	 */
	protected void createShapes()
	{
		shapes.clear();
		shapes.add(new Ellipse2D.Float(-size / 2, -size / 2, size, size));
	}
	
	/**
	 * relationship with the uplink; instructs the drawing of the element's shapes in <i>current</i> state
	 * 
	 * @param g
	 */
	protected void draw(Graphics2D g)
	{
		if(!doDraw)
			return;
		AffineTransform save = g.getTransform();
		
		g.translate(currentPosition.getX(), currentPosition.getY());
		g.scale(currentScaleFactor, currentScaleFactor);
		g.setStroke(new BasicStroke(1f / currentScaleFactor));
		
		drawShapes(g);
		
		g.setTransform(save);
	}
	
	/**
	 * internal; actually draws the shapes; it is meant to be overridden for drawing particular shapes but avoiding rewriting the positioning/scaling transform
	 * 
	 * @param g
	 */
	protected void drawShapes(Graphics2D g)
	{
		for(Shape shape : shapes)
			g.draw(shape);
	}
	
	/**
	 * relationship with uplink (from uplink); used to see if zoom point or selection point corresponds to this element; considers <i>current</i> state
	 * 
	 * @param pt
	 *            : {@link Point2D} to test if contained in the shapes of this element; in canvas-relative coordinates
	 * @return : <code>true</code> if point is contained
	 */
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
			if(t.createTransformedShape(s).contains(pt))
				return true;
		}
		return false;
	}
	
	/**
	 * setter; relationship with referencing elements; sets reference relationship between elements, with this element as parent
	 * 
	 * @param el
	 *            : the child {@link GElement}
	 * @return the element itself, for chained calls
	 */
	protected GElement addReferencingElement(GElement el)
	{
		referencingElements.add(el);
		el.setReference(this, ReferenceType.CENTER);
		return this;
	}
	
	/**
	 * relationship with referencing elements; instructs referencing elements that the element's <i>target</i> has changed.
	 */
	protected void refreshReferencingElements()
	{
		for(GElement sub : referencingElements)
			sub.refreshReference();
	}
	
	/**
	 * internal; related to the reference
	 * 
	 * @return the point that represents the reference for the layout
	 */
	protected Point2D getReferencePoint()
	{
		if(layoutReference == null)
			return new Point2D.Float();
		Rectangle2D refBox = layoutReference.getTargetBox();
		switch(layoutReferenceType)
		{
		case BOTTOMLEFT:
			return new Point2D.Float((float)refBox.getX(), (float)refBox.getMaxY());
		case TOPLEFT:
			return new Point2D.Float((float)refBox.getX(), (float)refBox.getY());
		case CENTER:
			return new Point2D.Float((float)refBox.getCenterX(), (float)refBox.getCenterY());
		}
		return new Point2D.Float();
	}
	
	/**
	 * relationship with the reference; called when the reference has changed. will instruct the recalculation of the <i>target</i>
	 */
	protected void refreshReference()
	{
		recalculateTarget();
		refreshReferencingElements();
	}
	
	/**
	 * internal; recalculates <i>target</i> state based on <code>layoutPosition</code>, reference, <code>targetScale</code>, etc
	 */
	protected void recalculateTarget()
	{
		if(!doDraw)
			return;
		float refScale = 1.0f;
		if(layoutReference != null)
			refScale = layoutReference.targetScaleFactor;
		targetPosition.setLocation(this.layoutPosition.getX() * refScale + getReferencePoint().getX(), this.layoutPosition.getY() * refScale + getReferencePoint().getY());
		magneticPosition.setLocation(targetPosition);
		refreshReferencingElements();
		if(isMagnetic)
			updateMagnetics();
		setUpdatable();
	}
	
	/**
	 * internal, relationship with uplink (to); called when <i>target</i> has been modified.
	 */
	protected void updateMagnetics()
	{
		if(!isMagnetic)
			return;
		uplink.updateMagnetics(this);
	}
	
	/**
	 * relationship with uplink (from); called to update <i>target</i> depending on neighbors.
	 * 
	 * @param magneticNeighbors
	 *            : the updated magnetic neighbor
	 */
	protected void magneticUpdate(GElement magneticNeighbor)
	{
		if(!isMagnetic || !doDraw)
			return;
		magneticNeighbors.add(magneticNeighbor);
		
		Point2D specForce = getSpecificMagneticVector();
		double forceX = specForce.getX();
		double forceY = specForce.getY();
		
		Rectangle2D box = this.getTargetBox();
		for(GElement mag : magneticNeighbors)
		{
			Rectangle2D box2 = mag.getTargetBox();
			if(box.intersects(box2))
			{
				log.trace("considering overlap with: " + mag);
				Rectangle2D intersection = box.createIntersection(box2);
				double directionX = intersection.getCenterX() - box.getCenterX();
				double directionY = intersection.getCenterY() - box.getCenterY();
				double distance = Math.sqrt(directionX * directionX + directionY * directionY);
				// normalize
				directionX /= distance;
				directionY /= distance;
				// calculate force
				double force = (intersection.getWidth() + intersection.getHeight());
				forceX -= force * directionX;
				forceY -= force * directionY;
			}
		}
		forceX += targetPosition.getX() - magneticPosition.getX();
		forceY += targetPosition.getY() - magneticPosition.getY();
		
		log.trace("forces: " + forceX + " " + forceY + " " + this.toString() + "neighbors: " + magneticNeighbors);
		
		force.setLocation(forceX, forceY);
		
		if((forceX > MAGNETIC_THRESHOLD) || (forceY > MAGNETIC_THRESHOLD))
		{ // do move
			magneticPosition.setLocation(magneticPosition.getX() + forceX * MAGNETIC_DAMPENING, magneticPosition.getY() + forceY * MAGNETIC_DAMPENING);
			updateMagnetics();
			setUpdatable();
		}
	}
	
	protected Point2D getSpecificMagneticVector()
	{
		return new Point2D.Double();
	}
	
	protected Rectangle2D getBox(Point2D position, float scaleFactor)
	{
		float x = (float)position.getX();
		float y = (float)position.getY();
		float returnedSize = size * scaleFactor;
		return new Rectangle2D.Float(x - returnedSize / 2, y - returnedSize / 2, returnedSize, returnedSize);
	}
	
	/**
	 * internal or relationship with referencing elements; gets the <i>target</i> box around the element.
	 * 
	 * @return the box.
	 */
	protected Rectangle2D getTargetBox()
	{
		return getBox(targetPosition, targetScaleFactor);
	}
	
	protected Rectangle2D getMagneticBox()
	{
		return getBox(magneticPosition, targetScaleFactor);
	}
	
	/**
	 * internal or relationship with referencing elements; gets the <i>current</i> box around the element.
	 * 
	 * @return the box.
	 */
	protected Rectangle2D getCurrentBox()
	{
		return getBox(currentPosition, currentScaleFactor);
	}
	
	/**
	 * relationship with referencing elements (i.e. {@link GConnector} instances); works with <i>current</i> state
	 * 
	 * @param to
	 * @return intersection of a ray towards <code>to</code> from the center of this element's box
	 */
	protected Point2D intersectRay(Point2D to)
	{
		double x0 = this.getCurrentBox().getCenterX();
		double y0 = this.getCurrentBox().getCenterY();
		
		double dx = to.getX() - x0;
		double dy = to.getY() - y0;
		
		double angle;
		if(dx == 0)
			if(dy > 0)
				angle = Math.PI / 2;
			else
				angle = -Math.PI / 2;
		else
			angle = Math.atan(dy / dx);
		if(dx < 0)
			angle += Math.PI;
		
		double xi = x0 + size / 2 * currentScaleFactor * Math.cos(angle);
		double yi = y0 + size / 2 * currentScaleFactor * Math.sin(angle);
		
		return new Point2D.Float((float)xi, (float)yi);
	}
	
	@Override
	public String toString()
	{
		Rectangle2D box = this.getTargetBox();
		if(represented != null)
			return "GElement[" + represented.toString() + "|" + box.getMinX() + " " + box.getMaxX() + " " + box.getMinY() + " " + box.getMaxY() + "]";
		else
			return "GElement[-|" + box.getMinX() + " " + box.getMaxX() + " " + box.getMinY() + " " + box.getMaxY() + "]";
	}
}
