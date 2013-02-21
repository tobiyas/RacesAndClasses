package de.tobiyas.races.tutorial.steps;

import org.bukkit.ChatColor;

import de.tobiyas.races.tutorial.TutorialPath;
import de.tobiyas.races.util.tutorial.TutorialState;

public class InfoClassState extends AbstractStep{

	public InfoClassState(String playerName, TutorialPath writeBack){
		this.playerName = playerName;
		this.writeBack = writeBack;
		this.currentState = TutorialState.infoClass;
		
		this.startMessage = ChatColor.YELLOW + "[Tutorial]: Now to Class Selection. First you have to inform yourself for which Classes there are. This can be done with:" +
				ChatColor.RED + " /class list" + ChatColor.YELLOW + ". To get to the next step, you need to use this command.";
		this.doneMessage = ChatColor.YELLOW + "[Tutorial]: Very good!";
		
		finished = false;
		postStartMessage();
	}
}
