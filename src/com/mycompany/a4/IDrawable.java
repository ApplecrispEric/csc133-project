package com.mycompany.a4;

import com.codename1.ui.Graphics;
import com.codename1.ui.geom.Point;


/**
 * Inherited by objects that can be drawn to the screen.
 * 
 * @author Eric Brown
 */
public interface IDrawable {
	/**
	 * 
	 * @param g						MapView's graphics object
	 * @param pCmpRelPrnt			MapView’s origin location (upper left corner) relative to the content pane of the Game form 
	 */
	public void draw(Graphics g);
}
