package com.mycompany.a4;

import java.util.Random;
import java.util.Set;

import com.codename1.charts.util.ColorUtil;
import com.codename1.ui.geom.Point;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Observable;


/**
 * GameWorld holds the collection of GameObjects currently in play and other various
 * state information that represents the current game in progress.
 * 
 * @author Eric Brown
 */
public class GameWorld extends Observable {
	private static final int INITIAL_LIVES = 3;
	private static final int SPEED_INCREMENT = 10;
	private static final int TOTAL_BASE_COUNT = 9;

	private int width;
	private int height;
	private int lives = INITIAL_LIVES;
	private int clock = 0;  // Tracks number of ticks of in-game time have occurred in seconds.
	private long millisecondClock = 0;
	
	private boolean soundEnabled = false;
	private boolean movingObject = false;
	
	private GameObjectCollection objectCollection = new GameObjectCollection();
	private PlayerRobot player;
	private static Random generator = new Random(System.currentTimeMillis());
	
	private HashMap<GameObject, HashSet<GameObject>> collisionHistory = new HashMap<GameObject, HashSet<GameObject>>();
	
	private static Sound crashSound = null;
	private static Sound chargeSound = null;
	private static Sound explosionSound = null;
	private static BGSound backgroundSound = null;
	
	/**
	 * Setter for width.
	 * @param width
	 */
	public void setWidth(int width) {
		this.width = width;
	}
	
	/**
	 * Setter for height.
	 * @param height
	 */
	public void setHeight(int height) {
		this.height = height;
	}
	
	/**
	 * Initializes the game state by creating the GameObjects.
	 */
	public void init() {	
		// Instantiate the bases.
		for (int i = 1; i < TOTAL_BASE_COUNT + 1; i++) {
			int x = randomInt(0, width);
			int y = randomInt(0, height);
			this.objectCollection.add(new Base(ColorUtil.BLUE, x, y, i));
		}
		
		int robotColor = ColorUtil.rgb(255, 0, 0);  // Red.
		float playerRobotY = getBaseLocationY(1);
		float playerRobotX = getBaseLocationX(1);
		float deltaX = 200;
		float deltaY = 200;
		PlayerRobot.setInitialLocation(playerRobotX, playerRobotY);
		this.player = PlayerRobot.resetInstance();
		this.objectCollection.add(player);
		
		// Create a robot that races the player.
		NonPlayerRobot robot = new NonPlayerRobot(robotColor, playerRobotX + deltaX, playerRobotY + deltaY, 0, null);;
		robot.setStrategy(new RaceStrategy(robot, this));
		this.objectCollection.add(robot);
		
		// Create a robot that attacks the player.
		robot = new NonPlayerRobot(robotColor, playerRobotX + deltaX, playerRobotY - deltaY, 0, null);
		robot.setStrategy(new AttackStrategy(robot));
		this.objectCollection.add(robot);
		
		// Create a third robot as per specifications.
		robot = new NonPlayerRobot(robotColor, playerRobotX - deltaX, playerRobotY + deltaY, 0, null);
		robot.setStrategy(new RaceStrategy(robot, this));
		this.objectCollection.add(robot);
		
		// Add two energy stations.
		this.objectCollection.add(new EnergyStation(randomInt(50, 120), randomInt(0, width), randomInt(0, height)));
		this.objectCollection.add(new EnergyStation(randomInt(50, 120), randomInt(0, width), randomInt(0, height)));
		
		// Add a two drones.
		this.objectCollection.add(new Drone(randomInt(50, 120), ColorUtil.GRAY, randomInt(0, width), randomInt(0, height), randomInt(30, 60), randomInt(0, 360), width, height));
		this.objectCollection.add(new Drone(randomInt(50, 120), ColorUtil.GRAY, randomInt(0, width), randomInt(0, height), randomInt(30, 60), randomInt(0, 360), width, height));
		
		// Initialize the text displayed by the GUI.
		this.setChanged();
		this.notifyObservers();
	}
	
