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
package de.tobiyas.racesandclasses.persistence;

import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationOptions;

import de.tobiyas.racesandclasses.chat.channels.container.ChannelSaveContainer;
import de.tobiyas.racesandclasses.configuration.member.database.DBConfigOption;
import de.tobiyas.racesandclasses.configuration.member.file.ConfigOption;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.PlayerHolderAssociation;
import de.tobiyas.racesandclasses.playermanagement.PlayerSavingContainer;

public interface PersistenceStorage {


	/**
	 * Inits the System if needed.
	 */
	public void initForStartup();
	
	
	/**
	 * Shuts the system down if needed
	 */
	public void shutDown();
	
	
	/**
	 * Saves a {@link PlayerSavingContainer} Persistent
	 * 
	 * @param container to save
	 * @return true if worked, false otherwise
	 */
	public boolean savePlayerSavingContainer(PlayerSavingContainer container);

	
	/**
	 * Saves a PlayerHolderAssociation Persistent
	 * 
	 * @param container to save
	 * @return true if worked, false otherwise
	 */
	public boolean savePlayerHolderAssociation(PlayerHolderAssociation container);

	/**
	 * Saves a {@link DBConfigOption} Persistent
	 * 
	 * @param container to save
	 * @param forces save even if the container sais it is saved.
	 * 
	 * @return true if worked, false otherwise
	 */
	public boolean savePlayerMemberConfigEntry(ConfigOption container, boolean forceSave);

	/**
	 * Saves a {@link ChannelSaveContainer} Persistent
	 * 
	 * @param container to save
	 * @return true if worked, false otherwise
	 */
	public boolean saveChannelSaveContainer(ChannelSaveContainer container);
	
	
	/**
	 * Retrieves and builds a {@link PlayerSavingContainer} from the Storage
	 * 
	 * @param name the PlayerName to search for
	 * @return the found {@link PlayerSavingContainer} or NULL if not found.
	 */
	public PlayerSavingContainer getPlayerContainer(UUID playerUUID);
	
	
	/**
	 * Returns the {@link PlayerHolderAssociation} from the Storage
	 * 
	 * @param name the playerUUID to search for
	 * @return the found {@link PlayerHolderAssociation} or NULL if not found.
	 */
	public PlayerHolderAssociation getPlayerHolderAssociation(UUID playerUUID);
	
	
	/**
	 * Returns the {@link ConfigOption} for the Player and the given entry Path
	 * 
	 * @param playerUUID to search for
	 * @param entryPath to search for
	 * @return the found Entry or NULL if not found.
	 */
	public ConfigOption getPlayerMemberConfigEntryByPath(UUID playerUUID, String entryPath);
	
	/**
	 * Returns the {@link ConfigOption} for the Player and the given entry Name
	 * 
	 * @param playerUUID to search for
	 * @param entryName to search for
	 * @return the found Entry or NULL if not found.
	 */
	public ConfigOption getPlayerMemberConfigEntryByName(UUID playerUUID, String entryName);
	
	/**
	 * Retrieves the ChannelSaveContainer for a Channel Name.
	 * 
	 * @param channelName to search for
	 * @param channelLevel to search for
	 * 
	 * @return the found ChannelSaveContainer or NULL if not found.
	 */
	public ChannelSaveContainer getChannelSaveContainer(String channelName, String channelLevel);
	
	
	/**
	 * Returns all {@link ConfigurationOptions} a Player contains.
	 * If none where found, an empty list is returned.
	 * 
	 * @param playerUUID to search for
	 * @return an List of all Entries for a Player.
	 */
	public List<ConfigOption> getAllConfigOptionsOfPlayer(UUID playerUUID);
	
	/**
	 * Returns all PlayerHolderAssociations found for a HolderName.
	 * 
	 * @return a List of all HolderAssociations found for a Holder.
	 */
	public List<PlayerHolderAssociation> getAllPlayerHolderAssociationsForHolder(String holderName);
	
	/**
	 * Returns a List of all Channel Savings.
	 * 
	 * @return a list of all Channel Savings
	 */
	public List<ChannelSaveContainer> getAllChannelSaves();
	
	/**
	 * Returns a List of all {@link PlayerSavingContainer}
	 * 
	 * @return a list of all PlayerSavingContainers
	 */
	public List<PlayerSavingContainer> getAllPlayerSavingContainers();
	
	
	/**
	 * Returns the Representation Name of this storage
	 *
	 * @return the canonical Name of the Representation
	 */
	public String getNameRepresentation();


	/**
	 * Removes the Player association.
	 * 
	 * @param object to remove
	 */
	public void removePlayerHolderAssociation(PlayerHolderAssociation object);


	/**
	 * Removes the Player Saving Container.
	 * 
	 * @param container to remove
	 */
	public void removePlayerSavingContainer(PlayerSavingContainer container);
}
