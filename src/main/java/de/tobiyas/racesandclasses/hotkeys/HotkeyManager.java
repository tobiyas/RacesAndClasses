package de.tobiyas.racesandclasses.hotkeys;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayerManager;

public class HotkeyManager implements Listener {

	/**
	 * The Inventory of the Players regarding the HotKeys
	 */
	private final Map<RaCPlayer,HotKeyInventory> inventories = new HashMap<RaCPlayer, HotKeyInventory>(); 
	
	
	public HotkeyManager() {
		Bukkit.getPluginManager().registerEvents(this, RacesAndClasses.getPlugin());
		new Listener_HotKey();
		
		new BukkitRunnable() {
			@Override
			public void run() {
				for(HotKeyInventory inv : inventories.values()){
					inv.updatePlayerInventory();
				}
			}
		}.runTaskTimer(RacesAndClasses.getPlugin(), 20, 20);
	}
	
	
	
	/**
	 * Returns the Inventory of the Player.
	 * 
	 * @param player to get for
	 * 
	 * @return the inv of the player.
	 */
	public HotKeyInventory getInv(RaCPlayer player){
		HotKeyInventory inv = inventories.get(player);
		if(inv == null){
			inv = new HotKeyInventory(player);
			inventories.put(player, inv);
		}
		
		return inv;		
	}
	
	
	/**
	 * Returns the Inventory of the Player.
	 * 
	 * @param playerId to get for
	 * 
	 * @return the inv of the player.
	 */
	public HotKeyInventory getInv(UUID playerId){
		return getInv(RaCPlayerManager.get().getPlayer(playerId));
	}
	
	/**
	 * Returns the Inventory of the Player.
	 * 
	 * @param player to get for
	 * 
	 * @return the inv of the player.
	 */
	public HotKeyInventory getInv(Player player){
		return getInv(RaCPlayerManager.get().getPlayer(player));
	}
	
	
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void playerLogout(PlayerQuitEvent event){
		Player player = event.getPlayer();
		HotKeyInventory inv = getInv(player);
		
		//notify inv that the player has quit.
		inv.changeToBuildInv();
	}
	
}