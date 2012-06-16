package de.tobiyas.races.commands.chat;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.races.Races;
import de.tobiyas.races.chat.LocalChat;

public class CommandExecutor_LocalChat implements CommandExecutor{

	private Races plugin;
	
	public CommandExecutor_LocalChat(){
		plugin = Races.getPlugin();
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
			player.sendMessage(ChatColor.RED + "No message givven.");
			return true;
		}
		
		String message = "";
		for(String arg : args)
			message += arg + " ";
		
		int chatRange = plugin.interactConfig().getConfig_localchat_range();
		LocalChat.sendMessageLocaly(player, message, chatRange);
		return true;
	}

}
