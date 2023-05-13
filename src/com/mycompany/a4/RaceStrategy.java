package com.mycompany.a4;


/**
 * RaceStrategy makes a NPR compete by ignoring the player robot and instead racing towards the next
 * base location.
 * 
 * @author Eric Brown
 */
public class RaceStrategy implements IStrategy {
	private GameWorld gw;
	private NonPlayerRobot robot;
	
	/**
	 * Constructor for RaceStrategy.
	 * 
	 * @param robot				robot to modify based on the strategy
	 * @param world				game world that holds the location of the next base
	 */
	public RaceStrategy(NonPlayerRobot robot, GameWorld world) {
		this.robot = robot;
		this.gw = world;
	}
	
	/**
	 * Change the robot's heading to the direction of the next base location.
	 */
	@Override
	public void invoke() {
		int nextBase = robot.getLastBaseReached() + 1;		
		robot.steerTowardsIntendedLocation(gw.getBaseLocationX(nextBase), gw.getBaseLocationY(nextBase));
	}
	
	/**
	 * @return					string representation of RaceStrategy
	 */
	@Override
	public String toString() {
		int nextBase = robot.getLastBaseReached() + 1;
		return "[Race Strategy] Traveling towards base at (" + gw.getBaseLocationX(nextBase) + ", " + gw.getBaseLocationY(nextBase) + ")";
	}
}
