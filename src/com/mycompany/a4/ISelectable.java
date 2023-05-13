package com.mycompany.a4;

import com.codename1.ui.geom.Point;

/**
 * Interface is inherited by objects that can be selected at runtime by the user.
 * 
 * @author Eric Brown
 * 
 */
public interface ISelectable {
	/**
	 * Setter for selected.
	 * 
	 * @param selected
	 */
	public void setSelected(boolean selected);
	
	/**
	 * Getter for selected.
	 * 
	 * @return
	 */
	public boolean isSelected();
	
	/**
	 * Tests whether point is contained within the object.
	 * 
	 * @param point					the point to test
	 * @return						true if point is within the object's bounds, false otherwise
	 */
	public boolean contains(Point point);
}
