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
				//sender.sendMessage(ChatColor.RED + "Only players can use this on themselves!");
				return true;
			}
			
			target = (Player) sender;
		}
		
		if(args.length > 1){
			sender.sendMessage(LanguageAPI.translateIgnoreError(wrong_command_use)
					.replace("command", "/racegod [playername]")
					.build());
			return true;
		}
		
		if(target == null){
			sender.sendMessage(LanguageAPI.translateIgnoreError(player_not_exist)
					.replace("player", args[0])
					.build());
			return true;
		}
		
		if(plugin.getPlayerManager().switchGod(target.getName())){
			sender.sendMessage(LanguageAPI.translateIgnoreError(success)
					.build());
		}else{
			sender.sendMessage(LanguageAPI.translateIgnoreError(failed)
					.build());
		}
			
		return true;
	}

}