	/**
	 * Generates pseudorandom integers within the given range.
	 * @param start						starting value of integer range (inclusive)
	 * @param end						ending value of integer range (exclusive)
	 * @return							random integer between [start, end)
	 */
	public static int randomInt(int start, int end) {
		return generator.nextInt(end - start) + start;
	}
	
	
	/**
	 * Increments the internal game state by a single time unit.
	 */
	public void tick(long milliseconds) {
		this.millisecondClock += milliseconds;
		this.clock += this.millisecondClock / 1000;
		this.millisecondClock %= 1000;
		
		IIterator outer = this.objectCollection.getIterator();
		
		// Move all the objects first.
		while (outer.hasNext()) {
			GameObject object = outer.getNext();
			
			if (object instanceof Movable) {
				((Movable) object).move(milliseconds);
				
				// Check to see if the player won.
				if (object instanceof PlayerRobot) {
					if (((PlayerRobot) object).getLastBaseReached() == TOTAL_BASE_COUNT) {
						System.out.println("Game over, you win! Total time: " + this.clock);
						System.exit(0);
					}
				}
				
				// Check to see if an NPR won the game.
				if (object instanceof NonPlayerRobot) {
					if (((NonPlayerRobot) object).getLastBaseReached() == TOTAL_BASE_COUNT) {
						System.out.println("Game over, a non-player robot wins!");
						System.exit(0);
					}
				}
			}
		}
		
		
		// Process the collisions.
		outer = this.objectCollection.getIterator();
		while (outer.hasNext()) {
			GameObject first = outer.getNext();
			
			if (!collisionHistory.containsKey(first)) {
				collisionHistory.put(first, new HashSet<GameObject>());
			}
			
			IIterator inner = this.objectCollection.getIterator();
			HashSet<GameObject> objects = collisionHistory.get(first);
			
			while (inner.hasNext()) {
				GameObject second = inner.getNext();
				
				if (first == second) {
					continue;
				}
				else if (first.collidesWith(second)) {
					if (!objects.contains(second)) {  // Collision hasn't been encountered before.
						first.handleCollision(second);
						objects.add(second);
						
						// Handle shockwaves.
						if ((first instanceof Robot || first instanceof Drone) && (second instanceof Robot || second instanceof Drone)) {
							if (collisionHistory.get(second) != null && collisionHistory.get(second).contains(first)) {  // Already spawned a shockwave for this collision.
								continue;
							}
							this.objectCollection.add(new ShockWave(first.getLocationX(), first.getLocationY()));
						}
					}
				}
				else {  // Objects are not collided or passed over each other, reset their history.
					objects.remove(second);
				}
			}
		}
		
		// Add new energy stations if needed and remove shockwaves if they are expired.
		int chargedEnergyStations = 0;
		IIterator i = this.objectCollection.getIterator();
		
		while (i.hasNext()) {
			GameObject obj = i.getNext();
			if (obj instanceof EnergyStation) {
				if (((EnergyStation) obj).hasEnergy()) {
					chargedEnergyStations++;
				}
			}
			else if (obj instanceof ShockWave) {
				ShockWave wave = (ShockWave) obj;
				if (wave.isExpired()) {
					this.objectCollection.remove(wave);
				}
			}
		}
		
		for (int j = chargedEnergyStations; j < 2; j++) {
			this.objectCollection.add(new EnergyStation(randomInt(50, 120), randomInt(0, width), randomInt(0, height)));
		}
		
		// Check to see the player lost.
		if (PlayerRobot.getInstance().isDead()) {
			System.out.println("Robot is unable to move, starting next life...\n");
			playExplosionSound(); 
			this.startNextLife();
		}
		
		this.setChanged();
		this.notifyObservers();
	}
	
	
	
	/**
	 * Increases the player robot's speed by a small amount.
	 */
	public void acceleratePlayerRobot() {
		player.setSpeed(player.getSpeed() + SPEED_INCREMENT);
		System.out.println("Player speed is now " + player.getSpeed() + ".\n");
		this.setChanged();
		this.notifyObservers();
	}
	
