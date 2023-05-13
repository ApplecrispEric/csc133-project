package com.mycompany.a4;

import com.codename1.ui.Command;
import com.codename1.ui.events.ActionEvent;
import com.mycompany.a4.Game.Mode;

/**
 * This class represents the command to change the current game mode.
 * @author Eric Brown
 */
public class GameModeCommand extends Command {
	
	private Game target;
	
	/**
	 * Constructor for GameModeCommand.
	 * 
	 * @param command			name of command
	 * @param target			game to invoke command upon
	 */
	public GameModeCommand(String command, Game target) {
		super(command);
		this.target = target;
	}
	
	/**
	 * Called when button is clicked on.
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		switch (this.getCommandName()) {
		case "Play":  // Resume the game.
			target.setGameMode(Mode.PLAY);
			break;
		case "Pause":  // Pause the game.
			target.setGameMode(Mode.PAUSED);
			break;
		}
	}
}
