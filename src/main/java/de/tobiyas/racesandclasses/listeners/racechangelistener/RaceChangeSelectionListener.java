package de.tobiyas.racesandclasses.listeners.racechangelistener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.cooldown.CooldownManager;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.raceevent.AfterRaceSelectedEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.raceevent.PreRaceSelectEvent;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;

/**
 * This class listens to {@link AfterRaceSelectedEvent} + subclasses.
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
	
	
	@EventHandler(ignoreCancelled = true)
	public void checkPlayerhasPermissionToRace(PreRaceSelectEvent event){
		if(event.getRaceToSelect() == plugin.getRaceManager().getDefaultHolder()) return;
		
		if(plugin.getConfigManager().getGeneralConfig().isConfig_usePermissionsForRaces()){
			Player player = event.getPlayer();
			String raceName = event.getRaceToSelect().getName();
			
			String permissionNode = PermissionNode.racePermPre + raceName;
			if(!plugin.getPermissionManager().checkPermissionsSilent(player, permissionNode)){
				event.setCancelled("You do not have the Permission to select the Race" + raceName);
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void checkPlayerHasUplinkOnChange(PreRaceSelectEvent event){
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
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void rescanHP(AfterRaceSelectedEvent selectEvent){
		if(selectEvent.getPlayer() == null) return;
		if(selectEvent.getPlayer().getName() == null) return;
		
		String playerName = selectEvent.getPlayer().getName();
		
		plugin.getPlayerManager().checkPlayer(playerName);
		plugin.getPlayerManager().displayHealth(playerName);
	}
}