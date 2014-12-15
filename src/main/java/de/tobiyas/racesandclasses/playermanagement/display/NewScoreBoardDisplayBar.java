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
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.util.bukkit.versioning.CertainVersionChecker;


public class NewScoreBoardDisplayBar extends AbstractScoreBoardDisplay{

	
	public NewScoreBoardDisplayBar(RaCPlayer player, DisplayInfos displayInfo) {
		super(player, displayInfo);
	}


	protected double currentHealth = 0;
	protected double maxHealth = 20;
	
	/**
	 * The Last addition.
	 */
	protected String oldName = null;
	
	@Override
	public void display(double currentHealth, double maxHealth) {
		this.currentHealth = currentHealth;
		this.maxHealth = maxHealth;
		
		super.display(currentHealth, maxHealth);
	}
	
	

	@Override
	protected void addValues(Objective objective) {
		String barString = "";
		
		if(oldName != null){
			if(CertainVersionChecker.isAbove1_7()){
				objective.getScoreboard().resetScores(oldName);
			}else{
				objective.getScoreboard().resetScores(Bukkit.getOfflinePlayer(oldName));
			}
		}
		
		if(displayInfo.useName()){
			barString = displayInfo.getName();
		}else{
			barString = calcForHealth(currentHealth, maxHealth, 7);			
		}
		
		if(player == null || !player.isOnline()){
			return;
		}
		
		//This deprecation is needed to show a false name in Scoreboard.
		Score score = CertainVersionChecker.isAbove1_7() ? objective.getScore(barString) : objective.getScore(Bukkit.getOfflinePlayer(barString));
		score.setScore((int) Math.ceil(currentHealth));
		
		oldName = barString;
	}


	@Override
	protected void removeOldValues(Scoreboard bordOfPlayer) {
		//should remove all occurences.
		//This deprecationa are needed to show a false name in Scoreboard.
		/*for(OfflinePlayer scorePlayer : bordOfPlayer.getPlayers()){
			if(scorePlayer.getName().startsWith(this.colorHigh + "|")
					|| scorePlayer.getName().startsWith(this.colorHigh + "" + this.colorLow + "|")){
				bordOfPlayer.resetScores(scorePlayer);
			}
			
		}*/
	}



	@Override
	public void unregister() {
		super.unregister();
		
		if(fadingTime > 0){
			if(player != null){
				if(oldBoardToStore != null){
					player.getPlayer().setScoreboard(oldBoardToStore);
				}else{
					player.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
				}
			}
		}
	}

	
}
