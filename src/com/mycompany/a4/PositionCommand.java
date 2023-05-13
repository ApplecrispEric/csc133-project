package com.mycompany.a4;

import com.codename1.ui.Command;
import com.codename1.ui.events.ActionEvent;


/**
 * This class represents command objects that have to do with changing the positions of gameObjects.
 * 
 * @author Eric Brown
 */
public class PositionCommand extends Command {
	private GameWorld gw;
	
	/**
	 * Constructor for PositionCommand.
	 */
	public PositionCommand(GameWorld target) {
		super("Position");
		this.gw = target;
	}
	
	/**
	 * Called when button is pressed.
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		switch (this.getCommandName()) {
		case "Position":
			this.gw.setMovingObject(true);
			break;
		}
	}
}
