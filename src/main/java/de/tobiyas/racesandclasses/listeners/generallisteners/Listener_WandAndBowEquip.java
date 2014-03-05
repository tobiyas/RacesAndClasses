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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
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
		
		Player player = event.getPlayer();
		ItemStack item = player.getInventory().getItem(event.getNewSlot());
		if(item != null){
			Material mat = item.getType();
			boolean newMatIsWand = isWand(player, mat);

			if(newMatIsWand){
				if(plugin.getPlayerManager().getSpellManagerOfPlayer(player.getName()).getSpellAmount() > 0){
					String currentActiveSpell = plugin.getPlayerManager().getSpellManagerOfPlayer(player.getName()).getCurrentSpell().toString();
					LanguageAPI.sendTranslatedMessage(player, wand_select_message, 
							"current_spell", currentActiveSpell);
				}
			}
			
			if(mat == Material.BOW){
				if(plugin.getPlayerManager().getArrowManagerOfPlayer(player.getName()).getNumberOfArrowTypes() > 0){
					String currentArrow = plugin.getPlayerManager().getArrowManagerOfPlayer(player.getName()).getCurrentArrow().getDisplayName();
					LanguageAPI.sendTranslatedMessage(player, bow_selected_message, 
							"current_arrow", currentArrow);
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
	private boolean isWand(Player player, Material mat) {
		String playerName = player.getName();
		
		Set<Material> wands = new HashSet<Material>();
		wands.add(plugin.getConfigManager().getGeneralConfig().getConfig_itemForMagic());
		
		AbstractTraitHolder classHolder = plugin.getClassManager().getHolderOfPlayer(playerName);
		if(classHolder != null){
			wands.addAll(classHolder.getAdditionalWandMaterials());
		}
		
		AbstractTraitHolder raceHolder = plugin.getRaceManager().getHolderOfPlayer(playerName);
		if(raceHolder != null){
			wands.addAll(raceHolder.getAdditionalWandMaterials());
		}
		
		return wands.contains(mat);
	}
}
