package com.mycompany.a4;


/**
 * ISteerable is an interface that movable objects can implement which allows clients to change their
 * steering direction by small increments which ultimately affects their heading.
 * 
 * @author Eric Brown
 */
public interface ISteerable {
	
	/**
	 * Change the steering direction slightly to the right (clockwise).
	 */
	public abstract void steerRight();
	
	/**
	 * Change the steering direction slightly to the left (counter-clockwise).
	 */
	public abstract void steerLeft();
}
