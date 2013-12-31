package de.tobiyas.racesandclasses.playermanagement.display;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;


public class NewScoreBoardDisplayBar extends AbstractScoreBoardDisplay{

	
	public NewScoreBoardDisplayBar(String playerName, DisplayInfos displayInfo) {
		super(playerName, displayInfo);
	}


	protected double currentHealth = 0;
	protected double maxHealth = 20;
	
	@Override
	public void display(double currentHealth, double maxHealth) {
		this.currentHealth = currentHealth;
		this.maxHealth = maxHealth;
		
		super.display(currentHealth, maxHealth);
	}
	
	

	@Override
	protected void addValues(Objective objective) {
		String barString = "";
		if(displayInfo.useName()){
			barString = displayInfo.getName();
		}else{
			barString = calcForHealth(currentHealth, maxHealth, 7);			
		}
		
		Player player = Bukkit.getPlayer(playerName);
		if(player == null || !player.isOnline()){
			return;
		}
		
		Score score = objective.getScore(Bukkit.getOfflinePlayer(barString));
		score.setScore((int) Math.ceil(currentHealth));
	
	}


	@Override
	protected void removeOldValues(Scoreboard bordOfPlayer) {
		//should remove all occurences.
		for(OfflinePlayer scorePlayer : bordOfPlayer.getPlayers()){
			if(scorePlayer.getName().startsWith(this.colorHigh + "|")
					|| scorePlayer.getName().startsWith(this.colorHigh + "" + this.colorLow + "|")){
				bordOfPlayer.resetScores(scorePlayer);
			}
			
		}
	}



	@Override
	public void unregister() {
		super.unregister();
		
		if(fadingTime > 0){
			Player player = Bukkit.getPlayer(playerName);
			if(player != null){
				if(oldBoardToStore != null){
					player.setScoreboard(oldBoardToStore);
				}else{
					player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
				}
			}
		}
	}

	
}
