package de.tobiyas.racesandclasses.chat.channels;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.chat.channels.container.ChannelContainer;
import de.tobiyas.racesandclasses.chat.channels.container.ChannelTicker;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.util.chat.ChannelLevel;
import de.tobiyas.racesandclasses.util.consts.Consts;
import de.tobiyas.util.config.YAMLConfigExtended;

public class ChannelManager {
	
	/**
	 * singleton instance
	 */
	protected static ChannelManager instance;
	
	/**
	 * plugin main
	 */
	private RacesAndClasses plugin;
	
	/**
	 * All channels saved as Map String -> {@link ChannelContainer}
	 */
	private Map<String, ChannelContainer> channels;
	
	/**
	 * The Config to save the channel settings in
	 */
	private YAMLConfigExtended config;

	/**
	 * Creates the ChannelManager
	 */
	public ChannelManager(){
		plugin = RacesAndClasses.getPlugin();
		instance = this;
		channels = new HashMap<String, ChannelContainer>();
		config = new YAMLConfigExtended(Consts.channelsYML);
	}
	
	/**
	 * Inits the Channel system.
	 * It creates all Channels + the ticker for timings.
	 */
	public void init(){
		channels.clear();
		ChannelTicker.init();
		loadChannelsFromFile();
		createSTDChannels();
	}
	
	/**
	 * Creates all channels given by default (races / local / global / tutorials)
	 */
	private void createSTDChannels(){
		registerChannel(ChannelLevel.GlobalChannel, "Global");
		registerChannel(ChannelLevel.LocalChannel, "Local");
		registerChannel(ChannelLevel.GlobalChannel, "Tutorial");
		
		for(World world : Bukkit.getWorlds())
			registerChannel(ChannelLevel.WorldChannel, world.getName());
		
		for(String race : plugin.getRaceManager().listAllVisibleHolders()){
			if(race.equalsIgnoreCase(Consts.defaultRace)) continue;
			registerChannel(ChannelLevel.RaceChannel, race);
		}
	}
	
	/**
	 * Registers a new Channel with the ChannelLevel passed
	 * 
	 * @param level to create with
	 * @param channelName to create with
	 * @return the {@link ChannelContainer} created, null otherwise
	 */
	public ChannelContainer registerChannel(ChannelLevel level, String channelName){
		ChannelContainer container = getContainer(channelName);
		if(container != null){
			return null;
		}
		
		channels.put(channelName, new ChannelContainer(channelName, level));
		return container;
	}
	
	/**
	 * Registers a Channel with a player in it.
	 * The player is notified to errors.
	 * 
	 * @param level to register
	 * @param channelName to register
	 * @param player to pass into the channel + send errors if failed
	 * 
	 * @return true if worked, false otherwise
	 */
	public boolean registerChannel(ChannelLevel level, String channelName, Player player){
		ChannelContainer container = registerChannel(level, channelName);
		if(container == null){
			if(player != null){
				player.sendMessage(ChatColor.RED + "Channel: " + ChatColor.AQUA + channelName + ChatColor.RED + " already exists.");
			}
			return false;
		}
		
		container.addPlayerToChannel(player.getName(), "", true);
		channels.put(channelName, container);
		return true;
	}
	
	/**
	 * Registers a Channel with a player in it.
	 * The player is notified to errors.
	 * 
	 * @param channelLevel
	 * @param channelName
	 * @param channelPassword
	 * @param player
	 */
	public void registerChannel(ChannelLevel channelLevel, String channelName, String channelPassword, Player player) {
		boolean worked = registerChannel(channelLevel, channelName, player);
		if(worked){
			ChannelContainer container = getContainer(channelName);
			container.setAdmin(player.getName());
			container.setPassword(channelPassword);
			player.sendMessage(ChatColor.GREEN + "The channel " + ChatColor.AQUA + channelName + 
								ChatColor.GREEN + " has been created successfully");
		}
	}
	
	/**
	 * Creates an empty Channel file if none exists
	 */
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
	
