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

import org.bukkit.ChatColor;

public abstract class AbstractDisplay implements Display{

	/**
	 * The player that this display belongs to
	 */
	protected String playerName;
	
	/**
	 * The Color indicating a HIGH amount of left Resource
	 */
	protected ChatColor colorHigh = ChatColor.GREEN;

	/**
	 * The Color indicating a MEDIUM amount of left Resource
	 */
	protected ChatColor colorMedium = ChatColor.YELLOW;
	
	/**
	 * The Color indicating a LOW amount of left Resource
	 */
	protected ChatColor colorLow = ChatColor.RED;
	
	/**
	 * The Type of Displaying.
	 */
	protected final DisplayInfos displayInfo;
	
	
	
	/**
	 * Inits with a player
	 * 
	 * @param player to display to
	 */
	public AbstractDisplay(String playerName, DisplayInfos displayInfo) {
		this.playerName = playerName;
		this.displayInfo = displayInfo;
		
		changeColorsToInfo();
	}
	
	/**
	 * Changes the color according to the Infos passed
	 */
	private void changeColorsToInfo(){
		this.colorLow = displayInfo.getLowValueColor();
		this.colorMedium = displayInfo.getMidValueColor();
		this.colorHigh = displayInfo.getHighValueColor();
	}
	
	
	@Override
	public abstract void display(double currentHealth, double maxHealth);
	
	
	/**
	 * Gets the Color of the percentage
	 * Green for > 60%
	 * <br>Yellow for < 60%, > 30%
	 * <br>Red for < 30%
	 * 
	 * @param currentHealth the current Health of a player
	 * @param maxHealth the maximal health of a player
	 * @return the color the percentage has
	 */
	protected ChatColor getColorOfPercent(double currentHealth, double maxHealth){
		double percentage = currentHealth / maxHealth;
		
		if(percentage > 0.6) return colorHigh;
		if(percentage >= 0.3) return colorMedium;
		if(percentage < 0.3) return colorLow;
		
		return ChatColor.LIGHT_PURPLE;
	}
	
	
	/**
	 * Calculates a bar with the passed health + the bar length
	 * 
	 * @param currentHealth
	 * @param maxHealth
	 * @return
	 */
	protected String calcForHealth(double currentHealth, double maxHealth, int healthBarLength){
		double healthPresentBarLength = ((currentHealth / maxHealth) * (healthBarLength));
		
		if(healthPresentBarLength < 0) healthPresentBarLength = 0;
		if(healthPresentBarLength > healthBarLength) healthPresentBarLength = healthBarLength;
		
		String healthLeft = colorHigh + "";
		for(int i = 0; i < healthPresentBarLength; i++){
			healthLeft += "|";
			healthBarLength --;
		}
		
		String healthRest = colorLow + "";
		for(; healthBarLength > 0; healthBarLength--){
			healthRest += "|";
		}
		
		if(currentHealth == maxHealth){
			healthRest += colorHigh + "|";
		}else{
			healthRest += colorLow + "|";
		}
		
		
		String chatString = healthLeft + healthRest;
		return chatString;
	}

	@Override
	public void unregister() {
		//Just default impl.
	}

	
	
}
