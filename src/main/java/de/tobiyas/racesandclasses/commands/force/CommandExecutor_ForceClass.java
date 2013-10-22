package de.tobiyas.racesandclasses.commands.force;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
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
			sender.sendMessage(LanguageAPI.translateIgnoreError("wrong_command_use")
					.replace("command", "/racforceclass <player> <class name>").build());
			return true;
		}
		
		String playerToChange = args[0];
		String newClass = args[1];
		
		if(Bukkit.getPlayer(playerToChange) == null){
			sender.sendMessage(LanguageAPI.translateIgnoreError("player_not_exist")
					.replace("player", playerToChange).build());
			return true;
		}
		
		ClassManager classManager = plugin.getClassManager();
		if(classManager.getHolderByName(newClass) == null){
			sender.sendMessage(LanguageAPI.translateIgnoreError("class_not_exist")
					.replace("class", newClass).build());
			return true;
		}
		
		if(classManager.getHolderOfPlayer(playerToChange) == classManager.getDefaultHolder()){
			classManager.addPlayerToHolder(playerToChange, newClass, true);
		}else{
			classManager.changePlayerHolder(playerToChange, newClass, true);
		}
		
		Player player = Bukkit.getPlayer(playerToChange);
		if(player.isOnline()){
			player.sendMessage(LanguageAPI.translateIgnoreError("class_changed_to")
					.replace("class", newClass).build());
		}
		
		sender.sendMessage(LanguageAPI.translateIgnoreError("class_changed_to_other")
				.replace("player", player.getName()).replace("class", newClass).build());
		
		return true;
	}

}
