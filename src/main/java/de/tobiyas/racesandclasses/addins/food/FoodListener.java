package de.tobiyas.racesandclasses.addins.food;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayerManager;

public class FoodListener implements Listener {

	private final RacesAndClasses plugin = RacesAndClasses.getPlugin();
	
	
	private final Map<RaCPlayer,Food> eating = new HashMap<RaCPlayer, Food>();
	
	
	public FoodListener() {
		if(!plugin.getConfigManager().getGeneralConfig().isConfig_food_enabled()) return;
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				Iterator<Entry<RaCPlayer,Food>> it = eating.entrySet().iterator();
				while(it.hasNext()){
					Entry<RaCPlayer,Food> entry = it.next();
					RaCPlayer player = entry.getKey();
					if(!player.isOnline()){
						it.remove();
						continue;
					}
					
					Food food = entry.getValue();
					food.tick(player);
					if(!food.isValid()) {
						player.sendMessage(ChatColor.GREEN + "Eating finished.");
						it.remove();
					}
				}
			}
			
		}.runTaskTimer(plugin, 20 * 1, 20 * 1);
	}

	
	
	@EventHandler
	public void playerInteract(PlayerInteractEvent event){
		//only RightClick is accepted.
		if(!(event.getAction() == Action.RIGHT_CLICK_AIR 
				|| event.getAction() == Action.RIGHT_CLICK_BLOCK)) return;
		
		RaCPlayer player = RaCPlayerManager.get().getPlayer(event.getPlayer());
		if(eating.containsKey(player)) return;
		
		//player is moving.
		if(event.getPlayer().getVelocity().length() > 0.1) return;
		
		//already eating.
		if(eating.containsKey(player)) return;
		
		ItemStack item = event.getItem();
		if(item == null) return;
		
		Food food = new Food(item);
		if(food.isValid()){
			event.setCancelled(true);
			
			item.setAmount(item.getAmount() - 1);
			player.getPlayer().setItemInHand(item);
			
			eating.put(player, food);
			player.sendMessage(ChatColor.GREEN + "Do not move, or your eating will be cancled.");
		}
	}
	
	
	@EventHandler
	public void playerMove(PlayerMoveEvent event){
		
		if(event.getFrom().getBlockX() == event.getTo().getBlockX()
				&& event.getFrom().getBlockY() == event.getTo().getBlockY()
				&& event.getFrom().getBlockZ() == event.getTo().getBlockZ()) return;
		
		RaCPlayer player = RaCPlayerManager.get().getPlayer(event.getPlayer());
		if(!eating.containsKey(player)) return;
		
		eating.remove(player);
		player.sendMessage(ChatColor.RED + "Eating cancled.");
	}
	
	
}
