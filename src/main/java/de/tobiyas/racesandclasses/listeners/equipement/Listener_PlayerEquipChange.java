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
package de.tobiyas.racesandclasses.listeners.equipement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.eventprocessing.TraitEventManager;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.resolvers.WorldResolver;
import de.tobiyas.racesandclasses.eventprocessing.events.inventoryitemevents.PlayerEquipsArmorEvent;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.util.inventory.InventoryResync;
import de.tobiyas.racesandclasses.util.items.ArmorContainer;
import de.tobiyas.racesandclasses.util.items.ItemUtils;
import de.tobiyas.racesandclasses.util.items.ItemUtils.ArmorSlot;

public class Listener_PlayerEquipChange implements Listener {

	private Map<RaCPlayer, ArmorContainer> inventoryCache;
	
	/**
	 * The plugin to register to
	 */
	private final RacesAndClasses plugin;

	
	/**
	 * Instantiates a Player equip listener
	 * listening to armory changes and forwards them as own event.
	 */
	public Listener_PlayerEquipChange(){
		plugin = RacesAndClasses.getPlugin();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		inventoryCache = new HashMap<RaCPlayer, ArmorContainer>();
	}
	
	
	@EventHandler
	public void playerClickedOnGroudToEquipItem(PlayerInteractEvent event){
		boolean disableArmorCheck = plugin.getConfigManager().getGeneralConfig().isConfig_disableArmorChecking();
		if(disableArmorCheck) return;
		
		ArmorSlot armorSlot = ItemUtils.getItemSlotEquiping(event.getItem());
		
		if(armorSlot == ArmorSlot.NONE){
			return;
		}
		
		ItemStack equiped = ItemUtils.getItemInArmorSlotOfPlayer(event.getPlayer(), armorSlot);
		if(equiped == null || equiped.getType() == Material.AIR){
			PlayerEquipsArmorEvent equipEvent = new PlayerEquipsArmorEvent(event.getPlayer(), event.getItem());
			TraitEventManager.fireEvent(equipEvent);
			
			if(equipEvent.isCancelled()){
				event.setCancelled(true);
				InventoryResync.resync(event.getPlayer());
			}
		}
	}
	
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void saveOldArmors(InventoryClickEvent inventEvent){
		boolean disableArmorCheck = plugin.getConfigManager().getGeneralConfig().isConfig_disableArmorChecking();
		if(disableArmorCheck) return;
		
		
		InventoryHolder invHolder = inventEvent.getInventory().getHolder();
		if(invHolder == null || !(invHolder instanceof HumanEntity)){
			//empty player is possible
			return;
		}
		
		HumanEntity holder = (HumanEntity) invHolder;
		if(!(holder instanceof Player)) return;
		
		RaCPlayer player = RaCPlayerManager.get().getPlayer((Player) holder);
		boolean onDisabledWorld = WorldResolver.isOnDisabledWorld(player);
		if(onDisabledWorld) return;
				
		if(!inventoryCache.containsKey(player.getName())){
			inventoryCache.put(player, new ArmorContainer(player.getPlayer().getInventory()));
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClose(InventoryCloseEvent closeEvent){
		boolean disableArmorCheck = plugin.getConfigManager().getGeneralConfig().isConfig_disableArmorChecking();
		
		if(disableArmorCheck) return;
		if(closeEvent.getPlayer() == null) return; //empty player is possible
		
		//only investigate Player inventory.
		if(closeEvent.getView().getBottomInventory().getType() != InventoryType.PLAYER) return;
		
		HumanEntity humanEntity = closeEvent.getPlayer();
		if(!(humanEntity instanceof Player)) return;
		
		RaCPlayer player = RaCPlayerManager.get().getPlayer((Player)humanEntity);
		ArmorContainer oldCache = inventoryCache.get(player);
		
		if(oldCache == null) return;
		
		boolean onDisabledWorld = WorldResolver.isOnDisabledWorld(player);
		if(onDisabledWorld) return;
		
		List<ItemStack> changed = oldCache.stillSame(closeEvent.getPlayer().getInventory());
		for(ItemStack change : changed){
			PlayerEquipsArmorEvent event = new PlayerEquipsArmorEvent(player.getPlayer(), change);
			TraitEventManager.fireEvent(event);
			
			if(event.isCancelled()) {
				removeArmor(player, change);
				InventoryResync.resync(player.getPlayer());
			}
		}
		
		inventoryCache.remove(player);
	}

	/**
	 * Removes the item located at the Item slot passed of a player passed.
	 * 
	 * @param player
	 * @param change
	 */
	private void removeArmor(RaCPlayer racPlayer, ItemStack change) {
		Player player = racPlayer.getPlayer();
		ArmorSlot slot = ItemUtils.getItemSlotEquiping(change);
		if(!player.getInventory().addItem(change).isEmpty()){
			player.getWorld().dropItem(player.getLocation(), change);
		}
		
		ItemStack air = new ItemStack(Material.AIR);
		switch (slot) {
			case HELMET: player.getInventory().setHelmet(air); break;
			case CHESTPLATE: player.getInventory().setChestplate(air); break;
			case LEGGINGS: player.getInventory().setLeggings(air); break;
			case BOOTS: player.getInventory().setBoots(air); break;

		default:
			break;
		}
	}
	
}
