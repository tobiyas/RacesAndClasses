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
package de.tobiyas.racesandclasses.traitcontainer.traitgui;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.TraitHolderCombinder;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;

public class TraitInventory extends InventoryView {

	/**
	 * The Player this inventory belongs too
	 */
	private final Player player;
	
	/**
	 * The Inventory to show
	 */
	private final Inventory traitInventory;
	
	
	/**
	 * This is an Inventory generated to show ALL visible Traits of a player
	 * in one inventory.
	 * 
	 * @param player to show.
	 */
	public TraitInventory(Player player) {
		this.player = player;
		
		Set<Trait> traits = TraitHolderCombinder.getReducedVisibleTraitsOfPlayer(player);
		
		int size = traits.size();
		size = ((size / 9) + 1) * 9;
		if(size < 27) size = 27;
		
		this.traitInventory = Bukkit.createInventory(player, size, "Traits of: " + player.getName());
		
		fillInventory(traits);
	}

	
	/**
	 * Fills the Inventory with infos to all Traits.
	 * 
	 * @param traits to fill with
	 */
	private void fillInventory(Set<Trait> traits) {
		for(Trait trait : traits){
			ItemStack traitStack = new ItemStack(Material.ANVIL);
			ItemMeta meta = traitStack.getItemMeta();
			List<String> lore = new LinkedList<String>();
			
			meta.setDisplayName(ChatColor.LIGHT_PURPLE + trait.getDisplayName());
			
			String traitConfig = trait.getPrettyConfiguration();
			
			String[] words = {"No", "Config", "Present."};
			if(traitConfig != null){
				words = traitConfig.split(" ");				
			}
			
			lore.add("- Trait: " + trait.getName());
			String currentLine = "- " + words[0];
			for(int i = 1; i < words.length; i++){
				String currentWord = words[i];
				
				if(currentLine.length() + words.length + 1 > 31){
					lore.add(currentLine);
					currentLine = currentWord;
				}else{
					currentLine += " " + currentWord;
				}
			}
			if(currentLine.length() > 0){
				lore.add(currentLine);
			}
			
			
			meta.setLore(lore);
			
			traitStack.setItemMeta(meta);
			
			this.traitInventory.addItem(traitStack);
		}
	}

	
	@Override
	public Inventory getTopInventory() {
		return traitInventory;
	}

	
	@Override
	public Inventory getBottomInventory() {
		return Bukkit.createInventory(player, 36);
	}

	
	@Override
	public HumanEntity getPlayer() {
		return player;
	}

	
	@Override
	public InventoryType getType() {
		return InventoryType.CHEST;
	}

	

}
