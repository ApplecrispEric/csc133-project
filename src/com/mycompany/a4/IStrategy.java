package com.mycompany.a4;


/**
 * Interface defined for the strategy design pattern. Robots can use implementations of this
 * to compete against the player.
 * 
 * @author Eric Brown
 */
public interface IStrategy {
	/**
	 * Invoke the strategy to perform an action.
	 */
	public void invoke();
	
	/**
	 * @return				a string representation of the strategy
	 */
	public String toString();
}
