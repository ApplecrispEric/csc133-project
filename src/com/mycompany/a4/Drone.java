package com.mycompany.a4;
import java.util.Random;

import com.codename1.ui.Graphics;
import com.codename1.ui.Transform;
import com.codename1.ui.geom.Point;


/**
 * Drones are obstacles for the player to avoid. They move around while changing their direction in 
 * small random increments. They may not go out of the game world boundaries and are their heading is reflected
 * in order to prevent them should they attempt to move out of bounds.
 * 
 * @author Eric Brown
 *
 */
public class Drone extends Movable {
	private static final int MAX_HEADING_INCREMENT = 5;
	public static final int DAMAGE_TO_OTHER_ENTITIES = 20;
	
	private int worldBoundX;
	private int worldBoundY;
	
	/**
	 * Constructor for Drone.
	 */
	public Drone(int size, int color, float locationX, float locationY, int initialSpeed, int initialHeading, int worldBoundX, int worldBoundY) {
		super(size, color, locationX, locationY, initialSpeed, initialHeading);
		this.worldBoundX = worldBoundX;
		this.worldBoundY = worldBoundY;
	}
	
	
	/**
	 * Moves the Drone's location by one time unit. Overridden, to provide random adjustments to
	 * the Drone's heading and enforce the boundary rules.
	 */
	@Override
	public void move(long milliseconds) {
		int heading = getHeading();
		Random generator = new Random(System.currentTimeMillis());
		
		// Change the heading by a small, random amount.
		int headingIncrement = generator.nextInt(MAX_HEADING_INCREMENT);
		boolean negative = generator.nextBoolean();
		if (negative) {
			headingIncrement = -headingIncrement;
		}
		
		this.hiddenSetHeading(heading + headingIncrement);
		super.move(milliseconds);
		
		// Drone collided with world edge horizontally, reflect heading.
		if (getLocationX() >= this.worldBoundX || getLocationX() <= 0) {
			int reflectedHeadingX = -getHeading();
			hiddenSetHeading(reflectedHeadingX);
		}
		
		// Drone collided with world edge vertically, reflect heading.
		if (getLocationY() >= this.worldBoundY || getLocationY() <= 0) {
			int reflectedHeadingY = 180 - getHeading();
			hiddenSetHeading(reflectedHeadingY);
		}
	}
	
	/**
	 * Overridden to prevent the Drone's heading to be changed by clients (not steerable).
	 */
	@Override
	public void setHeading(int newHeading) {}
	
	
	/**
	 * Used internally to randomize the Drone's heading.
	 */
	private void hiddenSetHeading(int newHeading) {
		super.setHeading(newHeading);
	}
	
	/**
	 * Overridden to prevent the Drone's color to be changed after instantiation.
	 */
	@Override
	public void setColor(int color) {}  // Do nothing: cannot change a Drone's color.
	
	/**
	 * @return			a string representing the drone's current state
	 */
	public String toString() {
		String substate = super.toString();
		return "[Drone] " + substate;
	}
	
	/**
	 * Draws the drone on the map.
	 */
	@Override
	public void draw(Graphics g) {
		float x = -this.getSize() / 2;
		float y = -this.getSize() / 2;
				
		Transform oldXform = Transform.makeIdentity();
		g.getTransform(oldXform);
		
		applyTranslation(g);
		applyRotation(g);
		applyScale(g);
		g.setColor(this.getColor());
		
		int[] xPoints = {(int) x, (int) (x + this.getSize()), (int) ((2 * x + this.getSize()) / 2)};  // X locations of triangle points.
		int[] yPoints = {(int) y, (int) y, (int) (y + this.getSize())};  // Y locations of triangle points.
		
		g.setColor(this.getColor());
		g.drawPolygon(xPoints, yPoints, 3);
		
		g.setTransform(oldXform);
	}
}
