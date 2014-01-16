package de.tobiyas.racesandclasses.commands.quickslot;

import static de.tobiyas.racesandclasses.translation.languages.Keys.only_players;
import static de.tobiyas.racesandclasses.translation.languages.Keys.open_holder;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;

public class CommandExecutor_QuickSlot implements CommandExecutor {

	private RacesAndClasses plugin;

	public CommandExecutor_QuickSlot(){
		plugin = RacesAndClasses.getPlugin();
		try{
			plugin.getCommand("racquickslot").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /racquickslot.");
		}
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		
		if(!plugin.getPermissionManager().checkPermissions(sender, PermissionNode.statistics)){
			return true;
		}
		
		if(!(sender instanceof Player)){
			LanguageAPI.sendTranslatedMessage(sender, only_players);
			return true;
		}
		
		LanguageAPI.sendTranslatedMessage(sender, open_holder);
		
		
		return true;
	}

}
