package de.tobiyas.races.tutorial.steps;

import org.bukkit.ChatColor;

import de.tobiyas.races.tutorial.TutorialPath;
import de.tobiyas.races.util.tutorial.TutorialState;

public class InfoRaceState extends AbstractStep {
	
	public InfoRaceState(String playerName, TutorialPath writeBack){
		this.playerName = playerName;
		this.writeBack = writeBack;
		this.currentState = TutorialState.infoRace;
		
		this.startMessage = ChatColor.YELLOW + "[Tutorial]: Now to Race Selection. First you have to inform yourself for which races there are. This can be done with:" +
							ChatColor.RED + " /race list" + ChatColor.YELLOW + ". To get to the next step, you need to use this command.";
		this.doneMessage = ChatColor.YELLOW + "[Tutorial]: Very good!";
		
		finished = false;
		postStartMessage();
	}

}
