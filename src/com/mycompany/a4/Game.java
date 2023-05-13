package com.mycompany.a4;

import java.util.Vector;

import com.codename1.charts.util.ColorUtil;
import com.codename1.ui.Button;
import com.codename1.ui.CheckBox;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Form;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.Border;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.util.UITimer;
import com.codename1.ui.Toolbar;


/**
 * Game holds the main logic for user commands and represents the GUI for the user.
 * 
 * @author Eric Brown
 *
 */

public class Game extends Form implements Runnable {
	private GameWorld gw;
	private UITimer timer;
	private long lastClockValue;
	private Mode currentMode = Mode.PLAY;
	
	private MapView map;
	private Button modeButton;
	private Button positionButton;
	private GameModeCommand playCommand;
	private GameModeCommand pauseCommand;
	
	private PlayerRobotCommand accelerateCommand;
	private PlayerRobotCommand brakeCommand;
	private PlayerRobotCommand turnLeftCommand;
	private PlayerRobotCommand turnRightCommand;
	
	private static Vector<Button> buttonList = new Vector<Button>();
	private static Game instance;
	
	public enum Mode {
		PLAY,
		PAUSED
	}
	
	/**
	 * Constructor for Game. Creates a game world, initializes it, and then starts a game.
	 */
	public Game() {
		instance = this;  // Reference used to query the game mode.
		
		this.setLayout(new BorderLayout());
		gw  = new GameWorld();		
		accelerateCommand = new PlayerRobotCommand("Accelerate", gw);
		brakeCommand = new PlayerRobotCommand("Brake", gw);
		turnLeftCommand = new PlayerRobotCommand("Left", gw);
		turnRightCommand = new PlayerRobotCommand("Right", gw);
		
		// Create the layout and components on the screen.
		Container leftContainer = new Container(BoxLayout.yCenter());
		Container rightContainer = new Container(BoxLayout.yCenter());
		Container bottomContainer = new Container(BoxLayout.xCenter());
		
		playCommand = new GameModeCommand("Play", this);
		pauseCommand = new GameModeCommand("Pause", this);
		Command position = new PositionCommand(gw);
		Command help = new GameCommand("Help", gw);
		Command about = new GameCommand("About", gw);
		Command exit = new GameCommand("Exit", gw);
		Command sound = new GameCommand("Enable Sound", gw);
		Command changeStrategies = new GameCommand("Change Strategies", gw);
		
		positionButton = new Button(position);
		modeButton = new Button(pauseCommand);
		
		// Add the buttons to the side containers.
		bottomContainer.add(positionButton);
		bottomContainer.add(modeButton);
		leftContainer.add(new Button(accelerateCommand));
		leftContainer.add(new Button(turnLeftCommand));
		leftContainer.add(new Button(changeStrategies));
		rightContainer.add(new Button(brakeCommand));
		rightContainer.add(new Button(turnRightCommand));
		
		ScoreView score = new ScoreView();

		// Add the containers to the screen.
		this.add(BorderLayout.NORTH, score);
		this.add(BorderLayout.WEST, leftContainer);
		this.add(BorderLayout.EAST, rightContainer);
		this.add(BorderLayout.SOUTH, bottomContainer);
		
		// Create the map.
		map = new MapView();
		map.getAllStyles().setBorder(Border.createLineBorder(10, ColorUtil.rgb(255, 0, 0)));
		this.add(BorderLayout.CENTER, map);
		
		// Create the side tool bar for the application.
		Toolbar toolbar = new Toolbar();
		this.setToolbar(toolbar);
		toolbar.setTitle("Robo-track Game");
		
		// Add sound toggle.
		CheckBox soundCheckBox = new CheckBox();
		soundCheckBox.setCommand(sound);
		soundCheckBox.setSelected(gw.getSoundEnabled());
		toolbar.addComponentToSideMenu(soundCheckBox);
		
		// Add buttons for user information and quitting.
		toolbar.addComponentToSideMenu(new Button(about));
		toolbar.addComponentToSideMenu(new Button(exit));
		toolbar.addComponentToSideMenu(new Button(accelerateCommand));
		toolbar.addCommandToRightBar(help);  // Right side of toolbar.
		
		// Style the containers and buttons.
		for (Container c : new Container[] {leftContainer, rightContainer, bottomContainer, score}) {
			c.getAllStyles().setBorder(Border.createLineBorder(3, ColorUtil.BLACK));
		}
		
		for (Container container : new Container[] {leftContainer, rightContainer, bottomContainer}) {
			for (Component b : container) {
				if (b instanceof Button) {
					buttonList.add((Button) b);
				}
			}
		}
		
		// Create a universal style for all the buttons used in the application.
		Style buttonStyle = new Style();
		int buttonStylePadding = 35;
		int buttonStyleBorderThickness = 3;
		int buttonStyleBgColor = ColorUtil.BLUE;
		int buttonStyleFgColor = ColorUtil.WHITE;
		
		buttonStyle.setPadding(Component.TOP, buttonStylePadding);
		buttonStyle.setPadding(Component.BOTTOM, buttonStylePadding);
		buttonStyle.setPadding(Component.LEFT, buttonStylePadding);
		buttonStyle.setPadding(Component.RIGHT, buttonStylePadding);
		buttonStyle.setBorder(Border.createLineBorder(buttonStyleBorderThickness, ColorUtil.BLACK));
		buttonStyle.setBgColor(buttonStyleBgColor);
		buttonStyle.setFgColor(buttonStyleFgColor);
		buttonStyle.setAlignment(CENTER);
		
		Style disabledStyle = new Style(buttonStyle);
		disabledStyle.setBgColor(buttonStyleFgColor);
		disabledStyle.setFgColor(buttonStyleBgColor);
		
		for (Button b : buttonList) {
			b.setDisabledStyle(new Style(disabledStyle));
			b.setSelectedStyle(new Style(buttonStyle));
			b.setUnselectedStyle(new Style(buttonStyle));
			b.setPressedStyle(new Style(buttonStyle));
		}
		
		// Add the toolbar buttons to disable them later on pause.
		for (Component c : toolbar) {
			if (c instanceof Button) {
				buttonList.add((Button) c);
			}
		}
				
		// Set the dimensions of the world after the map size has been calculated.
		this.show();
		map.initializeBoundaries();
		gw.setWidth(map.getWidth());
		gw.setHeight(map.getHeight());
		gw.addObserver(score);
		gw.addObserver(map);
		map.initializeBoundaries();
		gw.init();  // Initialize the GameWorld at the end to notify all components (observers).
		
		GameWorld.createSounds();
		this.setGameMode(Mode.PLAY);
		this.revalidate();
		
		lastClockValue = System.currentTimeMillis();
		timer = new UITimer(this);
		timer.schedule(20, true, this);
		
		gw.setSoundEnabled(false);
	}
	
