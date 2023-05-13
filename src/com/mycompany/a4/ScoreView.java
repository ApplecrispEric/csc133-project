package com.mycompany.a4;

import java.util.Observer;
import java.util.Observable;
import com.codename1.ui.Container;
import com.codename1.ui.Label;
import com.codename1.ui.layouts.GridLayout;


/**
 * ScoreView is a container for the GUI that observes the GameWorld's score and updates accordingly.
 * 
 * @author Eric Brown
 *
 */
public class ScoreView extends Container implements Observer {
	private Label clockLabel = new Label();
	private Label livesLabel = new Label();
	private Label lastBaseLabel = new Label();
	private Label playerEnergyLabel = new Label();
	private Label playerDamageLabel = new Label();
	private Label soundLabel = new Label();
	
	/**
	 * Constructor for ScoreView.
	 */
	public ScoreView() {
		this.setLayout(new GridLayout(1, 6));
		this.add(clockLabel);
		this.add(livesLabel);
		this.add(lastBaseLabel);
		this.add(playerEnergyLabel);
		this.add(playerDamageLabel);
		this.add(soundLabel);
		
		clockLabel.setText("Time: 0");
		livesLabel.setText("Lives Left: 3");
		lastBaseLabel.setText("Last Base: 1");
		playerEnergyLabel.setText("Energy Left: 100");
		playerDamageLabel.setText("Damage: 0");
		soundLabel.setText("Sound Enabled: OFF");
	}
	
	/**
	 * Changes the labels showing the score values when the GameWorld updates.
	 */
	@Override
	public void update(Observable observable, Object data) {
		GameWorld gw = (GameWorld) observable;
		clockLabel.setText("Time: " + gw.getClock());
		livesLabel.setText("Lives Left: " + gw.getLives());
		lastBaseLabel.setText("Last Base: " + gw.getPlayerLastBase());
		playerEnergyLabel.setText("Energy Left: " + (int) gw.getPlayerEnergyLeft());
		playerDamageLabel.setText("Damage: " + gw.getPlayerDamage());
		soundLabel.setText("Sound Enabled: " + (gw.getSoundEnabled() ? "ON" : "OFF"));
	}
}
