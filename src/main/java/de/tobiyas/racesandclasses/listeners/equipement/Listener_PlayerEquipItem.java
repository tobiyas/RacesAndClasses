package de.tobiyas.racesandclasses.listeners.equipement;

import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.eventprocessing.events.inventoryitemevents.PlayerEquipsArmorEvent;
import de.tobiyas.racesandclasses.util.inventory.InventoryResync;
import de.tobiyas.util.items.ItemUtils;
import de.tobiyas.util.items.ItemUtils.ArmorSlot;

public class Listener_PlayerEquipItem implements Listener {

	/**
	 * The Plugin to use.
	 */
	private final RacesAndClasses plugin;
	
	
	public Listener_PlayerEquipItem() {
		this.plugin = RacesAndClasses.getPlugin();
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	
	@EventHandler
	public void playerClicksToEquip(PlayerInteractEvent event){
		if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) return;
		if(plugin.getConfigManager().getGeneralConfig().isConfig_disableArmorChecking()) return;
		
		ArmorSlot armorSlot = ItemUtils.getItemSlotEquiping(event.getItem());
		if(armorSlot == ArmorSlot.NONE) return;
		
		evaluate(event, armorSlot, event.getPlayer(), event.getItem());
	}
	
	
	
	@EventHandler
	public void dropperEquip(BlockDispenseEvent event){
		if(plugin.getConfigManager().getGeneralConfig().isConfig_disableArmorChecking()) return;
		
		ArmorSlot slot = ItemUtils.getItemSlotEquiping(event.getItem());
		//none means no armor. Or at least no normal armor.
		if(slot == ArmorSlot.NONE) return;
		
		Location loc = event.getBlock().getLocation();
		
		Player near = null;
		for(Player player : loc.getWorld().getPlayers()){
			if(loc.distanceSquared(player.getLocation()) < 2 * 2) {
				near = player;
				break;
			}
		}
		
		if(near == null) return;
		evaluate(event, slot, near, event.getItem());
	}
	
	
	@EventHandler
	public void playerLetItemInItemslot(InventoryClickEvent event){
		if(plugin.getConfigManager().getGeneralConfig().isConfig_disableArmorChecking()) return;
		
		boolean isPlayerInventory = event.getInventory() instanceof CraftingInventory;
		if(!isPlayerInventory) return;
		
		if(((CraftingInventory)event.getInventory()).getType() != InventoryType.CRAFTING) return;
		
		HumanEntity clicker = event.getWhoClicked();
		if(!(clicker instanceof Player)) return;
		
		Player player = (Player) clicker;
		
		boolean isArmorSlot = event.getSlotType() == SlotType.ARMOR;
		boolean isEmptyClickedSlot = event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR;
		boolean isEmptyCrosshair = event.getCursor() == null || event.getCursor().getType() == Material.AIR;
		boolean isShift = event.isShiftClick();
		boolean usedNumber = event.getHotbarButton() != -1;
		
		ArmorSlot clickedItemArmorSlot = ItemUtils.getItemSlotEquiping(event.getCurrentItem());
		ArmorSlot cursorArmorSlot = ItemUtils.getItemSlotEquiping(event.getCursor());

		if(usedNumber && isEmptyCrosshair && isArmorSlot){
			//used a Hotkey.
			ItemStack used = player.getInventory().getItem(event.getHotbarButton());
			ArmorSlot slot = ItemUtils.getItemSlotEquiping(used);
			if(slot == ArmorSlot.NONE) return;
			
			evaluate(event, slot, player, used);
			return;
		}
		
		if(!isEmptyClickedSlot && isShift && clickedItemArmorSlot != ArmorSlot.NONE){
			//we have a shift click here.
			evaluate(event, clickedItemArmorSlot, player, event.getCurrentItem());
			return;
		}
		
		if(!isEmptyCrosshair && isArmorSlot && cursorArmorSlot != ArmorSlot.NONE){
			//we have a simple drag and drop.
			evaluate(event, cursorArmorSlot, player, event.getCursor());
			return;
		}
	}
	
	
	@EventHandler
	public void playerDraggedStuff(InventoryDragEvent event){
		if(plugin.getConfigManager().getGeneralConfig().isConfig_disableArmorChecking()) return;
		
		
		boolean isPlayerInventory = event.getInventory() instanceof CraftingInventory;
		if(!isPlayerInventory) return;
		
		if(((CraftingInventory)event.getInventory()).getType() != InventoryType.CRAFTING) return;
		
		HumanEntity clicker = event.getWhoClicked();
		if(!(clicker instanceof Player)) return;
		
		Player player = (Player) clicker;
		
		for(Entry<Integer,ItemStack> entry : event.getNewItems().entrySet()){
			int id = entry.getKey();
			ItemStack item = entry.getValue();
			
			if(item == null || item.getType() == Material.AIR) continue;
			
			if(id >= 5 && id <= 8){
				//only check Armor slots.
				
				ArmorSlot slot = ItemUtils.getItemSlotEquiping(item);
				evaluate(event, slot, player, item);
				return;
			}
		}
	}
	
	
	
	/**
	 * Evaluates the passed values.
	 * 
	 * @param cancellable the event to set
	 * @param slot the slot to eval
	 * @param player the player to eval
	 * @param item the item to eval
	 */
	protected void evaluate(Cancellable cancellable, ArmorSlot slot, Player player, ItemStack item){
		ItemStack equiped = ItemUtils.getItemInArmorSlotOfPlayer(player, slot);
		if(equiped == null || equiped.getType() == Material.AIR){
			PlayerEquipsArmorEvent newEvent = new PlayerEquipsArmorEvent(player, item);
			Bukkit.getPluginManager().callEvent(newEvent);
			
			if(newEvent.isCancelled()){
				cancellable.setCancelled(true);
				InventoryResync.resync(player);
			}
		}
	}
	
}
