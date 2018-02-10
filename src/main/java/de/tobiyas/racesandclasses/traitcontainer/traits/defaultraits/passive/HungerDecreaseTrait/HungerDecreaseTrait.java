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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.HungerDecreaseTrait;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.traits.pattern.TickEverySecondsTrait;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public class HungerDecreaseTrait extends TickEverySecondsTrait {
	
	/**
	 * The amount to remove at x per y second.
	 */
	private int amount = 1;
	

	@Override
	public String getName() {
		return "HungerDecreaseTrait";
	}
	
	
	@Override
	protected String getPrettyConfigIntern() {
		return "Hunger decrease: " + amount + " every " + seconds + " second";
	}

	@TraitConfigurationNeeded( fields = {
			@TraitConfigurationField( fieldName = "amount", classToExpect = Integer.class, optional = true )
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		amount = configMap.getAsInt( "amount", 1 );
	}

	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "Your hunger will decrease by time.");
		return helpList;
	}
	
	
	@Override
	public boolean isBetterThan(Trait trait) {
		if(!(trait instanceof HungerDecreaseTrait)) return false;
		HungerDecreaseTrait otherTrait = (HungerDecreaseTrait) trait;
		
		return amount <= otherTrait.amount;
	}

	@TraitInfos(category="passive", traitName="HungerDecreaseTrait", visible=true)
	@Override
	public void importTrait() {}
	

	@Override
	public boolean canBeTriggered(EventWrapper wrapper) {
		Event event = wrapper.getEvent();
		if(!(event instanceof FoodLevelChangeEvent)) return false;
		FoodLevelChangeEvent Eevent = (FoodLevelChangeEvent) event;
		
		if(!(Eevent.getEntity() instanceof Player)) return false;
		Player player = (Player) Eevent.getEntity();
		
		int orgValue = player.getFoodLevel();
		int newValue = Eevent.getFoodLevel();
		
		if(newValue > orgValue){
			return true;
		}
		
		return false;
	}


	@Override
	protected boolean tickDoneForPlayer(RaCPlayer player) {
		Player rPlayer = player.getPlayer();
		
		int hunger = rPlayer.getFoodLevel();
		float saturation = rPlayer.getSaturation();
		
		if( saturation > 0 ) rPlayer.setSaturation( Math.max( 0, saturation - amount ) );
		else if( hunger > amount )  rPlayer.setFoodLevel( Math.max( 1, hunger - amount  ) );
		
		return true;
	}
	


	@Override
	protected String getPrettyConfigurationPre() {
		return "Hunger decrease: " + amount;
	}
}
