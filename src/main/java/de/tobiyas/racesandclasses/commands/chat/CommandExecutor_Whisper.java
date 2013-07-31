package de.tobiyas.racesandclasses.commands.chat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.chat.PrivateChat;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;

public class CommandExecutor_Whisper implements CommandExecutor {
	
	private RacesAndClasses plugin;
	
	public CommandExecutor_Whisper(){
		plugin = RacesAndClasses.getPlugin();
		try{
			plugin.getCommand("whisper").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /whisper.");
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage("You must be a player to use this command.");
			return true;
		}
		
		Player player = (Player) sender;
		if(!plugin.getPermissionManager().checkPermissions(sender, PermissionNode.whisper)) return true;
		
		if(args.length == 0){
			player.sendMessage(ChatColor.RED + "You have to specify a player and a message.");
			return true;
		}
		

		Player target = Bukkit.getPlayer(args[0]);
		if(target == null){
			player.sendMessage(ChatColor.RED + "Target does not exist or is not online.");
			return true;
		}
		
		if(args.length == 1){
			return true;
		}
		
		if(player == target){
			player.sendMessage(ChatColor.RED + "You can't whisper yourself.");
			return true;
		}
		
		String message = "";
		for(int i = 1; i < args.length; i++)
			message += args[i] + " ";
		
		PrivateChat.sendMessageToPlayer(player, target, message);
		
		return true;
	}

}
