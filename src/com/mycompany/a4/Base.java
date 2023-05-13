package com.mycompany.a4;

import com.codename1.charts.util.ColorUtil;
import com.codename1.ui.Graphics;
import com.codename1.ui.Transform;
import com.codename1.ui.geom.Point;

/**
 * Base represents a physical, immovable GameObject that the player robot uses to recharge its energy level.
 * Bases are indexed by a sequenceNumber which keeps track of the order of bases along the intended
 * path for the player to traverse. The bases must be traversed in increasing order to progress the game.
 * 
 * @author Eric Brown
 *
 */
public class Base extends Fixed {
	private static final int BASE_SIZE = 100;  // Size of all bases.
	private int sequenceNumber;  // Sequence number of the base. Must be traversed in order to progress the game.
	
	/**
	 * Constructor for the Base class.
	 * 
	 * @param color				color of the base
	 * @param locationX			initial x coordinate of the base
	 * @param locationY			initial y coordinate of the base
	 * @param sequenceNumber	sequence number of the base
	 */
	public Base(int color, float locationX, float locationY, int sequenceNumber) {
		super(BASE_SIZE, color, locationX, locationY);
		this.sequenceNumber = sequenceNumber;
	}
	
	/**
	 * Getter for sequenceNumber.
	 * 
	 * @return		sequenceNumber for the base.
	 */
	public int getSequenceNumber() {
		return this.sequenceNumber;
	}
	
	/**
	 * @return		a string representing the base's state
	 */
	public String toString() {
		String state = super.toString();
		return "[Base] " + state + ", Sequence #: " + this.sequenceNumber;
	}
	
	/**
	 * Bases are not allowed to change color once instantiated.
	 */
	@Override
	public void setColor(int color) {}
	
	/**
	 * Draws the base to the screen.
	 */
	@Override
	public void draw(Graphics g) {
		float x = -this.getSize() / 2;
		float y = -this.getSize() / 2;
		int[] xPoints = {(int) x, (int) (x + this.getSize()), (int) ((2 * x + this.getSize()) / 2)};  // X locations of triangle points.
		int[] yPoints = {(int) y, (int) y, (int) (y + this.getSize())};  // Y locations of triangle points.
				
		Transform oldXform = Transform.makeIdentity();
		g.getTransform(oldXform);
		
		applyTranslation(g);
		applyRotation(g);
		applyScale(g);
		g.setColor(this.getColor());
		
		
		if (isSelected()) {
			g.drawPolygon(xPoints, yPoints, 3);
		}
		else {
			g.fillPolygon(xPoints, yPoints, 3);
		}
		
		g.setColor(ColorUtil.BLACK);
		
		// Invert the text.
		Transform t = Transform.makeIdentity();
		g.getTransform(t);
		t.scale(1, -1);
		g.setTransform(t);
		g.drawString(Integer.toString(this.sequenceNumber), (int) ((x * 2 + this.getSize()) / 2), (int) y - 200);
		
		g.setTransform(oldXform);
		
	}
}
