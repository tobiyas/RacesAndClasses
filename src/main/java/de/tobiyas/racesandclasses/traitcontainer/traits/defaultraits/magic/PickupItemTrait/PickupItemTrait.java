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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.PickupItemTrait;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.traits.magic.AbstractMagicSpellTrait;
import de.tobiyas.racesandclasses.translation.languages.Keys;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public class PickupItemTrait extends AbstractMagicSpellTrait  {

	/**
	 * The Blocks the enemy is pushed away.
	 */
	private int blocks = 0;
	
	
	@TraitEventsUsed(registerdClasses = {PlayerInteractEvent.class})
	@Override
	public void generalInit() {
	}

	@Override
	public String getName() {
		return "PickupItemTrait";
	}

	
	@Override
	protected String getPrettyConfigIntern(){
		return "picks item " + blocks + " blocks away up";
	}

	
	@TraitInfos(category="magic", traitName="PickupItemTrait", visible=true)
	@Override
	public void importTrait() {
	}

	@TraitConfigurationNeeded(fields = {
			@TraitConfigurationField( fieldName = "blocks", classToExpect = Integer.class)
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		this.blocks = (Integer) configMap.get("blocks");
	}
	
	
	@Override
	public boolean isBetterThan(Trait trait) {
		if(!(trait instanceof PickupItemTrait)) return false;
		
		PickupItemTrait otherTrait = (PickupItemTrait) trait;
		return cost > otherTrait.cost;
	}

	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "This trait picks Items up.");
		return helpList;
	}


	@Override
	protected void magicSpellTriggered(RaCPlayer player, TraitResults result) {
		List<Block> blocks = player.getPlayer().getLineOfSight(new HashSet<Material>(), 30);
		List<Entity> nearEntities = player.getPlayer().getNearbyEntities(30, 30, 30);
		
		Item pickupItem = null;
		for(Block block : blocks){
			for(Entity nearEntity : nearEntities){
				if(!(nearEntity instanceof Item)){
					continue;
				}
				
				if(block.getLocation().distance(nearEntity.getLocation()) < 1){
					pickupItem = (Item) nearEntity;
					break;
				}
			}
		}
		
		
		if(pickupItem != null){
			ItemStack item = pickupItem.getItemStack();
			PlayerPickupItemEvent event = new PlayerPickupItemEvent(player.getPlayer(), pickupItem, 10);
			plugin.fireEventToBukkit(event);
			if(event.isCancelled()) {
				result.setTriggered(false);
				return;
			}
			
			if(player.getPlayer().getInventory().addItem(item).isEmpty()){
				LanguageAPI.sendTranslatedMessage(player, Keys.trait_pickup_success);
				pickupItem.remove();
				result.setTriggered(true);
				return;
			}else{
				LanguageAPI.sendTranslatedMessage(player, Keys.trait_pickup_inv_full);
				result.setTriggered(false);
				return;
			}
		}
		
		
		result.setTriggered(false);
		return;
	}
	
}