	/**
	 * Adds key listeners for the required actions.
	 */
	public void addKeyListeners() {
		this.addKeyListener('a', accelerateCommand);
		this.addKeyListener('b', brakeCommand);
		this.addKeyListener('l', turnLeftCommand);
		this.addKeyListener('r', turnRightCommand);
	}
	
	/**
	 * Removes the key listeners (for when the game is paused).
	 */
	public void removeKeyListeners() {
		this.removeKeyListener('a', accelerateCommand);
		this.removeKeyListener('b', brakeCommand);
		this.removeKeyListener('l', turnLeftCommand);
		this.removeKeyListener('r', turnRightCommand);
	}
	
	/**
	 * Disables all the buttons on screen.
	 */
	public void disableAllButtons() {
		for (Button b : buttonList) {
			b.setEnabled(false);
		}
	}
	
	/**
	 * Enables all the buttons on screen.
	 */
	public void enableAllButtons() {
		for (Button b : buttonList) {
			b.setEnabled(true);
		}
	}
	
	/**
	 * Setter for the game mode.
	 */
	public void setGameMode(Mode mode) {
		this.currentMode = mode;
		
		if (this.currentMode == Mode.PAUSED) {
			removeKeyListeners();
			disableAllButtons();
			modeButton.setCommand(playCommand);
			positionButton.setEnabled(true);
			GameWorld.stopAllSounds();
		}
		else if (this.currentMode == Mode.PLAY) {
			addKeyListeners();
			enableAllButtons();			
			gw.deselectAllObjects();
			modeButton.setCommand(pauseCommand);
			positionButton.setEnabled(false);
			GameWorld.playBackgroundSound();
		}
	}
	
	/**
	 * @return				true if the game is currently paused, false otherwise
	 */
	public static boolean isPaused() {
		return instance.currentMode == Mode.PAUSED;
	}
	
	/**
	 * Called on every update cycle.
	 */
	@Override
	public void run() {
		long currentClockValue = System.currentTimeMillis();
		long milliseconds = currentClockValue - lastClockValue;
		lastClockValue = currentClockValue;
		
		if (currentMode == Mode.PLAY) {
			gw.tick(milliseconds);
		}
		else {
			map.repaint();
		}
	}
}
