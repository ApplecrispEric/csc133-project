package com.mycompany.a4;

/**
 * Interface is used by GameObject to handle collisions between other game objects.
 * 
 * @author Eric Brown
 */
public interface ICollider {
	/**
	 * @param otherObject			object to check for collision
	 * @return						true if colliding with otherObject, false otherwise
	 */
	public boolean collidesWith(GameObject otherObject);
	
	/**
	 * Handles the collision with another object.
	 * 
	 * @param otherObject				object that self is colliding with
	 */
	public void handleCollision(GameObject otherObject);
}
