/*
 * Races - by tobiyas
 * http://
 *
 * powered by Kickstarter
 */

package de.tobiyas.racesandclasses.commands.chat.channels;


import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;


public class CommandExecutor_Racechat implements CommandExecutor {
	private RacesAndClasses plugin;

	public CommandExecutor_Racechat(){
		plugin = RacesAndClasses.getPlugin();

		String command = "racechat";
		if(plugin.getConfigManager().getGeneralConfig().getConfig_general_disable_commands().contains(command)) return;
		
		try{
			plugin.getCommand(command).setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /" + command + ".");
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,String label, String[] args) {
		if (!(sender instanceof Player)) {
			LanguageAPI.sendTranslatedMessage(sender,"only_players");
			return true;
		}
		
		if(!plugin.getConfigManager().getGeneralConfig().isConfig_enableRaces()){
			LanguageAPI.sendTranslatedMessage(sender,"something_disabled",
					"value", "Races");
			return true;
		}
		
		if(!plugin.getConfigManager().getGeneralConfig().isConfig_channels_enable()){
			LanguageAPI.sendTranslatedMessage(sender,"something_disabled",
					"value", "RaceChat");
			return true;
		}
		
		if(args.length == 0){
			LanguageAPI.sendTranslatedMessage(sender,"send_empty_message");
			return true;
		}
		
		Player player = (Player) sender;		
		AbstractTraitHolder container = plugin.getRaceManager().getHolderOfPlayer(player.getName());
		AbstractTraitHolder stdContainer = plugin.getRaceManager().getDefaultHolder();
		if(container == null || container == stdContainer){
			LanguageAPI.sendTranslatedMessage(sender,"no_race_selected");
			return true;
		}
		
		String message = "";
		for(String snippet : args){
			message += snippet + " ";
		}

		plugin.getChannelManager().broadcastMessageToChannel(container.getName(), player, message);
		return true;
	}
}
