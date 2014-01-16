package de.tobiyas.racesandclasses.commands.debug;

import static de.tobiyas.racesandclasses.translation.languages.Keys.only_players;
import static de.tobiyas.racesandclasses.translation.languages.Keys.open_holder;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.racbuilder.gui.base.BaseSelectionInventory;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;
import de.tobiyas.util.inventorymenu.stats.StringSelectionInterface;

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
			LanguageAPI.sendTranslatedMessage(sender, only_players);
			return true;
		}
		
		Player player = (Player) sender;
		if(!plugin.getPermissionManager().checkPermissions(player, PermissionNode.racEdit)) return true;

		
		if(args.length > 0 && args[0].equalsIgnoreCase("test")){
			player.openInventory(new StringSelectionInterface(player, null, new HashMap<String, Object>(), "null", plugin));
			return true;
		}
		
		
		LanguageAPI.sendTranslatedMessage(sender, open_holder);
		player.openInventory(new BaseSelectionInventory(player, plugin));
		
		return true;
	}

}
