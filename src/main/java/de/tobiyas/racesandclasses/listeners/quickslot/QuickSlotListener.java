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
package de.tobiyas.racesandclasses.listeners.quickslot;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.playermanagement.spellmanagement.PlayerSpellManager;
import de.tobiyas.racesandclasses.playermanagement.spellmanagement.QuickSlotGUI;

public class QuickSlotListener implements Listener {

	public static final String QUICK_ITEM_PRE = ChatColor.GREEN + "[TRAIT]: " + ChatColor.YELLOW;
	
	
	/**
	 * The Plugin to register to.
	 */
	private final RacesAndClasses plugin;
	
	
	public QuickSlotListener(){
		this.plugin = RacesAndClasses.getPlugin();
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	/**
	 * Checks if the passed item is a QuickSlot item.
	 * 
	 * @param item to check
	 * 
	 * @return true if it is a quick slot item.
	 */
	private boolean isQuickBarItem(ItemStack item){
		if(item == null) return false;
		if(item.getType() != Material.WOOL) return false;
		
		if(!item.hasItemMeta()) return false;
		if(!item.getItemMeta().hasDisplayName()) return false;
		
		String spellName = item.getItemMeta().getDisplayName();		
		if(!spellName.startsWith(QUICK_ITEM_PRE)) return false;
		
		return true;
	}
	
	
	@EventHandler
	public void playerThrowQuickSlotItemAway(InventoryClickEvent event){
		ItemStack cursor = event.getCursor();
		boolean isDropped = event.getAction() == InventoryAction.DROP_ALL_CURSOR || event.getAction() == InventoryAction.DROP_ONE_CURSOR;
		
		if(!isDropped) return;
		
		if(!(event.getView() instanceof QuickSlotGUI)
					&& cursor != null
					&& cursor.getType() == Material.WOOL
					
					&& cursor.hasItemMeta()
					&& cursor.getItemMeta().hasDisplayName()
					&& !cursor.getItemMeta().hasLore()
					&& cursor.getItemMeta().getDisplayName().startsWith(QuickSlotListener.QUICK_ITEM_PRE)){
					
				
			//Player wants to have a quickslot item.
			//So we let him.
			
			event.setCurrentItem(null);
		}
		
	}
	
	@EventHandler
	public void playerPressedQuickSlot(PlayerDropItemEvent event){
		if(!isQuickBarItem(event.getItemDrop().getItemStack())) return;
		event.setCancelled(true);
		
		String playerName = event.getPlayer().getName();
		String spellName = event.getItemDrop().getItemStack().getItemMeta().getDisplayName();
		
		if(playerName == null || spellName == null) return;
		
		PlayerSpellManager manager = plugin.getPlayerManager().getSpellManagerOfPlayer(playerName);
		if(!manager.changeToSpell(spellName.replace(QUICK_ITEM_PRE, ""))) return;
		if(!manager.tryCastCurrentSpell()) return;
	}
	
	
	@EventHandler
	public void placeBlockForQuickSlotNotPossible(BlockPlaceEvent event){
		ItemStack item = event.getItemInHand();
		
		if(item.getType() != Material.WOOL) return;
		
		if(!item.hasItemMeta()) return;
		if(!item.getItemMeta().hasDisplayName()) return;
		
		String spellName = item.getItemMeta().getDisplayName();

		if(!spellName.startsWith(QUICK_ITEM_PRE)) return;
		event.setCancelled(true); //you can't place quickslot items
	}
	
	@EventHandler
	public void cancleCrafting(CraftItemEvent event){
		for(ItemStack item : event.getInventory()){
			if(isQuickBarItem(item)){
				//No crafting of quickslot items
				event.setCancelled(true);
			}
		}
	}
}
