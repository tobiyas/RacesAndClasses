package de.tobiyas.racesandclasses.listeners.racechangelistener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.cooldown.CooldownManager;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.classevent.ClassSelectEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.raceevent.RaceSelectEvent;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;

/**
 * This class listens to {@link RaceSelectEvent} + subclasses.
 * <br>It checks if the player has permission to this race (if active).
 * 
 * @author tobiyas
 */
public class RaceChangeSelectionListener implements Listener {
	
	
	/**
	 * The Main plugin
	 */
	private RacesAndClasses plugin;
	
	
	/**
	 * The CooldownManagerForCommands
	 */
	private CooldownManager cooldownManager;
	
	/**
	 * Registers to Bukkit EventListener
	 */
	public RaceChangeSelectionListener(){	
		plugin = RacesAndClasses.getPlugin();
		cooldownManager = plugin.getCooldownManager();
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	
	@EventHandler
	public void checkPlayerhasPermissionToRace(RaceSelectEvent event){
		if(event.getRaceToSelect() == plugin.getRaceManager().getDefaultHolder()) return;
		
		if(plugin.getConfigManager().getGeneralConfig().isConfig_usePermissionsForRaces()){
			Player player = event.getPlayer();
			String raceName = event.getRaceToSelect().getName();
			
			String permissionNode = PermissionNode.prePlugin + "races." + raceName;
			if(!plugin.getPermissionManager().checkPermissionsSilent(player, permissionNode)){
				event.setCancelled("You do not have the Permission to select the Race" + raceName);
			}
		}
	}
	
	@EventHandler
	public void checkPlayerHasUplinkOnChange(ClassSelectEvent event){
		String playerName = event.getPlayer().getName();
		String commandName = "racechange";
		
		int remainingCooldown = cooldownManager.stillHasCooldown(playerName, commandName);
		if(remainingCooldown > 0){
			String message = ChatColor.RED + "You still have " + ChatColor.LIGHT_PURPLE + remainingCooldown 
					+ ChatColor.RED + " seconds cooldown on that command";
			event.setCancelled(message);
		}else{
			int time = plugin.getConfigManager().getGeneralConfig().getConfig_classChangeCommandUplink();
			cooldownManager.setCooldown(playerName, commandName, time);
		}
	}
}