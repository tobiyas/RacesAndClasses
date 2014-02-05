package de.tobiyas.racesandclasses.chat.channels.container;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.chat.ChatFormatter;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.racesandclasses.tutorial.TutorialStepContainer;
import de.tobiyas.racesandclasses.util.chat.ChannelLevel;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;
import de.tobiyas.racesandclasses.util.tutorial.TutorialState;
import de.tobiyas.util.config.YAMLConfigExtended;

public class ChannelContainer extends Observable{

	private final RacesAndClasses plugin;
	
	private String channelName;
	private ChatFormatter channelFormat;
	
	private String channelPassword;
	private String channelAdmin = "";
	
	private ChannelLevel channelLevel;
	
	private ArrayList<String> participants;
	
	private MuteContainer muteContainer;
	private BanContainer banContainer;
	
	public ChannelContainer(String channelName, ChannelLevel level) throws ChannelInvalidException{
		plugin = RacesAndClasses.getPlugin();
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
		if("Tutorial".equalsIgnoreCase(channelName)){
			registerTutorial();
		}
		
		if(this.channelLevel == ChannelLevel.RaceChannel){
			boolean raceExists = plugin.getRaceManager().getHolderByName(this.channelName) != null;
			if(!raceExists){
				throw new ChannelInvalidException();
			}
		}
	}
	
