package de.tobiyas.racesandclasses.listeners.generallisteners;

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.healthmanagement.HealthManager;

public class Listener_GodModeDamagePrevent implements Listener{

	private RacesAndClasses plugin;
	
	public Listener_GodModeDamagePrevent() {
		plugin = RacesAndClasses.getPlugin();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void checkPlayerGodMode(EntityDamageEvent event){
		if(event.getEntityType() != EntityType.PLAYER){
			return;
		}
		
		//safe cast because of check before
		Player player = (Player) event.getEntity();
		if(HealthManager.getHealthManager().isGod(player.getName())){
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void checkPlayerGodMode(EntityDeathEvent event){
		if(event.getEntityType() != EntityType.PLAYER){
			return;
		}
		
		//safe cast because of check before
		Player player = (Player) event.getEntity();
		if(HealthManager.getHealthManager().isGod(player.getName())){
			player.sendMessage(ChatColor.GREEN + "Sorry, even " + ChatColor.GOLD + "GOD"
				+ ChatColor.GREEN + " could not prevent your death.  " + ChatColor.BLUE + ":(");
		}
	}

}
