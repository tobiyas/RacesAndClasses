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

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.util.consts.Consts;

public class ChatDisplayBar extends AbstractDisplay{

	
	
	/**
	 * Inits the display with a Player to post to.
	 * 
	 * @param player to display to
	 * @param displayInfo the type of display to show
	 */
	public ChatDisplayBar(RaCPlayer player, DisplayInfos displayInfos) {
		super(player, displayInfos);
	}

	
	@Override
	public void display(double currentAmount, double maxAmount) {
		if(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_disableAllChatBars()) return;
		
		String barString = "";
		if(!displayInfo.useName()){
			barString = calcForHealth(currentAmount, maxAmount, Consts.displayBarLength);
		}
		
		int pre = (int) Math.floor(currentAmount);
		int after = (int) Math.floor(currentAmount * 100D) % 100;
		
		String healthAsNumbers = "";
		
		if(displayInfo.onlyUseOneValue()){
			healthAsNumbers = String.valueOf( currentAmount );
		}else{
			healthAsNumbers = colorMedium + " " + getColorOfPercent(currentAmount, maxAmount) + 
					pre + "." + after + colorMedium + "/" + colorHigh + maxAmount;			
		}
		
		if(player != null && player.isOnline()){
			player.sendMessage(displayInfo.getMidValueColor() + displayInfo.getName() + ": " + barString + healthAsNumbers);
		}
	}
	
	
	
}