	/**
	 * Decreases the player robot's speed by a small amount.
	 */
	public void deceleratePlayerRobot() {
		player.setSpeed(player.getSpeed() - SPEED_INCREMENT);
		System.out.println("Player speed is now " + player.getSpeed() + ".\n");
		this.setChanged();
		this.notifyObservers();
	}
	
	/**
	 * Turns the robot player left (counter-clockwise) by a small amount.
	 */
	public void turnPlayerRobotLeft() {
		player.steerLeft();
		System.out.println("New steering direction is " + player.getSteeringDirection() + ".\n");
		this.setChanged();
		this.notifyObservers();
	}
	
	/**
	 * Turns the robot player right (clockwise) by a small amount.
	 */
	public void turnPlayerRobotRight() {
		player.steerRight();
		System.out.println("New steering direction is " + player.getSteeringDirection() + ".\n");
		this.setChanged();
		this.notifyObservers();
	}
	
	
	/**
	 * Initializes the game world again for the player's next life and checks for game over.
	 * The game objects are deleted and recreated for a clean slate.
	 */
	public void startNextLife() {
		this.objectCollection.clear();
		this.init();
		this.lives--;
		
		System.out.println("You have " + this.lives + " lives remaining...\n");
		
		if (this.lives == 0) {
			System.out.println("Game over, you failed!");
			System.exit(0);
		}
		this.setChanged();
		this.notifyObservers();
	}
	
	/**
	 * @return		string representation of the all the game objects currently in play.
	 */
	public String toString() {
		String state = "[MAP] Listing the GameObjects in play...";
		IIterator iterator = this.objectCollection.getIterator();
		
		while (iterator.hasNext()) {
			GameObject object = iterator.getNext();
			state += "\n\t" + object.toString();
		}
		return state;
	}
	
	
	/**
	 * Getter for clock.
	 * 
	 * @return			current clock value
	 */
	public int getClock() {
		return this.clock;
	}
	
	/**
	 * Getter for lives.
	 * 
	 * @return			current number of lives left
	 */
	public int getLives() {
		return this.lives;
	}
	
	/**
	 * Setter for soundEnabled.
	 * 
	 * @param enabled			Boolean for whether or not sound should be enabled.
	 */
	public void setSoundEnabled(boolean enabled) {
		this.soundEnabled = enabled;
		this.setChanged();
		this.notifyObservers();
		
		if (enabled) {
			GameWorld.unmuteAllSounds();
		}
		else {
			GameWorld.muteAllSounds();
		}
	}
	
	/**
	 * Getter for soundEnabled.
	 * 
	 * @return				whether sound enabled is true or false
	 */
	public boolean getSoundEnabled() {
		return this.soundEnabled;
	}
	
	/**
	 * Getter for last base reached by the player.
	 * 
	 * @return				the last base the player has reached
	 */
	public int getPlayerLastBase() {
		return this.player.getLastBaseReached();
	}
	
	/**
	 * Getter for the player's energy level.
	 * 
	 * @return				current energy level for the player
	 */
	public float getPlayerEnergyLeft() {
		return this.player.getEnergyLevel();
	}
	
	/**
	 * Getter for the player's damage level.
	 * 
	 * @return				current damage level for the player.
	 */
	public int getPlayerDamage() {
		return this.player.getDamageLevel();
	}
	
	/**
	 * Getter for the x coordinate of the specified base.
	 * 
	 * @param base				base number to locate
	 * @return					x coordinate of the specified base, -1 if base number is invalid
	 */
	public float getBaseLocationX(int base) {
		IIterator iterator = this.objectCollection.getIterator();
		
		while (iterator.hasNext()) {
			GameObject object = iterator.getNext();
			
			if (object instanceof Base) {
				Base b = (Base) object;
				if (b.getSequenceNumber() == base)
					return b.getLocationX();
			}
		}
		
		System.out.println("Couldn't find x location for base #" + base);
		return -1; // Returns an invalid location in case base number is invalid.
	}
	
