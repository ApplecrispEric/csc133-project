package com.mycompany.a4;

import com.codename1.ui.Command;
import com.codename1.ui.Dialog;
import com.codename1.ui.TextArea;
import com.codename1.ui.TextField;
import com.codename1.ui.events.ActionEvent;


/**
 * This class represents command objects that have to do with controlling the player robot (movement and collisions).
 * 
 * @author Eric Brown
 */
public class PlayerRobotCommand extends Command {
	private GameWorld gw;
	
	/**
	 * Constructor for PlayerRobotCommand.
	 * 
	 * @param command			name of the command
	 * @param gw				the game world that the command acts upon
	 */
	public PlayerRobotCommand(String command, GameWorld gw) {
		super(command);
		this.gw = gw;
	}
	
	/**
	 * Determines the appropriate method call depending upon the name of the command that was invoked.
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		switch (this.getCommandName()) {
		case "Accelerate":  // Accelerate the player.
			System.out.println("Accelerating the player...");
			gw.acceleratePlayerRobot();
			break;
		case "Brake":  // Decelerate the player (brake).
			System.out.println("Decelerating the player...");
			gw.deceleratePlayerRobot();
			break;
		case "Left":  // Steer player left (negative compass direction).
			System.out.println("Steering player left...");
			gw.turnPlayerRobotLeft();
			break;
		case "Right":  // Steer player right (positive compass direction).
			System.out.println("Steering player right...");
			gw.turnPlayerRobotRight();
			break;
		}
	}
}
