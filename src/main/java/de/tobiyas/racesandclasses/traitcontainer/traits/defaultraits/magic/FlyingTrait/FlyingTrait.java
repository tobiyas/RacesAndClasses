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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.FlyingTrait;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.event.player.PlayerInteractEvent;

import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.traits.magic.AbstractContinousCostMagicSpellTrait;
import de.tobiyas.racesandclasses.translation.languages.Keys;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public class FlyingTrait extends AbstractContinousCostMagicSpellTrait  {

	/**
	 * The Flyspeed to use.
	 */
	private double flyspeed = 0.5;
	
	
	@TraitEventsUsed(registerdClasses = {PlayerInteractEvent.class})
	@Override
	public void generalInit() {
	}

	@Override
	public String getName() {
		return "FlyingTrait";
	}

	
	@Override
	protected String getPrettyConfigIntern(){
		int time = everyXSeconds <= 0 ? durationInSeconds : everyXSeconds;
		return "duration: " + time + " seconds. Mana: " + cost;
	}

	
	@TraitInfos(category="magic", traitName="FlyingTrait", visible=true)
	@Override
	public void importTrait() {
	}

	
	@TraitConfigurationNeeded(fields = {
			@TraitConfigurationField(fieldName = "speed", classToExpect = Double.class, optional = true)
	})
	@Override
	public void setConfiguration(TraitConfiguration configMap)
			throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		if(configMap.containsKey("speed")){
			this.flyspeed = (Double) configMap.get("speed");
			if(flyspeed < 0.05) flyspeed = 0.05;
			if(flyspeed > 1) flyspeed = 1;
		}
	}

	@Override
	public boolean isBetterThan(Trait trait) {
		if(!(trait instanceof FlyingTrait)) return false;
		
		FlyingTrait otherTrait = (FlyingTrait) trait;
		int time = everyXSeconds <= 0 ? durationInSeconds : everyXSeconds;
		int otherTime = otherTrait.everyXSeconds <= 0 ? otherTrait.durationInSeconds : otherTrait.everyXSeconds;
		return time > otherTime;
	}
	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "This trait makes you fly. It's some kind of magic.");
		return helpList;
	}


	@Override
	protected boolean activateIntern(RaCPlayer player) {
		if(player.getPlayer().getGameMode() != GameMode.CREATIVE){
			player.getPlayer().setAllowFlight(true);
		}
		
		player.getPlayer().setFlying(true);
		player.getPlayer().setFlySpeed((float) flyspeed);
		
		int time = everyXSeconds <= 0 ? durationInSeconds : everyXSeconds;
		time = modifyToPlayer(player, time, "duration");
		
		LanguageAPI.sendTranslatedMessage(player, Keys.trait_fly_toggle, 
				"duration", String.valueOf(time));
		
		return true;
	}

	@Override
	protected boolean deactivateIntern(RaCPlayer player) {
		if(player.getPlayer().getGameMode() != GameMode.CREATIVE){
			player.getPlayer().setFlying(false);
			player.getPlayer().setAllowFlight(false);			
		}
		
		player.getPlayer().setFlySpeed(0.1f);
		player.getPlayer().setFallDistance(0);
		return true;
	}

	@Override
	protected boolean tickInternal(RaCPlayer player) {
		//nothing to tick.
		return true;
	}
}
