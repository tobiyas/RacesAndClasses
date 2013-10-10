package de.tobiyas.racesandclasses.playermanagement.health.display;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;


public class ScoreBoardDisplayBar extends AbstractDisplay{

	private String oldScoreName = "";
	
	private boolean firstStart = true;
	
	/**
	 * Inits the Score board with the player to show to.
	 * 
	 * @param player for which it is
	 */
	public ScoreBoardDisplayBar(String playerName, DisplayInfos displayInfos) {
		super(playerName, displayInfos);	
	}


	@Override
	public void display(double currentHealth, double maxHealth) {
		String healthBarString = calcForHealth(currentHealth, maxHealth, 7);
		Player player = Bukkit.getPlayer(playerName);
		if(player == null || !player.isOnline()){
			return;
		}
		
		Scoreboard bordOfPlayer = player.getScoreboard();
		if(bordOfPlayer == null || firstStart){
			bordOfPlayer = Bukkit.getScoreboardManager().getNewScoreboard();
			player.setScoreboard(bordOfPlayer);
			firstStart = false;
		}
		
		String objectiveName = displayInfo.getMidValueColor() + displayInfo.getName();
		Objective objective = bordOfPlayer.getObjective(objectiveName);
		
		if(objective == null){
			objective = bordOfPlayer.registerNewObjective(objectiveName, "dummy");
		}
		
		bordOfPlayer.resetScores(Bukkit.getOfflinePlayer(oldScoreName));
		oldScoreName = healthBarString;
		
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(objectiveName);
		
		Score score = objective.getScore(Bukkit.getOfflinePlayer(healthBarString));
		score.setScore((int) Math.ceil(currentHealth));
	}

}
