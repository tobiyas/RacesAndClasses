package de.tobiyas.racesandclasses.commands.chat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;

public class CommandExecutor_LocalChat implements CommandExecutor{

	private RacesAndClasses plugin;
	
	public CommandExecutor_LocalChat(){
		plugin = RacesAndClasses.getPlugin();

		String command = "localchat";
		if(plugin.getConfigManager().getGeneralConfig().getConfig_general_disable_commands().contains(command)) return;
		
		try{
			plugin.getCommand(command).setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /" + command + ".");
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		if(!(sender instanceof Player)){
			LanguageAPI.sendTranslatedMessage(sender,"only_players");
			return true;
		}
		
		Player player = (Player) sender;		
		if(args.length == 0){
			LanguageAPI.sendTranslatedMessage(sender,"send_empty_message");
			return true;
		}
		
		String message = "";
		for(String arg : args){
			message += arg + " ";
		}
		
		plugin.getChannelManager().broadcastMessageToChannel("Local", player, message);
		return true;
	}

}
