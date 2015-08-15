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
package de.tobiyas.racesandclasses.persistence.file;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import de.tobiyas.racesandclasses.chat.channels.container.ChannelSaveContainer;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.PlayerHolderAssociation;
import de.tobiyas.racesandclasses.persistence.PersistenceStorage;
import de.tobiyas.racesandclasses.playermanagement.PlayerSavingContainer;
import de.tobiyas.racesandclasses.util.chat.ChannelLevel;
import de.tobiyas.util.config.YAMLConfigExtended;

public class YAMLPersistenceStorage implements PersistenceStorage {
	
	///////////////////////////////////////////////
	/////Constants START
	
	/**
	 * The path to the player invisible part
	 */
	protected final static String DEFAULT_VISIBLE_PATH = "visible";
	
	/**
	 * The path to the value
	 */
	protected final static String DEFAULT_VALUE_PATH = "val";
	
	/**
	 * The path to the value
	 */
	protected final static String DEFAULT_DEFAULTVALUE_PATH = "defval";
	
	/**
	 * The path to the value
	 */
	protected final static String DEFAULT_FORMAT_PATH = "format";
	
	/**
	 * path to nice human readable name
	 */
	protected final static String DEFAULT_DISPLAY_NAME_PATH = "name";
	
	
	
	////Constants END
	//////////////////////////////////////////////
	
	
	

	@Override
	public void initForStartup() {
	}

	@Override
	public void shutDown() {
	}

	@Override
	public boolean savePlayerSavingContainer(PlayerSavingContainer container) {
		UUID playerUUID = container.getPlayerUUID();
		boolean hasGod = container.isHasGod();
		
		int level = container.getPlayerLevel();
		int expOfLevel = container.getPlayerLevelExp();
		
		YAMLConfigExtended config = YAMLPersistenceProvider.getLoadedPlayerFile(playerUUID);
		config.set("hasGod", hasGod);		
		config.set("level.currentLevel", level);		
		config.set("level.currentLevelEXP", expOfLevel);		
		
		return true;
	}

	@Override
	public boolean savePlayerHolderAssociation(PlayerHolderAssociation container) {
		String race = container.getRaceName();
		String clazz = container.getClassName();
		
		YAMLConfigExtended config = YAMLPersistenceProvider.getLoadedPlayerFile(container.getPlayerUUID());
		config.set("race", race);
		config.set("class", clazz);
		
		return true;
	}


	@SuppressWarnings("deprecation")
	@Override
	public boolean saveChannelSaveContainer(ChannelSaveContainer container) {
		String channelName = container.getChannelName();
		String channelPre = "channel." + container.getChannelLevel().name() + "." + channelName;
		
		YAMLConfigExtended config = YAMLPersistenceProvider.getLoadedChannelsFile(true);
		
		config.set(channelPre + ".prefix" , container.getPrefix());
		config.set(channelPre + ".suffix" , container.getSuffix());
		config.set(channelPre + ".channelColor" , container.getChannelColor());
		config.set(channelPre + ".members" , container.getParticipants());
		config.set(channelPre + ".channelFormat", container.getChannelFormat());
		config.set(channelPre + ".channelPassword", container.getChannelPassword());
		config.set(channelPre + ".channelAdmin", container.getChannelAdmin());

		
		config.set(channelPre + ".banned", container.getBannedMap());
		config.set(channelPre + ".muted", container.getMutedMap());
		
		return true;
	}

	@Override
	public PlayerSavingContainer getPlayerContainer(RaCPlayer player) {
		PlayerSavingContainer container = new PlayerSavingContainer();
		YAMLConfigExtended config = YAMLPersistenceProvider.getLoadedPlayerFile(player);
		
		int level = config.getInt("level.currentLevel", 1);
		int expOfLevel = config.getInt("level.currentLevelEXP", 0);
		boolean hasGod = config.getBoolean("hasGod", false);
		
		container.setPlayerUUID(player.getUniqueId());
		container.setPlayerLevel(level);
		container.setPlayerLevelExp(expOfLevel);
		container.setHasGod(hasGod);
		
		return container;
	}

