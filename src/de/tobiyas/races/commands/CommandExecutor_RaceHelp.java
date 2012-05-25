package de.tobiyas.races.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.tobiyas.races.Races;

public class CommandExecutor_RaceHelp implements CommandExecutor {
	
	private Races plugin;
	
	public CommandExecutor_RaceHelp(){
		plugin = Races.getPlugin();
		try{
			plugin.getCommand("racehelp").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /racehelp.");
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		
		sender.sendMessage(ChatColor.RED + "Not implemented yet.");
		
		return true;
	}

}
