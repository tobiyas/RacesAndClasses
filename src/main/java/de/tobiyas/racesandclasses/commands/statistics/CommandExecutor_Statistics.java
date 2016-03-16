/*******************************************************************************
 * Copyright 2014 Tobias Welther
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.tobiyas.racesandclasses.commands.statistics;

import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.StatisticAPI;
import de.tobiyas.racesandclasses.commands.AbstractCommand;
import de.tobiyas.racesandclasses.statistics.StartupStatisticCategory;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;

public class CommandExecutor_Statistics extends AbstractCommand {

	private RacesAndClasses plugin;

	public CommandExecutor_Statistics(){
		super("racstatistics", new String[]{"racs"});
		plugin = RacesAndClasses.getPlugin();

//		String command = "racstatistics";
//		if(plugin.getConfigManager().getGeneralConfig().getConfig_general_disable_commands().contains(command)) return;
//		
//		try{
//			plugin.getCommand(command).setExecutor(this);
//		}catch(Exception e){
//			plugin.log("ERROR: Could not register command /" + command + ".");
//		}
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
		
		if(traitName.equalsIgnoreCase("loadedplayers")){
			showLoadedPlayers(sender);
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


	private void showLoadedPlayers(CommandSender sender) {
		sender.sendMessage(ChatColor.YELLOW + "Loaded Players: " + plugin.getPlayerManager().getPlayerNumber());
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
