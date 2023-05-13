package com.mycompany.a4;

import java.io.InputStream;
import com.codename1.media.Media;
import com.codename1.media.MediaManager;
import com.codename1.ui.Display;


/**
 * This class represents a sound that continuously plays in the background.
 * @author Eric Brown
 */
public class BGSound extends Sound implements Runnable {
	/**
	 * Constructor for BGSound.
	 * @param filename			name of the sound file
	 * @param format			format of the sound file
	 */
	public BGSound(String filename, String format) {
		try {
			InputStream steam = Display.getInstance().getResourceAsStream(getClass(), "/" + filename);
			media = MediaManager.createMedia(steam, "audio/" + format, this);		
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Runs the sound (loops).
	 */
	public void run() {
		media.setTime(0);
		play();
	}
}
