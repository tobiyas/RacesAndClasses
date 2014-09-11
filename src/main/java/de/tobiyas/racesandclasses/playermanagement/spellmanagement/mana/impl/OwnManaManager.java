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
package de.tobiyas.racesandclasses.playermanagement.spellmanagement.mana.impl;

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
import de.tobiyas.racesandclasses.playermanagement.spellmanagement.mana.ManaFoodBarRunner;
import de.tobiyas.racesandclasses.playermanagement.spellmanagement.mana.ManaManager;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.MagicSpellTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.MagicSpellTrait.CostType;

public class OwnManaManager implements Observer, ManaManager {

	/**
	 * The Plugin to call Stuff on
	 */
	private RacesAndClasses plugin = RacesAndClasses.getPlugin();
	
	/**
	 * The Player this mana Manager belongs.
	 */
	private final RaCPlayer player;
	
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
	 * The Food bar runnable to use.
	 */
	private final ManaFoodBarRunner foodBar;
	
	
	
	/**
	 * Generates a new Mana Manager for the Player passed.
	 * 
	 * @param player
	 */
	public OwnManaManager(RaCPlayer player) {
		this.player = player;
		this.currentMana = 0;
		
		rescanDisplay();
		
		player.getConfig().addObserver(this);
		
		foodBar = new ManaFoodBarRunner(this);
		foodBar.start();
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
	
	
	/* (non-Javadoc)
	 * @see de.tobiyas.racesandclasses.playermanagement.spellmanagement.mana.ManaManager#rescanPlayer()
	 */
	@Override
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

	
	/* (non-Javadoc)
	 * @see de.tobiyas.racesandclasses.playermanagement.spellmanagement.mana.ManaManager#outputManaToPlayer()
	 */
	@Override
	public void outputManaToPlayer(){
		if(getMaxMana() <= 0) return;
		
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				manaDisplay.display(currentMana, getMaxMana());
			}
		}, 1);
	}
	
	/* (non-Javadoc)
	 * @see de.tobiyas.racesandclasses.playermanagement.spellmanagement.mana.ManaManager#playerCastSpell(de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.MagicSpellTrait)
	 */
	@Override
	public boolean playerCastSpell(MagicSpellTrait spellToCast){
		if(!hasEnoughMana(spellToCast)) return false;
		
		currentMana -= spellToCast.getCost();
		outputManaToPlayer();
		return true;
	}
	
	
	
	/* (non-Javadoc)
	 * @see de.tobiyas.racesandclasses.playermanagement.spellmanagement.mana.ManaManager#hasEnoughMana(de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.MagicSpellTrait)
	 */
	@Override
	public boolean hasEnoughMana(MagicSpellTrait spell){
		if(spell.getCostType() != CostType.MANA) return false;
		return hasEnoughMana(spell.getCost());
	}
	
	
	/* (non-Javadoc)
	 * @see de.tobiyas.racesandclasses.playermanagement.spellmanagement.mana.ManaManager#fillMana(double)
	 */
	@Override
	public double fillMana(double value){
		currentMana += value;
		if(currentMana > getMaxMana()){
			currentMana = getMaxMana();
		}
		
		outputManaToPlayer();
		return currentMana;
	}
	
	
	/* (non-Javadoc)
	 * @see de.tobiyas.racesandclasses.playermanagement.spellmanagement.mana.ManaManager#drownMana(double)
	 */
	@Override
	public double drownMana(double value){
		currentMana -= value;
		if(currentMana < 0){
			currentMana = 0;
		}
		
		outputManaToPlayer();
		return currentMana;
	}

	
	/* (non-Javadoc)
	 * @see de.tobiyas.racesandclasses.playermanagement.spellmanagement.mana.ManaManager#hasEnoughMana(double)
	 */
	@Override
	public boolean hasEnoughMana(double manaNeeded){
		return currentMana - manaNeeded >= 0;
	}
	
	

	/* (non-Javadoc)
	 * @see de.tobiyas.racesandclasses.playermanagement.spellmanagement.mana.ManaManager#getMaxMana()
	 */
	@Override
	public double getMaxMana() {
		double max = 0;
		for(Double value : maxManaBonus.values()){
			max += value;
		}
		
		return max;
	}


	/* (non-Javadoc)
	 * @see de.tobiyas.racesandclasses.playermanagement.spellmanagement.mana.ManaManager#getCurrentMana()
	 */
	@Override
	public double getCurrentMana() {
		return currentMana;
	}
	
	/* (non-Javadoc)
	 * @see de.tobiyas.racesandclasses.playermanagement.spellmanagement.mana.ManaManager#addMaxManaBonus(java.lang.String, double)
	 */
	@Override
	public void addMaxManaBonus(String key, double value){
		maxManaBonus.put(key.toLowerCase(), value);
	}
	
	/* (non-Javadoc)
	 * @see de.tobiyas.racesandclasses.playermanagement.spellmanagement.mana.ManaManager#removeMaxManaBonus(java.lang.String)
	 */
	@Override
	public void removeMaxManaBonus(String key){
		maxManaBonus.remove(key.toLowerCase());
	}
	
	/* (non-Javadoc)
	 * @see de.tobiyas.racesandclasses.playermanagement.spellmanagement.mana.ManaManager#getAllBonuses()
	 */
	@Override
	public Map<String,Double> getAllBonuses(){
		return maxManaBonus;
	}


	/* (non-Javadoc)
	 * @see de.tobiyas.racesandclasses.playermanagement.spellmanagement.mana.ManaManager#getPlayer()
	 */
	@Override
	public RaCPlayer getPlayer() {
		return player;
	}
	
	/* (non-Javadoc)
	 * @see de.tobiyas.racesandclasses.playermanagement.spellmanagement.mana.ManaManager#isManaFull()
	 */
	@Override
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
