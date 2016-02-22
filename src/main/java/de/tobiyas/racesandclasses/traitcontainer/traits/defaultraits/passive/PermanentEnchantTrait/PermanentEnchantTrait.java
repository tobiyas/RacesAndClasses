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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.PermanentEnchantTrait;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.TraitHolderCombinder;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.eventprocessing.events.inventoryitemevents.PlayerDisequipsArmorEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.inventoryitemevents.PlayerEquipsArmorEvent;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.AbstractBasicTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.util.items.ItemUtils;
import de.tobiyas.racesandclasses.util.items.ItemUtils.ArmorSlot;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public class PermanentEnchantTrait extends AbstractBasicTrait {

	
	/**
	 * The amplifier of the Enchant
	 */
	private int amplifier = 1;
	
	/**
	 * The type of the Enchantment
	 */
	private Enchantment type;
	
	/**
	 * The Type of slot to apply.
	 */
	private ArmorSlot armorSlot;
	
	/**
	 * The Set of containing Players.
	 */
	private Set<UUID> containing = new HashSet<UUID>();
	

	@Override
	public String getName() {
		return "PermanentEnchantTrait";
	}


	@TraitConfigurationNeeded( fields = {
			@TraitConfigurationField(fieldName = "amplifier", classToExpect = Integer.class, optional = true),
			@TraitConfigurationField(fieldName = "type", classToExpect = Integer.class, optional = false),
			@TraitConfigurationField(fieldName = "slot", classToExpect = String.class, optional = false)
		}
	)
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		//Put the overriding stuff in it.
		configMap.put("seconds", (Integer)5);
		super.setConfiguration(configMap);
		
		
		if(configMap.containsKey("amplifier")) amplifier = configMap.getAsInt("amplifier");
		type = Enchantment.getByName(configMap.getAsString("type"));

		if(configMap.containsKey("slot")){
			armorSlot = ArmorSlot.valueOf(configMap.getAsString("slot").toUpperCase());
		}
		
		if(type == null) throw new TraitConfigurationFailedException("unknown Enchantment effect.");
		if(armorSlot == null) throw new TraitConfigurationFailedException("unknown armor Slot.");
	}

	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "This trait aplies an enchantment effect permanently to the player.");
		
		return helpList;
	}
	

	@TraitInfos(category = "passive", traitName = "PermanentEnchantTrait", visible = true)
	@Override
	public void importTrait() {
	}

	
	@Override
	public boolean isStackable(){
		return true;
	}


	@Override
	public void generalInit() {
	}


	@Override
	public TraitResults trigger(EventWrapper wrapper) {
		return TraitResults.False();
	}


	@Override
	public boolean isBetterThan(Trait trait) {
		return false;
	}


	@Override
	public boolean canBeTriggered(EventWrapper wrapper) {
		return false;
	}


	@Override
	protected String getPrettyConfigIntern() {
		return "Applies " + Enchantment.ARROW_FIRE.getName() + "-" + amplifier + " to " + armorSlot.name();
	}
	
	
	@EventHandler
	public void onPlayerEquipItem(PlayerEquipsArmorEvent event){
		Player eventPlayer = event.getPlayer();
		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(eventPlayer);
		
		if(eventPlayer == null || racPlayer == null) return;
		if(TraitHolderCombinder.checkContainer(racPlayer, this)){
			ItemStack item = ItemUtils.getItemInArmorSlotOfPlayer(eventPlayer, armorSlot);
			if(item == null) return;
			
			int amp = item.getEnchantmentLevel(type);
			if(amp > 0) return;
			
			item.addUnsafeEnchantment(type, amplifier);
			containing.add(racPlayer.getUniqueId());
		}
	}
	
	
	
	@EventHandler
	public void onPlayerDisequipArmor(PlayerDisequipsArmorEvent event){
		Player eventPlayer = event.getPlayer();
		if(!containing.contains(eventPlayer.getUniqueId())) return;
		
		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(eventPlayer);
		
		if(eventPlayer == null || racPlayer == null) return;
		if(TraitHolderCombinder.checkContainer(racPlayer, this)){
			ItemStack item = ItemUtils.getItemInArmorSlotOfPlayer(eventPlayer, armorSlot);
			item.removeEnchantment(type);
			
			containing.remove(eventPlayer.getUniqueId());
		}
	}
	
	
}
