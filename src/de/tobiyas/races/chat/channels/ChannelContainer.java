package de.tobiyas.races.chat.channels;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import de.tobiyas.races.Races;
import de.tobiyas.races.configuration.global.YAMLConfigExtended;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceManager;
import de.tobiyas.races.util.chat.ChannelLevel;

public class ChannelContainer {

	private Races plugin;
	
	private String prefix;
	private String suffix;
	private String channelName;
	private String channelColor;
	private String channelFormat;
	
	private String channelPassword;
	private String channelAdmin = "";
	
	private ChannelLevel channelLevel;
	
	private ArrayList<String> participants;
	
	public ChannelContainer(String channelName, ChannelLevel level){
		plugin = Races.getPlugin();
		this.channelName = channelName;
		this.channelLevel = level;
		this.channelColor = plugin.interactConfig().getconfig_racechat_default_color();
		
		this.prefix = "§f[" + channelColor;
		this.suffix =  "§f]" + channelColor;
		this.channelFormat = plugin.interactConfig().getconfig_racechat_default_format();
		
		this.channelPassword = "";
		participants = new ArrayList<String>();
	}
	
	private ChannelContainer(String channelName, ChannelLevel level, YAMLConfigExtended config){
		participants = new ArrayList<String>();
		this.channelLevel = level;
		this.channelName = channelName;
		plugin = Races.getPlugin();
		config.load();
		
		
		String channelPre = "channel." + level.name() + "." + channelName;
		prefix = config.getString(channelPre + ".prefix" , "§f[");
		suffix = config.getString(channelPre + ".suffix" , "§f]");
		channelColor = config.getString(channelPre + ".channelColor", plugin.interactConfig().getconfig_racechat_default_color());
		channelFormat = config.getString(channelPre + ".channelFormat", plugin.interactConfig().getconfig_racechat_default_format());
		channelPassword = config.getString(channelPre + ".channelPassword", "");
		channelAdmin = config.getString(channelPre + ".channelAdmin", "");
		
		List<String> tempList = config.getStringList(channelPre + ".members");
		participants.addAll(tempList);
	}
	
	public void setPassword(String password){
		if(channelLevel == ChannelLevel.PasswordChannel)
			this.channelPassword = password;
	}
	
	public void setAdmin(String player){
		if(!participants.contains(player))
			participants.add(player);
		channelAdmin = player;
	}
	
	public void saveChannel(YAMLConfigExtended config){
		if(channelLevel == ChannelLevel.GlobalChannel || 
			channelLevel == ChannelLevel.WorldChannel || 
			channelLevel == ChannelLevel.RaceChannel) return;
		
		config.load();
		String channelPre = "channel." + channelLevel.name() + "." + channelName;
		config.createSection(channelPre);
		config.set(channelPre + ".prefix" , prefix);
		config.set(channelPre + ".suddix" , suffix);
		config.set(channelPre + ".color" , channelColor);
		config.set(channelPre + ".members" , participants);
		config.set(channelPre + ".format", channelFormat);
		config.set(channelPre + ".channelPassword", channelPassword);
		config.set(channelPre + ".channelAdmin", channelAdmin);
		config.save();
	}
	
	public static ChannelContainer constructFromYml(YAMLConfigExtended config, String channelName, ChannelLevel level){
		return new ChannelContainer(channelName, level, config);
	}
	
	public void addPlayerToChannel(String playerName, String password, boolean notify){
		Player player = Bukkit.getPlayer(playerName);
		if(participants.contains(playerName)){ 
			player.sendMessage(ChatColor.RED + "You are already member of this channel.");
			return;
		}
		
		if(!password.equals(channelPassword)){
			if(player != null)
				player.sendMessage(ChatColor.RED + "Wrong password.");
			return;
		}
		
		participants.add(playerName);
		if(player != null){
			if(notify)
				sendMessageInChannel(null, "Player joined Channel: " + ChatColor.DARK_AQUA + player.getDisplayName());
		}
	}
	
	public void removePlayerFromChannel(String playerName, boolean notify){
		Player player = Bukkit.getPlayer(playerName);
		if(!participants.contains(playerName)){
			player.sendMessage(ChatColor.RED + "You are no member of this channel.");
			return;
		}
		
		if(player != null)
			if(notify)
				sendMessageInChannel(null, "Player left Channel: " + ChatColor.DARK_AQUA + player.getDisplayName());
		
		participants.remove(playerName);
		if(channelLevel == ChannelLevel.PasswordChannel || channelLevel == ChannelLevel.PrivateChannel || channelLevel == ChannelLevel.PublicChannel){
			if(participants.size() == 0){
				ChannelManager.GetInstance().removeChannel(this);
				return;
			}else{
				if(playerName.equalsIgnoreCase(channelAdmin)){
					String oldChannelAdmin = channelAdmin;
					String newChannelAdmin = participants.get(0);
					setAdmin(newChannelAdmin);
					sendMessageInChannel(null, "Channel-Admin changed from: " + ChatColor.RED + oldChannelAdmin + " TO " + newChannelAdmin);
				}
			}
		}
	}
	
