package com.mycompany.a4;

import com.codename1.charts.util.ColorUtil;
import com.codename1.ui.Graphics;
import com.codename1.ui.Transform;


/**
 * ShockWave represents a randomly generated bezier curve that spawns when two moving game objects
 * collide with each other and later despawns.
 * 
 * @author Eric Brown
 */
public class ShockWave extends Movable {
	
	public static final int SIZE = 100;
	public static final int COLOR = ColorUtil.BLUE;
	public static final int SPEED = 200;
	public static final int TIME_TO_LIVE = 8000; // Milliseconds.
	private static final double EPSILON = 0.001;
	
	private Point[] controlPoints = new Point[4];
	private long ttl = TIME_TO_LIVE;
	
	/**
	 * Constructor for the shockwave object.
	 * @param locationX				initial x location
	 * @param locationY				initial y location
	 */
	public ShockWave(float locationX, float locationY) {
		super(SIZE, COLOR, locationX, locationY, 100, GameWorld.randomInt(0, 360));
		
		for (int i = 0; i < 4; i++) {
			int x = GameWorld.randomInt(-SIZE, SIZE);
			int y = GameWorld.randomInt(-SIZE, SIZE);
			controlPoints[i] = new Point(x, y);
		}
	}
	
	@Override
	public void move(long milliseconds) {
		super.move(milliseconds);
		ttl -= milliseconds;
	}
	
	/**
	 * @return					true if the shockwave has reached the end of its lifetime
	 */
	public boolean isExpired() {
		return ttl <= 0;
	}
	
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
		drawBezierCurve(g, controlPoints);
		
		g.setTransform(oldXform);
	}
	

	/** Draws the (cubic) Bezier curve represented by the (1x4) input Control Point Vector
	* by recursively subdividing the Control Point Vector until the control points are
	* within some tolerance of being colinear, at which time the Control Points are deemed
	* "close enough" to the curve for the 1st and last control points to be used as the
	* ends of a line segment representing a short piece of the actual Bezier curve.
	* 
	* Note: the psuedocode for this method was sourced from Lecture 13A from CSC 133 (Kwan).
	*/
	void drawBezierCurve (Graphics g, Point[] currentControlPoints) {
		if (straightEnough(currentControlPoints)) {  // Draw Line from 1st Control Point to last Control Point.
			double x1 = currentControlPoints[0].getX();
			double x2 = currentControlPoints[3].getX();
			double y1 = currentControlPoints[0].getY();
			double y2 = currentControlPoints[3].getY();
			
			g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
		}
		else {
			Point[] leftCurvePoints = new Point[4];
			Point[] rightCurvePoints = new Point[4];
			subdivideCurve (currentControlPoints, leftCurvePoints, rightCurvePoints);
			drawBezierCurve(g, leftCurvePoints);
			drawBezierCurve(g, rightCurvePoints);
		}
	}
	
	/** Determines whether the four points Q0, Q1, Q2, Q3 in the input array of Control
	* Points are within some tolerance "epsilon" of being colinear.
	* 
	* Note: the psuedocode for this method was sourced from Lecture 13A from CSC 133 (Kwan).
	*/
	boolean straightEnough (Point[] q) {
		// Find length around control polygon.
		double d1 = lengthBetween(q[0], q[1]) + lengthBetween(q[1], q[2]) + lengthBetween(q[2], q[3]);
		// Find distance directly between first and last control point.
		double d2 = lengthBetween(q[0], q[3]);
		
		return Math.abs(d1-d2) <= EPSILON;  // Epsilon (“tolerance”) = (e.g.) .001
	}
	
	
	/** Splits the input control point vector Q into two control point
	* vectors R and S such that R and S define two Bezier curve segments that
	* together exactly match the Bezier curve defined by Q.
	* 
	* Note: the psuedocode for this method was sourced from Lecture 13A from CSC 133 (Kwan).
	*/
	void subdivideCurve (Point[] q, Point[] r, Point[] s) {
		r[0] = q[0];  // R(0) = Q(0)
		r[1] = dividePoint(addPoints(q[0], q[1]), 2.0);  // R(1) = (Q(0)+Q(1)) / 2.0
		r[2] = addPoints(dividePoint(r[1], 2.0), dividePoint(addPoints(q[1], q[2]), 4.0));  // R(2) = (R(1)/2.0) + (Q(1)+Q(2))/4.0
		s[3] = q[3];  // S(3) = Q(3)
		s[2] = dividePoint(addPoints(q[2], q[3]), 2.0);  // (Q(2)+Q(3)) / 2.0
		s[1] = addPoints(dividePoint(addPoints(q[1], q[2]), 4.0), dividePoint(s[2], 2.0));  // S(1) = (Q(1)+Q(2))/4.0 + S(2)/2.0
		r[3] = dividePoint(addPoints(r[2], s[1]), 2.0);  // R(3) = (R(2)+S(1)) / 2.0
		s[0] = r[3];  // S(0) = R(3)
	}
	
	/**
	 * Adds two points together and returns a new point of the result.
	 * 
	 * @param a					first point
	 * @param b					second point
	 * @return					first added to second
	 */
	private Point addPoints(Point a, Point b) {
		return new Point(a.getX() + b.getX(), a.getY() + b.getY());
	}
	
	/**
	 * Divides a point by a constant.
	 * 
	 * @param a					point to divide
	 * @param divisor			amount to divide by
	 * @return					the point divided by divisor
	 */
	private Point dividePoint(Point a, double divisor) {
		double x = a.getX() / divisor;
		double y = a.getY() / divisor;
		return new Point(x, y);
	}
	
	/**
	 * Returns the length between the points.
	 * @param a				first point
	 * @param b				second point
	 * @return				the length between the points
	 */
	private double lengthBetween(Point a, Point b) {
		double dx = b.getX() - a.getX();
		double dy = b.getY() - a.getY();
		return Math.sqrt((dx * dx) + (dy * dy));
	}
	
	/**
	 * Separate point class from Codename1's implementation to simplify the calculations when subdividing the bezier curve.
	 * 
	 * @author Eric Brown
	 */
	private class Point {
		private double x = 0;
		private double y = 0;
		
		/**
		 * Constructor for the point class.
		 * 
		 * @param x				initial x value
		 * @param y				initial y value
		 */
		Point(double x, double y) {
			this.x = x;
			this.y = y;
		}
		
		/**
		 * @return			x component of the point
		 */
		public double getX() {
			return this.x;
		}
		
		/**
		 * @return			y component of the point
		 */
		public double getY() {
			return this.y;
		}
	}
}
