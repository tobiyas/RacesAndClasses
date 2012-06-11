package de.tobiyas.races.chat.channels;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import de.tobiyas.races.Races;
import de.tobiyas.races.configuration.global.YAMLConfigExtended;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceManager;
import de.tobiyas.races.util.chat.ChannelLevel;
import de.tobiyas.races.util.consts.Consts;

public class ChannelManager {
	
	private static ChannelManager instance;
	private Races plugin;
	private HashMap<String, ChannelContainer> channels;
	private YAMLConfigExtended config;

	public ChannelManager(){
		plugin = Races.getPlugin();
		instance = this;
		channels = new HashMap<String, ChannelContainer>();
		config = new YAMLConfigExtended(Consts.channelsYML);
	}
	
	public void init(){
		channels.clear();
		createSTDChannels();
		loadChannelsFromFile();
	}
	
	private void createSTDChannels(){
		registerChannel(ChannelLevel.GlobalChannel, "Global");
		
		for(World world : Bukkit.getWorlds())
			registerChannel(ChannelLevel.WorldChannel, world.getName());
		
		for(String race : RaceManager.getManager().listAllRaces())
			registerChannel(ChannelLevel.RaceChannel, race);
	}
	
	private boolean registerChannel(ChannelLevel level, String channelName){
		ChannelContainer container = getContainer(channelName);
		if(container != null)
			return false;
		
		channels.put(channelName, new ChannelContainer(channelName, level));
		return true;
	}
	
	public boolean registerChannel(ChannelLevel level, String channelName, Player player){
		ChannelContainer container = getContainer(channelName);
		if(container != null){
			player.sendMessage(ChatColor.RED + "Channel: " + ChatColor.AQUA + container.getChannelName() + ChatColor.RED + " already exists.");
			return false;
		}
		
		container = new ChannelContainer(channelName, level);
		container.addPlayerToChannel(player.getName(), "", true);
		channels.put(channelName, container);
		return true;
	}
	
	public void registerChannel(ChannelLevel channelLevel, String channelName, String channelPassword, Player player) {
		boolean worked = registerChannel(channelLevel, channelName, player);
		if(worked){
			ChannelContainer container = getContainer(channelName);
			container.setPassword(channelPassword);
			player.sendMessage(ChatColor.GREEN + "The channel " + ChatColor.AQUA + channelName + 
								ChatColor.GREEN + " has been created successfully");
		}
	}
	
	private void createStructure(){
		File file = new File(Consts.channelsYML);
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				plugin.log("Could not create " + Consts.channelsYML);
				return;
			}
			config = new YAMLConfigExtended(Consts.channelsYML).load();
			
			if(!config.contains("channel"))
				config.createSection("channel");
			
			if(!config.contains("channel." + ChannelLevel.PasswordChannel.name()))
				config.createSection("channel." + ChannelLevel.PasswordChannel.name());
			
			if(!config.contains("channel." + ChannelLevel.PrivateChannel.name()))
				config.createSection("channel." + ChannelLevel.PrivateChannel.name());
			
			if(!config.contains("channel." + ChannelLevel.PublicChannel.name()))
				config.createSection("channel." + ChannelLevel.PublicChannel.name());
			
