package com.mycompany.a4;

import com.codename1.ui.Graphics;
import com.codename1.ui.Transform;

/**
 * GameObject is the abstract class that represents an in-game entity which are
 * physical objects that have some impact on the actual game play.
 * 
 * @author Eric Brown
 *
 */
public abstract class GameObject implements ICollider, IDrawable {
	private int size;
	private int color;
	
	private Transform myTranslate = Transform.makeIdentity();
	private Transform myRotate = Transform.makeIdentity();
	private Transform myScale = Transform.makeIdentity();
	
	
	/**
	 * Constructor for GameObject.
	 * 
	 * @param size			size of object
	 * @param color			color of object
	 * @param locationX		x coordinate of object's location
	 * @param locationY		y coordinate of object's location
	 */
	public GameObject(int size, int color, float locationX, float locationY) {
		this.size = size;
		this.color = color;
		translate(locationX, locationY);
	}
	
	/**
	 * Moves the objects translation transform
	 * @param x				amount to move in the x direction
	 * @param y				amount to move in the y direction
	 */
	public void translate(float x, float y) {
		this.myTranslate.translate(x, y);
	}
	
	/**
	 * Rotates the objects rotation transform
	 * @param angle
	 */
	public void rotate(float angle) {
		this.myRotate.rotate(angle, 0, 0);
	}
	
	/**
	 * Sets the objects rotation to a specified angle
	 * @param angle				angle to rotate to
	 */
	public void setRotation(float angle) {
		Transform rotation = Transform.makeIdentity();
		rotation.setRotation(-angle, -size/5, size);
		this.myRotate = rotation;
	}
	
	/**
	 * Scales the objects scale transform
	 * 
	 * @param x					x scale factor
	 * @param y					y scale factor
	 */
	public void scale(float x, float y) {
		this.myTranslate.scale(x, y);
	}
	
	/**
	 * Applies the local translation to the graphics object g.
	 * @param g						graphics object to apply translation to
	 */
	public void applyTranslation(Graphics g) {
		Transform gXform = Transform.makeIdentity();
		g.getTransform(gXform);
		
		gXform.translate(myTranslate.getTranslateX(), myTranslate.getTranslateY());
		
		g.setTransform(gXform);
	}
	
	/**
	 * Applies the local rotation to the graphics object g.
	 * @param g						graphics object to apply rotation to
	 */
	public void applyRotation(Graphics g) {
		Transform gXform = Transform.makeIdentity();
		g.getTransform(gXform);
		
		gXform.concatenate(myRotate);
		
		g.setTransform(gXform);
	}
	
	/**
	 * Applies the local scale to the graphics object g.
	 * @param g						graphics object to apply scale to
	 */
	public void applyScale(Graphics g) {
		Transform gXform = Transform.makeIdentity();
		g.getTransform(gXform);
		
		gXform.concatenate(myScale);
		
		g.setTransform(gXform);
	}
	
	/**
	 * Getter for size.
	 * 
	 * @return		current size of the object
	 */
	public int getSize() {
		return this.size;
	}
	
	/**
	 * Setter for locationX.
	 * 
	 * @param 		new X coordinate for the object in game space
	 */
	public void setLocationX(float x) {
		float dx = x - getLocationX();
		translate(dx, 0);
	}
	
	/**
	 * Setter for locationY.
	 * 
	 * @param y		new Y coordinate for the object in game space
	 */
	public void setLocationY(float y) {
		float dy = y - getLocationY();
		translate(0, dy);
	}
	
	/**
	 * Getter for locationX.
	 * 
	 * @return		current X coordinate of the object
	 */
	public float getLocationX() {
		return this.myTranslate.getTranslateX();
	}
	
	/**
	 * Getter for locationY.
	 * 
	 * @return		current Y coordinate of the object
	 */
	public float getLocationY() {
		return this.myTranslate.getTranslateY();
	}
	
	
	/**
	 * Getter for color.
	 * 
	 * @return		current object color
	 */
	public int getColor() {
		return this.color;
	}
	
	/**
	 * Setter for color.
	 * 
	 * @param color		new color to set object to
	 */
	public void setColor(int color) {
		this.color = color;
	}
	
	/**
	 * @return			a string representing the object's current state
	 */
	public String toString() {
		return "[GameObject] Location: (" + this.getLocationX() + ", " + this.getLocationY() + ") "
				+ "Color: " + this.color + ", Size: " + this.size;
	}
	
	/**
	 * Checks for collisions between two game objects (squares).
	 */
	@Override
	public boolean collidesWith(GameObject otherObject) {
		float thisLeft = this.getLocationX() - this.getSize() / 2;
		float otherLeft = otherObject.getLocationX() - otherObject.getSize() / 2;
		float thisRight = thisLeft + this.getSize();
		float otherRight = otherLeft + otherObject.getSize();
		
		// Check for lack of horizontal overlap for the objects.
		if (thisLeft > otherRight) {
			return false;
		}
		else if (thisRight < otherLeft) {
			return false;
		}
		
		float thisTop = this.getLocationY() + this.getSize() / 2;
		float otherTop = otherObject.getLocationY() + otherObject.getSize() / 2;
		float thisBottom = thisTop - this.getSize();
		float otherBottom = otherTop - otherObject.getSize();
		
		// Check for lack of vertical overlap for the objects.
		if (thisTop < otherBottom) {
			return false;
		}
		else if (thisBottom > otherTop) {
			return false;
		}
		
		// Otherwise, there must be overlap between the objects, hence they collide.
		return true;
	}
	
	/**
	 * Default behavior to handle the collision is to do nothing.
	 */
	@Override
	public void handleCollision(GameObject otherObject) {}
}
