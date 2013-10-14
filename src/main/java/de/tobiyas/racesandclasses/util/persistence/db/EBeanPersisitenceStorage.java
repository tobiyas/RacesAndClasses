package de.tobiyas.racesandclasses.util.persistence.db;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.PersistenceException;

import com.avaje.ebean.EbeanServer;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.chat.channels.container.ChannelSaveContainer;
import de.tobiyas.racesandclasses.configuration.member.database.DBConfigOption;
import de.tobiyas.racesandclasses.configuration.member.file.ConfigOption;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.PlayerHolderAssociation;
import de.tobiyas.racesandclasses.playermanagement.PlayerSavingContainer;
import de.tobiyas.racesandclasses.util.persistence.PersistenceStorage;

public class EBeanPersisitenceStorage implements PersistenceStorage {

	/**
	 * The Server to call stuff on
	 */
	private final EbeanServer ebeanServer;

	public EBeanPersisitenceStorage() {
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
		//TODO getDB classes here!
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
	public boolean savePlayerMemberConfigEntry(DBConfigOption container) {
		try {
			ebeanServer.save(container);
		} catch (Exception exp) {
			return false;
		}

		return true;
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
	public PlayerSavingContainer getPlayerContainer(String name) {
		try {
			return ebeanServer.find(PlayerSavingContainer.class).where()
					.ieq("playerName", name).findUnique();
		} catch (Exception exp) {
			return null;
		}
	}

	@Override
	public PlayerHolderAssociation getPlayerHolderAssociation(String name) {
		try {
			return ebeanServer.find(PlayerHolderAssociation.class).where()
					.ieq("playerName", name).findUnique();
		} catch (Exception exp) {
			return null;
		}
	}

	@Override
	public ConfigOption getPlayerMemberConfigEntryByPath(String playerName,
			String entryName) {
		try {
			return ebeanServer.find(DBConfigOption.class).where()
					.ieq("playerName", playerName).ieq("path", entryName)
					.findUnique();
		} catch (Exception exp) {
			return null;
		}
	}

	@Override
	public ConfigOption getPlayerMemberConfigEntryByName(String playerName,
			String entryName) {
		try {
			return ebeanServer.find(DBConfigOption.class).where()
					.ieq("playerName", playerName)
					.ieq("displayName", entryName).findUnique();
		} catch (Exception exp) {
			return null;
		}
	}

	@Override
	public ChannelSaveContainer getChannelSaveContainer(String channelName) {
		try {
			return ebeanServer.find(ChannelSaveContainer.class).where()
					.ieq("channelName", channelName).findUnique();
		} catch (Exception exp) {
			return null;
		}
	}

	@Override
	public List<ConfigOption> getAllConfigOptionsOfPlayer(String playerName) {
		try {
			List<DBConfigOption> options = ebeanServer
					.find(DBConfigOption.class).where()
					.ieq("playerName", playerName).findList();

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

}