			config.save();
		}
	}
	
	private void loadChannelsFromFile(){
		createStructure();
		config.load();
		for(ChannelLevel level : ChannelLevel.values())
			for(String channelName : config.getYAMLChildren("channel." + level.name())){
				ChannelContainer container = ChannelContainer.constructFromYml(config, channelName, level);
				if(container != null)
					channels.put(channelName, container);
			}
	}
	
	public void saveChannels(){
		for(String channelName : channels.keySet()){
			ChannelContainer container = getContainer(channelName);
			container.saveChannel(config);
		}
	}
	
	public boolean broadcastMessageToChannel(String channel, Player player,
			String message) {
		
		ChannelContainer container = getContainer(channel);
		if(container == null){
			if(player != null)
				player.sendMessage(ChatColor.RED + "Channel " + ChatColor.AQUA + channel + ChatColor.RED + " was not found.");
			return false;
		}
		
		container.sendMessageInChannel(player, message);
		return true;		
	}
	
	public ArrayList<String> listAllChannels(){
		ArrayList<String> channelList = new ArrayList<String>();
		for(String channel : channels.keySet())
			channelList.add(channel);
		return channelList;
	}
	
	public ArrayList<String> listAllPublicChannels() {
		ArrayList<String> channelList = new ArrayList<String>();
		for(String channel : channels.keySet()){
			ChannelContainer container = getContainer(channel);
			ChannelLevel level = container.getChannelLevel();
			if(level == ChannelLevel.PasswordChannel || level == ChannelLevel.PrivateChannel)
				continue;
			
			channelList.add(channel);
		}
		return channelList;
	}
	
	public void postChannelInfo(Player player, String channel) {
		ChannelContainer container = getContainer(channel);
		if(container == null){
			player.sendMessage(ChatColor.RED + "Channel " + ChatColor.AQUA + channel + ChatColor.RED + " could not be found.");
			return;
		}
		
		container.postInfo(player);
	}
	
	public ChannelLevel getChannelLevel(String channel){
		for(String containerName : channels.keySet()){
			ChannelContainer container = getContainer(containerName);
			if(container.getChannelName().equalsIgnoreCase(channel))
				return container.getChannelLevel();
		}
		
		return ChannelLevel.NONE;
	}
	
	public void joinChannel(Player player, String channelName, String password, boolean notify) {
		ChannelContainer container = getContainer(channelName);
		if(container == null)
			return;
		
		container.addPlayerToChannel(player.getName(), password, notify);	
	}
	
	private ChannelContainer getContainer(String channelName){
		for(String name : channels.keySet())
			if(name.equalsIgnoreCase(channelName))
				return channels.get(name);
		return null;
	}
	
	public void leaveChannel(Player player, String channelName, boolean notify) {
		ChannelContainer container = getContainer(channelName);
		if(container == null)
			return;
		
		container.removePlayerFromChannel(player.getName(), notify);
	}
	
	public boolean isMember(String playerName, String changeTo) {
		ChannelContainer container = getContainer(changeTo);
		if(container == null)
			return false;
		
		return container.isMember(playerName);
	}	
	
	public static ChannelManager GetInstance(){
		return instance;
	}

	public void removeChannel(ChannelContainer channelContainer) {
		String channelName = channelContainer.getChannelName();
		config.load();
		config.set("channel." + channelContainer.getChannelLevel() + "." + channelName, 1);
		config.set("channel." + channelContainer.getChannelLevel() + "." + channelName, null);
		config.save();
		channels.remove(channelName);		
	}
	
	public void playerLogin(Player player){
		this.joinChannel(player, "Global", "", true);
		this.joinChannel(player, player.getWorld().getName(), "", false);
		
		RaceContainer container = RaceManager.getManager().getRaceOfPlayer(player.getName());
		if(container != null)
			this.joinChannel(player, container.getName(), "", false);
	}

	public void playerQuit(Player player) {
		this.leaveChannel(player, "Global", true);
		this.leaveChannel(player, player.getWorld().getName(), false);
		
		RaceContainer container = RaceManager.getManager().getRaceOfPlayer(player.getName());
		if(container != null)
			this.leaveChannel(player, container.getName(), false);
	}

	public void playerChangedWorld(World oldWorld, Player player) {
		World newWorld = player.getWorld();
		leaveChannel(player, oldWorld.getName(), true);
		joinChannel(player, newWorld.getName(), "", true);
	}
	
	public void playerChangeRace(String oldRace, Player player){
		RaceContainer container = RaceManager.getManager().getRaceOfPlayer(player.getName());
		String newRace = container.getName();
		
		if(!oldRace.equals(""))
			leaveChannel(player, oldRace, true);
		joinChannel(player, newRace, "", true);
	}
}