	/**
	 * Loads all Channels from the Channel file
	 */
	private void loadChannelsFromFile(){
		createStructure();
		config.load();
		for(ChannelLevel level : ChannelLevel.values())
			for(String channelName : config.getChildren("channel." + level.name())){
				ChannelContainer container = ChannelContainer.constructFromYml(config, channelName, level);
				if(container != null)
					channels.put(channelName, container);
			}
	}
	
	/**
	 * Saves all channels to Channel file.
	 * Note: Channels can save themselves
	 */
	public void saveChannels(){
		for(String channelName : channels.keySet()){
			ChannelContainer container = getContainer(channelName);
			container.saveChannel(config);
		}
	}
	
	/**
	 * Sends an Broadcast message to the channel.
	 * This can be sent from an Player or from the Console (player = null)
	 * 
	 * @param channel
	 * @param player
	 * @param message
	 * 
	 * @return true if worked, false if channel not found
	 */
	public boolean broadcastMessageToChannel(String channel, Player player,
			String message) {
		
		ChannelContainer container = getContainer(channel);
		if(container == null){
			if(player != null)
				player.sendMessage(ChatColor.RED + "Channel " + ChatColor.AQUA + channel + ChatColor.RED + " was not found.");
			return false;
		}
		
		container.sendMessageInChannel(player, message, "");
		return true;		
	}
	
	/**
	 * Returns a list of all Channel names present
	 * 
	 * @return
	 */
	public ArrayList<String> listAllChannels(){
		ArrayList<String> channelList = new ArrayList<String>();
		for(String channel : channels.keySet())
			channelList.add(channel);
		return channelList;
	}
	
	/**
	 * Returns a list of all public Channel names present
	 * 
	 * @return
	 */
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
	
	/**
	 * Posts the channel info to a player.
	 * 
	 * @param player
	 * @param channel
	 */
	public void postChannelInfo(Player player, String channel) {
		ChannelContainer container = getContainer(channel);
		if(container == null){
			player.sendMessage(ChatColor.RED + "Channel " + ChatColor.AQUA + channel + ChatColor.RED + " could not be found.");
			return;
		}
		
		container.postInfo(player);
	}
	
	/**
	 * Returns the Level of a channel according to it's name.
	 * Returns {@link ChannelLevel#NONE} if the channel was not found.
	 * 
	 * @param channel
	 * @return
	 */
	public ChannelLevel getChannelLevel(String channel){
		for(String containerName : channels.keySet()){
			ChannelContainer container = getContainer(containerName);
			if(container.getChannelName().equalsIgnoreCase(channel))
				return container.getChannelLevel();
		}
		
		return ChannelLevel.NONE;
	}
	
	/**
	 * Joins the player to the channel.
	 * If a password is required, it is checked here.
	 * <br>
	 * If notify flag is set, other players in the channel will be informed about this.
	 * 
	 * @param player to join
	 * @param channelName to join
	 * @param password needed for the channel (if it has one, ignored otherwise)
	 * @param notify if the player join should be notified.
	 */
	public void joinChannel(Player player, String channelName, String password, boolean notify) {
		ChannelContainer container = getContainer(channelName);
		if(container == null)
			return;
		
		container.addPlayerToChannel(player.getName(), password, notify);	
	}
	
	/**
	 * passes the ChannelContainer associated with a channel Name.
	 * 
	 * @param channelName
	 * @return
	 */
	private ChannelContainer getContainer(String channelName){
		for(String name : channels.keySet())
			if(name.equalsIgnoreCase(channelName))
				return channels.get(name);
		return null;
	}
	
	/**
	 * Removes a player from a channel.
	 * <br>
	 * If the notify flag is set, other players in the channel will be notified.
	 * 
	 * @param player
	 * @param channelName
	 * @param notify
	 */
	public void leaveChannel(Player player, String channelName, boolean notify) {
		ChannelContainer container = getContainer(channelName);
		if(container == null)
			return;
		
		container.removePlayerFromChannel(player.getName(), notify);
	}
	
