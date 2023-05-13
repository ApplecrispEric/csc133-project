package com.mycompany.a4;
import com.codename1.charts.util.ColorUtil;
import com.codename1.ui.Graphics;
import com.codename1.ui.Transform;
import com.codename1.ui.geom.Point;
import com.codename1.util.MathUtil;


/**
 * NonPlayerRobots represent opposing robots that compete against the player to either
 * damage the player or beat them by reaching the last base first.
 * 
 * @author Eric Brown
 */
public class NonPlayerRobot extends Robot {
	private static final int MAX_DAMAGE_AMOUNT = 250;
	private IStrategy strategy;

	
	/**
	 * Constructor for the NPR.
	 * 
	 * @param size				size of the robot
	 * @param color				color of the robot	
	 * @param locationX			starting x location of the robot
	 * @param locationY			starting y location of the robot
	 * @param heading			initial heading
	 * @param strategy			initial strategy
	 */
	public NonPlayerRobot(int color, float locationX, 
			float locationY, int heading, IStrategy strategy) {
		super(color, locationX, locationY, Robot.INTIAL_MAX_SPEED, heading, MAX_DAMAGE_AMOUNT);
		this.strategy = strategy;
	}
	
	/**
	 * Moves the robot by one tick of in-game time.
	 */
	@Override
	public void move(long milliseconds) {
		this.invokeStrategy();
		super.move(milliseconds);
		this.chargeRobot(Robot.INITIAL_ENERGY_CAPACITY - (int)this.getEnergyLevel());  // NPR's should never run out of energy.
	}
	
	/**
	 * Alter the robot's state according to its current strategy.
	 */
	private void invokeStrategy() {
		this.strategy.invoke();
	}
	
	/**
	 * Getter for the robot's strategy.
	 * 
	 * @return			the current strategy
	 */
	public IStrategy getStrategy() {
		return this.strategy;
	}
	
	/**
	 * Setter for the robot's strategy.
	 * 
	 * @param strategy		the new strategy
	 */
	public void setStrategy(IStrategy strategy) {
		this.strategy = strategy;
	}
	
	/**
	 * Alters the robot's steering direction based on an intended location.
	 * 
	 * @param locationX					x coordinate of intended location
	 * @param locationY					y coordinate of intended location
	 */
	public void steerTowardsIntendedLocation(float locationX, float locationY) {
		float deltaX = locationX - getLocationX();
		float deltaY = locationY - getLocationY();
		
		double intendedHeadingInRadians = MathUtil.atan2(deltaX, deltaY);
		int intendedHeadingInDegrees = (int) Math.toDegrees(intendedHeadingInRadians);
		
		if (intendedHeadingInDegrees < 0) {
			intendedHeadingInDegrees += 360;
		}
		
		int steerLeftAmount;  // Amount to reach intended heading by steering left.
		int steerRightAmount;  // Amount to reach intended heading by steering right.
		
		if (getHeading() > intendedHeadingInDegrees) {
			steerLeftAmount = getHeading() - intendedHeadingInDegrees;
			steerRightAmount = getHeading() + (360 - intendedHeadingInDegrees);
		}
		else {
			steerLeftAmount = getHeading() + (360 - intendedHeadingInDegrees);
			steerRightAmount = intendedHeadingInDegrees - getHeading();
		}
		
		// Lower the speed if robot needs to turn a larger amount;
		if (Math.min(steerLeftAmount, steerRightAmount) >= MAX_STEER_VALUE) {
			this.setSpeed(this.getMaximumSpeed() / 2);
		}
		else {
			this.setSpeed(this.getMaximumSpeed());
		}
		
		if (steerRightAmount < steerLeftAmount) {
			this.steerRight();
		}
		else {
			this.steerLeft();
		}
	}
	
	/**
	 * @return			string representation of the NPR
	 */
	@Override
	public String toString() {
		String state = super.toString();
		return "[NPR] " + state + ", Strategy: " + strategy.toString(); 
	}
	
	/**
	 * Draws the robot to the screen.
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
		g.drawRect((int) x, (int) y, this.getSize(), this.getSize());
		
		g.setTransform(oldXform);
	}
}