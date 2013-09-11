package de.tobiyas.racesandclasses.commands.debug;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.racbuilder.gui.base.BaseSelectionInventory;
import de.tobiyas.racesandclasses.racbuilder.gui.stats.StringSelectionInterface;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;

public class CommandExecutor_Edit implements CommandExecutor {
	
	private RacesAndClasses plugin;
	
	
	public CommandExecutor_Edit() {
		plugin = RacesAndClasses.getPlugin();
		try{
			plugin.getCommand("racedit").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /racedit.");
		}
	}

	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "[RaC] Needing a player to open an Inventory. Sorry.");
			return true;
		}
		
		Player player = (Player) sender;
		if(!plugin.getPermissionManager().checkPermissions(player, PermissionNode.racEdit)) return true;

		
		if(args.length > 0 && args[0].equalsIgnoreCase("test")){
			player.openInventory(new StringSelectionInterface(player, null, new HashMap<String, Object>(), "null"));
			return true;
		}
		
		
		
		player.sendMessage(ChatColor.GREEN + "Opening RaC Editor...");
		player.openInventory(new BaseSelectionInventory(player));
		
		return true;
	}

}
