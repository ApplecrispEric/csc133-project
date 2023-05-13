package com.mycompany.a4;

import com.codename1.ui.Graphics;
import com.codename1.ui.Transform;

/**
 * Movable is the abstract classes that represents GameObjects whose location changes
 * dynamically at runtime depending on the game state.
 * 
 * @author Eric Brown
 *
 */
public abstract class Movable extends GameObject {
	private static final int UNIT_CIRCLE_DEGREES = 360;
	private int heading;
	private int speed;
	
	/**
	 * Base constructor for movable objects.
	 * 
	 * @param initialSpeed		the starting speed for the object
	 * @param initialHeading	the starting direction the object travels in
	 */
	public Movable(int size, int color, float locationX, float locationY, int initialSpeed, int initialHeading) {
		super(size, color, locationX, locationY);
		this.speed = initialSpeed;
		this.heading = initialHeading;
	}
	
	/**
	 * Updates the objects location internally by a single time unit based on its
	 * current heading and speed.
	 */
	public void move(long milliseconds) {
		double headingInRadians = Math.toRadians(90 - heading);
		float deltaX = (float)(Math.cos(headingInRadians) * this.speed) * (float) milliseconds / 1000;
		float deltaY = (float)(Math.sin(headingInRadians) * this.speed) * (float) milliseconds / 1000;
		
		this.setLocationX(getLocationX() + deltaX);
		this.setLocationY(getLocationY() + deltaY);
	}
	
	/**
	 * Setter for speed.
	 * 
	 * @param speed		the new speed for the object
	 */
	public void setSpeed(int speed) {
		if (speed < 0) {
			speed = 0;
		}
		this.speed = speed;
	}
	
	/**
	 * Getter for speed.
	 * 
	 * @return			the object's current speed
	 */
	public int getSpeed() {
		return this.speed;
	}
	
	/**
	 * Setter for heading.
	 * 
	 * @param heading	the new heading for the object
	 */
	public void setHeading(int heading) {
		this.heading = ((heading % UNIT_CIRCLE_DEGREES) + 360) % UNIT_CIRCLE_DEGREES;  // Keeps heading within [0, 360) degrees.
		this.setRotation((float)Math.toRadians(heading));
	}
	
	/**
	 * Getter for heading.
	 * 
	 * @return		the object's current heading
	 */
	public int getHeading() {
		return this.heading;
	}
	
	
	/**
	 * @return			a string representing the object's current state
	 */
	public String toString() {
		String substate = super.toString();
		return "[Moveable] " + substate + ", Heading: " + this.heading + ", Speed: " + this.speed;
	}
}
