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
package de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.gui;

import java.util.LinkedList;
import java.util.List;

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

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractHolderManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.HolderPreSelectEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.classevent.PreClassSelectEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.raceevent.PreRaceSelectEvent;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.translation.languages.Keys;
import de.tobiyas.util.formating.StringFormatUtils;

public class HolderInventory extends InventoryView{

	/**
	 * The Number of holders given in the selection.
	 */
	protected int numberOfHolder;
	
	/**
	 * The player that will select a holders
	 */
	private final Player player;
	
	/**
	 * The inventory with the selectable Holders
	 */
	private final Inventory holderInventory;
	
	/**
	 * Plugin to call stuff on like config
	 */
	private static final RacesAndClasses plugin = RacesAndClasses.getPlugin();
	
	
	/**
	 * Generates an Inventory for the Player and the passed HolderManager
	 * 
	 * @param player
	 * @param holderManager
	 */
	public HolderInventory(Player player, AbstractHolderManager holderManager) {
		super();
		
		this.player = player;
		
		this.numberOfHolder = 0;
		
		int inventorySize = (int) (Math.ceil((holderManager.getAllHolderNames().size() / 9d)) + 1)  * 9;
		if(inventorySize < 35){
			inventorySize = 36;
		}
		
		String name = LanguageAPI.translate(Keys.holder_selectiongui_header,
				"holder", StringFormatUtils.firstCapitalRestLow(holderManager.getContainerTypeAsString()),
				"player", player.getName());
		
		
		if(name.length() > 32) name = name.substring(0, 32);
		this.holderInventory = Bukkit.getServer().createInventory(player, inventorySize, name);
		
		fillWithHolders(holderManager);
	}
	
	
	/**
	 * Fills the inventory with items representing the holders.
	 * 
	 * @param manager
	 */
	private void fillWithHolders(AbstractHolderManager manager){
		for(String holderName : manager.listAllVisibleHolders()){
			if(plugin.testingMode){ 
				//Testing seems to not realize Bukkit is present. Method not found on ItemStack.
				this.numberOfHolder++;
				continue;
			}
			
			AbstractTraitHolder holder = manager.getHolderByName(holderName);
			if(!hasPermission(holder, manager)) continue;
			
			
			ItemStack item = holder.getHolderSelectionItem() != null 
					? holder.getHolderSelectionItem().clone()
					: new ItemStack(Material.BOOK_AND_QUILL);
			
			ItemMeta meta = item.getItemMeta();
			
			boolean isEmptyTag = holder.getTag() == null || holder.getTag().equals("");
			meta.setDisplayName(isEmptyTag ? "[" + holder.getDisplayName() + "]" : holder.getTag());
			
			List<String> lore = meta.hasLore() ? meta.getLore() : new LinkedList<String>();
			
			if(holder.hasHolderDescription()){
				String description = holder.getHolderDescription();
				String[] split = description.split("#n");
				
				for(String desc : split){
					lore.add(ChatColor.translateAlternateColorCodes('&', desc));					
				}
			}

			String healthString = getHealthString(holder);
			if(healthString != null){
				lore.add(ChatColor.AQUA + LanguageAPI.translate(Keys.health) + ": ");
				lore.add(ChatColor.LIGHT_PURPLE + "  " + healthString);
			}
			
			//add armor as lore
			lore.add(ChatColor.AQUA + LanguageAPI.translate(Keys.armor) + ":");
			if(holder.getArmorPerms().size() > 0){
				lore.add(ChatColor.LIGHT_PURPLE + holder.getArmorString());
			}else{
				lore.add(ChatColor.LIGHT_PURPLE + "NONE");
			}
			
			lore.add(ChatColor.AQUA + LanguageAPI.translate(Keys.traits) + ":");
			
			//add trait text as lore
			for(Trait trait: holder.getVisibleTraits()){
				lore.add(ChatColor.DARK_AQUA + trait.getDisplayName() + ": " );
				String traitConfig = trait.getPrettyConfiguration();
				String[] words = traitConfig.split(" ");
				
				String currentLine = ChatColor.YELLOW + " -" + words[0];
				for(int i = 1; i < words.length; i++){
					String currentWord = words[i];
					
					if(currentLine.length() + words.length + 1 > 29){
						lore.add(currentLine);
						currentLine = ChatColor.YELLOW + "  " + currentWord;
					}else{
						currentLine += " " + currentWord;
					}
				}
				if(currentLine.length() > 0){
					lore.add(currentLine);
				}
			}
			
			meta.setLore(lore);
			item.setItemMeta(meta);
			
			holderInventory.addItem(item);
			this.numberOfHolder++;
		}
	}

	
	/**
	 * Returns a health String depending on the ContainerType
	 * 
	 * @param holders
	 * @return
	 */
	private String getHealthString(AbstractTraitHolder holder) {
		if(holder instanceof ClassContainer){
			ClassContainer container = (ClassContainer) holder;
			return container.getClassHealthModify();
		}
		
		if(holder instanceof RaceContainer){
			RaceContainer container = (RaceContainer) holder;
			return String.valueOf(container.getRaceMaxHealth());
		}
		
		return null;
	}


	/**
	 * Checks if a player has the Permission for a holders
	 * 
	 * @param holders to check
	 * @param manager the manager of the holders to check
	 * @return true if the player has access, false otherwise.
	 */
	private boolean hasPermission(AbstractTraitHolder holder, AbstractHolderManager manager) {		
		HolderPreSelectEvent event = null;
		if(manager == plugin.getClassManager()){
			event = new PreClassSelectEvent(player, (ClassContainer) holder, true, false);
		}
		
		if(manager == plugin.getRaceManager()){
			event = new PreRaceSelectEvent(player, (RaceContainer) holder, true, false);
		}
		
		if(event == null) return true;
		
		plugin.getServer().getPluginManager().callEvent(event);
		return !event.isCancelled();
	}


	@Override
	public Inventory getTopInventory() {
		return holderInventory;
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


	/**
	 * @return the numberOfHolder
	 */
	public int getNumberOfHolder() {
		return numberOfHolder;
	}

}
