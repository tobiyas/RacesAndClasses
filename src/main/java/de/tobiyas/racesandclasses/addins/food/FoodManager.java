package de.tobiyas.racesandclasses.addins.food;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.translation.languages.Keys;
import de.tobiyas.util.schedule.DebugBukkitRunnable;

public class FoodManager implements Listener {

	/**
	 * The plugin to use for calls.
	 */
	private final RacesAndClasses plugin;
	
	/**
	 * The map of currently eating people.
	 */
	private final Map<RaCPlayer,Food> eating = new HashMap<RaCPlayer, Food>();
	
	/**
	 * the task for the Eat-Ticker.
	 */
	private BukkitTask task;
	
	
	public FoodManager(RacesAndClasses plugin) {
		this.plugin = plugin;
	}
	
	
	public void reload(){
		//First unregister + kill old task:
		HandlerList.unregisterAll(this);
		if(task != null) task.cancel();
		eating.clear();
		
		//Now check if enabled:
		if(!plugin.getConfigManager().getGeneralConfig().isConfig_food_enabled()) return;
		plugin.registerEvents(this);
		
		task = new DebugBukkitRunnable("FoodListenerEating"){
			@Override
			protected void runIntern() {
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
						LanguageAPI.sendTranslatedMessage(player, Keys.food_finished);
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
			
			//Player has max health.
			if(player.getHealth() >= player.getMaxHealth()){
				LanguageAPI.sendTranslatedMessage(player, Keys.health_full);
				return;
			}
			
			item.setAmount(item.getAmount() - 1);
			player.getPlayer().setItemInHand(item);
			
			eating.put(player, food);
			LanguageAPI.sendTranslatedMessage(player, Keys.food_eat);
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
		LanguageAPI.sendTranslatedMessage(player, Keys.food_cancle);
	}
	
	
}
