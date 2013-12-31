package de.tobiyas.racesandclasses.listeners.generallisteners;

import static de.tobiyas.racesandclasses.translation.languages.Keys.stun_still;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.entitystatusmanager.stun.StunManager;

public class StunCancelListener implements Listener {

	/**
	 * The plugin to get the StunManager.
	 */
	private final RacesAndClasses plugin;
	
	/**
	 * Init the Listener.
	 */
	public StunCancelListener() {
		this.plugin = RacesAndClasses.getPlugin();
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	/**
	 * Returns the Stunmanager of the Plugin.
	 * 
	 * @return stun manager.
	 */
	private StunManager getStunManager(){
		return plugin.getStunManager();
	}
	
	/**
	 * Returns if an Entity is stunned at the moment.
	 * 
	 * @param entity to check
	 * 
	 * @return true if is stunned, false otherwise.
	 */
	private boolean isStunned(Entity entity){
		return getStunManager().isStunned(entity);
	}
	
	
	/**
	 * Returns the remaining stun time in seconds.
	 * 
	 * @param entity to check
	 * 
	 * @return returns the Rest time of the Stun in Seconds
	 */
	private int getRemainingStunSeconds(Entity entity){
		int restTimeInTicks = getStunManager().getRestStunTime(entity);
		int restTimeInSeconds = restTimeInTicks / 20;
		
		return restTimeInSeconds;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onMovement(PlayerMoveEvent event){
		Player player = event.getPlayer();
		if(isStunned(player)){
			event.setCancelled(true);
			
			String time = String.valueOf(getRemainingStunSeconds(player));
			String action = "move";
			LanguageAPI.sendTranslatedMessage(player, stun_still, "time", time, "action", action);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onTeleport(PlayerTeleportEvent event){
		Player player = event.getPlayer();
		if(isStunned(player)){
			event.setCancelled(true);
			String time = String.valueOf(getRemainingStunSeconds(player));
			String action = "teleport";
			LanguageAPI.sendTranslatedMessage(player, stun_still, "time", time, "action", action);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDamage(EntityDamageByEntityEvent event){
		if(isStunned(event.getDamager())){
			event.setCancelled(true);
			
			if(event.getDamager() instanceof Player){
				Player player = (Player) event.getDamager();
				
				String time = String.valueOf(getRemainingStunSeconds(player));
				String action = "attack";
				LanguageAPI.sendTranslatedMessage(player, stun_still, "time", time, "action", action);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBowShoot(EntityShootBowEvent event){
		if(isStunned(event.getEntity())){
			event.setCancelled(true);
			
			if(event.getEntity() instanceof Player){
				Player player = (Player) event.getEntity();
				
				String time = String.valueOf(getRemainingStunSeconds(player));
				String action = "shoot your bow";
				LanguageAPI.sendTranslatedMessage(player, stun_still, "time", time, "action", action);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event){
		Player player = event.getPlayer();
		
		if(isStunned(player)){
			event.setCancelled(true);
			
			String time = String.valueOf(getRemainingStunSeconds(player));
			String action = "place anything";
			LanguageAPI.sendTranslatedMessage(player, stun_still, "time", time, "action", action);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event){
		Player player = event.getPlayer();
		
		if(isStunned(player)){
			event.setCancelled(true);
			
			String time = String.valueOf(getRemainingStunSeconds(player));
			String action = "break anything";
			LanguageAPI.sendTranslatedMessage(player, stun_still, "time", time, "action", action);
		}
	}
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCreeperExplode(ExplosionPrimeEvent event){
		if(isStunned(event.getEntity())){
			event.setCancelled(true);
		}
	}
}