	/**
	 * Checks if a Player (as name) is present in a channel.
	 * 
	 * @param playerName
	 * @param channelName
	 * @return if is present, false otherwise
	 */
	public boolean isMember(String playerName, String channelName) {
		ChannelContainer container = getContainer(channelName);
		if(container == null)
			return false;
		
		if(container.getChannelLevel() == ChannelLevel.LocalChannel)
			return true;
		
		return container.isMember(playerName);
	}
	
	/**
	 * Mutes a Player in a channel.
	 * The time passed is the time in Seconds the player is muted.
	 * <br>
	 * NOTE: The muting player needs Admin previleges in the channel.
	 * 
	 * @param admin
	 * @param mutedName
	 * @param channelName
	 * @param time
	 */
	public void mutePlayer(Player admin, String mutedName, String channelName, int time){
		ChannelContainer container = getContainer(channelName);
		if(container == null){
			admin.sendMessage(ChatColor.RED + "Channel not found.");
			return;
		}
		
		if(!container.checkPermissionMute(admin)){
			admin.sendMessage(ChatColor.RED + "You don't have the permission to do this.");
			return;
		}
		
		if(!container.isMember(mutedName)){
			admin.sendMessage(ChatColor.RED + "There is no player with this name in this channel.");
			return;
		}
		
		if(container.isMuted(mutedName)){
			admin.sendMessage(ChatColor.RED + "The Player is already muted.");
			return;
		}
		
		container.mutePlayer(mutedName, time);
		broadcastMessageToChannel(channelName, null, " Player: " + mutedName + " got muted by: " + admin.getDisplayName());
	}
	
	/**
	 * Unmutes a player in a channel.
	 * <br>
	 * Note: The unmuteing player needs Admin privileges in this channel. 
	 * 
	 * @param admin
	 * @param unmutedName
	 * @param channelName
	 */
	public void unmutePlayer(Player admin, String unmutedName, String channelName){
		ChannelContainer container = getContainer(channelName);
		if(container == null){
			admin.sendMessage(ChatColor.RED + "Channel not found.");
			return;
		}
		
		if(!container.checkPermissionUnmute(admin)){
			admin.sendMessage(ChatColor.RED + "You don't have the permission to do this.");
			return;
		}
		
		if(!container.isMember(unmutedName)){
			admin.sendMessage(ChatColor.RED + "There is no player with this name in this channel.");
			return;
		}
		
		if(!container.isMuted(unmutedName)){
			admin.sendMessage(ChatColor.RED + "No player with this name is muted.");
			return;
		}
		
		container.unmutePlayer(unmutedName);
		broadcastMessageToChannel(channelName, null, " Player: " + unmutedName + " got unmuted by: " + admin.getDisplayName());
	}
	
	/**
	 * Bans a Player in a channel.
	 * The time passed is the time in Seconds the player is banned.
	 * <br>
	 * NOTE: The banning player needs Admin previleges in the channel.
	 * 
	 * @param admin
	 * @param banName
	 * @param channelName
	 * @param time
	 */
	public void banPlayer(Player admin, String banName, String channelName, int time){
		ChannelContainer container = getContainer(channelName);
		if(container == null){
			admin.sendMessage(ChatColor.RED + "Channel not found.");
			return;
		}
		
		if(!container.checkPermissionBann(admin)){
			admin.sendMessage(ChatColor.RED + "You don't have the permission to do this.");
			return;
		}
		
		if(!container.isMember(banName)){
			admin.sendMessage(ChatColor.RED + "There is no player with this name in this channel.");
			return;
		}
		
		if(container.isBanned(banName)){
			admin.sendMessage(ChatColor.RED + "This Player is already banned from the channel.");
			return;
		}
		
		broadcastMessageToChannel(channelName, null, " Player: " + banName + " got baned from channel by: " + admin.getDisplayName());
		container.banAndRemovePlayer(banName, time);
	}
	
