package com.mycompany.a4;

import java.util.Observer;
import java.util.Observable;

import com.codename1.charts.util.ColorUtil;
import com.codename1.ui.Container;
import com.codename1.ui.Graphics;
import com.codename1.ui.Transform;
import com.codename1.ui.geom.Point;


/**
 * MapView represents a map of the current game state to be displayed in the center of
 * the screen. It observes the game world to update the map accordingly.
 * 
 * @author Eric Brown
 */
public class MapView extends Container implements Observer {
	private GameWorld gw;
	private Transform theVTM = Transform.makeIdentity();
	private Transform inverseVTM = Transform.makeIdentity();
	
	private float winLeft = 0;
	private float winBottom = 0;
	private float winRight = 0;
	private float winTop = 0;
	
	private final float MAX_SIZE_FACTOR = 3;
	private final float HORIZONTAL_ZOOM_INCREMENT = 5;
	private float maxWinWidth = 0;
	private float minWinWidth = 0;
	private float initialWidth = 0;
	private float currentScaleFactor = 1;
	private float previousScaleFactor = currentScaleFactor;
	
	private float initialPointXClickLocation = 0;
	private float initialPointYClickLocation = 0;
	
	/**
	 * Updates the map.
	 */
	@Override
	public void update(Observable observable, Object data) {
		if (this.gw == null) {  // Grab a reference to the GameWorld if the map does not already have one, used in the call to paint.
			this.gw = (GameWorld) observable;
		}
		this.repaint();
	}
	
	/**
	 * Initializes the view's boundaries after the screen has been drawn.
	 */
	public void initializeBoundaries() {
		winRight = getWidth() / currentScaleFactor;
		winTop = getHeight() / currentScaleFactor;
		
		initialWidth = winRight - winLeft;
		
		maxWinWidth = initialWidth * MAX_SIZE_FACTOR;
		minWinWidth = initialWidth / MAX_SIZE_FACTOR;
	}
	
	/**
	 * Draws the map (and in-game objects) on the screen.
	 * 
	 * Note: the VTM transformation pipeline was sourced from Lecture 12 A from CSC 133 (Kwan).
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Transform oldXform = Transform.makeIdentity();
		g.getTransform(oldXform);
		
		Transform worldToND = buildWorldToNDXform();		
		theVTM = buildNDToDisplayXform(this.getWidth(), this.getHeight());
		theVTM.concatenate(worldToND);
		
		inverseVTM = buildWorldToNDXform();
		inverseVTM.concatenate(buildNDToDisplayXform(this.getWidth(), this.getHeight()));
		
		Transform gXform = Transform.makeIdentity();
		g.getTransform(gXform);
		gXform.translate(getAbsoluteX(),getAbsoluteY());
		gXform.concatenate(theVTM);
		gXform.translate(-getAbsoluteX(),-getAbsoluteY());
		g.setTransform(gXform);
				
		IIterator iterator = gw.getObjectCollectionIterator();

		while (iterator.hasNext()) {
			GameObject object = iterator.getNext();
			
			if (object instanceof IDrawable) {
				((IDrawable) object).draw(g);
			}
		}
		
		g.setTransform(oldXform);
	}
	
	/**
	 * Converts world transform to a normalized device coordinate transform.
	 * 
	 * Note: this function was sourced from Lecture 12 A from CSC 133 (Kwan). 
	 * @return			the normalized device transform
	 */
	private Transform buildWorldToNDXform() {
		float winWidth = winRight - winLeft;
		float winHeight = winTop - winBottom;
		Transform tmpXfrom = Transform.makeIdentity();
		tmpXfrom.scale((1 / winWidth) , (1 / winHeight));
		tmpXfrom.translate(-winLeft,-winBottom);
		return tmpXfrom;
	}
	
