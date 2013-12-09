package de.tobiyas.racesandclasses.playermanagement.display;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import de.tobiyas.racesandclasses.RacesAndClasses;


public class ScoreBoardDisplayBar extends AbstractDisplay{

	/**
	 * The Objective of the Scoreboard.
	 */
	private static final String SCOREBOARD_OBJECTIVE_NAME = "RaC";
	
	
	/**
	 * The Map of ScoreBoards to show.
	 */
	private static ConcurrentMap<String, Scoreboard> boardMap = new ConcurrentHashMap<String, Scoreboard>();
	
	/**
	 * Fading map of the Scoreboards.
	 */
	private static Map<String, FadingHolder> fadingMap = new HashMap<String, FadingHolder>();
	
	/**
	 * The TaskId of the Bukkit task for fading
	 */
	private static int bukkitTaskId = -1;
	
	/**
	 * Inits the Score board with the player to show to.
	 * 
	 * @param player for which it is
	 */
	public ScoreBoardDisplayBar(String playerName, DisplayInfos displayInfos) {
		super(playerName, displayInfos);
		
		if(bukkitTaskId < 0 || 
				!(Bukkit.getScheduler().isCurrentlyRunning(bukkitTaskId) 
						|| Bukkit.getScheduler().isQueued(bukkitTaskId))){
			bukkitTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(RacesAndClasses.getPlugin(), new Runnable() {
				
				@Override
				public void run() {
					tickFading();
				}
				
			}, 20, 20);
		}
	}


	@Override
	public void display(double currentHealth, double maxHealth) {
		if(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_disableAllScoreboardOutputs()){
			//server does not want ANY Scoreboards from RaC. 
			//Plugin is sad. :(
			return;
		}
		
		String barString = calcForHealth(currentHealth, maxHealth, 7);
		Player player = Bukkit.getPlayer(playerName);
		if(player == null || !player.isOnline()){
			return;
		}
		
		boardMap.putIfAbsent(playerName, Bukkit.getScoreboardManager().getNewScoreboard());		
		Scoreboard bordOfPlayer = boardMap.get(playerName);
		
		String objectiveName = ChatColor.YELLOW + SCOREBOARD_OBJECTIVE_NAME;
		Objective objective = bordOfPlayer.getObjective(objectiveName);
		
		if(objective == null){
			objective = bordOfPlayer.registerNewObjective(objectiveName, "dummy");
		}
		
		
		//should remove all occurences.
		for(OfflinePlayer scorePlayer : bordOfPlayer.getPlayers()){
			if(scorePlayer.getName().startsWith(this.colorHigh + "|")
					|| scorePlayer.getName().startsWith(this.colorHigh + "" + this.colorLow + "|")){
				bordOfPlayer.resetScores(scorePlayer);
			}
			
		}
		
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(objectiveName);
		
		Score score = objective.getScore(Bukkit.getOfflinePlayer(barString));
		score.setScore((int) Math.ceil(currentHealth));
		
		delayFading(player, 3);
		player.setScoreboard(bordOfPlayer);
	}

	
	/**
	 * Delays the Fading in a time in seconds.
	 * 
	 * @param playerName to delay
	 * @param time to delay
	 */
	private void delayFading(Player player, int time){
		synchronized (fadingMap) {
			Scoreboard oldBoard = player.getScoreboard();
			if(fadingMap.containsKey(playerName)){
				oldBoard = fadingMap.get(playerName).oldBoard;
			}
			
			if(oldBoard.getObjective(SCOREBOARD_OBJECTIVE_NAME) != null){
				oldBoard = Bukkit.getScoreboardManager().getNewScoreboard();
			}
			
			FadingHolder newHolder = new FadingHolder();
			newHolder.time = time;
			newHolder.oldBoard = oldBoard;
			
			fadingMap.put(playerName, newHolder);
		}
	}
	
	
	/**
	 * Ticks the current ticking map.
	 */
	private void tickFading(){
		synchronized (fadingMap) {
			Iterator<Entry<String, FadingHolder>> it = fadingMap.entrySet().iterator();
			
			Map.Entry<String, FadingHolder> currentEntry = null;
			while(it.hasNext()){
				currentEntry = it.next();
				
				currentEntry.getValue().time--;
				if(currentEntry.getValue().time <= 0){
					Scoreboard oldBoard = currentEntry.getValue().oldBoard;
					Player player = Bukkit.getPlayer(currentEntry.getKey());
					
					if(oldBoard == null){
						oldBoard = Bukkit.getScoreboardManager().getNewScoreboard();
					}
					
					if(player != null && player.isOnline()
							&& player.getScoreboard() == boardMap.get(playerName)){
						player.setScoreboard(oldBoard);
					}
					
					it.remove();
				}
			}
		}
	}
	
	
	/**
	 * Simple pair holder.
	 * 
	 * @author tobiyas
	 *
	 */
	private class FadingHolder{
		private int time;
		private Scoreboard oldBoard;
	}

}
