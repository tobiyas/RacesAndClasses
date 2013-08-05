package de.tobiyas.racesandclasses.listeners.classchangelistener;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.configuration.racetoclass.RaceNotFoundException;
import de.tobiyas.racesandclasses.cooldown.CooldownManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.classevent.ClassSelectEvent;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;

/**
 * This class listens to {@link ClassSelectEvent} + subclasses.
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
	
	
	@EventHandler
	public void checkRaceHasPermissionForClass(ClassSelectEvent event){
		if(plugin.getConfigManager().getGeneralConfig().isConfig_useRaceClassSelectionMatrix()){
			Player playerSelecting = event.getPlayer();
			String playerName = playerSelecting.getName();
			
			String className = event.getClassToSelect().getName();
			
			AbstractTraitHolder holder = plugin.getRaceManager().getHolderOfPlayer(playerName);
			String raceName = holder.getName();
			
			try{
				List<String> classList = plugin.getConfigManager().getRaceToClassConfig().getClassesValidForRace(raceName);
				if(!classList.contains(className)){
					event.setCancelled("Your race can not select the class: " + className);
				}
			}catch(RaceNotFoundException exp){
				//If no class is found in list,
				//we assume that there is no limitation.
			}
			
		}
	}
	
		
	@EventHandler
	public void checkPlayerhasPermissionToClass(ClassSelectEvent event){
		if(plugin.getConfigManager().getGeneralConfig().isConfig_usePermissionsForClasses()){
			Player player = event.getPlayer();
			String className = event.getClassToSelect().getName();
			
			String permissionNode = PermissionNode.prePlugin + "classes." + className.toLowerCase();
			if(!plugin.getPermissionManager().checkPermissionsSilent(player, permissionNode)){
				event.setCancelled("You do not have the Permission to select the Class " + className);
			}
		}
	}
	
	@EventHandler
	public void checkPlayerHasUplinkOnChange(ClassSelectEvent event){
		String playerName = event.getPlayer().getName();
		String commandName = "classchange";
		
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