package de.tobiyas.racesandclasses.commands.chat;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.chat.channels.ChannelManager;

public class CommandExecutor_LocalChat implements CommandExecutor{

	private RacesAndClasses plugin;
	
	public CommandExecutor_LocalChat(){
		plugin = RacesAndClasses.getPlugin();
		try{
			plugin.getCommand("localchat").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /localchat.");
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
		if(args.length == 0){
			player.sendMessage(ChatColor.RED + "No message given.");
			return true;
		}
		
		String message = "";
		for(String arg : args)
			message += arg + " ";
		
		ChannelManager.GetInstance().broadcastMessageToChannel("Local", player, message);
		return true;
	}

}
