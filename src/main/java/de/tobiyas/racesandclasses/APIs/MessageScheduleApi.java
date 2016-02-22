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

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.util.player.PlayerUtils;

public class MessageScheduleApi {

	
	/**
	 * Sends a Message to a Player in X seconds.
	 * 
	 * @param player the player to send to
	 * @param timeInSeconds in which time to send
	 * @param message the message to send
	 */
	public static void scheduleMessageToPlayer(final String playerName, final int timeInSeconds, final String message){
		RacesAndClasses plugin = RacesAndClasses.getPlugin();
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				Player player = PlayerUtils.getPlayer(playerName);
				if(player != null && player.isOnline()){
					player.sendMessage(message);
				}
			}
		}, timeInSeconds * 20);
	}
	
	/**
	 * Sends a Message to a Player in X seconds.
	 * 
	 * @param player the player to send to
	 * @param timeInSeconds in which time to send
	 * @param tag to be translated
	 */
	public static void scheduleTranslateMessageToPlayer(final String playerName, final int timeInSeconds, final String tag){
		RacesAndClasses plugin = RacesAndClasses.getPlugin();
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				Player player = PlayerUtils.getPlayer(playerName);
				if(player != null && player.isOnline()){
					LanguageAPI.sendTranslatedMessage(player, tag);
				}
			}
		}, timeInSeconds * 20);
	}

	/**
	 * Sends a Message to a Player in X seconds.
	 * 
	 * @param player the player to send to
	 * @param timeInSeconds in which time to send
	 * @param tag to be translated
	 * @param replacements that should be made
	 */
	public static void scheduleTranslateMessageToPlayer(final String playerName, final int timeInSeconds, final String tag, final String... replacements ){
		RacesAndClasses plugin = RacesAndClasses.getPlugin();
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				Player player = PlayerUtils.getPlayer(playerName);
				if(player != null && player.isOnline()){
					LanguageAPI.sendTranslatedMessage(player, tag, replacements);
				}
			}
		}, timeInSeconds * 20);
	}
	
	
	/**
	 * Sends a Message to a Player in X seconds.
	 * 
	 * @param player the player to send to
	 * @param timeInSeconds in which time to send
	 * @param message the message to send
	 */
	public static void scheduleMessageToPlayer(final RaCPlayer racPlayer, final int timeInSeconds, final String message){
		RacesAndClasses plugin = RacesAndClasses.getPlugin();
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				Player player = racPlayer.getPlayer();
				if(player != null && player.isOnline()){
					player.sendMessage(message);
				}
			}
		}, timeInSeconds * 20);
	}
	
	/**
	 * Sends a Message to a Player in X seconds.
	 * 
	 * @param player the player to send to
	 * @param timeInSeconds in which time to send
	 * @param tag to be translated
	 */
	public static void scheduleTranslateMessageToPlayer(final RaCPlayer racPlayer, final int timeInSeconds, final String tag){
		scheduleTranslateMessageToPlayer(racPlayer, timeInSeconds, tag);
	}
	
	/**
	 * Sends a Message to a Player in X seconds.
	 * 
	 * @param player the player to send to
	 * @param timeInSeconds in which time to send
	 * @param tag to be translated
	 * @param replacements that should be made
	 */
	public static void scheduleTranslateMessageToPlayer(final RaCPlayer racPlayer, final int timeInSeconds, final String tag, final String... replacements ){
		RacesAndClasses plugin = RacesAndClasses.getPlugin();
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				Player player = racPlayer.getPlayer();
				if(player != null && player.isOnline()){
					LanguageAPI.sendTranslatedMessage(player, tag, replacements);
				}
			}
		}, timeInSeconds * 20);
	}

}
