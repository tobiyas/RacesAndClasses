package de.tobiyas.races.commands.general;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.tobiyas.races.Races;
import de.tobiyas.races.util.consts.PermissionNode;

public class CommandExecutor_RacesReload implements CommandExecutor {
	
	private Races plugin;
	
	public CommandExecutor_RacesReload(){
		plugin = Races.getPlugin();
		try{
			plugin.getCommand("racesreload").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /racesreload.");
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		if(!plugin.getPermissionManager().checkPermissions(sender, PermissionNode.reload)) return true;
		
		long timeTaken = 0;
		if(args.length == 1){
			timeTaken = plugin.fullReload(true, args[0].equalsIgnoreCase("gc"));
		}else
			timeTaken = plugin.fullReload(true, false);
		
		plugin.getDebugLogger().log("Reload called by: " + sender.getName() + " took: " + timeTaken + " ms.");
		sender.sendMessage(ChatColor.GREEN + "Reload of " + ChatColor.LIGHT_PURPLE + "Races" + ChatColor.GREEN + 
							" done successfully. Time taken: " + ChatColor.LIGHT_PURPLE + timeTaken + ChatColor.GREEN + " ms");
		return true;
	}

}
