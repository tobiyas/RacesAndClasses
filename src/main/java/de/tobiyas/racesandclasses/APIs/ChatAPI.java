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
package de.tobiyas.racesandclasses.APIs;

import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.chat.channels.ChannelManager;
import de.tobiyas.racesandclasses.util.chat.ChannelLevel;

public class ChatAPI {

	//TODO do rest
	
	/**
	 * The Plugin to get the {@link ChannelManager} from.
	 */
	private static RacesAndClasses plugin = RacesAndClasses.getPlugin();
	
	
	/**
	 * Broadcasts a Message to the given Channel by name
	 * 
	 * @param message to send
	 * @param channelName to send to
	 */
	public static void postMessageToChannel(String message, String channelName){
		plugin.getChannelManager().broadcastMessageToChannel(channelName, null, message);
	}
	
	
	/**
	 * Registers a new Channel with the Channel name and
	 * the passed channelLevel
	 * 
	 * @param channelName
	 * @param channelLevel
	 */
	public static void createChannel(String channelName, ChannelLevel channelLevel){
		plugin.getChannelManager().registerChannel(channelLevel, channelName);
	}
	
	
	/**
	 * Adds a Player to a certain channel.
	 * 
	 * If notifyOthers is set to true, others in the channel will be notified to the player's join.
	 * 
	 * @param channelName
	 * @param playerUUID
	 */
	public static void addPlayerToChannel(String channelName, Player player, boolean notifyOthers){
		plugin.getChannelManager().joinChannel(player.getUniqueId(), channelName, "", notifyOthers);
	}
	
	
	/**
	 * Removes a Player from the channel name passed.
	 * 
	 * @param channelName
	 * @param playerUUID
	 */
	public static void removePlayerFromChannel(String channelName, Player player, boolean notifyOthers){
		plugin.getChannelManager().leaveChannel(player.getUniqueId(), channelName, notifyOthers);
	}
}