	public ArrayList<String> getAllParticipants(){
		return participants;
	}
	
	public void sendMessageInChannel(Player sender, String message){
		rescanPartitions();
		String modifiedMessage = "";
		if(sender == null)
			modifiedMessage = modifyMessageToPlayer("CONSOLE", message);
		else
			modifiedMessage = modifyMessageToPlayer(sender.getName(), message);
		
		for(String playerName : participants){
			Player player = Bukkit.getPlayer(playerName);
			if(player != null)
				player.sendMessage(modifiedMessage);
		}
	}
	
	private void rescanPartitions(){
		switch(channelLevel){
			case GlobalChannel: 
				participants.clear();
				for(Player player : Bukkit.getOnlinePlayers())
					participants.add(player.getName());
				break;
				
			case WorldChannel:
				participants.clear();
				for(Player player : Bukkit.getWorld(channelName).getPlayers())
					participants.add(player.getName());
				break;
				
			case RaceChannel: 
				participants.clear();
				RaceContainer container = RaceManager.getManager().getRaceByName(channelName);
				for(String playerName : RaceManager.getManager().getAllPlayerOfRace(container)){
					Player player = Bukkit.getPlayer(playerName);
					if(player != null)
						participants.add(playerName);
				}
					
				break;
			default: return;
		}
	}
	
	private String modifyMessageToPlayer(String playerName, String message){		
		Player player = Bukkit.getPlayer(playerName);
		RaceContainer container = RaceManager.getManager().getRaceOfPlayer(playerName);
		
		String displayName;
		String realName;
		String world;
		
		if(playerName.equalsIgnoreCase("CONSOLE")){
			displayName = "CONSOLE";
			realName = "CONSOLE";
			world = "None";
		}else{
			if(player == null){
				displayName = "UNKNOWN";
				realName = "UNKOWN";
				world = "UNKNOWN";
			}else{
				displayName = player.getDisplayName();
				realName = player.getName();
				world = player.getWorld().getName();
			}
		}
		
		String messageFormat = new String(channelFormat);
		String raceTag = "NONE";
		if(container != null)
			raceTag = container.getTag();
		
		//default: {color}[{nick}] &f{prefix}{sender}{suffix}{color}: {msg}
		messageFormat = messageFormat.replaceAll(Pattern.quote("{color}"), channelColor);
		messageFormat = messageFormat.replaceAll(Pattern.quote("{nick}"), channelName);
		messageFormat = messageFormat.replaceAll(Pattern.quote("{prefix}"), prefix);
		messageFormat = messageFormat.replaceAll(Pattern.quote("{suffix}"), suffix);
		messageFormat = messageFormat.replaceAll(Pattern.quote("{sender}"), displayName);
		messageFormat = messageFormat.replaceAll(Pattern.quote("{realname}"), realName);
		messageFormat = messageFormat.replaceAll(Pattern.quote("{racetag}"), raceTag);
		messageFormat = messageFormat.replaceAll(Pattern.quote("{world}"), world);
		messageFormat = messageFormat.replaceAll(Pattern.quote("{channeltype}"), channelLevel.name());
		messageFormat = decodeColor(messageFormat);
		messageFormat = messageFormat.replaceAll(Pattern.quote("{msg}"), message);
	
		return messageFormat;
	}
	
	private String decodeColor(String message){
		return message.replaceAll("(&([a-f0-9]))", "§$2");
	}
	
	private String encodeColor(String message){
		return message.replaceAll("(§([a-f0-9]))", "&$2");
	}
	
	public String getChannelName(){
		return channelName;
	}

	public void postInfo(Player player) {
		rescanPartitions();
		
		player.sendMessage(ChatColor.YELLOW + "ChannelName: " + ChatColor.AQUA + channelName);
		player.sendMessage(ChatColor.YELLOW + "ChannelLevel: " + ChatColor.AQUA + channelLevel.name());
		player.sendMessage(ChatColor.YELLOW + "ChannelColor: " + decodeColor(channelColor) + "COLOR");
		player.sendMessage(ChatColor.YELLOW + "ChannelFormat: " + ChatColor.RESET + encodeColor(channelFormat));
		player.sendMessage(ChatColor.YELLOW + "Has Password: " + ChatColor.AQUA + !channelPassword.equalsIgnoreCase(""));
		if(!channelAdmin.equals(""))
			player.sendMessage(ChatColor.YELLOW + "ChannelAdmin: " + ChatColor.AQUA + channelAdmin);
		
		player.sendMessage(ChatColor.YELLOW + "===== Channel Members: =====");
		String memberString = "";
		for(String member : participants){
			if(Bukkit.getPlayer(member) == null)
				memberString += ChatColor.RED + member + " (offline), ";
			else
				memberString += ChatColor.GREEN + member + ", ";
		}
		if(memberString.length() > 0)
			player.sendMessage(memberString.substring(0, memberString.length() - 2));
		else
			player.sendMessage(ChatColor.RED + "This channel has currently no Members.");
	}

	public ChannelLevel getChannelLevel() {
		return channelLevel;
	}

	public boolean isMember(String playerName) {
		rescanPartitions();
		return participants.contains(playerName);
	}
}
