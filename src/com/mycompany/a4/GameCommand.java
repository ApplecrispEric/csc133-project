package com.mycompany.a4;

import com.codename1.ui.CheckBox;
import com.codename1.ui.Command;
import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.codename1.ui.events.ActionEvent;


/**
 * This class represents command objects that alter the universal game state such as options and the clock.
 * 
 * @author Eric Brown
 */
public class GameCommand extends Command {
	private GameWorld gw;
	
	/**
	 * Constructor for GameCommand.
	 * 
	 * @param command			name of the command
	 * @param gw				game world that the command acts upon
	 */
	public GameCommand(String command, GameWorld gw) {
		super(command);
		this.gw = gw;
	}
	
	/**
	 * Determines the appropriate method call depending upon the name of the command that was invoked.
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		switch (this.getCommandName()) {
		case "Exit":  // Exit the application.
			boolean quit = Dialog.show("Confirm quit?", "Are you sure you want to quit?", "OK", "Cancel");
			if (quit) {
				Display.getInstance().exitApplication();
			}
			break;
		case "Enable Sound":  // Toggle sound.
			boolean enabled = ((CheckBox)event.getComponent()).isSelected();
			System.out.println("Setting sound enabled to " + enabled + "...");
			gw.setSoundEnabled(enabled);
			break;
		case "About":  // Display application metadata.
			System.out.println("Showing the about dialog...");
			String about = 
					"Developer: Eric Brown\n"
					+ "Class: CSC 133\n"
					+ "Professor: Dr. Kin Chung Kwan\n"
					+ "Assignment 3 Build";
			
			Dialog.show("About", about, "OK", null);
			break;
		case "Help":
			System.out.println("Showing the help dialog...");
			String help = 
					"A: Accelerate\n"
					+ "B: Brake\n"
					+ "L: Turn Left\n"
					+ "R: Turn Right\n";
			
			Dialog.show("Commands", help, "OK", null);
			break;
		case "Change Strategies":
			System.out.println("Changing NPR strategies...");
			gw.changeNPRStrategies();
			break;
		}
	}
}
