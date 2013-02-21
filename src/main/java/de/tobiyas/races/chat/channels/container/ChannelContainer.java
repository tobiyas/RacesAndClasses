package de.tobiyas.races.chat.channels.container;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import de.tobiyas.races.Races;
import de.tobiyas.races.chat.ChatFormatter;
import de.tobiyas.races.chat.channels.ChannelManager;
import de.tobiyas.util.config.YAMLConfigExtended;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceManager;
import de.tobiyas.races.tutorial.TutorialManager;
import de.tobiyas.races.tutorial.TutorialStepContainer;
import de.tobiyas.races.util.chat.ChannelLevel;
import de.tobiyas.races.util.consts.PermissionNode;
import de.tobiyas.races.util.tutorial.TutorialState;

public class ChannelContainer extends Observable{

	private Races plugin;
	
	private String channelName;
	private ChatFormatter channelFormat;
	
	private String channelPassword;
	private String channelAdmin = "";
	
	private ChannelLevel channelLevel;
	
	private ArrayList<String> participants;
	
	private MuteContainer muteContainer;
	private BanContainer banContainer;
	
	public ChannelContainer(String channelName, ChannelLevel level){
		plugin = Races.getPlugin();
		this.channelName = channelName;
		this.channelLevel = level;
		
		String prefix = "§f[";
		String suffix =  "§f]";
		
		this.channelPassword = "";
		participants = new ArrayList<String>();
		muteContainer = new MuteContainer();
		banContainer = new BanContainer();
		
		adaptFormatingToLevel();
		channelFormat.setPrefix(prefix);
		channelFormat.setSuffix(suffix);
		
		rescanPartitions(null);
		
		ChannelTicker.registerChannel(this);
		if(channelName.equalsIgnoreCase("Tutorial"))
			registerTutorial();
	}
	
	private ChannelContainer(String channelName, ChannelLevel level, YAMLConfigExtended config){
		participants = new ArrayList<String>();
		this.channelLevel = level;
		this.channelName = channelName;
		plugin = Races.getPlugin();
		config.load();
		
		String channelPre = "channel." + level.name() + "." + channelName;
		String prefix = config.getString(channelPre + ".prefix" , "§f[");
		String suffix = config.getString(channelPre + ".suffix" , "§f]");
		
		//Chanel formating
		String channelColor = config.getString(channelPre + ".channelColor", plugin.getChannelConfig().getConfig_channel_default_color());
		String stdFormat = config.getString(channelPre + ".channelFormat", plugin.getChannelConfig().getConfig_channel_default_format());
		
		boolean forceOverride = channelLevel == ChannelLevel.RaceChannel && RaceManager.getManager().getRaceByName(channelName) != null;
		if(forceOverride){
			channelColor = RaceManager.getManager().getRaceByName(channelName).getRaceChatColor();
			stdFormat = RaceManager.getManager().getRaceByName(channelName).getRaceChatFormat();
		}
			
		channelPassword = config.getString(channelPre + ".channelPassword", "");
		channelAdmin = config.getString(channelPre + ".channelAdmin", "");
		
		muteContainer = new MuteContainer(config, channelPre);
		banContainer = new BanContainer(config, channelPre);
		
		List<String> tempList = config.getStringList(channelPre + ".members");
		participants.addAll(tempList);
		
		channelFormat = new ChatFormatter(channelName, channelColor, channelLevel, stdFormat);
		channelFormat.setPrefix(prefix);
		channelFormat.setSuffix(suffix);
		
		if(!config.getBoolean(channelPre + ".saveLoad", false))
			rescanPartitions(null);
		
		config.set(channelPre + ".saveLoad", false);
		config.save();
		
		ChannelTicker.registerChannel(this);
		if(channelName.equalsIgnoreCase("Tutorial"))
			registerTutorial();
	}
	
	private void registerTutorial(){
		TutorialManager.registerObserver(this);
		this.setChanged();
	}
	
	private void adaptFormatingToLevel(){
		String stdFormat = "";
		String stdColor = "";
		
		switch(channelLevel){
			case GlobalChannel:
				stdColor = plugin.getChannelConfig().getConfig_globalchat_default_color();
				stdFormat = plugin.getChannelConfig().getConfig_globalchat_default_format();
				break;
			
			case WorldChannel:
				stdColor = plugin.getChannelConfig().getConfig_worldchat_default_color();
				stdFormat = plugin.getChannelConfig().getConfig_worldchat_default_format();
				break;
			
			case RaceChannel:
				if(RaceManager.getManager().getRaceByName(channelName) != null){
					stdColor = plugin.getChannelConfig().getConfig_racechat_default_color();
					stdFormat = plugin.getChannelConfig().getConfig_racechat_default_format();
				}else{
					stdColor = RaceManager.getManager().getRaceByName(channelName).getRaceChatColor();
					stdFormat = RaceManager.getManager().getRaceByName(channelName).getRaceChatFormat();
				}
				break;
				
			case LocalChannel:
				stdColor = plugin.getChannelConfig().getConfig_localchat_default_color();
				stdFormat = plugin.getChannelConfig().getConfig_localchat_default_format();
				break;
				
			default:
				stdColor = plugin.getChannelConfig().getConfig_channel_default_color();
				stdFormat = plugin.getChannelConfig().getConfig_channel_default_format();
		}
		
		channelFormat = new ChatFormatter(channelName, stdColor, channelLevel, stdFormat);
	}
	
