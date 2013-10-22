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
		try{
			plugin.getCommand("racechat").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /racechat.");
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(LanguageAPI.translateIgnoreError("only_players").build());
			return true;
		}
		
		if(!plugin.getConfigManager().getGeneralConfig().isConfig_channels_enable()){
			sender.sendMessage(LanguageAPI.translateIgnoreError("something_disabled")
					.replace("value", "RaceChat").build());
			return true;
		}
		
		if(args.length == 0){
			sender.sendMessage(LanguageAPI.translateIgnoreError("send_empty_message")
					.build());
			return true;
		}
		
		Player player = (Player) sender;		
		AbstractTraitHolder container = plugin.getRaceManager().getHolderOfPlayer(player.getName());
		AbstractTraitHolder stdContainer = plugin.getRaceManager().getDefaultHolder();
		if(container == null || container == stdContainer){
			player.sendMessage(LanguageAPI.translateIgnoreError("no_race_selected")
					.build());
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
