package de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.reminder;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;

public class RaceReminder implements Runnable {

	private RacesAndClasses plugin;
	
	public RaceReminder(){
		plugin = RacesAndClasses.getPlugin();
		int reminderTime = plugin.getConfigManager().getGeneralConfig().getConfig_reminder_interval() * 20 * 60;
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, reminderTime, reminderTime);
	}
	
	@Override
	public void run() {
		if(plugin.getConfigManager().getGeneralConfig().isConfig_activate_reminder()){
			AbstractTraitHolder defaultContainer = plugin.getRaceManager().getDefaultHolder();
			List<String> list = plugin.getRaceManager().getAllPlayersOfHolder(defaultContainer);
			for(String name : list){
				Player player = Bukkit.getPlayer(name);

				if(player == null){
					continue;
				}
				
				postSelectRace(player);
				
			}
		}
	}
	
	/**
	 * Checks if the Player has any permission for any Race.
	 * This is only checked, when the Configuration option for checking is set.
	 * 
	 * @param player
	 * @return
	 */
	private boolean hasAnyRacePermission(Player player) {
		if(!plugin.getConfigManager().getGeneralConfig().isConfig_usePermissionsForRaces()) return true;
		
		if(plugin.getPermissionManager().checkPermissionsSilent(player, PermissionNode.racePermPre + "*")) return true;
		
		for(String raceName : plugin.getRaceManager().listAllVisibleHolders()){
			if(plugin.getPermissionManager().checkPermissionsSilent(player, PermissionNode.racePermPre + raceName)){
				return true;
			}
		}
		
		return false;
	}
	

	/**
	 * Tries posting the reminder to the {@link Player}.
	 * It also checks the Permissions if set in Configuration.
	 * 
	 * @param player
	 */
	private void postSelectRace(Player player){
		if(!hasAnyRacePermission(player)) return;
		
		player.sendMessage(ChatColor.YELLOW + "[PRIVATE-INFO]: You have not selected a race.");
		if(plugin.getConfigManager().getGeneralConfig().isConfig_tutorials_enable()){
			player.sendMessage(ChatColor.YELLOW + "[PRIVATE-INFO]: If you want to use the Tutorial, use " + ChatColor.LIGHT_PURPLE + "/racestutorial start");
		}else{
			player.sendMessage(ChatColor.YELLOW + "[PRIVATE-INFO]: Use " + ChatColor.RED + "/race select <racename> " + ChatColor.YELLOW + "to select a race.");
			player.sendMessage(ChatColor.YELLOW + "[PRIVATE-INFO]: To see all races use: " + ChatColor.LIGHT_PURPLE + "/race list");
		}
	}

}