	public void setPassword(String password){
		if(channelLevel == ChannelLevel.PasswordChannel)
			this.channelPassword = password;
	}
	
	public void banPlayer(String playerName, int time){
		banContainer.banPlayer(playerName, time);
	}
	
	public void unbanPlayer(String playerName){
		banContainer.unbanPlayer(playerName);
	}
	
	public void mutePlayer(String playerName, int time){
		muteContainer.mutePlayer(playerName, time);
	}
	
	public void unmutePlayer(String playerName){
		muteContainer.unmutePlayer(playerName);
	}
	
	public void setAdmin(String player){
		if(!participants.contains(player))
			participants.add(player);
		channelAdmin = player;
	}
	
	public void saveChannel(YAMLConfigExtended config){
		if(channelLevel == ChannelLevel.LocalChannel) return;
		
		config.load();
		String channelPre = "channel." + channelLevel.name() + "." + channelName;
		config.createSection(channelPre);
		config.set(channelPre + ".prefix" , channelFormat.getPrefix());
		config.set(channelPre + ".suffix" , channelFormat.getSuffix());
		config.set(channelPre + ".channelColor" , channelFormat.getColor());
		config.set(channelPre + ".members" , participants);
		config.set(channelPre + ".channelFormat", channelFormat.getFormat());
		config.set(channelPre + ".channelPassword", channelPassword);
		config.set(channelPre + ".channelAdmin", channelAdmin);
		config.createSection(channelPre + ".banned");
		config.createSection(channelPre + ".muted");
		
		config.set(channelPre + ".saveLoad", true);
		
		banContainer.saveContainer(config, channelPre);
		muteContainer.saveContainer(config, channelPre);
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
		
		int isBanned = banContainer.isBanned(playerName);
		if(isBanned != -1){
			String time = getTimeString(isBanned);
			player.sendMessage(ChatColor.RED + "You are banned from this channel for: " + ChatColor.LIGHT_PURPLE + time);
			return;
		}
		
		if(channelLevel == ChannelLevel.RaceChannel){
			RaceContainer container = RaceManager.getManager().getRaceOfPlayer(playerName);
			
			if(container == null || !container.getName().equalsIgnoreCase(channelName)){
				player.sendMessage(ChatColor.RED + "You don't belong to this race.");
				return;
			}
		}
		
		if(channelLevel == ChannelLevel.WorldChannel){
			if(!player.getWorld().getName().equalsIgnoreCase(channelName)){
				player.sendMessage(ChatColor.RED + "You are not on this world.");
				return;
			}
		}
		
		if(!channelPassword.equals("")){
			if(!password.equals(channelPassword)){
				if(player != null)
					player.sendMessage(ChatColor.RED + "Wrong password.");
				return;
			}
		}
			
		participants.add(playerName);
		if(player != null){
			if(notify){
				String joinMessage = plugin.getChannelConfig().getConfig_PlayerJoinFormat();
				sendMessageInChannel(player, "", joinMessage);
			}
		}
		
		this.notifyObservers(new TutorialStepContainer(player.getName(), TutorialState.channels, 2));
		this.setChanged();
	}
	
