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
package de.tobiyas.racesandclasses.listeners.holderchangegui;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractHolderManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.gui.HolderInventory;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.HolderPreSelectEvent;

public abstract class HolderChangeListenerGui implements Listener {
	
	/**
	 * The Main plugin
	 */
	protected static final RacesAndClasses plugin = RacesAndClasses.getPlugin();
	
	/**
	 * The holder manager cotroling the holders
	 */
	protected final AbstractHolderManager manager;
	
	/**
	 * Registers to Bukkit EventListener
	 */
	public HolderChangeListenerGui(AbstractHolderManager manager){	
		this.manager = manager;
		
		//safety check to prevent registering some code that will error.
		if(manager != null){
			Bukkit.getPluginManager().registerEvents(this, plugin);
		}
	}


	@EventHandler(priority = EventPriority.HIGHEST)
	public void RemoveAnyHolderItemFromPlayer(InventoryCloseEvent invClose){
		if(!invClose.getViewers().iterator().hasNext()) return;
		
		if(! (invClose.getView() instanceof HolderInventory)) return;
		String prefix = manager.getContainerTypeAsString();
		if(! (invClose.getView().getTopInventory().getName()
				.contains(prefix))) return;
		
		//safe to use since we only use online players.
		Player player = Bukkit.getPlayer(invClose.getView().getPlayer().getUniqueId());
		if(player == null) return;
		
		//iterate through all items
		for(ItemStack item : player.getInventory()){
			
			//lets see if any item is equals to a holder tag.
			if(isHolderItem(item)){
				player.getInventory().remove(item);
			}
		}
		
		player.updateInventory();
		//InventoryResync.resync(player);
	}
	
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void stopClosingWhenNoHolder(InventoryCloseEvent event){
		if(!event.getViewers().iterator().hasNext()) return;
		
		if(! (event.getView() instanceof HolderInventory)) return;
		String prefix = manager.getContainerTypeAsString();
		if(! (event.getView().getTopInventory().getName()
				.contains(prefix))) return;
		
		final Player player = (Player) event.getView().getPlayer();
		if(player == null) return;
	
		AbstractTraitHolder holder = manager.getHolderOfPlayer(player.getUniqueId());
		
		boolean forceReopen = false;
		if(manager == plugin.getClassManager()){
			forceReopen = plugin.getConfigManager().getGeneralConfig().isConfig_cancleGUIExitWhenNoClassPresent();
		}
		
		if(manager == plugin.getRaceManager()){
			forceReopen = plugin.getConfigManager().getGeneralConfig().isConfig_cancleGUIExitWhenNoRacePresent();
		}
		
		
		if(holder == manager.getDefaultHolder() && forceReopen){
			rescheduleOpening(player.getUniqueId(), 1);
			return;
		}
		
		if(manager == plugin.getRaceManager()){
			boolean openClassSelectionAfterwards = plugin.getConfigManager().getGeneralConfig().isConfig_openClassSelectionAfterRaceSelectionWhenNoClass();
			if(openClassSelectionAfterwards){
				boolean hasDefaultClass = plugin.getClassManager().getHolderOfPlayer(player.getUniqueId()) == plugin.getClassManager().getDefaultHolder();
				if(hasDefaultClass){
					final HolderInventory classSelectInventory = new HolderInventory(player, plugin.getClassManager());
					if(classSelectInventory.getNumberOfHolder() > 0){
						
						//schedule opening of Class selection.
						plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
							
							@Override
							public void run() {
								player.openInventory(classSelectInventory);
							}
						}, 4);
					}
				}
			}
			
		}
	}
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void playerSelectsHolder(InventoryClickEvent event){
		if(!event.getViewers().iterator().hasNext()) return;
		
		if(! (event.getView() instanceof HolderInventory)) return;
		String prefix = manager.getContainerTypeAsString();
		if(! (event.getView().getTopInventory().getName()
				.contains(prefix))) return;
		//now we are sure to be in an HolderGUI.
		
		Player player = (Player) event.getView().getPlayer();
		
		String playerName = event.getView().getPlayer().getName();
		ItemStack clickedItem = event.getCurrentItem();
		if(clickedItem == null || clickedItem.getType() == Material.AIR ){
			event.setCancelled(true);
			return;
		}
		
		if(!isHolderItem(clickedItem)){
			event.setCancelled(true);
			return;
		};
		
		if(event.getClick() != ClickType.RIGHT){
			if(!(event.getClick() == ClickType.LEFT 
					&& plugin.getConfigManager().getGeneralConfig().isConfig_alsoUseLeftClickInGuis())){
				
				event.setCancelled(true);
				return;
			}
			
		}
		
		AbstractTraitHolder holder = manager.getHolderOfPlayer(player.getUniqueId());
		HolderPreSelectEvent selectEvent = null;
		
		AbstractTraitHolder newHolder= getHolder(clickedItem);
		
		boolean hasNoHolder = holder == manager.getDefaultHolder();
		if(hasNoHolder){
			selectEvent = generateHolderSelectEvent(playerName, newHolder);
		}else{
			selectEvent = generateHolderChangeEvent(playerName, newHolder, holder);
		}
		
		plugin.getServer().getPluginManager().callEvent(selectEvent);
		if(selectEvent.isCancelled()){
			if(player != null){
				player.sendMessage(ChatColor.RED + selectEvent.getCancelMessage());
				return;
			}
		}
		
		boolean worked = true;
		if(hasNoHolder){
			worked = manager.addPlayerToHolder(player.getUniqueId(), newHolder.getName(), true);
		}else{
			worked = manager.changePlayerHolder(player.getUniqueId(), newHolder.getName(), true);
		}
		
		if(worked){
			if(player != null){
				player.sendMessage(ChatColor.GREEN + "You are now a " + ChatColor.LIGHT_PURPLE + newHolder.getName() + ChatColor.GREEN + ".");
				player.closeInventory();
				player.updateInventory();
			}
		}else{
			if(player != null){
				player.sendMessage(ChatColor.RED + "Did not work. :( .");
				player.closeInventory();
				player.updateInventory();
				
				//InventoryResync.resync(player);
			}
		}
	}
	
	/**
	 * The player wants to select his holder.
	 * Generate an Event for that.
	 * 
	 * @return
	 */
	protected abstract HolderPreSelectEvent generateHolderSelectEvent(String playerName, AbstractTraitHolder newHolder);

	
	/**
	 * The player wants to change his holder.
	 * Generate an Event for that.
	 * 
	 * @return
	 */
	protected abstract HolderPreSelectEvent generateHolderChangeEvent(String playerName, AbstractTraitHolder newHolder, AbstractTraitHolder oldHolder);
	
	
	/**
	 * Schedules a reopen of the Holder in 1 tick
	 * 
	 * @param playerUUID to schedule
	 */
	private void rescheduleOpening(final UUID uuid, int ticks) {
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				Player player = plugin.getServer().getPlayer(uuid);
				if(player != null && player.isOnline()){
					HolderInventory holderInv = new HolderInventory(player, manager);
					if(holderInv.getNumberOfHolder() > 0){
						player.openInventory(holderInv);
						return;
					}
					
					//No holder to select... So no opening needed.
				}
			}
		}, ticks);
	}


	/**
	 * Returns true if the Item is an Item of an Holder
	 * 
	 * @param item to check 
	 * @return true if it is, false otherwise
	 */
	private boolean isHolderItem(ItemStack item){
		return getHolder(item) != null;
	}
	
	
	/**
	 * Gets the Holder if it is found.
	 * Returns null Null if no Holder found.
	 * 
	 * @param item to check
	 * @return the Holder or NULL if not found.
	 */
	private AbstractTraitHolder getHolder(ItemStack item){
		if(item == null) return null;
		if(!item.hasItemMeta()) return null;
		if(!item.getItemMeta().hasDisplayName()) return null;
		
		String itemName = item.getItemMeta().getDisplayName().toLowerCase();
		
		//lets see if any item is equals to a holder tag.
		for(String holderName : manager.listAllVisibleHolders()){
			AbstractTraitHolder holder = manager.getHolderByName(holderName);
			String holderTag = holder.getTag().toLowerCase();
			if("".equals(holderTag)) holderTag = holder.getName().toLowerCase();
			
			if(itemName.contains(holderTag)){
				return holder;
			}
		}
		
		return null;
	}

}
