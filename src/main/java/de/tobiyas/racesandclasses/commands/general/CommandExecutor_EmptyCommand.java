package de.tobiyas.racesandclasses.commands.general;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.tobiyas.racesandclasses.RacesAndClasses;

public class CommandExecutor_EmptyCommand implements CommandExecutor {

	private RacesAndClasses plugin;
	
	
	public CommandExecutor_EmptyCommand(String commandName) {
		plugin = RacesAndClasses.getPlugin();
		try{
			plugin.getCommand(commandName).setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command " + commandName + ".");
		}

	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {

		sender.sendMessage(ChatColor.RED + "The Plugin: '" + ChatColor.AQUA + "RacesAndClasses" 
				+ ChatColor.RED + "' had errors on Startup! All commands are disabled.");

		return true;
	}

}