	/**
	 * Unbans a player in a channel.
	 * <br>
	 * Note: The unbanning player needs Admin privileges in this channel. 
	 * 
	 * @param admin
	 * @param unbanName
	 * @param channelName
	 */
	public void unbanPlayer(Player admin, String unbanName, String channelName){
		ChannelContainer container = getContainer(channelName);
		if(container == null){
			admin.sendMessage(ChatColor.RED + "Channel not found.");
			return;
		}
		
		if(!container.checkPermissionMute(admin)){
			admin.sendMessage(ChatColor.RED + "You don't have the permission to do this.");
			return;
		}
		
		if(!container.isBanned(unbanName)){
			admin.sendMessage(ChatColor.RED + "No player with this name is banned.");
			return;
		}
		
		container.unbanPlayer(unbanName);
		//broadcastMessageToChannel(channelName, null, " Player: " + unbanName + " got unbaned by: " + admin.getDisplayName());
		admin.sendMessage(ChatColor.GREEN + "Player: " + ChatColor.LIGHT_PURPLE +  unbanName + ChatColor.GREEN +
							" got unbanned from channel: " + ChatColor.LIGHT_PURPLE + channelName);
		
		Player player = Bukkit.getPlayer(unbanName);
		if(player != null)
			player.sendMessage(ChatColor.GREEN + "You got unbanned from channel: " + ChatColor.LIGHT_PURPLE + channelName);
	}
	
	/**
	 * Removes a channel from the Channel list.
	 * 
	 * @param channelContainer
	 */
	public void removeChannel(ChannelContainer channelContainer) {
		ChannelTicker.unregisterChannel(channelContainer);
		String channelName = channelContainer.getChannelName();
		
		config.load();
		config.set("channel." + channelContainer.getChannelLevel() + "." + channelName, 1);
		config.set("channel." + channelContainer.getChannelLevel() + "." + channelName, null);
		config.save();
		channels.remove(channelName);
	}
	
	/**
	 * Notifies all players that a player has joined
	 * 
	 * @param player
	 */
	public void playerLogin(Player player){
		this.joinChannel(player, "Global", "", true);
		this.joinChannel(player, player.getWorld().getName(), "", false);
		
		AbstractTraitHolder container = plugin.getRaceManager().getHolderOfPlayer(player.getName());
		if(container != null){
			this.joinChannel(player, container.getName(), "", false);
		}
	}

	/**
	 * Notifies all players that a player has left
	 * 
	 * @param player
	 */
	public void playerQuit(Player player) {
		this.leaveChannel(player, "Global", true);
		this.leaveChannel(player, player.getWorld().getName(), false);
		
		AbstractTraitHolder container = plugin.getRaceManager().getHolderOfPlayer(player.getName());
		if(container != null){
			this.leaveChannel(player, container.getName(), false);
		}
	}

	/**
	 * Changes the world channels for a player
	 * 
	 * @param oldWorld
	 * @param player
	 */
	public void playerChangedWorld(World oldWorld, Player player) {
		World newWorld = player.getWorld();
		leaveChannel(player, oldWorld.getName(), true);
		joinChannel(player, newWorld.getName(), "", true);
	}
	
	/**
	 * Changes the race channel if a player changes his race
	 * 
	 * @param oldRace
	 * @param player
	 */
	public void playerChangeRace(String oldRace, Player player){
		AbstractTraitHolder container = plugin.getRaceManager().getHolderOfPlayer(player.getName());
		String newRace = container.getName();
		
		if(!oldRace.equals(""))
			leaveChannel(player, oldRace, true);
		joinChannel(player, newRace, "", true);
	}

	/**
	 * Edits the properties of a channel.
	 * <br>
	 * Only specific properties are present.
	 * Look at {@link ChannelContainer} to see the properties.
	 * 
	 * @param player
	 * @param channel
	 * @param property
	 * @param newValue
	 */
	public void editChannel(Player player, String channel, String property,
			String newValue) {
		ChannelContainer container = getContainer(channel);
		if(container == null)
			return;
		
		container.editChannel(player, property, newValue);
	}
	
	/**
	 * Retrieves the Singleton instance of the ChannelManager
	 * @return
	 */
	public static ChannelManager GetInstance(){
		return instance;
	}

}