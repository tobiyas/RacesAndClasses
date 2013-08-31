package de.tobiyas.racesandclasses.configuration.member;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.configuration.member.database.DBMemberConfig;
import de.tobiyas.racesandclasses.configuration.member.file.MemberConfig;
import de.tobiyas.racesandclasses.util.consts.Consts;
import de.tobiyas.racesandclasses.util.persistence.YAMLPersistenceProvider;
import de.tobiyas.util.config.YAMLConfigExtended;


public class MemberConfigManager {

	/**
	 * The config of members: player -> Config 
	 */
	private Map<String, MemberConfig> memberConfigs;
	
	
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
		memberConfigs = new HashMap<String, MemberConfig>();
		
		checkFiles();
		if(!plugin.getConfigManager().getGeneralConfig().isConfig_savePlayerDataToDB()){
			loadConfigs();
		}
	}
	
	/**
	 * Creates a new playerData File if not existent
	 */
	private void checkFiles(){
		boolean useDB = plugin.getConfigManager().getGeneralConfig().isConfig_savePlayerDataToDB();
		if(!useDB){
			File file = new File(RacesAndClasses.getPlugin().getDataFolder().toString() + File.separator + "PlayerData" + File.separator);
			if(!file.exists()){
				file.mkdirs();
			}
			
			
			File memberFile = new File(Consts.playerDataYML);
			if(!memberFile.exists()){
				try {
					memberFile.createNewFile();
				} catch (IOException e) {
					plugin.log("Could not create File: 'plugins/Races/PlayerData/playerdata.yml'");
				}
			}
		}
	}
	
	/**
	 * Creates a new MemberConfig for a player and saves it.
	 * If the Config of the player is already present, it will be returned instead.
	 * 
	 * @param player
	 * @return
	 */
	private MemberConfig getCreateNewConfig(String player){
		if(memberConfigs.containsKey(player)){
			return memberConfigs.get(player);
		}
		
		MemberConfig config = null;
		boolean useDB = plugin.getConfigManager().getGeneralConfig().isConfig_savePlayerDataToDB();
		if(useDB){
			config = DBMemberConfig.createMemberConfig(player);
		}else{
			config = MemberConfig.createMemberConfig(player).save();
		}
		
		memberConfigs.put(player, config);
		return config;
	}
	
	/**
	 * Loads all configs from the playerdata.yml file
	 */
	private void loadConfigs(){
		Set<String> playerList = new HashSet<String>();
		
		YAMLConfigExtended config = YAMLPersistenceProvider.getLoadedPlayerFile(true);
		playerList = config.getChildren("playerdata");
		
		
		for(String player : playerList){
			getCreateNewConfig(player);
		}
	}
	
	/**
	 * Forces save to all configs
	 */
	public void saveConfigs(){
		for(String player : memberConfigs.keySet()){
			MemberConfig config = memberConfigs.get(player);
			if(config != null) {
				if(config instanceof DBMemberConfig){
					((DBMemberConfig) config).save();
				}else{
					config.save();
				}
			}
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
		return getCreateNewConfig(player);
	}
	
}
