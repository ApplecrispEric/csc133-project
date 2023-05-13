package com.mycompany.a4;

import com.codename1.ui.Graphics;
import com.codename1.ui.geom.Point;

/**
 * Fixed represents game objects whose location cannot be changed after they have been instantiated.
 * 
 * @author Eric Brown
 */
public abstract class Fixed extends GameObject implements ISelectable {
	private boolean selected = false;
	
	/**
	 * Base constructor for fixed objects.
	 * @param size				size of the object
	 * @param color				color of the object
	 * @param locationX			permanent x coordinate of the object
	 * @param locationY			permanent y coordinate of the object
	 */
	public Fixed(int size, int color, float locationX, float locationY) {
		super(size, color, locationX, locationY);
	}
	
	
	/**
	 * @return			a string representing the objects current state
	 */
	@Override
	public String toString() {
		String state = super.toString();
		return "[Fixed] " + state;
	}
	
	/**
	 * Setter for selected.
	 */
	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	/**
	 * Getter for selected.
	 */
	@Override
	public boolean isSelected() {
		return this.selected;
	}
	
	/**
	 * Returns true if the given point is within the bounds of the object.
	 */
	@Override
	public boolean contains(Point point) {
		int x = point.getX();
		int y = point.getY();
		float posX = this.getLocationX();
		float posY = this.getLocationY();
		float radius = this.getSize() / 2;
		
		if (x < posX - radius || x > posX + radius) {
			return false;
		}
		
		if (y < posY - radius || y > posY + radius) {
			return false;
		}
		
		return true;
	}
}