	/**
	 * Getter for the y coordinate of the specified base.
	 * 
	 * @param base				base number to locate
	 * @return					y coordinate of the specified base, -1 if base number is invalid
	 */
	public float getBaseLocationY(int base) {
		IIterator iterator = this.objectCollection.getIterator();
		
		while (iterator.hasNext()) {
			GameObject object = iterator.getNext();
			
			if (object instanceof Base) {
				Base b = (Base) object;
				if (b.getSequenceNumber() == base)
					return b.getLocationY();
			}
		}
		
		System.out.println("Couldn't find y location for base #" + base + "\n");
		return -1; // Returns an invalid location in case base number is invalid.
	}
	
	/**
	 * Changes the strategies of all the NPRs that are currently in play and also increments the last base reached values.
	 */
	public void changeNPRStrategies() {
		IIterator iterator = this.objectCollection.getIterator();
		
		while (iterator.hasNext()) {
			GameObject object = iterator.getNext();
			
			if (object instanceof NonPlayerRobot) {
				NonPlayerRobot robot = (NonPlayerRobot) object;
				
				// The NPRs switch to race if they were attacking before and vice versa.
				if (robot.getStrategy() instanceof RaceStrategy) {
					robot.setStrategy(new AttackStrategy(robot));
				}
				else {
					robot.setStrategy(new RaceStrategy(robot, this));
				}
			}
		}
		System.out.println("Strategies for each robot have been flipped.\n");
		
		this.setChanged();
		this.notifyObservers();
	}
	
	/**
	 * @return			an iterator for objectCollection
	 */
	public IIterator getObjectCollectionIterator() {
		return this.objectCollection.getIterator();
	}
	
	/**
	 * Plays "crash.wav".
	 */
	public static void playCrashSound() {
		if (crashSound == null) {
			crashSound = new Sound("crash.wav", "wav");
		}
		crashSound.play();
	}
	
	/**
	 * Plays "charge.wav".
	 */
	public static void playChargeSound() {
		if (chargeSound == null) {
			
		}
		chargeSound.play();
	}
	
	/**
	 * Plays "explosion.wav".
	 */
	public static void playExplosionSound() {
		if (explosionSound == null) {
			explosionSound = new Sound("explosion.wav", "wav");
		}
		explosionSound.play();
	}
	
	/**
	 * Plays "background.mp3".
	 */
	public static void playBackgroundSound() {
		if (backgroundSound == null) {
			backgroundSound = new BGSound("background.mp3", "mp3");
		}
		backgroundSound.run();
	}
	
	/**
	 * Plays "crash.wav".
	 */
	public static Sound[] getAllSounds() {
		return new Sound[] {crashSound, chargeSound, explosionSound, backgroundSound};
	}
	
	/**
	 * Creates the sound objects that the game uses.
	 */
	public static void createSounds() {
		crashSound = new Sound("crash.wav", "wav");
		backgroundSound = new BGSound("background.mp3", "mp3");
		chargeSound = new Sound("charge.wav", "wav");
		explosionSound = new Sound("explosion.wav", "wav");
	}
	
	/**
	 * Stops all sounds.
	 */
	public static void stopAllSounds() {
		for (Sound s : getAllSounds()) {
			if (s == null) continue;
			s.stop();
		}
	}
	
	/**
	 * Mutes all sounds.
	 */
	public static void muteAllSounds() {
		for (Sound s : getAllSounds()) {
			if (s == null) continue;
			s.mute();
		}
	}
	
	/**
	 * Unmutes all sounds.
	 */
	public static void unmuteAllSounds() {
		for (Sound s : getAllSounds()) {
			if (s == null) continue;
			s.unmute();
		}
	}
	
	/**
	 * Setter for movingObject.
	 * @param b
	 */
	public void setMovingObject(boolean b) {
		this.movingObject = b;
	}
	
	/**
	 * Getter for movingObject.
	 * @return
	 */
	public boolean isMovingObject() {
		return this.movingObject;
	}
	
	/**
	 * Deselects any currently selected object.
	 */
	public void deselectAllObjects() {
		this.setMovingObject(false);
		IIterator iterator = this.getObjectCollectionIterator();
		
		while (iterator.hasNext()) {
			GameObject obj = iterator.getNext();
			if (obj instanceof ISelectable) {
				((ISelectable) obj).setSelected(false);
			}
		}
	}
}