	/**
	 * Converts a normalized device coordinate transform to display transform.
	 * 
	 * Note: this function was sourced from Lecture 12 A from CSC 133 (Kwan). 
	 * @return			the display transform
	 */
	private Transform buildNDToDisplayXform (float displayWidth, float displayHeight) {
		Transform tmpXfrom = Transform.makeIdentity();
		tmpXfrom.translate(0, displayHeight);
		tmpXfrom.scale(displayWidth, -displayHeight);
		return tmpXfrom;
	}

	
	/**
	 * Handles object selection.
	 */
	@Override
	public void pointerPressed(int x, int y) {
		super.pointerPressed(x, y);
		
		initialPointXClickLocation = x;
		initialPointYClickLocation = y;
		
		int worldX = (int)((x - getAbsoluteX()) * currentScaleFactor + winLeft) + getAbsoluteX();
		int worldY = (int)((-(y - getAbsoluteY())) * currentScaleFactor + winTop) + getAbsoluteY();
				
		if (!Game.isPaused()) {
			return;
		}
		
		worldY -= 110;
		if (gw.isMovingObject()) {
			Fixed movingObject = getSelectedObject();
			
			if (movingObject != null) {
				movingObject.setLocationX(worldX);
				movingObject.setLocationY(worldY);
				movingObject.setSelected(false);
			}
			
			gw.setMovingObject(false);
			repaint();
			return;
		}
		
		IIterator iterator = gw.getObjectCollectionIterator();
		Point point = new Point(worldX, worldY);
		
		while (iterator.hasNext()) {
			GameObject object = iterator.getNext();
			
			if (object instanceof Fixed) {
				Fixed selectable = (Fixed) object;
				boolean selected = selectable.contains(point);
				
				if (selected) {
					gw.deselectAllObjects();
				}
				selectable.setSelected(selected);
			}
		}
		
		this.repaint();
	}
	
	/**
	 * Called when a pointer drag event occurs (panning).
	 */
	@Override
	public void pointerDragged(int x, int y) {
		float dx = -(initialPointXClickLocation - x);
		float dy = initialPointYClickLocation - y;
		
		dx *= currentScaleFactor;
		dy *= currentScaleFactor;
		
		panHorizontal(dx);
		panVertical(dy);
		
		initialPointXClickLocation = x;
		initialPointYClickLocation = y;
	}
	
	/**
	 * Shifts the viewport horizontally.
	 * @param dx			shift amount
	 */
	public void panHorizontal(float dx) {
		winLeft -= dx;
		winRight -= dx;
	}
	
	/**
	 * Shifts the viewport vertically.
	 * @param dy			shift amount	
	 */
	public void panVertical(float dy) {
		winTop -= dy;
		winBottom -= dy;
	}
	
	/**
	 * Called after a pinch event (zooming).
	 */
	@Override
	public boolean pinch(float scale) {
		zoom(scale);
		return true;
	}
	
	/**
	 * Zooms the viewport by the scale factor detected after a pinch event.
	 * @param scale
	 */
	public void zoom(float scale) {
		if (scale == 1) {
			previousScaleFactor = 1;
			return;
		}
		
		float winWidth = winRight - winLeft;
		float winHeight = winTop - winBottom;
		
		float scaleAmount = HORIZONTAL_ZOOM_INCREMENT / winWidth;
		float dx = winWidth * scaleAmount;
		float dy = winHeight * scaleAmount;
		
		if (scale > previousScaleFactor) {  // Zooming in.
			dx *= -1;
			dy *= -1;
		}
		
		previousScaleFactor = scale;
		
		if (winWidth + dx >= maxWinWidth || winWidth + dx <= minWinWidth) {
			return;
		}
		
		winRight += dx;
		winLeft -= dx;
		winTop += dy;
		winBottom -= dy;
		
		winWidth = winRight - winLeft;
		currentScaleFactor = winWidth / initialWidth;  // Scale factor for x axis (equivalent to y scale factor).
	}
	
	
	/**
	 * @return					a object that is currently selected if it exists
	 */
	public Fixed getSelectedObject() {		
		IIterator iterator = gw.getObjectCollectionIterator();
		
		while (iterator.hasNext()) {
			GameObject current = iterator.getNext();
			if (current instanceof Fixed) {
				Fixed fixed = (Fixed) current;
				
				if (fixed.isSelected()) {
					return fixed;
				}
			}
		}
		return null;
	}
}
