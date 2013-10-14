package de.tobiyas.racesandclasses.commands.force;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassManager;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;

public class CommandExecutor_ForceClass implements CommandExecutor {

	/**
	 * The plugin called stuff upon
	 */
	private RacesAndClasses plugin;
	
	
	public CommandExecutor_ForceClass() {
		plugin = RacesAndClasses.getPlugin();
		try{
			plugin.getCommand("racforceclass").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /racforceclass.");
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {

		
		if(!plugin.getPermissionManager().checkPermissions(sender, PermissionNode.forceChange)) return true;
		
		if(args.length < 2){
			sender.sendMessage(ChatColor.RED + "[RaC] Wrong usage. Use: " + ChatColor.LIGHT_PURPLE + "/racforceclass <player> <class name>");
			return true;
		}
		
		String playerToChange = args[0];
		String newClass = args[1];
		
		if(Bukkit.getPlayer(playerToChange) == null){
			sender.sendMessage(ChatColor.RED + "[RaC] Player: " + ChatColor.LIGHT_PURPLE + playerToChange + ChatColor.RED + " does not exist.");
			return true;
		}
		
		ClassManager classManager = plugin.getClassManager();
		if(classManager.getHolderByName(newClass) == null){
			sender.sendMessage(ChatColor.RED + "[RaC] Class: " + ChatColor.LIGHT_PURPLE + newClass + ChatColor.RED + " does not exist.");
			return true;
		}
		
		if(classManager.getHolderOfPlayer(playerToChange) == classManager.getDefaultHolder()){
			classManager.addPlayerToHolder(playerToChange, newClass, true);
		}else{
			classManager.changePlayerHolder(playerToChange, newClass, true);
		}
		
		Player player = Bukkit.getPlayer(playerToChange);
		if(player.isOnline()){
			player.sendMessage(ChatColor.GREEN + "[RaC] Your Class has been changed to: " + ChatColor.LIGHT_PURPLE + newClass + ChatColor.GREEN + ".");
		}
		
		sender.sendMessage(ChatColor.GREEN + "[RaC] Class of " + ChatColor.LIGHT_PURPLE + playerToChange 
				+ ChatColor.GREEN + " changed to: " + ChatColor.LIGHT_PURPLE + newClass + ChatColor.GREEN + ".");
		
		return true;
	}

}
