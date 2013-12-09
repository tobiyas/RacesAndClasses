package de.tobiyas.racesandclasses.listeners.classchangelistener;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.configuration.racetoclass.RaceNotFoundException;
import de.tobiyas.racesandclasses.cooldown.CooldownManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.classevent.AfterClassSelectedEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.classevent.PreClassSelectEvent;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;

/**
 * This class listens to {@link AfterClassSelectedEvent} + subclasses.
 * <br>It checks if the selecting player has the Race - selection - possibility to do so (if active).
 * <br>It also checks if the player has permission to this class (if active).
 * 
 * @author tobiyas
 */
public class ClassChangeSelectionListener implements Listener {
	
	
	/**
	 * The Main plugin
	 */
	private RacesAndClasses plugin;
	
	/**
	 * The command cooldown manager looking for cooldown
	 */
	private CooldownManager cooldownManager;
	
	
	/**
	 * Registers to Bukkit EventListener
	 */
	public ClassChangeSelectionListener(){	
		plugin = RacesAndClasses.getPlugin();
		cooldownManager = plugin.getCooldownManager();
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	
	@EventHandler(ignoreCancelled = true)
	public void checkRaceHasPermissionForClass(PreClassSelectEvent event){
		if(event.getClassToSelect() == plugin.getClassManager().getDefaultHolder()) return;
		
		if(plugin.getConfigManager().getGeneralConfig().isConfig_useRaceClassSelectionMatrix()){
			Player playerSelecting = event.getPlayer();
			String playerName = playerSelecting.getName();
			
			String className = event.getClassToSelect().getName();
			
			AbstractTraitHolder holder = plugin.getRaceManager().getHolderOfPlayer(playerName);
			String raceName = holder.getName();
			
			try{
				List<String> classList = plugin.getConfigManager().getRaceToClassConfig().getClassesValidForRace(raceName);
				if(classList != null && !classList.contains(className)){
					event.setCancelled("Your race can not select the class: " + className);
				}
			}catch(RaceNotFoundException exp){
				//If no class is found in list,
				//we assume that there is no limitation.
			}
			
		}
	}
	
		
	@EventHandler(ignoreCancelled = true)
	public void checkPlayerhasPermissionToClass(PreClassSelectEvent event){
		if(event.getClassToSelect() == plugin.getClassManager().getDefaultHolder()) return;
		if(!event.isCheckPermissions()) return;
		
		if(plugin.getConfigManager().getGeneralConfig().isConfig_usePermissionsForClasses()){
			Player player = event.getPlayer();
			String className = event.getClassToSelect().getName();
			
			String permissionNode = PermissionNode.classPermPre + className;
			if(!plugin.getPermissionManager().checkPermissionsSilent(player, permissionNode)){
				event.setCancelled("You do not have the Permission to select the Class " + className);
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void checkPlayerHasUplinkOnChange(PreClassSelectEvent event){
		if(!event.isCheckCooldown()) return;
		
		String playerName = event.getPlayer().getName();
		String commandName = "classchange";
		
		int remainingCooldown = cooldownManager.stillHasCooldown(playerName, commandName);
		if(remainingCooldown > 0){
			String message = ChatColor.RED + "You still have " + ChatColor.LIGHT_PURPLE + remainingCooldown 
					+ ChatColor.RED + " seconds cooldown on that command";
			event.setCancelled(message);
		}
	}
	
	@EventHandler
	public void givePlayerUplinkAfterSelect(AfterClassSelectedEvent event){
		if(!event.isGiveCooldown()) return;
		
		String playerName = event.getPlayer().getName();
		String commandName = "classchange";
		
		int time = plugin.getConfigManager().getGeneralConfig().getConfig_raceChangeCommandUplink();
		cooldownManager.setCooldown(playerName, commandName, time);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void rescanHP(AfterClassSelectedEvent selectEvent){
		if(selectEvent.getPlayer() == null) return;
		if(selectEvent.getPlayer().getName() == null) return;
		
		plugin.getPlayerManager().displayHealth(selectEvent.getPlayer().getName());
	}
}