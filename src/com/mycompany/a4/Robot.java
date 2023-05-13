package com.mycompany.a4;

import com.codename1.charts.util.ColorUtil;
import com.codename1.ui.Graphics;
import com.codename1.ui.geom.Point;

/**
 * Robot is a movable class that the user control's to complete the objective.
 * Robots can change their speed and heading by small increments at runtime.
 * 
 * @author Eric Brown
 */
public abstract class Robot extends Movable implements ISteerable {
	public static final int MAX_STEER_INCREMENT = 5;  // Increment the heading changes by.
	public static final int MAX_STEER_VALUE = 40;  // Maximum steering value at a given time.

	public static final int ROBOT_SIZE = 100;
	public static final int DAMAGE_TO_OTHER_ENTITIES = 20;  // Damage robots do to other entities in a collision.
	public static final int INITIAL_ENERGY_CAPACITY = 500;  // Starting energy of each robot.
	public static final int INTIAL_MAX_SPEED = 100;
	
	private int steeringDirection = 0;
	private int maximumSpeed = INTIAL_MAX_SPEED;
	private float energyLevel = INITIAL_ENERGY_CAPACITY;
	private int energyConsumptionRate = 5;
	private int damageLevel = 0;  // Robots start off with no damage taken.
	private int lastBaseReached = 1;
	private int maxDamage = 0;
	
	/**
	 * Robot constructor.
	 * 
	 * @param size			size of the robot
	 * @param color			color of the robot
	 * @param locationX		initial X coordinate of the robot's location
	 * @param locationY		initial Y coordinate of the robot's location
	 * @param speed			starting speed of the robot
	 * @param heading		starting direction of the robot
	 */
	public Robot(int color, float locationX, float locationY, int speed, int heading, int maxDamage) {
		super(ROBOT_SIZE, color, locationX, locationY, speed, heading);
		this.maxDamage = maxDamage;
	}
	
	/**
	 * Moves the robot by 1 time unit. This is based on the robot's speed, heading, and turning direction.
	 */
	@Override
	public void move(long milliseconds) {
		if (this.isDead()) {
			return;
		}
		super.setHeading(super.getHeading() + (int) (this.steeringDirection * (float) milliseconds / 1000));
		super.move(milliseconds);
		this.energyLevel -= (float) this.energyConsumptionRate * ((float) milliseconds / 1000);
	}
	
	/**
	 * Overridden setter function for heading. Clients must use steerLeft or steerRight to change the robots heading (ISteerable).
	 */
	@Override
	public void setHeading(int newHeading) {}
	
	/**
	 * Turns the robot's steering direction, not heading, a small amount to the left (counter-clockwise).
	 */
	@Override
	public void steerLeft() {
		this.steeringDirection -= MAX_STEER_INCREMENT;
		if (this.steeringDirection <= -MAX_STEER_VALUE) {
			this.steeringDirection = -MAX_STEER_VALUE;
		}
	}
	
	/**
	 * Turns the robot's steering direction, not heading, a small amount to the right (clockwise).
	 */
	@Override
	public void steerRight() {
		this.steeringDirection += MAX_STEER_INCREMENT;
		if (this.steeringDirection >= MAX_STEER_VALUE) {
			this.steeringDirection = MAX_STEER_VALUE;
		}
	}
	
	/**
	 * Getter for steeringDirection.
	 * 
	 * @return			the robot's current steering direction
	 */
	public int getSteeringDirection() {
		return this.steeringDirection;
	}
	
	/**
	 * Getter for maximumSpeed.
	 * 
	 * @return			the robot's current maximum speed
	 */
	public int getMaximumSpeed() {
		return this.maximumSpeed;
	}
	
	/**
	 * Setter for the robot's maximum speed. It's current speed will also be changed accordingly if it is greater than the new maximum.
	 * 
	 * @param speed			the new maximum speed
	 */
	public void setMaximumSpeed(int speed) {
		if (speed < 0) {  // Can't have negative max speed.
			speed = 0;
		}
		
		this.maximumSpeed = speed;
		this.setSpeed(this.getSpeed());  // Ensures the current speed is not larger than maximum speed.
	}
	
