package de.tobiyas.racesandclasses.commands.health;

import static de.tobiyas.racesandclasses.translation.languages.Keys.failed;
import static de.tobiyas.racesandclasses.translation.languages.Keys.only_players;
import static de.tobiyas.racesandclasses.translation.languages.Keys.player_not_exist;
import static de.tobiyas.racesandclasses.translation.languages.Keys.success;
import static de.tobiyas.racesandclasses.translation.languages.Keys.wrong_command_use;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
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
				LanguageAPI.sendTranslatedMessage(sender, only_players, new HashMap<String, String>());
				return true;
			}
			
			target = (Player) sender;
		}
		
		if(args.length > 1){
			LanguageAPI.sendTranslatedMessage(sender, wrong_command_use,
					"command", "/racegod [playername]");
			return true;
		}
		
		if(target == null){
			LanguageAPI.sendTranslatedMessage(sender, player_not_exist,
					"player", args[0]);
			return true;
		}
		
		if(plugin.getPlayerManager().switchGod(target.getName())){
			LanguageAPI.sendTranslatedMessage(sender, success);
		}else{
			LanguageAPI.sendTranslatedMessage(sender, failed);
		}
			
		return true;
	}

}
