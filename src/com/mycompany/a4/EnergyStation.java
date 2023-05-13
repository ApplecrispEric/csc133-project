package com.mycompany.a4;
import com.codename1.charts.util.ColorUtil;
import com.codename1.ui.Graphics;
import com.codename1.ui.Transform;
import com.codename1.ui.geom.Point;


/**
 * EnergyStation is a immovable object that hold's a certain energy amount proportional to its size.
 * When a robot collides with an EnergyStation, it uses up its energy, recharging the robot while draining
 * the station which changes color to reflect this.
 * 
 * @author Eric Brown
 */
public class EnergyStation extends Fixed implements ICollider {
	public static final int COLOR_FULL = ColorUtil.GREEN;  // Color of stations when they are full of energy.
	public static final int COLOR_EMPTY = ColorUtil.rgb(245, 105, 105);  // Light red. Color of stations when they are empty.
	private int capacity;
	
	/**
	 * Constructor for the EnergyStation.
	 * 
	 * @param size				size of the station
	 * @param locationX			initial x coordinate of the station
	 * @param locationY			initial y coordinate of the station
	 */
	public EnergyStation(int size, float locationX, float locationY) {
		super(size, COLOR_FULL, locationX, locationY);
		this.capacity = size;
	}
	

	/**
	 * Sets the energy capacity to 0 and changes the station's color.
	 * 
	 * @return				amount of energy the station had
	 */
	public int drainEnergy() {
		int energy = this.capacity;
		this.capacity = 0;
		this.setColor(COLOR_EMPTY);
		
		return energy;
	}
	
	/**
	 * @return		true if the energy station has energy left
	 */
	public boolean hasEnergy() {
		return this.capacity > 0;
	}
	
	/**
	 * @return			a string representing the energy station's current state (capacity)
	 */
	@Override
	public String toString() {
		String state = super.toString();
		return "[EnergyStation] " + state + ", Capacity: " + this.capacity;
	}
	
	/**
	 * Draws the energy station to the screen.
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
		int size = this.getSize();
		
		if (isSelected()) {
			g.drawArc((int) x, (int) y, size, size, 0, 360);
		}
		else {
			g.fillArc((int) x, (int) y, size, size, 0, 360);
		}
		
		// Invert the text.
		Transform t = Transform.makeIdentity();
		g.getTransform(t);
		t.scale(1, -1);
		g.setTransform(t);
		g.setColor(ColorUtil.BLACK);
		g.drawString(Integer.toString(this.capacity), (int) ((x * 2 + this.getSize()) / 2), (int) y - 200);
		
		g.setTransform(oldXform);
	}
}
