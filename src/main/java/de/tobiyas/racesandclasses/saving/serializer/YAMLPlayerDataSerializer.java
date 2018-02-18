package de.tobiyas.racesandclasses.saving.serializer;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.saving.PlayerSavingData;
import de.tobiyas.util.config.YAMLConfigExtended;
import de.tobiyas.util.schedule.DebugBukkitRunnable;

public class YAMLPlayerDataSerializer implements PlayerDataSerializer {

	/**
	 * The Plugin to use.
	 */
	private final RacesAndClasses plugin;
	
	/**
	 * The Root for saving.
	 */
	private final File saveDir;
	
	
	public YAMLPlayerDataSerializer(RacesAndClasses plugin) {
		this.plugin = plugin;
		this.saveDir = new File(plugin.getDataFolder(), "PlayerDataYML");
		if(!saveDir.exists()) saveDir.mkdirs();
	}
	
	@Override
	public void saveData(PlayerSavingData data) {
		boolean sync = RacesAndClasses.isBukkitInShutdownMode();
		saveData(data, sync);
	}
	
	private void saveData(PlayerSavingData data, boolean sync){
		UUID playerID = data.getPlayerId();
		File playerFile = new File(saveDir, playerID.toString() + ".yml");
		
		YAMLConfigExtended config = new YAMLConfigExtended(playerFile);
		config.set(UUID_PATH, playerID.toString());
		config.set(LAST_NAME_PATH, data.getLastName());
		config.set(RACE_PATH, data.getRaceName());
		config.set(CLASS_PATH, data.getClassName());
		
		config.set(LEVEL_PATH, data.getLevel());
		config.set(EXP_PATH, data.getLevelExp());
		config.set(LAST_PLAYED_PATH, data.getLastLogin());
		config.set(GOD_MODE_PATH, data.isGodModeEnabled());
		
		//Save Hotkeys:
		for(Map.Entry<Integer,String> entry : data.getHotKeys().entrySet()){
			config.set(HOTKEY_PATH + "." + entry.getKey(), entry.getValue());
		}
		
		//Save SkillTree:
		for(Map.Entry<String,Integer> entry : data.getSkillTree().entrySet()){
			config.set(SKILL_TREE_PATH + "." + entry.getKey(), entry.getValue());
		}
		
		//Do saving:
		if(sync) config.save();
		else config.saveAsync();
	}
	
	
	@Override
	public void loadData(final UUID id, final PlayerDataLoadedCallback callback) {
		new DebugBukkitRunnable("RaCSingleLoader") {
			@Override
			protected void runIntern() {
				PlayerSavingData data = loadDataNow(id);
				callback.playerDataLoaded(data);
			}
		}.runTaskAsynchronously(plugin);
	}
	
	@Override
	public void bulkLoadData(final Set<UUID> ids, final PlayerDataLoadedCallback callback) {
		new DebugBukkitRunnable("RaCBulkLoader") {
			@Override
			protected void runIntern() {
				for(UUID id : ids){
					PlayerSavingData data = loadDataNow(id);
					callback.playerDataLoaded(data);
				}
			}
		}.runTaskAsynchronously(plugin);
	}
	
	
	@Override
	public Set<PlayerSavingData> bulkLoadDataNow(Set<UUID> ids) {
		Set<PlayerSavingData> datas = new HashSet<>();
		for(UUID id : ids){
			PlayerSavingData data = loadDataNow(id);
			if(data != null) datas.add(data);
		}
		
		return datas;
	}
	

	@Override
	public PlayerSavingData loadDataNow(UUID id) {
		File playerFile = new File(saveDir, id.toString()+".yml");
		if(!playerFile.exists()) return new PlayerSavingData(id);
		
		YAMLConfigExtended config = new YAMLConfigExtended(playerFile).load();
		if(!config.getValidLoad()) {
			plugin.logDebug("Could not load PlayerData: " + id.toString());
			return new PlayerSavingData(id);
		}
		
		String raceName = config.getString(RACE_PATH, "");
		String className = config.getString(CLASS_PATH, "");
		String lastName = config.getString(LAST_NAME_PATH, "");
		
		boolean godMode = config.getBoolean(GOD_MODE_PATH, false);
		int level = config.getInt(LEVEL_PATH, 1);
		int exp = config.getInt(EXP_PATH, 0);
		long lastPlayed = config.getLong(LAST_PLAYED_PATH, 0);
		
		//Load HotKeys:
		Map<Integer,String> hotkeys = new HashMap<>();
		for(String keyName : config.getChildren(HOTKEY_PATH)){
			try{
				int key = Integer.parseInt(keyName);
				String trait = config.getString(HOTKEY_PATH+"."+key, "");
				if(!trait.isEmpty()) hotkeys.put(key, trait);
			}catch(Throwable e){}
		}
		
		//Load SkillTrees:
		Map<String,Integer> skillTree = new HashMap<>();
		for(String trait : config.getChildren(SKILL_TREE_PATH)){
			try{
				int traitLevel = config.getInt(SKILL_TREE_PATH+"."+trait, 0);
				if(traitLevel > 0) skillTree.put(trait, traitLevel);
			}catch(Throwable e){}
		}
		
		return new PlayerSavingData(id, lastPlayed, lastName, raceName, className, level, exp, godMode, hotkeys, skillTree);
	}

	
	@Override
	public Set<UUID> getAllIDsPresent() {
		File[] files = saveDir.listFiles();
		Set<UUID> ids = new HashSet<>();
		
		if(files == null || files.length == 0) return ids;
		
		//Parse the IDs:
		for(File file : files){
			try{
				String name = file.getName().replace(".yml", "");
				UUID id  = UUID.fromString(name);
				if(id != null) ids.add(id);
			}catch(Throwable exp){}
		}
		
		return ids;
	}
	
	@Override
	public void shutdown() {
		//Nothing to do here.
	}
	
	@Override
	public boolean isFunctional() {
		return true;
	}

}
