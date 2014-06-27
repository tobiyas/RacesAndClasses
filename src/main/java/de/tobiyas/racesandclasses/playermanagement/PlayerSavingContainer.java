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
package de.tobiyas.racesandclasses.playermanagement;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;

import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;

@Entity
@Table(name="_player_general_infos")
public class PlayerSavingContainer {

	@Id
	@NotEmpty
	private UUID playerUUID;

	@NotNull
	private int playerLevel;
	
	@NotNull
	private int playerLevelExp;
	
	@NotNull
	private boolean hasGod;
	
	
	/**
	 * Generates a new Container with default Values.
	 * 
	 * @param player
	 * @return
	 */
	public static PlayerSavingContainer generateNewContainer(RaCPlayer player){
		PlayerSavingContainer container = new PlayerSavingContainer();
		container.playerUUID = player.getUniqueId();
		
		container.playerLevel = 1;
		container.playerLevelExp = 1;
		
		container.hasGod = false;
		return container;
	}



	public int getPlayerLevel() {
		return playerLevel;
	}



	public void setPlayerLevel(int playerLevel) {
		this.playerLevel = playerLevel;
	}



	public int getPlayerLevelExp() {
		return playerLevelExp;
	}



	public void setPlayerLevelExp(int playerLevelExp) {
		this.playerLevelExp = playerLevelExp;
	}



	public UUID getPlayerUUID() {
		return playerUUID;
	}



	public void setPlayerUUID(UUID playerUUID) {
		this.playerUUID = playerUUID;
	}



	public boolean isHasGod() {
		return hasGod;
	}



	public void setHasGod(boolean hasGod) {
		this.hasGod = hasGod;
	}
	
}
