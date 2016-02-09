/*******************************************************************************
 * Copyright 2014 Tob
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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.UnderwaterBreathTrait;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;

import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.bypasses.ByPassWorldDisabledCheck;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.bypasses.BypassHolderCheck;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.RemoveSuperConfigField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.traits.pattern.TickEverySecondsTrait;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

@BypassHolderCheck
@ByPassWorldDisabledCheck
public class UnderwaterBreathTrait extends TickEverySecondsTrait {
	
	/**
	 * The default time a player can breath.
	 * <br>15 Seconds default time.
	 */
	private final int DEFAULT_BREATH_TIME = 15 * 20;
	
	/**
	 * The New time to stay underwater
	 */
	private int newTimeInTicks = DEFAULT_BREATH_TIME;
	
	/**
	 * The Current ticks for the players
	 */
	private final Map<String, AirStorage> currentTicks = new HashMap<String, AirStorage>();
	
	
	
	@TraitInfos(category="passive", traitName="UnderwaterBreathTrait", visible=true)
	@Override
	public void importTrait() {
	}

	
	@TraitEventsUsed(registerdClasses = {})
	@Override
	public void generalInit() {
		super.generalInit();
	}

	
	@Override
	public String getName() {
		return "UnderwaterBreathTrait";
	}

	
	@Override
	public boolean isBetterThan(Trait trait) {
		if(!(trait instanceof UnderwaterBreathTrait)) return false;
		UnderwaterBreathTrait otherTrait = (UnderwaterBreathTrait) trait;
		
		return this.newTimeInTicks >= otherTrait.newTimeInTicks;
	}

	
	@TraitConfigurationNeeded( fields = {
			@TraitConfigurationField(fieldName = "time", classToExpect = Integer.class)
		}, removedFields = {
			@RemoveSuperConfigField(name = "seconds")
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		configMap.put("seconds", 1);
		
		super.setConfiguration(configMap);
		
		if(configMap.containsKey("time")){
			newTimeInTicks = 20 * (Integer) configMap.get("time");
		}
	}

	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "The trait modifies the time you can stay underwater.");
		return helpList;
	}


	@Override
	public boolean canBeTriggered(EventWrapper wrapper) {
		return true;
	}


	@Override
	protected boolean tickDoneForPlayer(RaCPlayer player) {
	 	if(player == null || !player.isOnline()) return false; //player is not present.
		if(player.getPlayer().getGameMode() == GameMode.CREATIVE) return false; //Creative players don't need checks
		
		String playerName = player.getName();
		int currentBreath = player.getPlayer().getRemainingAir();		
		
		if(!currentTicks.containsKey(playerName)) {
			AirStorage storage = new AirStorage();
			storage.currentScaledAir = currentBreath;
			
			storage.currentRawAir = currentBreath * (newTimeInTicks / DEFAULT_BREATH_TIME);
			currentTicks.put(playerName, storage);
		}
		
		AirStorage playerStorage = currentTicks.get(playerName);
		int changeRatio = currentBreath - playerStorage.currentScaledAir;
		if(changeRatio == 0) return false; //Nothing has changed.
		
		//AIR has changed.
		if(changeRatio > 0){
			//Air has increased.
			playerStorage.currentRawAir += (int)((double)changeRatio * ((double)newTimeInTicks / (double)DEFAULT_BREATH_TIME));
			if(playerStorage.currentRawAir > newTimeInTicks) playerStorage.currentRawAir = newTimeInTicks;
		}
		
		if(changeRatio < 0){
			//Air has decreased
			
			//We have to be sure to reach EXACTLY -20 ticks to trigger Draining.
			if(playerStorage.currentRawAir < 0) playerStorage.currentRawAir = 0;
			
			//We only decrease by 20 ticks every second.
			playerStorage.currentRawAir -= 20;
		}
		
		playerStorage.currentScaledAir = (int)((double)playerStorage.currentRawAir * ((double)DEFAULT_BREATH_TIME / (double)newTimeInTicks));
		player.getPlayer().setRemainingAir(playerStorage.currentScaledAir);
		return false;
	}


	@Override
	protected String getPrettyConfigIntern() {
		return "you can breath for " + (newTimeInTicks/20) + " Seconds";
	}



	private static class AirStorage{
		protected int currentScaledAir = 0;
		protected int currentRawAir = 0;
	}



	@Override
	protected String getPrettyConfigurationPre() {
		return "";
	}
}