	private ChannelContainer(String channelName, ChannelLevel level, YAMLConfigExtended config){
		participants = new ArrayList<String>();
		this.channelLevel = level;
		this.channelName = channelName;
		plugin = RacesAndClasses.getPlugin();
		config.load();
		
		String channelPre = "channel." + level.name() + "." + channelName;
		String prefix = config.getString(channelPre + ".prefix" , "§f[");
		String suffix = config.getString(channelPre + ".suffix" , "§f]");
		
		//Chanel formating
		String channelColor = config.getString(channelPre + ".channelColor", plugin.getConfigManager().getChannelConfig().getConfig_channel_default_color());
		String stdFormat = config.getString(channelPre + ".channelFormat", plugin.getConfigManager().getChannelConfig().getConfig_channel_default_format());
		
		RaceContainer raceContainer = (RaceContainer) plugin.getRaceManager().getHolderByName(channelName);
		boolean forceOverride = channelLevel == ChannelLevel.RaceChannel && raceContainer != null;
		
		if(forceOverride){
			channelColor = raceContainer.getRaceChatColor();
			stdFormat = raceContainer.getRaceChatFormat();
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
		plugin.getTutorialManager().registerObserver(this);
		this.setChanged();
	}
	
	private void adaptFormatingToLevel(){
		String stdFormat = "";
		String stdColor = "";
		
		switch(channelLevel){
			case GlobalChannel:
				stdColor = plugin.getConfigManager().getChannelConfig().getConfig_globalchat_default_color();
				stdFormat = plugin.getConfigManager().getChannelConfig().getConfig_globalchat_default_format();
				break;
			
			case WorldChannel:
				stdColor = plugin.getConfigManager().getChannelConfig().getConfig_worldchat_default_color();
				stdFormat = plugin.getConfigManager().getChannelConfig().getConfig_worldchat_default_format();
				break;
			
			case RaceChannel:
				RaceContainer raceContainer = (RaceContainer) plugin.getRaceManager().getHolderByName(channelName);
				if(raceContainer == null){
					stdColor = plugin.getConfigManager().getChannelConfig().getConfig_racechat_default_color();
					stdFormat = plugin.getConfigManager().getChannelConfig().getConfig_racechat_default_format();
				}else{
					stdColor = raceContainer.getRaceChatColor();
					stdFormat = raceContainer.getRaceChatFormat();
				}
				break;
				
			case LocalChannel:
				stdColor = plugin.getConfigManager().getChannelConfig().getConfig_localchat_default_color();
				stdFormat = plugin.getConfigManager().getChannelConfig().getConfig_localchat_default_format();
				break;
				
			default:
				stdColor = plugin.getConfigManager().getChannelConfig().getConfig_channel_default_color();
				stdFormat = plugin.getConfigManager().getChannelConfig().getConfig_channel_default_format();
		}
		
		channelFormat = new ChatFormatter(channelName, stdColor, channelLevel, stdFormat);
	}
	
	public void setPassword(String password){
		if(channelLevel == ChannelLevel.PasswordChannel){
			this.channelPassword = password;
		}
	}
	
	protected String getPassword(){
		return channelPassword;
	}
	
	public void banAndRemovePlayer(String playerName, int time){
		banContainer.banPlayer(playerName, time);
		if(isMember(playerName)){
			removePlayerFromChannel(playerName, false);
		}
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
		if(!participants.contains(player)){
			participants.add(player);
		}
		channelAdmin = player;
	}
	
	public String getAdmin(){
		return channelAdmin;
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
		
		config.set(channelPre + ".saveLoad", true);
		
		banContainer.saveContainer(config, channelPre);
		muteContainer.saveContainer(config, channelPre);
		config.save();
	}
	
	public static ChannelContainer constructFromYml(YAMLConfigExtended config, String channelName, ChannelLevel level) throws ChannelInvalidException{
		try{
			return new ChannelContainer(channelName, level, config);
		}catch(Exception exp){
			throw new ChannelInvalidException();
		}
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
			AbstractTraitHolder container = plugin.getRaceManager().getHolderOfPlayer(playerName);
			
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
				String joinMessage = plugin.getConfigManager().getChannelConfig().getConfig_PlayerJoinFormat();
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
		
		if(player != null){
			if(notify){
				String leaveMessage = plugin.getConfigManager().getChannelConfig().getConfig_PlayerLeaveFormat();
				sendMessageInChannel(player, "", leaveMessage);
			}
		}
		
		participants.remove(playerName);
		if(channelLevel == ChannelLevel.PasswordChannel || channelLevel == ChannelLevel.PrivateChannel || channelLevel == ChannelLevel.PublicChannel){
			if(participants.size() == 0){
				plugin.getChannelManager().removeChannel(this.channelName);
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
	
	public void sendMessageInChannel(CommandSender sender, String message, String forceFormat){
		if(channelLevel == ChannelLevel.LocalChannel && sender instanceof Player) rescanPartitions((Player) sender);

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
			if(player != null){
				player.sendMessage(modifiedMessage);
			}
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
				RaceContainer container = (RaceContainer) plugin.getRaceManager().getHolderByName(channelName);
				if(container == null){
					//channel is not valid any more.
					break;
				}
				
				List<String> allPlayersOfRace = plugin.getRaceManager().getAllPlayersOfHolder(container);
				for(String playerName : allPlayersOfRace) {
					Player player = Bukkit.getPlayer(playerName);
					if(player != null)
						participants.add(playerName);
				}
				break;
				
			case LocalChannel:
				participants.clear();
				if(localPlayer == null) return;
				int distance = plugin.getConfigManager().getChannelConfig().getConfig_localchat_range();
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

	public void postInfo(CommandSender sender) {
		sender.sendMessage(ChatColor.YELLOW + "ChannelName: " + ChatColor.AQUA + channelName);
		sender.sendMessage(ChatColor.YELLOW + "ChannelLevel: " + ChatColor.AQUA + channelLevel.name());
		sender.sendMessage(ChatColor.YELLOW + "ChannelColor: " + decodeColor(channelFormat.getColor()) + "COLOR");
		sender.sendMessage(ChatColor.YELLOW + "ChannelFormat: " + ChatColor.RESET + encodeColor(channelFormat.getFormat()));
		
		if(sender.getName().equalsIgnoreCase(channelAdmin)){
			sender.sendMessage(ChatColor.YELLOW + "ChannelPassword: " + ChatColor.AQUA + channelPassword);
		}else{
			sender.sendMessage(ChatColor.YELLOW + "Has Password: " + ChatColor.AQUA + !channelPassword.equalsIgnoreCase(""));
		}
			
		if(!channelAdmin.equals("")){
			sender.sendMessage(ChatColor.YELLOW + "ChannelAdmin: " + ChatColor.AQUA + channelAdmin);
		}
		
		int isMuted = muteContainer.isMuted(sender.getName());
		if(isMuted != -1)
			sender.sendMessage(ChatColor.YELLOW + "Muted for: " + ChatColor.AQUA + getTimeString(isMuted));
		
		int isBanned = banContainer.isBanned(sender.getName());
		if(isBanned != -1)
			sender.sendMessage(ChatColor.YELLOW + "Banned for: " + ChatColor.AQUA + getTimeString(isBanned));
		
		sender.sendMessage(ChatColor.YELLOW + "===== Channel Members: =====");
		String memberString = "";
		if(channelLevel == ChannelLevel.LocalChannel && sender instanceof Player){ 
			rescanPartitions((Player) sender);
		}	
		
		for(String member : participants){
			Player playerMember = Bukkit.getPlayer(member);
			if(playerMember == null || !playerMember.isOnline()){
				memberString += ChatColor.RED + member + " (offline), ";
			}else{
				memberString += ChatColor.GREEN + member + ", ";
			}
		}
		if(memberString.length() > 0){
			sender.sendMessage(memberString.substring(0, memberString.length() - 2));
		}else{
			sender.sendMessage(ChatColor.RED + "This channel has currently no Members.");
		}
		
		this.notifyObservers(new TutorialStepContainer(sender.getName(), TutorialState.channels, 3));
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

	/**
	 * Edits a property of the channel.
	 * 
	 * @param player
	 * @param property
	 * @param newValue
	 */
	public void editChannel(Player player, String property, String newValue) {
		String playerName = player.getName();
		if(channelLevel == ChannelLevel.PasswordChannel || channelLevel == ChannelLevel.PrivateChannel || channelLevel == ChannelLevel.PublicChannel || channelLevel == ChannelLevel.LocalChannel){
			if(!playerName.equalsIgnoreCase(channelAdmin)){
				player.sendMessage(ChatColor.RED + "You must be the channel-admin to edit the channel");
				return;
			}
		}
		
		boolean changed = true;
		String loweredProperty = property.toLowerCase();
		
		if("format".equals(loweredProperty)){
			channelFormat.setFormat(newValue);
		}else
		if("color".equals(loweredProperty)){
			channelFormat.setColor(newValue);
		}else
		if("admin".equals(loweredProperty)){
			changed = changeAdmin(player, newValue);
		}else
		if("prefix".equals(loweredProperty)){
			channelFormat.setPrefix(newValue);
		}else
		if("suffix".equals(loweredProperty)){
			channelFormat.setSuffix(newValue);
		}else
		if("password".equals(loweredProperty)){
			if(channelLevel == ChannelLevel.PasswordChannel){
				channelPassword = newValue;
			}
		}else{
			changed = false;
		}
		
		if(changed){
			player.sendMessage(ChatColor.LIGHT_PURPLE + property + ChatColor.GREEN + " was changed to: " + ChatColor.LIGHT_PURPLE + newValue);
		}else{
			player.sendMessage(ChatColor.LIGHT_PURPLE + property + ChatColor.RED + " could not be found or your new Argument is invalid.");
			player.sendMessage(ChatColor.RED + "Valid properties are: " + ChatColor.LIGHT_PURPLE + "format, color, prefix, suffix, admin, password");
		}
	}
	
	/**
	 * Changes the Admin of a Channel to a new one.
	 * This is not possible in Password / Private / Public channels.
	 * 
	 * @param oldAdmin, that is removed
	 * @param newAdmin, that is the new Admin
	 * @return
	 */
	private boolean changeAdmin(Player oldAdmin, String newAdmin){
		if(channelLevel == ChannelLevel.PasswordChannel || 
			channelLevel == ChannelLevel.PrivateChannel || 
			channelLevel == ChannelLevel.PublicChannel)
			return false;
		
		boolean isFoundInList = false;
		for(String member : participants){
			if(member.equalsIgnoreCase(newAdmin)){
				isFoundInList = true;
				newAdmin = member;
			}
		}
		
		if(isFoundInList){
			this.sendMessageInChannel(null, "New Admin of this channel is: " + ChatColor.LIGHT_PURPLE + newAdmin, "");
			channelAdmin = newAdmin;
			return true;
		}
		
		return false;
	}

}