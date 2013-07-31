package de.tobiyas.racesandclasses.listeners.holderchangegui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractHolderManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.gui.HolderInventory;

public abstract class HolderChangeListenerGui implements Listener {

	/**
	 * The Main plugin
	 */
	protected final RacesAndClasses plugin;
	
	/**
	 * The holder manager cotrolling the holders
	 */
	protected final AbstractHolderManager manager;
	
	/**
	 * Registers to Bukkit EventListener
	 */
	public HolderChangeListenerGui(AbstractHolderManager manager){	
		plugin = RacesAndClasses.getPlugin();
		this.manager = manager;
		
		//safety check to prevent registering some code that will error.
		if(manager != null){
			Bukkit.getPluginManager().registerEvents(this, plugin);
		}
	}


	@EventHandler(priority = EventPriority.HIGHEST)
	public void RemoveAnyHolderItemFromPlayer(InventoryCloseEvent invClose){
		if(!invClose.getViewers().iterator().hasNext()) return;
		
		//if(! (invClose.getView() instanceof HolderInventory)) return;
		String prefix = manager.getContainerTypeAsString();
		if(! (invClose.getView().getTopInventory().getName()
				.startsWith(prefix))) return;
		
		String playerName = invClose.getView().getPlayer().getName();
		Player player = Bukkit.getPlayer(playerName);
		if(player == null) return;
		
		//iterate through all items
		for(ItemStack item : player.getInventory()){
			
			//lets see if any item is equals to a holder tag.
			if(isHolderItem(item)){
				player.getInventory().remove(item);
			}
		}
		
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void stopClosingWhenNoHolder(InventoryCloseEvent event){
		if(!event.getViewers().iterator().hasNext()) return;
		
		String prefix = manager.getContainerTypeAsString();
		if(! (event.getView().getTopInventory().getName()
				.contains(prefix))) return;
	
		String playerName = event.getView().getPlayer().getName();
		AbstractTraitHolder holder = manager.getHolderOfPlayer(playerName);
		if(holder == manager.getDefaultHolder()){
			rescheduleOpening(playerName);
			//TODO feedback!
		}
	}
	
	@EventHandler
	public void saveOnInvOpen(InventoryOpenEvent event){
		if(!event.getViewers().iterator().hasNext()) return;
		
		String prefix = manager.getContainerTypeAsString();
		if(! (event.getView().getTopInventory().getName()
				.contains(prefix))) return;
		
		
	}
	
	/**
	 * Schedules a reopen of the Holder in 1 tick
	 * 
	 * @param playerName to schedule
	 */
	private void rescheduleOpening(final String playerName) {
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				Player player = plugin.getServer().getPlayer(playerName);
				if(player != null && player.isOnline()){
					player.openInventory(new HolderInventory(player, manager));
				}
			}
		}, 1);
	}


	/**
	 * Returns true if the Item is an Item of an Holder
	 * 
	 * @param item to check 
	 * @return true if it is, false otherwise
	 */
	private boolean isHolderItem(ItemStack item){
		if(item == null) return false;
		if(!item.hasItemMeta()) return false;
		
		String itemName = item.getItemMeta().getDisplayName();		
		
		//lets see if any item is equals to a holder tag.
		for(String holderName : manager.listAllVisibleHolders()){
			AbstractTraitHolder holder = manager.getHolderByName(holderName);
			if(holder.getTag().equalsIgnoreCase(itemName)){
				return true;
			}
		}
		
		return false;
	}
	
	@EventHandler
	public void movingItemInChangeGui(InventoryClickEvent event){
		String prefix = manager.getContainerTypeAsString();
		if(! (event.getView().getTopInventory().getName()
				.contains(prefix))) return;
		
		
	}

}
