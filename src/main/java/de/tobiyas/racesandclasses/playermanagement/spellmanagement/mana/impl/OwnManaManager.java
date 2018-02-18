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

import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.spellmanagement.mana.ManaFoodBarRunner;

public class OwnManaManager extends AbstractManaManager {
	
	/**
	 * The current Mana the Player contains.
	 */
	private double currentMana;
	
	/**
	 * The Food bar runnable to use.
	 */
	private final ManaFoodBarRunner foodBar;
	
	
	
	/**
	 * Generates a new Mana Manager for the Player passed.
	 * @param player to use.
	 */
	public OwnManaManager(RaCPlayer player) {
		super(player);
		
		this.currentMana = 0;
		
		foodBar = new ManaFoodBarRunner(this);
		foodBar.start();
	}
	
	

	@Override
	public void rescanPlayer(){
		super.rescanPlayer();
		
		this.currentMana = Math.min(getMaxMana(), currentMana);
	}
	
	@Override
	public double fillMana(double value){
		currentMana += value;
		if(currentMana > getMaxMana()){
			currentMana = getMaxMana();
		}
		
		return currentMana;
	}
	

	@Override
	public double drownMana(double value){
		currentMana -= value;
		if(currentMana < 0){
			currentMana = 0;
		}
		
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



	@Override
	public double getManaBoostByName(String boostID) {
		Double amount = maxManaBonusses.get( boostID );
		return amount == null ? 0 : amount;
	}

}
