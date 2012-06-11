package de.tobiyas.races.configuration.member;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import de.tobiyas.races.Races;
import de.tobiyas.races.configuration.global.YAMLConfigExtended;
import de.tobiyas.races.util.consts.Consts;


public class MemberConfigManager {

	private HashMap<String, MemberConfig> memberConfigs;
	private static MemberConfigManager manager;
	
	private Races plugin;
	
	public MemberConfigManager(){
		manager = this;
		plugin = Races.getPlugin();
		memberConfigs = new HashMap<String, MemberConfig>();
	}
	
	public void init(){
		checkFiles();
		loadConfigs();
	}
	
	private void checkFiles(){
		File file = new File(Races.getPlugin().getDataFolder().toString() + File.separator + "PlayerData" + File.separator);
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
	
	private MemberConfig createNewConfig(String player){
		MemberConfig config = MemberConfig.createMemberConfig(player);
		memberConfigs.put(player, config);
		
		return config;
	}
	
	private void loadConfigs(){
		YAMLConfigExtended config = new YAMLConfigExtended(Consts.playerDataYML);
		config.load();
		
		for(String player : config.getYAMLChildren("playerdata"))
			createNewConfig(player);
	}
	
	public void saveConfigs(){
		for(String player : memberConfigs.keySet()){
			MemberConfig config = memberConfigs.get(player);
			if(config != null) config.save();
		}
	}
	
	public MemberConfig getConfigOfPlayer(String player){
		MemberConfig config = memberConfigs.get(player);
		if(config == null)
			config = createNewConfig(player);
			
		return config;
	}
	
	public static MemberConfigManager getInstance(){
		return manager;
	}
}
