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
package de.tobiyas.racesandclasses.persistence.db;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.persistence.PersistenceException;

import com.avaje.ebean.EbeanServer;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.chat.channels.container.ChannelSaveContainer;
import de.tobiyas.racesandclasses.configuration.member.database.DBConfigOption;
import de.tobiyas.racesandclasses.configuration.member.file.ConfigOption;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.PlayerHolderAssociation;
import de.tobiyas.racesandclasses.persistence.PersistenceStorage;
import de.tobiyas.racesandclasses.playermanagement.PlayerSavingContainer;

public class EBeanPersistenceStorage implements PersistenceStorage {

	/**
	 * The Server to call stuff on
	 */
	private final EbeanServer ebeanServer;

	public EBeanPersistenceStorage() {
		this.ebeanServer = RacesAndClasses.getPlugin().getDatabase();
	}

	@Override
	public void initForStartup() {
		RacesAndClasses plugin = RacesAndClasses.getPlugin();

		try {
			if (plugin.getDatabase() == null) {
				//plugin.installDDL();
				return;
			}

			for (Class<?> dbClasses : plugin.getDatabaseClasses()) {
				plugin.getDatabase().find(dbClasses).findRowCount();
			}

		} catch (PersistenceException ex) {
			plugin.log("Installing database for "
					+ plugin.getDescription().getName()
					+ " due to first time usage");
			//plugin.installDDL();
		}
	}

	@Override
	public void shutDown() {
		// We need no shutdown since EBean Servers don't need that.
	}

	@Override
	public boolean savePlayerSavingContainer(PlayerSavingContainer container) {
		try {
			ebeanServer.save(container);
		} catch (Exception exp) {
			return false;
		}

		return true;
	}

	@Override
	public boolean savePlayerHolderAssociation(PlayerHolderAssociation container) {
		try {
			ebeanServer.save(container);
		} catch (Exception exp) {
			return false;
		}

		return true;
	}

	@Override
	public boolean savePlayerMemberConfigEntry(ConfigOption uncheckedContainer, boolean forceSave) {
		DBConfigOption container = uncheckedContainer instanceof DBConfigOption 
				? (DBConfigOption) uncheckedContainer
				: DBConfigOption.copyFrom(uncheckedContainer);
		return saveMemberConfigEntryIntern(container);
	}
	
	/**
	 * Internal Proxy function to save for {@link DBConfigOption}.
	 * 
	 * @param container to save
	 * 
	 * @return true if worked, false otherwise.
	 */
	private boolean saveMemberConfigEntryIntern(DBConfigOption container){
		try {
			ebeanServer.save(container);
			return true;
		} catch (Exception exp) {
			return false;
		}

	}

	@Override
	public boolean saveChannelSaveContainer(ChannelSaveContainer container) {
		try {
			ebeanServer.save(container);
		} catch (Exception exp) {
			return false;
		}

		return true;
	}

	@Override
	public PlayerSavingContainer getPlayerContainer(UUID player) {
		try {
			return ebeanServer.find(PlayerSavingContainer.class).where()
					.ieq("playerUUID", player.toString()).findUnique();
		} catch (Exception exp) {
			return null;
		}
	}

	@Override
	public PlayerHolderAssociation getPlayerHolderAssociation(UUID player) {
		try {
			return ebeanServer.find(PlayerHolderAssociation.class).where()
					.ieq("playerUUID", player.toString()).findUnique();
		} catch (Exception exp) {
			return null;
		}
	}

	@Override
	public ConfigOption getPlayerMemberConfigEntryByPath(UUID player,
			String entryName) {
		try {
			return ebeanServer.find(DBConfigOption.class).where()
					.ieq("playerUUID", player.toString()).ieq("path", entryName)
					.findUnique();
		} catch (Exception exp) {
			return null;
		}
	}

	@Override
	public ConfigOption getPlayerMemberConfigEntryByName(UUID player,
			String entryName) {
		try {
			return ebeanServer.find(DBConfigOption.class).where()
					.ieq("playerUUID", player.toString())
					.ieq("displayName", entryName).findUnique();
		} catch (Exception exp) {
			return null;
		}
	}

	@Override
	public ChannelSaveContainer getChannelSaveContainer(String channelName, String channelLevel) {
		try {
			return ebeanServer.find(ChannelSaveContainer.class).where()
					.ieq("channelName", channelName).findUnique();
		} catch (Exception exp) {
			return null;
		}
	}

	@Override
	public List<ConfigOption> getAllConfigOptionsOfPlayer(UUID player) {
		try {
			List<DBConfigOption> options = ebeanServer
					.find(DBConfigOption.class).where()
					.ieq("playerUUID", player.toString()).findList();

			List<ConfigOption> convertedOptions = new LinkedList<ConfigOption>(
					options);
			return convertedOptions;
		} catch (Exception exp) {
			return new LinkedList<ConfigOption>();
		}
	}

	@Override
	public List<PlayerHolderAssociation> getAllPlayerHolderAssociationsForHolder(
			String holderName) {

		try {
			String raceColumnName = "raceName";
			String classColumnName = "className";

			List<PlayerHolderAssociation> returnList = new LinkedList<PlayerHolderAssociation>();
			
			List<PlayerHolderAssociation> raceList = ebeanServer
					.find(PlayerHolderAssociation.class).where()
					.ieq(raceColumnName, holderName).findList();
			
			List<PlayerHolderAssociation> classList = ebeanServer
					.find(PlayerHolderAssociation.class).where()
					.ieq(classColumnName, holderName).findList();

			if (raceList != null && !raceList.isEmpty()) {
				returnList.addAll(raceList);
			}

			if (classList != null && !classList.isEmpty()) {
				returnList.addAll(classList);
			}

			return returnList;
		} catch (Exception exp) {
			return new LinkedList<PlayerHolderAssociation>();
		}
	}

	@Override
	public List<ChannelSaveContainer> getAllChannelSaves() {
		try {
			return ebeanServer.find(ChannelSaveContainer.class).where()
					.findList();
		} catch (Exception exp) {
			return new LinkedList<ChannelSaveContainer>();
		}
	}

	@Override
	public List<PlayerSavingContainer> getAllPlayerSavingContainers() {
		try {
			return ebeanServer.find(PlayerSavingContainer.class).where()
					.findList();
		} catch (Exception exp) {
			return new LinkedList<PlayerSavingContainer>();
		}
	}

	@Override
	public String getNameRepresentation() {
		return "EBean DB";
	}

	@Override
	public void removePlayerHolderAssociation(PlayerHolderAssociation object) {
		ebeanServer.delete(object);
	}

	@Override
	public void removePlayerSavingContainer(PlayerSavingContainer container) {
		ebeanServer.delete(container);
	}

}
