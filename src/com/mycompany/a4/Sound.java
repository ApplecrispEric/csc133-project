package com.mycompany.a4;

import java.io.InputStream;
import com.codename1.media.Media;
import com.codename1.media.MediaManager;
import com.codename1.ui.Display;


/**
 * This class wraps the functionality of the JavaFX library into a sound object which plays sounds.
 * 
 * @author Eric Brown
 */
public class Sound{
	protected Media media;
	
	protected Sound() {
		media = null;
	}
	
	/**
	 * Constructor for sound.
	 * 
	 * @param filename				name of sound file
	 * @param format				format of sound file
	 */
	public Sound(String filename, String format) {
		try {
			InputStream steam = Display.getInstance().getResourceAsStream(getClass(), "/" + filename);
			media = MediaManager.createMedia(steam, "audio/" + format);
		}
		catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * Plays the sound from the beginning.
	 */
	public void play() {
		if (media == null) return;
		media.setTime(0);
		media.play();
	}
	
	/**
	 * Pauses sound playback.
	 */
	public void pause() {
		if (media == null) return;
		media.pause();
	}
	
	/**
	 * Mutes the sound.
	 */
	public void mute() {
		if (media == null) return;
		media.setVolume(0);
	}
	
	/**
	 * Unmutes the sound.
	 */
	public void unmute() {
		if (media == null) return;
		media.setVolume(100);
	}
	
	/**
	 * Stops audio playback.
	 */
	public void stop() {
		if (media == null) return;
		media.pause();
	}
}