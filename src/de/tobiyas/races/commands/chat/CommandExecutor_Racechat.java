/*
 * Races - by tobiyas
 * http://
 *
 * powered by Kickstarter
 */

package de.tobiyas.races.commands.chat;


import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.races.Races;
import de.tobiyas.races.chat.channels.ChannelManager;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceManager;
import de.tobiyas.races.util.consts.PermissionNode;


public class CommandExecutor_Racechat implements CommandExecutor {
	private Races plugin;

	public CommandExecutor_Racechat(){
		plugin = Races.getPlugin();
		try{
			plugin.getCommand("racechat").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /racechat.");
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only players can use this command.");
			return true;
		}
		
		if(!plugin.interactConfig().getconfig_racechat_enable()){
			sender.sendMessage(ChatColor.RED + "RaceChat is not active.");
			return true;
		}
		
		Player player = (Player) sender;
		
		String[] permChecks = {PermissionNode.raceChatBasic, PermissionNode.raceChatWrite};
		if(!plugin.getPermissionManager().hasAnyPermissions(sender, permChecks)) return true;
		
		RaceContainer container = RaceManager.getManager().getRaceOfPlayer(player.getName());
		if(container == null){
			player.sendMessage(ChatColor.RED + "You have no race selected.");
			return true;
		}
		
		String message = "";
		for(String snippet : args){
			message += snippet + " ";
		}

		ChannelManager.GetInstance().broadcastMessageToChannel(container.getName(), player, message);
		return true;
	}
}
