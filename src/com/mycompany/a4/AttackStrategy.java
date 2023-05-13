package com.mycompany.a4;


/**
 * This strategy class makes NPRs attack the player by driving straight got the player robot to damage it
 * instead of competing to win.
 * 
 * @author Eric Brown
 */
public class AttackStrategy implements IStrategy {
	NonPlayerRobot robot;
	
	/**
	 * Constructor for AttackStrategy.
	 * 
	 * @param robot
	 */
	public AttackStrategy(NonPlayerRobot robot) {
		this.robot = robot;
	}
	
	/**
	 * Performs the strategy by moving the robot towards the player. Should be called once per in-game tick.
	 */
	@Override
	public void invoke() {
		robot.steerTowardsIntendedLocation(PlayerRobot.getInstance().getLocationX(), PlayerRobot.getInstance().getLocationY());
	}
	
	/**
	 * Returns a string representation of the AttackStrategy.
	 */
	@Override
	public String toString() {
		return "[Attack Strategy] Traveling towards player at (" + PlayerRobot.getInstance().getLocationX() + ", " + PlayerRobot.getInstance().getLocationY() + ")";
	}
}
