package de.tobiyas.races.tutorial.steps;

import org.bukkit.ChatColor;

import de.tobiyas.races.tutorial.TutorialPath;
import de.tobiyas.races.util.tutorial.TutorialState;

public class StartState extends AbstractStep {
	
	public StartState(String playerName, TutorialPath writeBack){
		this.playerName = playerName;
		this.writeBack = writeBack;
		this.currentState = TutorialState.start;
		
		this.startMessage = ChatColor.YELLOW + "[Tutorial]: You have enabled the tutorial for the Plugin: " + ChatColor.RED + "Races" + ChatColor.YELLOW + "." +
							" It will give you a breaf overview of some of the features. To skip a step, use: " + ChatColor.RED + "/racestutorial skip" + ChatColor.YELLOW +
							". To get to the next step , use this command.";
		this.doneMessage = ChatColor.YELLOW + "[Tutorial]: Very good!";
		
		finished = false;
		postStartMessage();
	}
}
