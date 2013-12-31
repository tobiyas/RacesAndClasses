package de.tobiyas.racesandclasses.commands.debug;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.tobiyas.racesandclasses.RacesAndClasses;

public class CommandExecutor_ResetPlayer implements CommandExecutor {

	
	private RacesAndClasses plugin;
	
	public CommandExecutor_ResetPlayer(){
		plugin = RacesAndClasses.getPlugin();
		try{
			plugin.getCommand("resetplayer").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /resetplayer.");
		}
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		
		sender.sendMessage(ChatColor.RED + "Nothing to see here yet.");
		return true;
	}

}
