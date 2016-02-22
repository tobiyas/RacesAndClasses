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

import de.tobiyas.racesandclasses.playermanagement.display.Display;
import de.tobiyas.racesandclasses.playermanagement.display.Display.DisplayInfos;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.display.DisplayGenerator;
import de.tobiyas.racesandclasses.playermanagement.spellmanagement.mana.ManaFoodBarRunner;
import de.tobiyas.util.schedule.DebugBukkitRunnable;

public class OwnManaManager extends AbstractManaManager {
	
	/**
	 * The current Mana the Player contains.
	 */
	private double currentMana;
	
	/**
	 * The Display displaying mana change to the Player
	 */
	private Display manaDisplay;
	
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
		super(player);
		
		this.currentMana = 0;
		
		rescanDisplay();
		
		foodBar = new ManaFoodBarRunner(this);
		foodBar.start();
	}
	
	/**
	 * This re-registers the display.
	 * <br>Meaning to throw the old one away and generate a new one.
	 */
	private void rescanDisplay(){
		if(manaDisplay != null) manaDisplay.unregister();
		
		String prefered = plugin.getConfigManager().getGeneralConfig().getConfig_magic_manaShowPlace();
		manaDisplay = DisplayGenerator.generateDisplay(racPlayer, DisplayInfos.MANA, prefered);
	}
	
	

	@Override
	public void rescanPlayer(){
		super.rescanPlayer();
		
		double max = getMaxMana();
		if(currentMana > max) currentMana = max;
		
		outputManaToPlayer();
	}

	

	@Override
	public void outputManaToPlayer(){
		double maxMana = getMaxMana();
		if(maxMana <= 0) maxMana = 0.1;
		
		new DebugBukkitRunnable("ManaToPlayerDisplay"){
			@Override
			protected void runIntern() {
				manaDisplay.display(currentMana, getMaxMana());
			}
		}.runTask(plugin);
	}	
	
	
	@Override
	public double fillMana(double value){
		currentMana += value;
		if(currentMana > getMaxMana()){
			currentMana = getMaxMana();
		}
		
		outputManaToPlayer();
		return currentMana;
	}
	

	@Override
	public double drownMana(double value){
		currentMana -= value;
		if(currentMana < 0){
			currentMana = 0;
		}
		
		outputManaToPlayer();
		return currentMana;
	}

	
	@Override
	public double getMaxMana() {
		double max = 0;
		for(Double value : maxManaBonusses.values()){
			max += value;
		}
		
		return max;
	}



	@Override
	public double getCurrentMana() {
		return currentMana;
	}


	@Override
	protected void applyMaxManaBonus(double bonus) {}

}