	/**
	 * Setter for the current speed.
	 * 
	 * @param speed			the new speed
	 */
	@Override
	public void setSpeed(int speed) {
		if (speed > this.maximumSpeed) {
			speed = this.maximumSpeed;
		}
		
		super.setSpeed(speed);
	}
	
	/**
	 * Getter for lastBaseReached.
	 * 
	 * @return			the last base the robot has reached
	 */
	public int getLastBaseReached() {
		return this.lastBaseReached;
	}
	
	/**
	 * Setter for lastBaseReached.
	 * 
	 * @param base		the next base that the robot has just reached
	 */
	public void setLastBaseReached(int base) {
		// Bases must be traversed in order.
		if (base == this.lastBaseReached+ 1 ) {
			this.lastBaseReached = base;
		}
	}
	
	/**
	 * Charges the robot's energy level by the specified amount.
	 * 
	 * @param amount	amount to increase the robot's energy level by
	 */
	public void chargeRobot(int amount) {
		this.energyLevel += amount;
	}
	
	/**
	 * Getter for energyLevel.
	 * 
	 * @return			the robot's current energy level
	 */
	public float getEnergyLevel() {
		return this.energyLevel;
	}
	
	/**
	 * Getter for damageLevel.
	 * 
	 * @return			the robot's current damage level
	 */
	public int getDamageLevel() {
		return this.damageLevel;
	}
	
	/**
	 * Damage's the robot by the specified amount. The more damage a robot has, the slower it will move.
	 * Its maximum speed will be decreases proportionally by the amount of damage it has taken.\
	 * 
	 * @param amount	amount to damage the robot by
	 */
	public void damage(int amount) {
		this.damageLevel += amount;
		float damagePercentageRemaining = 1f - ((float) this.damageLevel / this.maxDamage);
		int newMaxSpeed = (int)(damagePercentageRemaining * INTIAL_MAX_SPEED);  // The more damage a robot has, the lower its max speed.
		this.setMaximumSpeed(newMaxSpeed);
	}
	
	/**
	 * @return			a string representation of the robot's state
	 */
	public String toString() {
		String state = super.toString();
		return "[Robot] " + state + ", Last Base: " + this.lastBaseReached + 
				", Energy: " + this.energyLevel + 
				", Damage: " + this.damageLevel + 
				", Max Speed: " + this.maximumSpeed + 
				", Steering: " + this.steeringDirection;
	}
	
	/**
	 * @return			true if the robot is immobile by too much damage or no energy left to move or false if otherwise
	 */
	public boolean isDead() {
		return this.damageLevel >= this.maxDamage || this.energyLevel <= 0;
	}
	
	/**
	 * @return				the energy consumption rate for the robot
	 */
	public int getEnergyConsumptionRate() {
		return this.energyConsumptionRate;
	}
	

	/**
	 * Handles the robots state when colliding with another gameObject, does not affect the other objects state.
	 */
	@Override
	public void handleCollision(GameObject otherObject) {
		if (otherObject instanceof Drone) {
			this.damage(Drone.DAMAGE_TO_OTHER_ENTITIES);
			GameWorld.playCrashSound();
		}
		else if (otherObject instanceof NonPlayerRobot) {
			this.damage(Robot.DAMAGE_TO_OTHER_ENTITIES);
			GameWorld.playCrashSound();
		}
		else if (otherObject instanceof Base) {
			Base base = (Base) otherObject;
			if (base.getSequenceNumber() == getLastBaseReached() + 1) {
				this.setLastBaseReached(base.getSequenceNumber());
			}
		}
		else if (otherObject instanceof EnergyStation) {
			EnergyStation station = (EnergyStation) otherObject;
			if (station.hasEnergy()) {
				this.chargeRobot(station.drainEnergy());
				GameWorld.playChargeSound();
			}
		}
	}
}
