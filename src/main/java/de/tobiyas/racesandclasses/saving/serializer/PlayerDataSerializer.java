package de.tobiyas.racesandclasses.saving.serializer;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import de.tobiyas.racesandclasses.saving.PlayerSavingData;

public interface PlayerDataSerializer {

	public static final String RACE_PATH = "race";
	public static final String CLASS_PATH = "class";
	public static final String HOTKEY_PATH = "hotkeys";
	public static final String SKILL_TREE_PATH = "skilltree";
	public static final String UUID_PATH = "id";
	public static final String LAST_NAME_PATH = "lastname";
	public static final String LAST_PLAYED_PATH = "lastplayed";
	public static final String GOD_MODE_PATH = "god";
	public static final String LEVEL_PATH = "level";
	public static final String EXP_PATH = "exp";
	
	
	/**
	 * Saves the Data.
	 * @param data to save.
	 */
	public void saveData(PlayerSavingData data);
	
	
	/**
	 * Loadss the Player data.
	 * @param id to load.
	 * @param callback to call.
	 */
	public void loadData(UUID id, PlayerDataLoadedCallback callback);
	
	
	/**
	 * Bulk-Loads data.
	 * @param ids to load
	 * @param callback to call after got data.
	 */
	public void bulkLoadData(final Set<UUID> ids, final PlayerDataLoadedCallback callback);
	
	/**
	 * Bulk-Loads data.
	 * @param ids to load
	 * @param callback to call after got data.
	 * 
	 * @return returns the Bulk-Loaded Data.
	 */
	public Collection<PlayerSavingData> bulkLoadDataNow(Set<UUID> ids);
	
	/**
	 * Loadss the Player data.
	 * @param id to load.
	 * @param callback to call.
	 */
	public PlayerSavingData loadDataNow(UUID id);
	
	
	/**
	 * Returns all IDs present.
	 * @return all present IDs.
	 */
	public Set<UUID> getAllIDsPresent();
	
	
	/**
	 * Shuts down the Serializer.
	 */
	public void shutdown();
	
	/**
	 * If the Serializer is useable.
	 * @return true if useable.
	 */
	public boolean isFunctional();
	
	
	interface PlayerDataLoadedCallback {
		
		/**
		 * The Data got loaded.
		 * @param data that was loaded.
		 */
		public void playerDataLoaded(PlayerSavingData data);
		
	}
	
	
	enum DataSerializerType {
		YAML,
		DATABASE
	}


	
}
