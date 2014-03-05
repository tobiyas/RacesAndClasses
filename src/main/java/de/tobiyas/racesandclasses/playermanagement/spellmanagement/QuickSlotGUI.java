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
package de.tobiyas.racesandclasses.playermanagement.spellmanagement;

import static de.tobiyas.racesandclasses.translation.languages.Keys.quick_slot_item_lore;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.listeners.quickslot.QuickSlotListener;
import de.tobiyas.util.inventorymenu.BasicSelectionInterface;

public class QuickSlotGUI extends BasicSelectionInterface {

	/**
	 * The SpellManager to use.
	 */
	private final PlayerSpellManager manager;
	
	/**
	 * The plugin to call stuff on.
	 */
	private final RacesAndClasses plugin;
	
	
	public QuickSlotGUI(Player player) {
		super(player, null, "Magic", "Magic", RacesAndClasses.getPlugin());

		String playerName = player.getName();
		this.plugin = RacesAndClasses.getPlugin();
		this.manager = plugin.getPlayerManager().getSpellManagerOfPlayer(playerName);
		
		redraw();
	}

	/**
	 * Redraws the GUI
	 */
	private void redraw() {
		
	}
	
	
	@Override
	public Inventory getBottomInventory() {
		return player.getInventory();
	}




	@EventHandler
	@Override
	public void onInterfaceInteract(InventoryClickEvent event) {
		ItemStack cursor = event.getCursor();
		ItemStack currentItem = event.getCurrentItem();
		
		if(event.getView() == this){
			if(!event.isShiftClick()
					&& event.isRightClick()
					
					&& cursor != null 
					&& cursor.getType() == Material.AIR
					
					&& currentItem != null
					&& currentItem.getType() == Material.WOOL
					
					&& currentItem.hasItemMeta()
					&& currentItem.getItemMeta().hasDisplayName()
					&& !currentItem.getItemMeta().hasLore()
					&& currentItem.getItemMeta().getDisplayName().startsWith(QuickSlotListener.QUICK_ITEM_PRE)){
					
				
				//Player wants to have a quickslot item.
				//So we let him.
				
				ItemStack itemToAdd = currentItem.clone();
				ItemMeta meta = itemToAdd.getItemMeta();
				List<String> lore = new LinkedList<String>();
				lore.add(LanguageAPI.translateIgnoreError(quick_slot_item_lore).build());
				
				meta.setLore(lore);
				itemToAdd.setItemMeta(meta);
				getBottomInventory().addItem(itemToAdd);
				LanguageAPI.sendTranslatedMessage(player, "Quickslot Item added.");
			}
		}
		
		super.onInterfaceInteract(event);
	}




	@Override
	protected boolean onBackPressed() {
		return false;
	}

	@Override
	protected void onAcceptPressed() {
	}

	@Override
	protected void onSelectionItemPressed(ItemStack item) {
	}

	@Override
	protected void onControlItemPressed(ItemStack item) {
	}

}
