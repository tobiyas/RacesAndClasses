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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.ManaOnHitTrait;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.PlayerAction;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.traits.pattern.AbstractWeaponDamageIncreaseTrait;
import de.tobiyas.racesandclasses.translation.languages.Keys;
import de.tobiyas.racesandclasses.util.friend.EnemyChecker.FriendDetectEvent;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public class ManaOnHitTrait extends AbstractWeaponDamageIncreaseTrait {
	/**
	 * The chance for regeneration 0-1.
	 */
	protected double chance = 0.1;
	
	
	@TraitEventsUsed(registerdClasses = {EntityDamageByEntityEvent.class})
	@Override
	public void generalInit(){
	}


	@Override
	protected String getPrettyConfigIntern(){
		return operation + " " +  value;
	}

	@TraitConfigurationNeeded( fields = {})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		value = configMap.getAsDouble("value", 0);
	}
	
	@Override
	public TraitResults trigger(EventWrapper eventWrapper) {   
		RaCPlayer player = eventWrapper.getPlayer();
		double value = modifyToPlayer(player, this.value, "value");
		player.getManaManager().fillMana(value);
		
		player.sendTranslatedMessage(Keys.trait_mana_refill, "value", String.valueOf((int)value));
		return TraitResults.True();
	}

	
	@Override
	public boolean isBetterThan(Trait trait) {
		return false;
	}
	
	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "Regenerates mana on hits sometimes.");
		
		return helpList;
	}

	
	@Override
	public boolean canBeTriggered(EventWrapper wrapper) {
		if(wrapper.getPlayerAction() != PlayerAction.DO_DAMAGE) return false;
		
		//If cancled, can not be used.
		if(wrapper.getEvent() instanceof Cancellable && ((Cancellable)wrapper.getEvent()).isCancelled()) return false;
		if(wrapper.getEvent() instanceof FriendDetectEvent) return false;
		if(wrapper.getPlayer().getManaManager().isManaFull()) return false;
		
		//Chance to hit.
		double chance = modifyToPlayer(wrapper.getPlayer(), this.chance, "chance");
		return Math.random() <= chance;
	}
	

	
	@TraitInfos(category="passive", traitName="ManaOnHitTrait", visible=true)
	@Override
	public void importTrait() {
	}


	@Override
	public String getName() {
		return "ManaOnHitTrait";
	}

}
