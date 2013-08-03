package de.tobiyas.racesandclasses.listeners.holderchangegui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractHolderManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.gui.HolderInventory;
import de.tobiyas.racesandclasses.eventprocessing.TraitEventManager;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.HolderSelectEvent;
import de.tobiyas.racesandclasses.util.inventory.InventoryResync;

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
		
		if(! (invClose.getView() instanceof HolderInventory)) return;
		String prefix = manager.getContainerTypeAsString();
		if(! (invClose.getView().getTopInventory().getName()
				.contains(prefix))) return;
		
		String playerName = invClose.getView().getPlayer().getName();
		Player player = Bukkit.getPlayer(playerName);
		if(player == null) return;
		
		boolean hasToRefresh = false;
		//iterate through all items
		for(ItemStack item : player.getInventory()){
			
			//lets see if any item is equals to a holder tag.
			if(isHolderItem(item)){
				player.getInventory().remove(item);
				hasToRefresh = true;
			}
		}
		
		if(hasToRefresh){
			InventoryResync.resync(player);
		}
	}
	
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void stopClosingWhenNoHolder(InventoryCloseEvent event){
		if(!event.getViewers().iterator().hasNext()) return;
		
		if(! (event.getView() instanceof HolderInventory)) return;
		String prefix = manager.getContainerTypeAsString();
		if(! (event.getView().getTopInventory().getName()
				.contains(prefix))) return;
	
		String playerName = event.getView().getPlayer().getName();
		AbstractTraitHolder holder = manager.getHolderOfPlayer(playerName);
		if(holder == manager.getDefaultHolder()){
			rescheduleOpening(playerName, 1);
			//TODO feedback!
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
		
		if(!event.isRightClick()){
			event.setCancelled(true);
			return;
		}
		
		AbstractTraitHolder holder = manager.getHolderOfPlayer(playerName);
		HolderSelectEvent selectEvent = null;
		
		AbstractTraitHolder newHolder= getHolder(clickedItem);
		
		boolean hasNoHolder = holder == manager.getDefaultHolder();
		if(hasNoHolder){
			selectEvent = generateHolderSelectEvent(playerName, newHolder);
		}else{
			selectEvent = generateHolderChangeEvent(playerName, newHolder, holder);
		}
		
		TraitEventManager.fireEvent(selectEvent);
		if(selectEvent.isCancelled()){
			Player player = Bukkit.getPlayer(playerName);
			if(player != null){
				player.sendMessage(ChatColor.RED + selectEvent.getCancelMessage());
				return;
			}
		}
		
		boolean worked = true;
		if(hasNoHolder){
			worked = manager.addPlayerToHolder(playerName, newHolder.getName());
		}else{
			worked = manager.changePlayerHolder(playerName, newHolder.getName());
		}
		
		
		if(worked){
			Player player = Bukkit.getPlayer(playerName);
			if(player != null){
				player.sendMessage(ChatColor.GREEN + "You are now a " + ChatColor.LIGHT_PURPLE + newHolder.getName() + ChatColor.GREEN + ".");
				player.closeInventory();
			}
		}else{
			Player player = Bukkit.getPlayer(playerName);
			if(player != null){
				player.sendMessage(ChatColor.RED + "Did not work. :( .");
				player.closeInventory();
			}
		}
	}
	
	/**
	 * The player wants to select his holder.
	 * Generate an Event for that.
	 * 
	 * @return
	 */
	protected abstract HolderSelectEvent generateHolderSelectEvent(String playerName, AbstractTraitHolder newHolder);

	
	/**
	 * The player wants to change his holder.
	 * Generate an Event for that.
	 * 
	 * @return
	 */
	protected abstract HolderSelectEvent generateHolderChangeEvent(String playerName, AbstractTraitHolder newHolder, AbstractTraitHolder oldHolder);
	
	
	/**
	 * Schedules a reopen of the Holder in 1 tick
	 * 
	 * @param playerName to schedule
	 */
	private void rescheduleOpening(final String playerName, int ticks) {
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				Player player = plugin.getServer().getPlayer(playerName);
				if(player != null && player.isOnline()){
					player.openInventory(new HolderInventory(player, manager));
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
		
		String itemName = item.getItemMeta().getDisplayName();		
		
		//lets see if any item is equals to a holder tag.
		for(String holderName : manager.listAllVisibleHolders()){
			AbstractTraitHolder holder = manager.getHolderByName(holderName);
			if(holder.getTag().equalsIgnoreCase(itemName)){
				return holder;
			}
		}
		
		return null;
	}

}
