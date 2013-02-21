package de.tobiyas.races.datacontainer.traitholdercontainer.race.reminder;

import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.tobiyas.races.Races;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceManager;

public class RaceReminder implements Runnable {

	private Races plugin;
	
	public RaceReminder(){
		plugin = Races.getPlugin();
		int reminderTime = plugin.getGeneralConfig().getConfig_reminder_interval() * 20 * 60;
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, reminderTime, reminderTime);
	}
	
	@Override
	public void run() {
		if(plugin.getGeneralConfig().getConfig_activate_reminder()){
			RaceContainer defaultContainer = RaceManager.getManager().getDefaultContainer();
			LinkedList<String> list = RaceManager.getManager().getAllPlayersOfRace(defaultContainer);
			for(String name : list){
				Player player = Bukkit.getPlayer(name);
				if(player != null)
					postSelectRace(player);
			}
		}
	}
	
	private void postSelectRace(Player player){
		player.sendMessage(ChatColor.YELLOW + "[PRIVATE-INFO]: You have not selected a race.");
		if(plugin.getGeneralConfig().getConfig_tutorials_enable()){
			player.sendMessage(ChatColor.YELLOW + "[PRIVATE-INFO]: If you want to use the Tutorial, use " + ChatColor.LIGHT_PURPLE + "/racestutorial start");
		}else{
			player.sendMessage(ChatColor.YELLOW + "[PRIVATE-INFO]: Use " + ChatColor.RED + "/race select <racename> " + ChatColor.YELLOW + "to select a race.");
			player.sendMessage(ChatColor.YELLOW + "[PRIVATE-INFO]: To see all races use: " + ChatColor.LIGHT_PURPLE + "/race list");
		}
	}

}
