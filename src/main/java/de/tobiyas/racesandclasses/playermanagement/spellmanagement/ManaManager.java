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
package de.tobiyas.racesandclasses.playermanagement.spellmanagement;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.resolvers.WorldResolver;
import de.tobiyas.racesandclasses.playermanagement.display.Display;
import de.tobiyas.racesandclasses.playermanagement.display.Display.DisplayInfos;
import de.tobiyas.racesandclasses.playermanagement.display.DisplayGenerator;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.MagicSpellTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.MagicSpellTrait.CostType;

public class ManaManager implements Observer {

	/**
	 * The Plugin to call Stuff on
	 */
	private RacesAndClasses plugin = RacesAndClasses.getPlugin();
	
	/**
	 * The Player this mana Manager belongs.
	 */
	private final RaCPlayer player;
	
//	
//	/**
//	 * The Maximal Mana the Player contains.
//	 */
//	private double maxMana;
	
	
	/**
	 * The current Mana the Player contains.
	 */
	private double currentMana;
	
	/**
	 * The Display displaying mana change to the Player
	 */
	private Display manaDisplay;
	
	/**
	 * The Max manas.
	 */
	private final Map<String,Double> maxManaBonus = new HashMap<String, Double>();
	
	
	/**
	 * Generates a new Mana Manager for the Player passed.
	 * 
	 * @param player
	 */
	public ManaManager(RaCPlayer player) {
		this.player = player;
		this.currentMana = 0;
		
		rescanDisplay();
		
		player.getConfig().addObserver(this);
	}
	
	/**
	 * This re-registers the display.
	 * <br>Meaning to throw the old one away and generate a new one.
	 */
	private void rescanDisplay(){
		if(manaDisplay != null){
			manaDisplay.unregister();
		}
		
		manaDisplay = DisplayGenerator.generateDisplay(player, DisplayInfos.MANA);
	}
	
	
	/**
	 * Rescans the Player and resets the Max Mana if needed.
	 */
	public void rescanPlayer(){
		if(player == null || !player.isOnline() || WorldResolver.isOnDisabledWorld(player)){
			return;
		}
		
		RaceContainer race = player.getRace();
		if(race != null){
			addMaxManaBonus("race", race.getManaBonus());
		}
		
		ClassContainer clazz = player.getclass();
		if(clazz != null){
			addMaxManaBonus("class", clazz.getManaBonus());
		}
		
		double max = getMaxMana();
		if(currentMana > max){
			currentMana = max;
		}
		
		outputManaToPlayer();
	}

	
	/**
	 * Displays the Mana to the Player
	 * 
	 * This is only displayed if the Player HAS Mana.
	 * This means his maxMana > 0.
	 */
	public void outputManaToPlayer(){
		if(getMaxMana() <= 0) return;
		
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				manaDisplay.display(currentMana, getMaxMana());
			}
		}, 1);
	}
	
	/**
	 * A player tries to cast a spell.
	 * Returns true if the spell may be casted.
	 * Returns false if the player does not have enough Mana.
	 * 
	 * When Mana is used, the Mana is removed from the mana Pool.
	 * 
	 * @param spellToCast to cast
	 * @return true if spell cast is permitted, false otherwise.
	 */
	public boolean playerCastSpell(MagicSpellTrait spellToCast){
		if(!hasEnoughMana(spellToCast)) return false;
		
		currentMana -= spellToCast.getCost();
		outputManaToPlayer();
		return true;
	}
	
	
	
	/**
	 * Checks if the Player has enough Mana for the Spell.
	 * 
	 * WARNING: Only returns true if the Spell actually needs Mana!
	 * 
	 * @param spell to check
	 * @return true if has enough Mana, false if not, or no Mana spell.
	 */
	public boolean hasEnoughMana(MagicSpellTrait spell){
		if(spell.getCostType() != CostType.MANA) return false;
		return hasEnoughMana(spell.getCost());
	}
	
	
	/**
	 * Fills the current Mana Pool of the player by a specific Value.
	 * It returns the resulting current mana Pool.
	 * 
	 * If the Mana Pool exceeds it's max size, it is filled but not overfilled.
	 * 
	 * @param value to fill
	 * @return the Current Mana Pool size aftert filling.
	 */
	public double fillMana(double value){
		currentMana += value;
		if(currentMana > getMaxMana()){
			currentMana = getMaxMana();
		}
		
		outputManaToPlayer();
		return currentMana;
	}
	
	
	/**
	 * Drowns the current Mana Pool of the player by a specific Value.
	 * It returns the resulting current mana Pool.
	 * 
	 * If the Mana Pool exceeds to be empty (0), it is emptied but will not be negative.
	 * 
	 * @param value to drown
	 * @return the Current Mana Pool size after drowning.
	 */
	public double drownMana(double value){
		currentMana -= value;
		if(currentMana < 0){
			currentMana = 0;
		}
		
		outputManaToPlayer();
		return currentMana;
	}

	
	/**
	 * Checks if the Player has enough Mana.
	 * 
	 * @param manaNeeded the mana needed.
	 * @return true if has enough, false if not.
	 */
	public boolean hasEnoughMana(double manaNeeded){
		return currentMana - manaNeeded >= 0;
	}
	
	

	/**
	 * @return the maxMana
	 */
	public double getMaxMana() {
		double max = 0;
		for(Double value : maxManaBonus.values()){
			max += value;
		}
		
		return max;
	}


	/**
	 * @return the currentMana
	 */
	public double getCurrentMana() {
		return currentMana;
	}
	
	/**
	 * Adds a max Mana bonus.
	 * 
	 * @param key to add
	 * @param value to add
	 */
	public void addMaxManaBonus(String key, double value){
		maxManaBonus.put(key.toLowerCase(), value);
	}
	
	/**
	 * Remove a max Mana bonus.
	 * 
	 * @param key to remove
	 */
	public void removeMaxManaBonus(String key){
		maxManaBonus.remove(key.toLowerCase());
	}
	
	/**
	 * Returns all Bonusses.
	 * 
	 * @return all Bonuses.
	 */
	public Map<String,Double> getAllBonuses(){
		return maxManaBonus;
	}


	/**
	 * @return the player
	 */
	public RaCPlayer getPlayer() {
		return player;
	}
	
	/**
	 * Returns if the Mana of this player is
	 * fully filled.
	 * 
	 * @return true if filled, false otherwise.
	 */
	public boolean isManaFull(){
		return currentMana >= getMaxMana();
	}

	@Override
	public void update(Observable o, Object arg) {
		String changedValue = (String) arg;
		
		if(changedValue.equalsIgnoreCase("displayType")){
			rescanDisplay();
		}
	}
}
