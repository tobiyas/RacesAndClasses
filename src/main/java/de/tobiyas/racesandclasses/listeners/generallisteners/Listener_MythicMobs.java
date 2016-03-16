package de.tobiyas.racesandclasses.listeners.generallisteners;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LevelAPI;
import net.elseland.xikage.MythicMobs.API.Bukkit.Events.MythicMobDeathEvent;

public class Listener_MythicMobs implements Listener {

	
	public Listener_MythicMobs() {
		if(Bukkit.getPluginManager().getPlugin("MythicMobs") == null) return;
		RacesAndClasses.getPlugin().registerEvents(this);
	}
	
	
	@EventHandler
	public void onMythicMobDead(MythicMobDeathEvent event){
		int expToGive = event.getExp();
		if(expToGive <= 0) return;
		
		LivingEntity killer = event.getKiller();
		if(killer instanceof Player){
			Player player = (Player) killer;
			LevelAPI.addExp(player, expToGive);
		}
	}
	
	
}
