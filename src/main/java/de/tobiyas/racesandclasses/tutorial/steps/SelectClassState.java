package de.tobiyas.racesandclasses.tutorial.steps;

import org.bukkit.ChatColor;

import de.tobiyas.racesandclasses.tutorial.TutorialPath;
import de.tobiyas.racesandclasses.util.tutorial.TutorialState;

public class SelectClassState extends AbstractStep {

	public SelectClassState(String playerName, TutorialPath writeBack){
		this.playerName = playerName;
		this.writeBack = writeBack;
		this.currentState = TutorialState.selectClass;
		
		this.startMessage = ChatColor.YELLOW + "[Tutorial]: Now we come to the real Class selection. Please select a Class using " + ChatColor.RED +
				"/class select <Class-Name>" + ChatColor.YELLOW + ". To get to the next step, select a Class using this command.";

		this.doneMessage = ChatColor.YELLOW + "[Tutorial]: Very good!";
		
		finished = false;
		postStartMessage();
	}
}
