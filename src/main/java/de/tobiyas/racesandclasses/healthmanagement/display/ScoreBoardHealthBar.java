package de.tobiyas.racesandclasses.healthmanagement.display;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;


public class ScoreBoardHealthBar extends AbstractHealthDisplay{

	
	/**
	 * Inits the Score board with the player to show to.
	 * 
	 * @param player
	 */
	public ScoreBoardHealthBar(String playerName) {
		super(playerName);
		
	}


	@Override
	public void display(double currentHealth, double maxHealth) {
		String healthBarString = calcForHealth(currentHealth, maxHealth, 7);
		
		Scoreboard bordOfPlayer = Bukkit.getScoreboardManager().getNewScoreboard();
		
		Objective objective = bordOfPlayer.registerNewObjective(ChatColor.YELLOW + "health", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR); //TODO evaluate which
		
		objective.setDisplayName(ChatColor.YELLOW + "Health");
		
		Score score = objective.getScore(Bukkit.getOfflinePlayer(healthBarString));
		score.setScore((int) Math.ceil(currentHealth));
	}

}
