/*******************************************************************************
 * Copyright 2014 Tobias Welther
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.tobiyas.racesandclasses.playermanagement.display;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;

public abstract class AbstractScoreBoardDisplay extends AbstractDisplay {

	/**
	 * The Objective of the Scoreboard.
	 */
	protected final String SCOREBOARD_OBJECTIVE_NAME;
	
	/**
	 * This is an Addition to the board name. It is concatenated to it.
	 */
	protected String boardAddition = "";
	
	/**
	 * The ScoreBoards to use.
	 */
	protected Scoreboard boardToShow = Bukkit.getScoreboardManager().getNewScoreboard();
	
	/**
	 * The old board stored while the RAC board is shown.
	 */
	protected Scoreboard oldBoardToStore;

	/**
	 * Fading map of the Scoreboards.
	 */
	protected int fadingTime = 0;
			
	/**
	 * The TaskId of the Bukkit task for fading
	 */
	protected int bukkitTaskId = -1;
	
	
	public AbstractScoreBoardDisplay(RaCPlayer player, DisplayInfos displayInfo) {
		super(player, displayInfo);
		
		SCOREBOARD_OBJECTIVE_NAME = ChatColor.translateAlternateColorCodes('&',
				RacesAndClasses.getPlugin().getConfigManager()
				.getGeneralConfig().getConfig_gui_scoreboard_name());

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
		
		String objectiveName = SCOREBOARD_OBJECTIVE_NAME + boardAddition;
		if(player == null || !player.isOnline()){
			return;
		}
		
		Scoreboard bordOfPlayer = boardToShow;
		
		Objective objective = bordOfPlayer.getObjective(objectiveName);
		
		if(objective == null){
			objective = bordOfPlayer.registerNewObjective(objectiveName, "dummy");
		}

		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(objectiveName);
		
		
		removeOldValues(bordOfPlayer);
		addValues(objective);
		
		delayFading(player.getPlayer(), 5);
		player.getPlayer().setScoreboard(bordOfPlayer);
	}

	/**
	 * Adds all values to the objective passed
	 * 
	 * @param objective to add all Values
	 */
	protected abstract void addValues(Objective objective);

	
	/**
	 * Remove all old stuff.
	 * 
	 * @param bordOfPlayer to remove from.
	 */
	protected abstract void removeOldValues(Scoreboard bordOfPlayer);

	/**
	 * Delays the Fading in a time in seconds.
	 * 
	 * @param time to delay
	 */
	protected void delayFading(Player player, int time){
		this.oldBoardToStore = player.getScoreboard();
		this.fadingTime = time;
	}
	
	
	/**
	 * Ticks the current ticking map.
	 */
	protected void tickFading(){
		if(fadingTime < 0) return;
		
		fadingTime --;

		if(fadingTime < 0){
			if(player != null && player.isOnline()){
				Scoreboard oldBoard = this.oldBoardToStore;
				Scoreboard currentBoard = player.getPlayer().getScoreboard();
				
				if(currentBoard != boardToShow){
					//check again in 2 seconds.
					fadingTime = 2;
					return;
				}
				
				if(oldBoard == null){
					oldBoard = Bukkit.getScoreboardManager().getNewScoreboard();
				}
				
				player.getPlayer().setScoreboard(oldBoard);
			}
		}
	}

	@Override
	public void unregister() {
		
		if(bukkitTaskId > 0){
			Bukkit.getScheduler().cancelTask(bukkitTaskId);
		}
	}
	
	
}
