package de.tobiyas.racesandclasses.APIs;

import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.chat.channels.ChannelManager;
import de.tobiyas.racesandclasses.util.chat.ChannelLevel;

public class ChatAPI {

	//TODO do rest
	
	/**
	 * Broadcasts a Message to the given Channel by name
	 * 
	 * @param message to send
	 * @param channelName to send to
	 */
	public static void postMessageToChannel(String message, String channelName){
		ChannelManager.GetInstance().broadcastMessageToChannel(channelName, null, message);
	}
	
	
	/**
	 * Registers a new Channel with the Channel name and
	 * the passed channelLevel
	 * 
	 * @param channelName
	 * @param channelLevel
	 */
	public static void createChannel(String channelName, ChannelLevel channelLevel){
		ChannelManager.GetInstance().registerChannel(channelLevel, channelName);
	}
	
	
	/**
	 * Adds a Player to a certain channel.
	 * 
	 * If notifyOthers is set to true, others in the channel will be notified to the player's join.
	 * 
	 * @param channelName
	 * @param playerName
	 */
	public static void addPlayerToChannel(String channelName, Player player, boolean notifyOthers){
		ChannelManager.GetInstance().joinChannel(player, channelName, "", notifyOthers);
	}
	
	
	/**
	 * Removes a Player from the channel name passed.
	 * 
	 * @param channelName
	 * @param playerName
	 */
	public static void removePlayerFromChannel(String channelName, Player player, boolean notifyOthers){
		ChannelManager.GetInstance().leaveChannel(player, channelName, notifyOthers);
	}
}
