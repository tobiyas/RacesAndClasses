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
package de.tobiyas.racesandclasses.playermanagement.leveling;

import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.PlayerSavingContainer;

public interface PlayerLevelManager {

	/**
	 * Returns the current Level of the Player
	 * 
	 * @return
	 */
	public int getCurrentLevel();
	
	
	/**
	 * Returns the current EXP of the Level
	 * 
	 * @return
	 */
	public int getCurrentExpOfLevel();
	
	
	/**
	 * @return the player
	 */
	public RaCPlayer getPlayer();
	
	
	/**
	 * @param currentLevel the currentLevel to set
	 */
	public void setCurrentLevel(int level);
	
	/**
	 * @param currentExpOfLevel the currentExpOfLevel to set
	 */
	public void setCurrentExpOfLevel(int currentExpOfLevel);
	
	
	/**
	 * Adds EXP for a Player. 
	 * If the maxExp is reached, the player will currentLevel up.
	 * 
	 * WARNING: This will cause an event which can be canceled.
	 * When it is canceled, there will be no EXP added.
	 * 
	 * @param exp to add
	 * 
	 * @return true if the EXP were added, false if someone
	 * Canceled the Event or EXP < 1
	 */
	public boolean addExp(int exp);
	
	
	/**
	 * Removes EXP for a Player. 
	 * If 0 exp of the currentLevel is reached, the player will currentLevel down.
	 * 
	 * WARNING: This will cause an event which can be canceled.
	 * When it is canceled, there will be no EXP removed or the EXP is < 1.
	 * 
	 * @param exp to remove
	 * 
	 * @return true if the EXP were removed, false if someone
	 * Canceled the Event
	 */
	public boolean removeExp(int exp);
	
	
	/**
	 * Saves the Container to the playerData File.
	 */
	public void save();
	
	
	/**
	 * Saves the current values to the passed container.
	 * 
	 * @param container
	 */
	public void saveTo(PlayerSavingContainer container);
	
	
	/**
	 * Reloads the Data from the passed container.
	 * 
	 * @param container to load from
	 */
	public void reloadFromPlayerSavingContaienr(PlayerSavingContainer container);
	
	
	/**
	 * Checks if the Player has any Levels to go Up or go Down.
	 * They will be fired as Event.
	 */
	public void checkLevelChanged();
	
	/**
	 * Reloads the Data from the Player Data file or DB.
	 */
	public void reloadFromYaml();


	/**
	 * Forces the Display to output
	 */
	public void forceDisplay();


	/**
	 * Tells if the player can be removed X exp.
	 * 
	 * @param toRemove to be removed later.
	 * 
	 * @return true if can be removed.
	 */
	public boolean canRemove(int toRemove);


	/**
	 * Adds a level to the player.
	 * <br>Must be positive
	 * 
	 * @param value to add.
	 */
	public void addLevel(int value);
	
	/**
	 * Removes a level from the player.
	 * <br>Must be positive.
	 * <br>Can not go below 1.
	 * 
	 * @param value to remove.
	 */
	public void removeLevel(int value);

}
