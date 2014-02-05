package de.tobiyas.racesandclasses.commands.statistics;

import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.StatisticAPI;
import de.tobiyas.racesandclasses.persistence.file.YAMLPersistenceProvider;
import de.tobiyas.racesandclasses.statistics.StartupStatisticCategory;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;

public class CommandExecutor_Statistics implements CommandExecutor {

	private RacesAndClasses plugin;

	public CommandExecutor_Statistics(){
		plugin = RacesAndClasses.getPlugin();

		String command = "racstatistics";
		if(plugin.getConfigManager().getGeneralConfig().getConfig_general_disable_commands().contains(command)) return;
		
		try{
			plugin.getCommand(command).setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /" + command + ".");
		}
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		
		if(!plugin.getPermissionManager().checkPermissions(sender, PermissionNode.statistics)){
			return true;
		}
		
		if(args.length > 0){
			String traitName = "";
			for(String arg : args){
				traitName += arg + " ";
			}
			
			traitName = traitName.trim();
			
			showStatisticToTrait(sender, traitName);
		}else{
			sendHelp(sender);
		}
		
		return true;
	}


	private void showStatisticToTrait(CommandSender sender, String traitName) {
		if(traitName.equalsIgnoreCase("total")){
			showOverallStatistics(sender);
			return;
		}
		
		if(traitName.equalsIgnoreCase("startup")){
			showStartupStatistics(sender);
			return;
		}

		if(traitName.equalsIgnoreCase("ymlcache")){
			showYMLCacheStatistics(sender);
			return;
		}
		
		sender.sendMessage(ChatColor.YELLOW + "==== " + ChatColor.AQUA + "Statistics to: " + ChatColor.LIGHT_PURPLE 
				+ traitName + ChatColor.YELLOW + " ====");
		
		long totalEvents = StatisticAPI.getTotalTriggersOfTrait(traitName);
		if(totalEvents < 0){
			sender.sendMessage(ChatColor.RED + "The Trait: " + ChatColor.LIGHT_PURPLE + traitName + ChatColor.RED + " does not exist.");
			return;
		}
		
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "Total events on Trait " + ChatColor.DARK_PURPLE + traitName 
				+ ChatColor.LIGHT_PURPLE + ": " + ChatColor.AQUA + totalEvents);
		
		double eventsPerMin = StatisticAPI.getTotalTriggersOfTraitPerMinute(traitName);
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "Events / Minute on Trait " + ChatColor.DARK_PURPLE + traitName 
				+ ChatColor.LIGHT_PURPLE + ": " + ChatColor.AQUA + eventsPerMin);
		
		long timeUsedForTrait = StatisticAPI.getTraitsUsedTime(traitName);
		String formated = String.format("%d min, %d sec", 
			    TimeUnit.MILLISECONDS.toMinutes(timeUsedForTrait),
			    TimeUnit.MILLISECONDS.toSeconds(timeUsedForTrait) - 
			    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeUsedForTrait))
			);
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "Time used for Trait: " + ChatColor.AQUA + formated);
	}


	private void showYMLCacheStatistics(CommandSender sender) {
		sender.sendMessage(ChatColor.YELLOW + "===== YML Cache =====");
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "total Cache accesses: " 
				+ ChatColor.LIGHT_PURPLE + YAMLPersistenceProvider.getTotalTries() + "x");
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "Cache Hit-Rate: " 
				+ ChatColor.LIGHT_PURPLE + (100*YAMLPersistenceProvider.getCacheHitRate()) + "%");
		
	}


	private void showStartupStatistics(CommandSender sender) {
		sender.sendMessage(ChatColor.YELLOW + "===== STARTUP =====");
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "Timings are in: " + ChatColor.LIGHT_PURPLE + "Millisecond");
		for(StartupStatisticCategory category : StartupStatisticCategory.values()){
			sender.sendMessage(ChatColor.GREEN + category.name() + ": " + ChatColor.LIGHT_PURPLE + category.timeInMiliSeconds);
		}
	}


	private void showOverallStatistics(CommandSender sender) {
		sender.sendMessage(ChatColor.YELLOW + "==== " + ChatColor.AQUA + "Overall Statistics " + ChatColor.YELLOW + " ====");
		String timeRunning = StatisticAPI.getTimeRunning();
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "Time running: " + ChatColor.AQUA  + timeRunning);
		
		long totalEvents = StatisticAPI.getEventsTriggeredTotal();
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "Total events: " + ChatColor.AQUA + totalEvents);
		
		double eventsPerMin = StatisticAPI.getEventsTotalPerMinute();
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "Events / Minute: " + ChatColor.AQUA + eventsPerMin);
		
		long totalTimeUsed = StatisticAPI.getTotalTraitsUsedTime();
		String formated = String.format("%d min, %d sec", 
			    TimeUnit.MILLISECONDS.toMinutes(totalTimeUsed),
			    TimeUnit.MILLISECONDS.toSeconds(totalTimeUsed) - 
			    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totalTimeUsed))
			);
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "Time used in Total: " + ChatColor.AQUA + formated);
		
	}


	private void sendHelp(CommandSender sender) {
		sender.sendMessage(ChatColor.YELLOW + "[RaC]" + ChatColor.RED + " Use this command as following: " 
				+ ChatColor.LIGHT_PURPLE + "/racstatistics <traitname>");
		sender.sendMessage(ChatColor.YELLOW + "[RAC]" + ChatColor.RED + " Use " + ChatColor.LIGHT_PURPLE + "/racestatistics total" 
				+ ChatColor.RED + " to see the total statistics.");
		sender.sendMessage(ChatColor.YELLOW + "[RAC]" + ChatColor.RED + " Use " + ChatColor.LIGHT_PURPLE + "/racestatistics startup" 
				+ ChatColor.RED + " to see startup statistics.");
	}

}
