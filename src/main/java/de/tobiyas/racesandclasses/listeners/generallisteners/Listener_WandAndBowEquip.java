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
package de.tobiyas.racesandclasses.listeners.generallisteners;

import static de.tobiyas.racesandclasses.translation.languages.Keys.bow_selected_message;
import static de.tobiyas.racesandclasses.translation.languages.Keys.wand_select_message;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;

public class Listener_WandAndBowEquip implements Listener {

	private RacesAndClasses plugin;
	

	public Listener_WandAndBowEquip(){
		plugin = RacesAndClasses.getPlugin();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onPlayerChangeItemInhands(PlayerItemHeldEvent event){
		World world = event.getPlayer().getWorld();
		if(plugin.getConfigManager().getGeneralConfig().getConfig_worldsDisabled().contains(world.getName())){
			return;
		}
		
		RaCPlayer player = RaCPlayerManager.get().getPlayer(event.getPlayer());
		ItemStack item = player.getPlayer().getInventory().getItem(event.getNewSlot());
		if(item != null){
			Material mat = item.getType();
			boolean newMatIsWand = isWand(player, mat);

			if(newMatIsWand){
				if(player.getSpellManager().getSpellAmount() > 0){
					if(plugin.getCooldownManager().stillHasCooldown(player.getName(), "message.wand") > 0){
						return;
					}
					
					String currentActiveSpell = player.getSpellManager().getCurrentSpell().toString();
					LanguageAPI.sendTranslatedMessage(player, wand_select_message, 
							"current_spell", currentActiveSpell);
					
					int time = plugin.getConfigManager().getGeneralConfig().getConfig_cooldown_on_wand_message();
					plugin.getCooldownManager().setCooldown(player.getName(), "message.wand", time);
				}
			}
			
			if(mat == Material.BOW){
				if(player.getArrowManager().getNumberOfArrowTypes() > 0){
					if(plugin.getCooldownManager().stillHasCooldown(player.getName(), "message.bow") > 0){
						return;
					}
					
					String currentArrow =player.getArrowManager().getCurrentArrow().getDisplayName();
					LanguageAPI.sendTranslatedMessage(player, bow_selected_message, 
							"current_arrow", currentArrow);
					
					int time = plugin.getConfigManager().getGeneralConfig().getConfig_cooldown_on_bow_message();
					plugin.getCooldownManager().setCooldown(player.getName(), "message.bow", time);
				}
			}
			
		}
	}

	/**
	 * Checks if the Material passed is a wand for the player.
	 * 
	 * @param player to check
	 * @return true if he has, false otherwise.
	 */
	private boolean isWand(RaCPlayer player, Material mat) {
		Set<Material> wands = new HashSet<Material>();
		wands.add(plugin.getConfigManager().getGeneralConfig().getConfig_itemForMagic());
		
		AbstractTraitHolder classHolder = player.getclass();
		if(classHolder != null){
			wands.addAll(classHolder.getAdditionalWandMaterials());
		}
		
		AbstractTraitHolder raceHolder = player.getRace();
		if(raceHolder != null){
			wands.addAll(raceHolder.getAdditionalWandMaterials());
		}
		
		return wands.contains(mat);
	}
}