	@Override
	public PlayerHolderAssociation getPlayerHolderAssociation(RaCPlayer player) {
		PlayerHolderAssociation container = new PlayerHolderAssociation();
		container.setPlayerUUID(player.getUniqueId());
		
		YAMLConfigExtended config = YAMLPersistenceProvider.getLoadedPlayerFile(player);
		String race = config.getString("race");
		String clazz = config.getString("class");

		container.setRaceName(race);
		container.setClassName(clazz);
		
		return container;
	}

	@SuppressWarnings("deprecation")
	@Override
	public ChannelSaveContainer getChannelSaveContainer(String channelName, String channelLevel) {
		ChannelSaveContainer container = new ChannelSaveContainer();
		container.setChannelName(channelName);
		container.setChannelLevel(ChannelLevel.valueOf(channelLevel));
		
		String channelPre = "channel." + channelLevel + "." + channelName;
		YAMLConfigExtended config = YAMLPersistenceProvider.getLoadedChannelsFile(true);
		
		container.setPrefix(config.getString(channelPre + ".prefix", ""));
		container.setSuffix(config.getString(channelPre + ".suffix", ""));
		container.setChannelColor(config.getString(channelPre + ".channelColor", ""));
		container.setParticipants(config.getString(channelPre + ".members", ""));
		container.setChannelFormat(config.getString(channelPre + ".channelFormat", ""));
		container.setChannelPassword(config.getString(channelPre + ".channelPassword", ""));
		container.setChannelAdmin(config.getString(channelPre + ".channelAdmin", ""));

		
		container.setBannedMap(config.getString(channelPre + ".banned", ""));
		container.setMutedMap(config.getString(channelPre + ".muted", ""));
		
		return container;
	}

	@Override
	public List<PlayerHolderAssociation> getAllPlayerHolderAssociationsForHolder(
			String holderName) {
		Set<RaCPlayer> allPlayers = YAMLPersistenceProvider.getAllPlayersKnown();
		
		List<PlayerHolderAssociation> containers = new LinkedList<PlayerHolderAssociation>();
		for(RaCPlayer player : allPlayers){
			PlayerHolderAssociation container = getPlayerHolderAssociation(player);
			if(holderName.equals(container.getRaceName())
					|| holderName.equals(container.getClassName())){
				containers.add(container);
			}
		}
		
		return containers;
	}

	@Override
	public List<ChannelSaveContainer> getAllChannelSaves() {
		YAMLConfigExtended config = YAMLPersistenceProvider.getLoadedChannelsFile(true);
		Set<String> levels = config.getChildren("channel");
		
		List<ChannelSaveContainer> containers = new LinkedList<ChannelSaveContainer>();
		for(String level : levels){
			Set<String> channelNames = config.getChildren("channel." + level);
			for(String channelName : channelNames){
				containers.add(getChannelSaveContainer(channelName, level));
			}
		}
		
		return containers;
	}

	@Override
	public List<PlayerSavingContainer> getAllPlayerSavingContainers() {
		Set<RaCPlayer> allPlayers = YAMLPersistenceProvider.getAllPlayersKnown();
		
		List<PlayerSavingContainer> containers = new LinkedList<PlayerSavingContainer>();
		for(RaCPlayer player : allPlayers){
			PlayerSavingContainer container = getPlayerContainer(player);
			containers.add(container);
		}
		
		return containers;
	}

	@Override
	public String getNameRepresentation() {
		return "YML Storage";
	}

	@Override
	public void removePlayerHolderAssociation(PlayerHolderAssociation object) {
		UUID playerUUID = object.getPlayerUUID();
		YAMLConfigExtended config = YAMLPersistenceProvider.getLoadedPlayerFile(playerUUID);
		config.set("race", null);
		config.set("class", null);
	}

	@Override
	public void removePlayerSavingContainer(PlayerSavingContainer container) {
		UUID playerUUID = container.getPlayerUUID();
		YAMLConfigExtended config = YAMLPersistenceProvider.getLoadedPlayerFile(playerUUID);
		
		config.set("level", null);
		config.set("hasGod", null);
	}

}
