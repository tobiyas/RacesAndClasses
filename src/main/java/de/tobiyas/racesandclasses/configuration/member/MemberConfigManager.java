package de.tobiyas.racesandclasses.configuration.member;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import de.tobiyas.util.config.YAMLConfigExtended;
import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.util.consts.Consts;


public class MemberConfigManager {

	/**
	 * The config of members: player -> Config 
	 */
	private HashMap<String, MemberConfig> memberConfigs;
	
	
	/**
	 * The plugin to call stuff on
	 */
	private RacesAndClasses plugin;
	
	/**
	 * Creates the member config
	 */
	public MemberConfigManager(){
		plugin = RacesAndClasses.getPlugin();
		memberConfigs = new HashMap<String, MemberConfig>();
	}
	
	
	/**
	 * creates member config file + reload the config
	 */
	public void reload(){
		checkFiles();
		loadConfigs();
	}
	
	/**
	 * Creates a new playerData File if not existent
	 */
	private void checkFiles(){
		File file = new File(RacesAndClasses.getPlugin().getDataFolder().toString() + File.separator + "PlayerData" + File.separator);
		if(!file.exists())
			file.mkdirs();
		
		File memberFile = new File(Consts.playerDataYML);
		if(!memberFile.exists())
			try {
				memberFile.createNewFile();
			} catch (IOException e) {
				plugin.log("Could not create File: 'plugins/Races/PlayerData/playerdata.yml'");
			}
	}
	
	/**
	 * Creates a new MemberConfig for a player and saves it
	 * 
	 * @param player
	 * @return
	 */
	private MemberConfig createNewConfig(String player){
		MemberConfig config = MemberConfig.createMemberConfig(player);
		memberConfigs.put(player, config);
		
		return config;
	}
	
	/**
	 * Loads all configs from the playerdata.yml file
	 */
	private void loadConfigs(){
		YAMLConfigExtended config = new YAMLConfigExtended(Consts.playerDataYML);
		config.load();
		
		for(String player : config.getChildren("playerdata")){
			createNewConfig(player);
		}
	}
	
	/**
	 * Forces save to all configs
	 */
	public void saveConfigs(){
		for(String player : memberConfigs.keySet()){
			MemberConfig config = memberConfigs.get(player);
			if(config != null) config.save();
		}
	}
	
	/**
	 * Returns the personal MemberConfig of a player
	 * If not existent a new one is created.
	 * 
	 * @param player to get the config from
	 * @return
	 */
	public MemberConfig getConfigOfPlayer(String player){
		MemberConfig config = memberConfigs.get(player);
		if(config == null){
			config = createNewConfig(player);
		}
			
		return config;
	}
	
}
