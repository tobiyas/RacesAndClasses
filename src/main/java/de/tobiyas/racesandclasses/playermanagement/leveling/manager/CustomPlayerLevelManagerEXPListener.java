package de.tobiyas.racesandclasses.playermanagement.leveling.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LevelAPI;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.playermanagement.leveling.LevelingSystem;

public class CustomPlayerLevelManagerEXPListener implements Listener {

	/**
	 * The instance to use.
	 */
	private static CustomPlayerLevelManagerEXPListener instance;
	
	
	private final RacesAndClasses plugin;
	
	
	public CustomPlayerLevelManagerEXPListener() {
		this.plugin = RacesAndClasses.getPlugin();
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	
	
	@EventHandler
	public void monsterDeadEvent(EntityDeathEvent event){
		if(plugin.getConfigManager().getGeneralConfig().getConfig_useLevelSystem() != LevelingSystem.RacesAndClasses) return;
		if(event.getEntity() == null) return;
		
		Player killer = event.getEntity().getKiller();
		if(killer == null) return;
		
		EntityType killed = event.getEntityType();
		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(killer);
		
		killedEntity(racPlayer, killed);
	}
	
	
	
	
	/**
	 * This adds EXP for killing something!
	 * 
	 * @param racPlayer that killed that entity
	 * @param killed the entity type killed.
	 */
	private void killedEntity(RaCPlayer racPlayer, EntityType killed) {
		if(!plugin.getConfigManager().getGeneralConfig().getConfig_custom_level_exp_gain().containsKey(killed)) return;
		
		int exp = plugin.getConfigManager().getGeneralConfig().getConfig_custom_level_exp_gain().get(killed);
		LevelAPI.addExp(racPlayer, exp);
	}



	/**
	 * This will register the Listener IF needed!
	 */
	public static void launch(){
		if(instance == null) instance = new CustomPlayerLevelManagerEXPListener();
	}
	
}
