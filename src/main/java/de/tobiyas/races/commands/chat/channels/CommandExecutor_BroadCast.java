package de.tobiyas.races.commands.chat.channels;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import de.tobiyas.races.Races;
import de.tobiyas.races.chat.channels.ChannelManager;
import de.tobiyas.races.util.consts.PermissionNode;

public class CommandExecutor_BroadCast implements CommandExecutor{

	private Races plugin;

	public CommandExecutor_BroadCast(){
		plugin = Races.getPlugin();
		try{
			plugin.getCommand("globalbroadcast").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /globalbroadcast.");
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if(!plugin.getPermissionManager().checkPermissions(sender, PermissionNode.broadcast)) return true;
		if(!plugin.getGeneralConfig().getConfig_channels_enable()){
			sender.sendMessage(ChatColor.RED + "Channels are disabled.");
			return true;
		}
		if(args.length == 0){
			sender.sendMessage(ChatColor.RED + "No message given.");
			return true;
		}
		
		String message = "";
		for(String arg : args){
			message += arg + " ";
		}
		
		ChannelManager.GetInstance().broadcastMessageToChannel("Global", null, message);
		return true;
	}

	
}
