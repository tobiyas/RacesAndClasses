package de.tobiyas.racesandclasses.listeners.equipement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.eventprocessing.TraitEventManager;
import de.tobiyas.racesandclasses.eventprocessing.events.inventoryitemevents.PlayerEquipsArmorEvent;
import de.tobiyas.racesandclasses.util.inventory.InventoryResync;
import de.tobiyas.racesandclasses.util.items.ArmorContainer;
import de.tobiyas.racesandclasses.util.items.ItemUtils;
import de.tobiyas.racesandclasses.util.items.ItemUtils.ArmorSlot;

public class Listener_PlayerEquipChange implements Listener {

	private Map<String, ArmorContainer> inventoryCache;
	
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
		inventoryCache = new HashMap<String, ArmorContainer>();
	}
	
	
	@EventHandler
	public void playerClickedOnGroudToEquipItem(PlayerInteractEvent event){
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
		if(!inventEvent.getViewers().iterator().hasNext()) return; //empty player is possible
		
		String name = inventEvent.getViewers().iterator().next().getName();
		Player player = plugin.getServer().getPlayer(name);
		if(player == null){
			return;
		}
		
		if(!inventoryCache.containsKey(name)){
			inventoryCache.put(name, new ArmorContainer(player.getInventory()));
		}
	}
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClose(InventoryCloseEvent closeEvent){
		if(closeEvent.getPlayer() == null) return; //empty player is possible
		
		String name = closeEvent.getPlayer().getName();
		Player player = plugin.getServer().getPlayer(name);
		
		ArmorContainer oldCache = inventoryCache.get(name);
		if(oldCache == null){
			return;
		}
		
		if(player == null){
			inventoryCache.remove(name);
			return;
		}
		
		List<ItemStack> changed = oldCache.stillSame(closeEvent.getPlayer().getInventory());
		for(ItemStack change : changed){
			PlayerEquipsArmorEvent event = new PlayerEquipsArmorEvent(player, change);
			TraitEventManager.fireEvent(event);
			
			if(event.isCancelled()) {
				removeArmor(player, change);
				InventoryResync.resync(player);
			}
		}
		
		inventoryCache.remove(name);
	}

	/**
	 * Removes the item located at the Item slot passed of a player passed.
	 * 
	 * @param player
	 * @param change
	 */
	private void removeArmor(Player player, ItemStack change) {
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
