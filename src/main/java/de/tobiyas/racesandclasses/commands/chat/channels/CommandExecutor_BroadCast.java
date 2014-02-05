package de.tobiyas.racesandclasses.commands.chat.channels;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;

public class CommandExecutor_BroadCast implements CommandExecutor{

	private RacesAndClasses plugin;

	public CommandExecutor_BroadCast(){
		plugin = RacesAndClasses.getPlugin();

		String command = "globalbroadcast";
		if(plugin.getConfigManager().getGeneralConfig().getConfig_general_disable_commands().contains(command)) return;
		
		try{
			plugin.getCommand(command).setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /" + command + ".");
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if(!plugin.getPermissionManager().checkPermissions(sender, PermissionNode.broadcast)) return true;
		if(!plugin.getConfigManager().getGeneralConfig().isConfig_channels_enable()){
			LanguageAPI.sendTranslatedMessage(sender, "something_disabled",
					"value", "Channels");
			return true;
		}
		
		if(args.length == 0){
			LanguageAPI.sendTranslatedMessage(sender, "no_message");
			return true;
		}
		
		String message = "";
		for(String arg : args){
			message += arg + " ";
		}
		
		plugin.getChannelManager().broadcastMessageToChannel("Global", sender, message);
		return true;
	}

	
}
