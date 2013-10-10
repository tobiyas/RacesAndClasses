package de.tobiyas.racesandclasses.eventprocessing.events.inventoryitemevents;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

/**
 * This event is fired when a Player equips a peace of armor.
 * 
 * @author Tobiyas
 */
public class PlayerEquipsArmorEvent extends PlayerEvent implements Cancellable{
	
	/**
	 * Some strange list. Whatever it tells...
	 */
	private static final HandlerList handlers = new HandlerList();
	
	
	/**
	 * The {@link ItemStack} of the player equipping.
	 */
	private final ItemStack armorItem;

	private boolean isCanclled;
	

	/**
	 * Indicates that a player is trying to put on a armor piece.
	 * 
	 * @param player
	 * @param armor
	 */
	public PlayerEquipsArmorEvent(Player player, ItemStack armorItem) {
		super(player);
		
		this.armorItem = armorItem;
		
	}


	public ItemStack getArmorItem() {
		return armorItem;
	}




	public static HandlerList getHandlerList() {
        return handlers;
    }
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}




	@Override
	public boolean isCancelled() {
		return isCanclled;
	}




	@Override
	public void setCancelled(boolean cancel) {
		isCanclled = cancel;
	}

}
