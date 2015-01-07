package de.tobiyas.racesandclasses.listeners.generallisteners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.CooldownApi;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.translation.languages.Keys;
import de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier.EntityDamage;

public class Listener_RaceTeams implements Listener {

	/**
	 * The Plugin to use.
	 */
	private final RacesAndClasses plugin;
	
	
	public Listener_RaceTeams() {
		this.plugin = RacesAndClasses.getPlugin();
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void playerDamagePlayer(EntityDamageByEntityEvent event){
		if(!plugin.getConfigManager().getGeneralConfig().getConfig_enableRaceTeams()) return;
		
		Entity damager = event.getDamager();
		Entity damagee = event.getEntity();
		
		if(damager == null || damagee == null) return;
		if(damager.getType() != EntityType.PLAYER || damagee.getType() != EntityType.PLAYER) return;
		
		RaCPlayer damagerP = RaCPlayerManager.get().getPlayer((Player) damager);
		RaCPlayer damageeP = RaCPlayerManager.get().getPlayer((Player) damagee);
		
		if(plugin.getConfigManager().getRaceTeamManager().sameTeam(damagerP, damageeP)){
			//they are on the Same team!
			event.setCancelled(true);
			EntityDamage.safeSetDamage(0, event);
			
			int cooldown = CooldownApi.getCooldownOfPlayer(damagerP.getName(), "friendly.fire");
			if(cooldown <= 0){
				//set Cooldown
				CooldownApi.setPlayerCooldown(damagerP.getName(), "friendly.fire", 2);
				
				//Send message.
				LanguageAPI.sendTranslatedMessage(damagerP, Keys.same_race_team);
			}
		}
	}
	
}
