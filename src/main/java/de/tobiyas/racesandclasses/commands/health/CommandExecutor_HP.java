package de.tobiyas.racesandclasses.commands.health;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;

public class CommandExecutor_HP implements CommandExecutor {

	private RacesAndClasses plugin;
	
	public CommandExecutor_HP(){
		plugin = RacesAndClasses.getPlugin();
		try{
			plugin.getCommand("playerhealth").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /playerhealth.");
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "Only Players can use this command.");
			return true;
		}
		
		Player player = (Player) sender;
		if(!plugin.getPlayerManager().displayHealth(player.getName())){
			player.sendMessage(ChatColor.RED + "Something gone Wrong. No healthcontainer found for you.");
		}
		
		return true;
	}

}
