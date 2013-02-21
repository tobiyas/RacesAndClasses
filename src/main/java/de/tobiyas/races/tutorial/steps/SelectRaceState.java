package de.tobiyas.races.tutorial.steps;

import org.bukkit.ChatColor;

import de.tobiyas.races.tutorial.TutorialPath;
import de.tobiyas.races.util.tutorial.TutorialState;

public class SelectRaceState extends AbstractStep {

	public SelectRaceState(String playerName, TutorialPath writeBack){
		this.playerName = playerName;
		this.writeBack = writeBack;
		this.currentState = TutorialState.selectRace;
		
		this.startMessage = ChatColor.YELLOW + "[Tutorial]: Now we come to the real Race selection. Please select a Race using " + ChatColor.RED +
							"/race select <Race-Name>" + ChatColor.YELLOW + ". To get to the next step, select a Race using this command. ";
		
		this.doneMessage = ChatColor.YELLOW + "[Tutorial]: Very good!";
		
		finished = false;
		postStartMessage();
	}
}