	public void removePlayerFromChannel(String playerName, boolean notify){
		Player player = Bukkit.getPlayer(playerName);
		if(!participants.contains(playerName)){
			player.sendMessage(ChatColor.RED + "You are no member of this channel.");
			return;
		}
		
		if(player != null)
			if(notify){
				String leaveMessage = plugin.getChannelConfig().getConfig_PlayerLeaveFormat();
				sendMessageInChannel(player, "", leaveMessage);
			}
		
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
					sendMessageInChannel(null, "Channel-Admin changed from: " + ChatColor.RED + oldChannelAdmin + " TO " + newChannelAdmin, "");
				}
			}
		}
		
		this.notifyObservers(new TutorialStepContainer(playerName, TutorialState.channels, 6));
		this.setChanged();
	}
	
	public ArrayList<String> getAllParticipants(){
		return participants;
	}
	
	public void sendMessageInChannel(Player sender, String message, String forceFormat){
		if(channelLevel == ChannelLevel.LocalChannel) rescanPartitions(sender);

		if(sender != null){
			int isMuted = muteContainer.isMuted(sender.getName());
			if(isMuted != -1){
				String time = getTimeString(isMuted);				
				sender.sendMessage(ChatColor.RED + "You are muted in this channel for: " + ChatColor.LIGHT_PURPLE + time);
				return;
			}
		}
		
		if(channelName.equalsIgnoreCase("Tutorial") && sender instanceof Player){
			this.notifyObservers(new TutorialStepContainer(sender.getName(), TutorialState.channels, 5));
			this.setChanged();
		}
		
		String modifiedMessage = "";
		if(sender == null)
			modifiedMessage = modifyMessageToPlayer("CONSOLE", message, forceFormat);
		else
			modifiedMessage = modifyMessageToPlayer(sender.getName(), message, forceFormat);
		
		for(String playerName : participants){
			Player player = Bukkit.getPlayer(playerName);
			if(player != null)
				player.sendMessage(modifiedMessage);
		}
	}
	
	private void rescanPartitions(Player localPlayer){
		switch(channelLevel){
			case GlobalChannel:
				if(!channelName.equalsIgnoreCase("Global"))
					return;
				
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
				for(String playerName : RaceManager.getManager().getAllPlayersOfRace(container)){
					Player player = Bukkit.getPlayer(playerName);
					if(player != null)
						participants.add(playerName);
				}
				break;
				
			case LocalChannel:
				if(localPlayer == null) return;
				int distance = plugin.getChannelConfig().getConfig_localchat_range();
				Location loc = localPlayer.getLocation();
				for(Player tempPlayer : loc.getWorld().getPlayers())
					if(loc.distance(tempPlayer.getLocation()) < distance)
						participants.add(tempPlayer.getName());
				break;
				
			default: return;
		}
	}
	
	private String modifyMessageToPlayer(String playerName, String message, String forceFormat){		
		return channelFormat.format(playerName, message, forceFormat);
	}
	
	private String getTimeString(int timeInSec){
		String time = "";
		int orgSeconds = timeInSec * 1000;
		int seconds = (int) (orgSeconds / 1000) % 60 ;
		int minutes = (int) ((orgSeconds / (1000*60)) % 60);
		int hours   = (int) ((orgSeconds / (1000*60*60)) % 24);
		
		if(hours != 0)
			time += hours + " hours, ";
		
		if(minutes != 0)
			time += minutes + " minutes, ";
		
		if(seconds != 0)
			time += seconds + " seconds";
		
		if(timeInSec == Integer.MAX_VALUE)
			time = " ever";
		
		return time;
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
		player.sendMessage(ChatColor.YELLOW + "ChannelName: " + ChatColor.AQUA + channelName);
		player.sendMessage(ChatColor.YELLOW + "ChannelLevel: " + ChatColor.AQUA + channelLevel.name());
		player.sendMessage(ChatColor.YELLOW + "ChannelColor: " + decodeColor(channelFormat.getColor()) + "COLOR");
		player.sendMessage(ChatColor.YELLOW + "ChannelFormat: " + ChatColor.RESET + encodeColor(channelFormat.getFormat()));
		
		if(player.getName().equalsIgnoreCase(channelAdmin))
			player.sendMessage(ChatColor.YELLOW + "ChannelPassword: " + ChatColor.AQUA + channelPassword);
		else
			player.sendMessage(ChatColor.YELLOW + "Has Password: " + ChatColor.AQUA + !channelPassword.equalsIgnoreCase(""));
		
		if(!channelAdmin.equals(""))
			player.sendMessage(ChatColor.YELLOW + "ChannelAdmin: " + ChatColor.AQUA + channelAdmin);
		
		int isMuted = muteContainer.isMuted(player.getName());
		if(isMuted != -1)
			player.sendMessage(ChatColor.YELLOW + "Muted for: " + ChatColor.AQUA + getTimeString(isMuted));
		
		int isBanned = banContainer.isBanned(player.getName());
		if(isBanned != -1)
			player.sendMessage(ChatColor.YELLOW + "Banned for: " + ChatColor.AQUA + getTimeString(isBanned));
		
		player.sendMessage(ChatColor.YELLOW + "===== Channel Members: =====");
		String memberString = "";
		if(channelLevel == ChannelLevel.LocalChannel) 
			rescanPartitions(player);
			
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
		
		this.notifyObservers(new TutorialStepContainer(player.getName(), TutorialState.channels, 3));
		this.setChanged();
	}

	public ChannelLevel getChannelLevel() {
		return channelLevel;
	}

	public boolean isMember(String playerName) {
		return participants.contains(playerName);
	}

	public void tick() {
		muteContainer.tick();
		banContainer.tick();
	}

	public boolean checkPermissionMute(Player admin) {
		if(channelLevel == ChannelLevel.GlobalChannel || channelLevel == ChannelLevel.RaceChannel || channelLevel == ChannelLevel.WorldChannel || channelLevel == ChannelLevel.LocalChannel){
			return plugin.getPermissionManager().checkPermissionsSilent(admin, PermissionNode.channelGlobalMutePower);
		}
		
		return admin.getName().equalsIgnoreCase(this.channelAdmin);
	}
	
	public boolean checkPermissionUnmute(Player admin) {
		if(channelLevel == ChannelLevel.GlobalChannel || channelLevel == ChannelLevel.RaceChannel || channelLevel == ChannelLevel.WorldChannel || channelLevel == ChannelLevel.LocalChannel){
			return plugin.getPermissionManager().checkPermissionsSilent(admin, PermissionNode.channelGlobalUnmutePower);
		}
		
		return admin.getName().equalsIgnoreCase(this.channelAdmin);
	}
	
	public boolean checkPermissionBann(Player admin) {
		if(channelLevel == ChannelLevel.GlobalChannel || channelLevel == ChannelLevel.RaceChannel || channelLevel == ChannelLevel.WorldChannel || channelLevel == ChannelLevel.LocalChannel){
			return plugin.getPermissionManager().checkPermissionsSilent(admin, PermissionNode.channelGlobalBanPower);
		}
		
		return admin.getName().equalsIgnoreCase(this.channelAdmin);
	}
	
	public boolean checkPermissionUnban(Player admin) {
		if(channelLevel == ChannelLevel.GlobalChannel || channelLevel == ChannelLevel.RaceChannel || channelLevel == ChannelLevel.WorldChannel || channelLevel == ChannelLevel.LocalChannel){
			return plugin.getPermissionManager().checkPermissionsSilent(admin, PermissionNode.channelGlobalUnbanPower);
		}
		
		return admin.getName().equalsIgnoreCase(this.channelAdmin);
	}
	
	public boolean isMuted(String playerName){
		return muteContainer.isMuted(playerName) != -1;
	}
	
	public boolean isBanned(String playerName){
		return banContainer.isBanned(playerName) != -1;
	}

	public void editChannel(Player player, String property, String newValue) {
		String playerName = player.getName();
		if(channelLevel == ChannelLevel.PasswordChannel || channelLevel == ChannelLevel.PrivateChannel || channelLevel == ChannelLevel.PublicChannel || channelLevel == ChannelLevel.LocalChannel){
			if(!playerName.equalsIgnoreCase(channelAdmin)){
				player.sendMessage(ChatColor.RED + "You must be the channel-admin to edit the channel");
				return;
			}
		}
		
		boolean changed = true;
		
		switch(property.toLowerCase()){
			case "format": channelFormat.setFormat(newValue); break;
			case "color" : channelFormat.setColor(newValue); break;
			case "admin" : changed = changeAdmin(player, newValue); break;
			case "prefix": channelFormat.setPrefix(newValue); break;
			case "suffix": channelFormat.setSuffix(newValue); break;
			case "password" : if(channelLevel == ChannelLevel.PasswordChannel) channelPassword = newValue;
			default: changed = false;
		}
		
		if(changed)
			player.sendMessage(ChatColor.LIGHT_PURPLE + property + ChatColor.GREEN + " was changed to: " + ChatColor.LIGHT_PURPLE + newValue);
		else{
			player.sendMessage(ChatColor.LIGHT_PURPLE + property + ChatColor.RED + " could not be found or your new Argument is invalid.");
			player.sendMessage(ChatColor.RED + "Valid properties are: " + ChatColor.LIGHT_PURPLE + "format, color, prefix, suffix, admin, password");
		}
	}
	
	private boolean changeAdmin(Player oldAdmin, String newAdmin){
		if(channelLevel == ChannelLevel.PasswordChannel || 
			channelLevel == ChannelLevel.PrivateChannel || 
			channelLevel == ChannelLevel.PublicChannel)
			return false;
		
		boolean isIn = false;
		for(String member : participants)
			if(member.equalsIgnoreCase(newAdmin)){
				isIn = true;
				newAdmin = member;
			}
		
		if(isIn){
			this.sendMessageInChannel(null, "New Admin of this channel is: " + ChatColor.LIGHT_PURPLE + newAdmin, "");
			channelAdmin = newAdmin;
			return true;
		}
		
		return false;
	}

}
