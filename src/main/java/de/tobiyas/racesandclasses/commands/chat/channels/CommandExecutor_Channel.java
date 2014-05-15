/*******************************************************************************
 * Copyright 2014 Tobias Welther
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.tobiyas.racesandclasses.commands.chat.channels;

import java.util.List;
import java.util.Observable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.commands.CommandInterface;
import de.tobiyas.racesandclasses.configuration.member.file.MemberConfig;
import de.tobiyas.racesandclasses.tutorial.TutorialStepContainer;
import de.tobiyas.racesandclasses.util.chat.ChannelLevel;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;
import de.tobiyas.racesandclasses.util.tutorial.TutorialState;

public class CommandExecutor_Channel extends Observable implements CommandInterface {

	private RacesAndClasses plugin;
	
	public CommandExecutor_Channel(){
		plugin = RacesAndClasses.getPlugin();

//		String command = "channel";
//		if(plugin.getConfigManager().getGeneralConfig().getConfig_general_disable_commands().contains(command)) return;
//		
//		try{
//			plugin.getCommand(command).setExecutor(this);
//		}catch(Exception e){
//			plugin.log("ERROR: Could not register command /" + command + ".");
//		}
		
		plugin.getTutorialManager().registerObserver(this);
		this.setChanged();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		if(!plugin.getConfigManager().getGeneralConfig().isConfig_channels_enable()){
			LanguageAPI.sendTranslatedMessage(sender, "something_disabled",
					"value", "Channels");
			return true;
		}

		
		if(args.length == 0){
			postHelp(sender);
			return true;
		}
		
		String channelCommand = args[0];
		
		if(channelCommand.equalsIgnoreCase("info")){
			String channelName = "";
			if(args.length == 2){
				channelName = args[1];
			}
				
			postChannelInfo(sender, channelName);
			return true;
		}
		
		if(channelCommand.equalsIgnoreCase("list")){
			listChannels(sender);
			return true;
		}
		
		
		if(!(sender instanceof Player)){
			LanguageAPI.sendTranslatedMessage(sender,"only_players");
			return true;
		}
	
		Player player = (Player) sender;
		
		
		if(channelCommand.equalsIgnoreCase("change") || channelCommand.equalsIgnoreCase("post") || channelCommand.equalsIgnoreCase("switch")
				|| channelCommand.equalsIgnoreCase("ch")){
			
			if(args.length != 2){
				LanguageAPI.sendTranslatedMessage(player, "wrong_command_use",
									"command", "/channel change <channelname>");
				return true;
			}

			String changeTo = args[1];
			changeChannel(player, changeTo);
			return true;
		}
		
		if(channelCommand.equalsIgnoreCase("join")){
			if(args.length == 1 || args.length > 3){
				
				LanguageAPI.sendTranslatedMessage(player,"wrong_command_use",
						"command", "/channel join <channelname> [@password@]");
				return true;
			}
			
			String channelName = args[1];
			String password = "";
			if(args.length == 3){
				password = args[2];
			}
			
			joinChannel(player, channelName, password);
			return true;
		}
		
		if(channelCommand.equalsIgnoreCase("leave")){
			if(args.length != 2){
				LanguageAPI.sendTranslatedMessage(player,"wrong_command_use",
						"command", "/channel leave <channelname>");
				return true;
			}
			String channelName = args[1];
			leaveChannel(player, channelName);
			return true;
		}
		
		if(channelCommand.equalsIgnoreCase("create")){
			if(args.length == 1 || args.length > 4){
				LanguageAPI.sendTranslatedMessage(player,"wrong_command_use",
						"command", "/channel create <channelname> [channelType] [@password@]");
				return true;
			}
			
			String channelName = args[1];
			String channelType = "PublicChannel";
			String channelPassword = "";
			
			if(args.length > 2)
				channelType = args[2];
			
			if(args.length > 3)
				channelPassword = args[3];
			
			createChannel(player, channelName, channelType, channelPassword);
			return true;
		}
		
		if(channelCommand.equalsIgnoreCase("ban")){
			if(!(args.length == 3 ||  args.length == 4)){
				LanguageAPI.sendTranslatedMessage(player, "wrong_command_use",
						"command", "/channel ban <channelname> <playername> [@time_in_seconds@]");
				return true;
			}
			
			int time = Integer.MAX_VALUE;
			try{
				time = Integer.valueOf(args[3]);
			}catch(Exception e){
			}
			
			Player target = Bukkit.getPlayer(args[2]);
			if(target == null){
				//TODO add some output
				return true;
			}
			
			plugin.getChannelManager().banPlayer(player.getUniqueId(), target.getUniqueId(), args[1], time);
			return true;
		}
		
		if(channelCommand.equalsIgnoreCase("unban")){
			if(!(args.length == 3)){
				LanguageAPI.sendTranslatedMessage(player,"wrong_command_use",
						"command", "/channel unban <channelname> <playername>");
				return true;
			}
			
			Player target = Bukkit.getPlayer(args[2]);
			if(target == null){
				//TODO add some output
				return true;
			}
			
			plugin.getChannelManager().unbanPlayer(player.getUniqueId(), target.getUniqueId(), args[1]);
			return true;
		}
		
		if(channelCommand.equalsIgnoreCase("mute")){
			if(!(args.length == 3 ||  args.length == 4)){
				LanguageAPI.sendTranslatedMessage(player,"wrong_command_use",
						"command", "/channel mute <channelname> <playername> [@time_in_seconds@]");
				return true;
			}
			
			int time = Integer.MAX_VALUE;
			try{
				time = Integer.valueOf(args[3]);
			}catch(Exception e){
			}
			
			Player target = Bukkit.getPlayer(args[2]);
			if(target == null){
				//TODO add some output
				return true;
			}
			
			plugin.getChannelManager().mutePlayer(player.getUniqueId(), target.getUniqueId(), args[1], time);
			return true;
		}
		
		if(channelCommand.equalsIgnoreCase("unmute")){
			if(!(args.length == 3)){
				LanguageAPI.sendTranslatedMessage(player,"wrong_command_use",
						"command", "/channel unmute <channelname> <playername>");
				return true;
			}
			
			Player target = Bukkit.getPlayer(args[2]);
			if(target == null){
				//TODO add some output
				return true;
			}
			
			plugin.getChannelManager().unmutePlayer(player.getUniqueId(), target.getUniqueId(), args[1]);
			return true;
		}
		
		if(channelCommand.equalsIgnoreCase("edit")){
			if(args.length < 4){
				LanguageAPI.sendTranslatedMessage(player, "wrong_command_use",
						"command", "/channel edit <channelname> <@channel_propertie@> <@new_value@>");
				return true;
			}
			
			String channel = args[1];
			String property = args[2];
			
			String newValue = "";
			for(int i = 3; i < args.length; i++){
				newValue += args[i];
				if(i != args.length - 1){
					newValue += " ";
				}
			}
			
			channelEdit(player, channel, property, newValue);
			return true;
		}
		
		
		postHelp(player);
		return true;
	}
	
	private void postHelp(CommandSender sender){
		LanguageAPI.sendTranslatedMessage(sender,"wrong_command_use",
				"command", "");
		sender.sendMessage(ChatColor.RED + "/channel " + ChatColor.LIGHT_PURPLE + "info " + ChatColor.AQUA + "[channelname]");
		sender.sendMessage(ChatColor.RED + "/channel " + ChatColor.LIGHT_PURPLE + "list");
		
		sender.sendMessage(ChatColor.RED + "/channel " + ChatColor.LIGHT_PURPLE + "<post/change/switch> " + ChatColor.YELLOW + "<channelname>");
		
		sender.sendMessage(ChatColor.RED + "/channel " + ChatColor.LIGHT_PURPLE + "join " + ChatColor.YELLOW + "<channelname> " + 
							ChatColor.AQUA + "[@password@]");
		
		sender.sendMessage(ChatColor.RED + "/channel " + ChatColor.LIGHT_PURPLE + "leave " + ChatColor.YELLOW + "<channelname> ");
		
		sender.sendMessage(ChatColor.RED + "/channel " + ChatColor.LIGHT_PURPLE + "create " + ChatColor.YELLOW + "<channelname> " +
						   ChatColor.AQUA + "[channeltype] [@password@]");
		
		sender.sendMessage(ChatColor.RED + "/channel " + ChatColor.LIGHT_PURPLE + "edit " + ChatColor.YELLOW + "<channelname> " +
							ChatColor.AQUA + "<@channel_propertie@> <@new_value@>");
	}
	
	
	private void postChannelInfo(CommandSender sender, String channel){
		if(channel == "" && sender instanceof Player){
			channel = plugin.getConfigManager().getMemberConfigManager().getConfigOfPlayer(((Player) sender).getUniqueId()).getCurrentChannel();
		}
		
		sender.sendMessage(ChatColor.YELLOW + "=====" + ChatColor.RED + " Channel Information: " + 
							ChatColor.AQUA + channel + ChatColor.YELLOW + " =====");
		plugin.getChannelManager().postChannelInfo(sender, channel);
	}
	
	
	private void listChannels(CommandSender sender){
		sender.sendMessage(ChatColor.YELLOW + "======" + ChatColor.RED + "Channel-List:" + ChatColor.YELLOW + "=====");
		sender.sendMessage(ChatColor.YELLOW + "HINT: Format is: " + ChatColor.BLUE + "ChannelName: " + ChatColor.AQUA + "ChannelLevel");
		for(ChannelLevel level : ChannelLevel.values())
			for(String channel : plugin.getChannelManager().listAllPublicChannels()){
				if(plugin.getChannelManager().getChannelLevel(channel) == level && sender instanceof Player){
					String addition = "";
					if(plugin.getChannelManager().isMember(((Player)sender).getUniqueId(), channel)){
						addition =  ChatColor.YELLOW + "   <-[Joined]";
					}
					
					sender.sendMessage(ChatColor.BLUE + channel + ": " + ChatColor.AQUA + level.name() + addition);
				}
			}
		
		if(sender instanceof Player) this.notifyObservers(new TutorialStepContainer(((Player)sender).getUniqueId(), TutorialState.channels, 1));
		this.setChanged();
	}
	
	private void joinChannel(Player player, String channelName, String password){
		ChannelLevel level = plugin.getChannelManager().getChannelLevel(channelName);
		if(level == ChannelLevel.NONE){
			player.sendMessage(ChatColor.RED + "Could not find any channel named: " + ChatColor.LIGHT_PURPLE + channelName);
			return;
		}
		
		plugin.getChannelManager().joinChannel(player.getUniqueId(), channelName, password, true);
	}
	
	private void leaveChannel(Player player, String channelName){
		ChannelLevel level = plugin.getChannelManager().getChannelLevel(channelName);
		if(level == ChannelLevel.NONE){
			player.sendMessage(ChatColor.RED + "Could not find any channel named: " + ChatColor.LIGHT_PURPLE + channelName);
			return;
		}
		
		plugin.getChannelManager().leaveChannel(player.getUniqueId(), channelName, true);
		
		MemberConfig config = plugin.getConfigManager().getMemberConfigManager().getConfigOfPlayer(player.getUniqueId());
		if(channelName.equalsIgnoreCase(config.getCurrentChannel())){
				config.setValue(MemberConfig.chatChannel, "Global");
		}
			
	}
	
	private void createChannel(Player player, String channelName, String channelType, String channelPassword){
		ChannelLevel channelLevel = getChannelLevel(channelType);
		if(channelLevel == ChannelLevel.NONE){
			player.sendMessage(ChatColor.RED + "Channel Level could not be recognized: " + ChatColor.LIGHT_PURPLE + channelType);
			return;
		}
		
		if(channelLevel == ChannelLevel.GlobalChannel || channelLevel == ChannelLevel.RaceChannel 
				|| channelLevel == ChannelLevel.WorldChannel || channelLevel == ChannelLevel.LocalChannel){
			player.sendMessage(ChatColor.RED + "You can't create a new " + ChatColor.AQUA + channelLevel);
			return;
		}
		
		if(channelLevel == ChannelLevel.PasswordChannel && 
		   !plugin.getPermissionManager().checkPermissions(player, PermissionNode.channelCreatePassword))
			return;
		
		if(channelLevel == ChannelLevel.PublicChannel && 
		   !plugin.getPermissionManager().checkPermissions(player, PermissionNode.channelCreatePublic))
			return;
		
		if(channelLevel == ChannelLevel.PrivateChannel && 
			!plugin.getPermissionManager().checkPermissions(player, PermissionNode.channelCreatePrivate))
			return;
		
		if(channelLevel != ChannelLevel.PasswordChannel && channelPassword != "")
			player.sendMessage(ChatColor.YELLOW + "[INFO] You try to create a non-password channel with a password. The password will be ignored.");
		
		
		if(plugin.getChannelManager().getChannelLevel(channelName) != ChannelLevel.NONE){
			player.sendMessage(ChatColor.RED + "This channel already exisists.");
			return;
		}
		
		plugin.getChannelManager().registerChannel(channelLevel, channelName, channelPassword, player.getUniqueId());
	}
	
	private void changeChannel(Player player, String changeTo){
		ChannelLevel level = plugin.getChannelManager().getChannelLevel(changeTo);
		if(level == ChannelLevel.NONE){
			player.sendMessage(ChatColor.RED + "Could not find any channel named: " + ChatColor.LIGHT_PURPLE + changeTo);
			return;
		}
		
		if(level != ChannelLevel.LocalChannel){
			if(!plugin.getChannelManager().isMember(player.getUniqueId(), changeTo)){
				player.sendMessage(ChatColor.RED + "You are no member of: " + ChatColor.LIGHT_PURPLE + changeTo);
				return;
			}
		}
		
		MemberConfig config = plugin.getConfigManager().getMemberConfigManager().getConfigOfPlayer(player.getUniqueId());
		if(config == null){
			player.sendMessage(ChatColor.RED + "Something gone wrong with your config. Try relogging or ask an Admin.");
			return;
		}
		
		config.setValue(MemberConfig.chatChannel, changeTo);
		player.sendMessage(ChatColor.GREEN + "You now write in the channel: " + ChatColor.AQUA + changeTo);
		
		if(changeTo.equalsIgnoreCase("tutorial")){
			this.notifyObservers(new TutorialStepContainer(player.getUniqueId(), TutorialState.channels, 4));
			this.setChanged();
		}
	}
	
	private ChannelLevel getChannelLevel(String channelType){
		channelType = channelType.toLowerCase();
		
		for(ChannelLevel level : ChannelLevel.values()){
			String realChannelName = level.name().toLowerCase();
			if(realChannelName.contains(channelType)){
				return level;
			}
		}
		
		return ChannelLevel.NONE;
	}
	
	private void channelEdit(Player player, String channel, String property, String newValue){
		ChannelLevel level = plugin.getChannelManager().getChannelLevel(channel);
		if(level == ChannelLevel.NONE){
			player.sendMessage(ChatColor.RED + "Could not find any channel named: " + ChatColor.LIGHT_PURPLE + channel);
			return;
		}
		
		if(level == ChannelLevel.GlobalChannel || level == ChannelLevel.WorldChannel || level == ChannelLevel.RaceChannel){
			if(!plugin.getPermissionManager().checkPermissions(player, PermissionNode.channelEdit)){
				return;
			}
		}
		
		plugin.getChannelManager().editChannel(player.getUniqueId(), channel, property, newValue);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command,
			String alias, String[] args) {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * Returns the CommandName
	 */
	@Override
	public String getCommandName(){
		return "channel";
	}
	
	@Override
	public String[] getAliases() {
		return new String[]{};
	}
	
	@Override
	public boolean hasAliases() {
		return false;
	}
	
}
