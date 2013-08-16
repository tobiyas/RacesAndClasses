package de.tobiyas.racesandclasses.commands.health;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;

public class CommandExecutor_RaceGod implements CommandExecutor {
	
	private RacesAndClasses plugin;
	
	public CommandExecutor_RaceGod(){
		plugin = RacesAndClasses.getPlugin();
		try{
			plugin.getCommand("racegod").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /racegod.");
		}
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		if(!plugin.getPermissionManager().checkPermissions(sender, PermissionNode.god))
			return true;
		
		Player target = null;
		if(args.length == 1){
			target = Bukkit.getPlayer(args[0]);
		}
		
		if(args.length == 0){
			if(!(sender instanceof Player)){
				sender.sendMessage(ChatColor.RED + "Only players can use this on themselves!");
				return true;
			}
			
			target = (Player) sender;
		}
		
		if(args.length > 1){
			sender.sendMessage(ChatColor.RED + "Wrong usage. Use: /racegod [playername]");
			return true;
		}
		
		if(target == null){
			sender.sendMessage(ChatColor.RED + "Target not found.");
			return true;
		}
		
		if(plugin.getPlayerManager().switchGod(target.getName())){
			sender.sendMessage(ChatColor.GREEN + "Success.");
		}else{
			sender.sendMessage(ChatColor.RED + "failed.");
		}
			
		return true;
	}

}
