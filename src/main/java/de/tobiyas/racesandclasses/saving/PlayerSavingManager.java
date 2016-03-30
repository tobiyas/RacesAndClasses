package de.tobiyas.racesandclasses.saving;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.saving.serializer.DatabasePlayerDataSerializer;
import de.tobiyas.racesandclasses.saving.serializer.DisabledDataSerializer;
import de.tobiyas.racesandclasses.saving.serializer.PlayerDataSerializer;
import de.tobiyas.racesandclasses.saving.serializer.PlayerDataSerializer.DataSerializerType;
import de.tobiyas.racesandclasses.saving.serializer.YAMLPlayerDataSerializer;
import de.tobiyas.util.schedule.DebugBukkitRunnable;

public class PlayerSavingManager implements PlayerDataSerializer.PlayerDataLoadedCallback {

	/**
	 * The Instance to use.
	 */
	private static PlayerSavingManager instance;
	
	/**
	 * The Serializer to use.
	 */
	private final PlayerDataSerializer serializer;
	
	/**
	 * The plugin to use.
	 */
	private final RacesAndClasses plugin;
	
	
	/**
	 * The Data for Players.
	 */
	private final Map<UUID,PlayerSavingData> playerSaveMap = new HashMap<>();
	
	
	private PlayerSavingManager() {
		this.plugin = RacesAndClasses.getPlugin();
		
		String serializerName = plugin.getConfigManager().getGeneralConfig().getConfig_serializer();
		DataSerializerType type = fromString(serializerName);
		this.serializer = generateSerializer(type);
		
		boolean asyncLoadAll =  plugin.getConfigManager().getGeneralConfig().isConfig_preload_data_async();
		if(asyncLoadAll) startAsyncLoading();
	}
	
	
	/**
	 * Creates and start an Async loading!
	 */
	protected void startAsyncLoading() {
		new DebugBukkitRunnable("BulkID-Loader") {
			@Override
			protected void runIntern() {
				Set<UUID> toLoad = serializer.getAllIDsPresent();
				
				int i = 0;
				int MAX_TO_LOAD_PER_BULK = plugin.getConfigManager().getGeneralConfig().getConfig_preload_bulk_amount();
				Set<UUID> toLoadCopied = new HashSet<>();
				for(UUID id : toLoad){
					toLoadCopied.add(id);
					
					//Bulk up to MAX:
					if(i% MAX_TO_LOAD_PER_BULK == 0 && i > 0) {
						Collection<PlayerSavingData> datas = serializer.bulkLoadDataNow(toLoadCopied);
						synchronized (playerSaveMap) {
							for(PlayerSavingData data : datas){
								if(data != null && !playerSaveMap.containsKey(data.getPlayerId())) playerSaveMap.put(data.getPlayerId(), data);
							}
						}
						
						toLoadCopied.clear();
					}
					
					i++;
				}
				
				RacesAndClasses.getPlugin().log("Pre-Loaded " + i + " Player-Data. Done.");
			}
		}.runTaskAsynchronously(plugin);
	}


	/**
	 * Generates a Type from the Name passed.
	 * @param name to parse
	 * @return the Type.
	 */
	private DataSerializerType fromString(String name){
		if(name == null) return DataSerializerType.YAML;
		
		name = name.toLowerCase();
		if(name.startsWith("y") || name.startsWith("f")) return DataSerializerType.YAML;
		if(name.startsWith("o") || name.startsWith("di")) return DataSerializerType.DISABLED;
		if(name.startsWith("da") || name.startsWith("my") || name.startsWith("sql")) return DataSerializerType.DATABASE;
		
		return DataSerializerType.YAML;
	}
	
	
	/**
	 * Generates a Data-Serializer.
	 * @return the Serializer.
	 */
	private PlayerDataSerializer generateSerializer(DataSerializerType type){
		PlayerDataSerializer serializer = null;
		switch(type){
			case DATABASE : serializer = new DatabasePlayerDataSerializer(plugin); break;
			case DISABLED : serializer = new DisabledDataSerializer(); break;
			
			case YAML :
			default : serializer = new YAMLPlayerDataSerializer(plugin); break;
		}
		
		if(serializer == null || !serializer.isFunctional()){
			if(type == DataSerializerType.DATABASE) RacesAndClasses.getPlugin().logWarning("Could not establish DB-Connection! Switching to YML files!");
			serializer = new YAMLPlayerDataSerializer(plugin);
			
		}
		return serializer;
	}
	
	
	/**
	 * Gets the Serializer to use.
	 * @return the serializer.
	 */
	protected PlayerDataSerializer getSerializer() {
		return serializer;
	}
	
	
	/**
	 * Gets the Data from the ID.
	 * @param id to search.
	 * @return the PlayerData.
	 */
	public PlayerSavingData getPlayerData(UUID id){
		PlayerSavingData data = null;
		synchronized (playerSaveMap) { data = playerSaveMap.get(id); }
		if(data != null) return data;
		
		data = serializer.loadDataNow(id);
		if(data == null) data = new PlayerSavingData(id);
		synchronized (playerSaveMap) { playerSaveMap.put(id, data); }
		
		return data;
	}
	
	
	/**
	 * Shuts everything down.
	 */
	private void shutdownInstance(){
		serializer.shutdown();
	}
	
	
	/**
	 * Gets the Instance.
	 * <br>Lazy init.
	 * @return the instance
	 */
	public static PlayerSavingManager get(){
		if(instance == null) instance = new PlayerSavingManager();
		return instance;
	}
	

	/**
	 * Reinits the Manager.
	 */
	public static void reload() {
		shutdown();
		instance = new PlayerSavingManager();
	}
	
	/**
	 * Reinits the Manager.
	 */
	public static void shutdown() {
		if(instance != null) instance.shutdownInstance();
		instance = null;
	}


	@Override
	public void playerDataLoaded(PlayerSavingData data) {
		if(data == null) return;
		
		synchronized (playerSaveMap) { if(!playerSaveMap.containsKey(data.getPlayerId())) playerSaveMap.put(data.getPlayerId(), data); }
	}

}
