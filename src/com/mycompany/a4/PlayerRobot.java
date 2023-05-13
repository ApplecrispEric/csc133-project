package com.mycompany.a4;

import com.codename1.charts.util.ColorUtil;
import com.codename1.ui.Graphics;
import com.codename1.ui.Transform;
import com.codename1.ui.geom.Point;


/**
 * Singleton class that represents the player robot in game.
 * 
 * @author Eric Brown
 */
public class PlayerRobot extends Robot {
	private static PlayerRobot instance;
	private static final int PLAYER_ROBOT_COLOR = ColorUtil.rgb(255, 0, 0);
	private static float initialX = 0;
	private static float initialY = 0;
	
	private static final int WHEEL_DIAMETER = 40;
	private static final int WHEEL_COLOR = ColorUtil.rgb(64, 197, 190);
	private static final int ARM_COLOR = ColorUtil.rgb(105, 196, 52);
	private static final int ARM_WIDTH = 30;
	
	private static int armSpeed = 35;
	private static float currentArmDisplacement = 0;
	private static final int ARM_MOVEMENT_DISTANCE = 25;
	/**
	 * Private constructor for the player robot.
	 */
	private PlayerRobot() {
		super(PLAYER_ROBOT_COLOR, initialX, initialY, 10, 0, 100);		
	}
	
	/**
	 * Getter for the player robot instance.
	 * 
	 * @return
	 */
	public static PlayerRobot getInstance() {
		if (instance == null) {
			instance = new PlayerRobot();
		}
		
		return instance;
	}
	
	/**
	 * Reconstructs a new player robot.
	 * @return
	 */
	public static PlayerRobot resetInstance() {
		instance = null;
		return getInstance();
	}
	
	/**
	 * Sets the initial location of the robot which is passed to its constructor.
	 * @param x					x location	
	 * @param y					y location
	 */
	public static void setInitialLocation(float x, float y) {
		initialX = x;
		initialY = y;
	}
	
	/**
	 * Draws the robot to the screen.
	 */
	@Override
	public void draw(Graphics g) {		
		Transform oldXform = Transform.makeIdentity();
		g.getTransform(oldXform);
		
		applyTranslation(g);
		applyRotation(g);
		applyScale(g);
		
		int size = this.getSize();
		int x = -size / 2;
		int y = -size / 2;
		
		g.setColor(getColor());
		g.fillRect(x, y, size, size);		
		
		g.setColor(WHEEL_COLOR);
		x -= WHEEL_DIAMETER / 2;
		y -= WHEEL_DIAMETER / 2;
		g.fillArc(x, y, WHEEL_DIAMETER, WHEEL_DIAMETER, 0, 360);
		
		x += size;
		g.fillArc(x, y, WHEEL_DIAMETER, WHEEL_DIAMETER, 0, 360);
		
		g.setColor(ARM_COLOR);
		
		// Draw left arm.
		x = -size / 2;
		y = size / 2;
		int[] xPoints = {x, x, x - ARM_WIDTH * 2};
		int[] yPoints = {y, y - ARM_WIDTH, y - ARM_WIDTH / 2};
		
		// Apply dynamic transformation.
		for (int i = 0; i < 3; i++) {
			yPoints[i] -= (int)currentArmDisplacement;
		}
		g.fillPolygon(xPoints, yPoints, 3);
		
		// Draw right arm.
		x = -size / 2;
		y = size / 2;
		xPoints = new int[] {x + size, x + size, x + ARM_WIDTH * 2 + size};
		
		g.fillPolygon(xPoints, yPoints, 3);
		
		g.setTransform(oldXform);
	}
	
	@Override
	public void move(long milliseconds) {
		super.move(milliseconds);
		
		currentArmDisplacement += armSpeed * (((float)milliseconds) / 1000);
		
		if (currentArmDisplacement <= 0) {
			currentArmDisplacement = 0;
			armSpeed *= -1;
		}
		
		if (currentArmDisplacement >= ARM_MOVEMENT_DISTANCE) {
			currentArmDisplacement = ARM_MOVEMENT_DISTANCE;
			armSpeed *= -1;
		}
	}
}