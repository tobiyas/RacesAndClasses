/*
 * Races - by tobiyas
 * http://
 *
 * powered by Kickstarter
 */
 
 package de.tobiyas.racesandclasses.configuration.global;

 
 import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.configuration.ConfigTemplate;
import de.tobiyas.racesandclasses.playermanagement.leveling.LevelCalculator;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.*;

 
 public class GeneralConfig{
	private final RacesAndClasses plugin;

	private boolean config_racechat_encrypt;
	
	private double config_defaultHealth;

	
	private boolean config_adaptListName;
	
	//Enables:
	private boolean config_whisper_enable;
	
	private boolean config_enableDebugOutputs;
	private boolean config_enableErrorUpload;
	
	private boolean config_classes_enable;

	private boolean config_channels_enable;

	private boolean config_metrics_enabled;
	
	private boolean config_activate_reminder;
	private int config_reminder_interval;
	private boolean config_enable_expDropBonus;
	
	private boolean config_enable_healthbar_in_chat;
	
	private boolean config_tutorials_enable;
	
	
	private boolean config_usePermissionsForRaces;
	private boolean config_usePermissionsForClasses;
	
	private boolean config_copyDefaultTraitsOnStartup;
	
	private boolean config_useRaceClassSelectionMatrix;

	//language to use
	private String config_usedLanguage;
	
	//disable on worlds
	private List<String> config_worldsDisabled;
	
	//Uplink for Race change command
	private int config_raceChangeCommandUplink;
	
	
	//Uplink for Class change command
	private int config_classChangeCommandUplink;
	
	private boolean config_useClassGUIToSelect;
	private boolean config_useRaceGUIToSelect;
	
	private boolean config_openRaceSelectionOnJoinWhenNoRace;
	private boolean config_openClassSelectionAfterRaceSelectionWhenNoClass;
	
	private boolean config_cancleGUIExitWhenNoRacePresent;
	private boolean config_cancleGUIExitWhenNoClassPresent;

	private String config_takeClassWhenNoClass;
	private String config_takeRaceWhenNoRace;
	
	private String config_defaultRaceName;
	private String config_defaultRaceTag;
	
	private Material config_itemForMagic;
	
	private String config_mapExpPerLevelCalculationString;
	private boolean config_useRaCInbuildLevelSystem;
	
	private boolean config_savePlayerDataToDB;
	
	private int config_debugTimeAfterLoginOpening;
	private boolean config_useAutoUpdater;
	
	
	/**
	 * Inits the Config system.
	 * Also loads the config directly
	 */
	public GeneralConfig(){
		this.plugin = RacesAndClasses.getPlugin();
		setupConfiguration();
		reload();
		
		ConfigTemplate template = new ConfigTemplate();
		if(template.isOldConfigVersion()){
			template.writeTemplate();
		}
	}

	/**
	 * Sets all default values.
	 * This is for first start + setting default values
	 * to have smart nulls.
	 */
	private void setupConfiguration(){
		FileConfiguration config = plugin.getConfig();

		config.addDefault(chat_whisper_enable, true);
		config.addDefault(chat_race_encryptForOthers, false);
		config.addDefault(chat_channel_enable, true);
		
		config.addDefault(health_defaultHealth, 20);
		config.addDefault(health_bar_inChat_enable, true);
		
		config.addDefault(debug_outputs_enable, true);
		config.addDefault(debug_outputs_errorUpload, true);
		
		config.addDefault(classes_enable, true);
		
		config.addDefault(metrics_enable, true);
		
		config.addDefault(updater_enableAutoUpdates, false);
		
		config.addDefault(races_remindDefaultRace_enable, true);
		config.addDefault(races_remindDefaultRace_interval, 10);
		config.addDefault(races_display_adaptListName, true);
		
		config.addDefault(races_drops_enable, true);
		config.addDefault(races_permissions_usePermissionsForEachRace, false);
		config.addDefault(races_change_uplinkInSeconds, 0);
		config.addDefault(races_defaultrace_name, "DefaultRace");
		config.addDefault(races_defaultrace_tag, "[NoRace]");
		config.addDefault(races_openRaceSelectionOnJoinWhenNoRace_enable, true);
		config.addDefault(races_openRaceSelectionOnJoinWhenNoRace_timeToOpenAfterLoginInSeconds, 2);
		config.addDefault(races_cancleGUIExitWhenNoRacePresent_enable, true);
		config.addDefault(races_takeRaceWhenNoRace, "");
		
		config.addDefault(classes_permissions_usePermissionsForEachClasses, false);
		config.addDefault(classes_useRaceClassSelectionMatrix, false);
		config.addDefault(classes_change_uplinkInSeconds, 0);
		config.addDefault(classes_openClassSelectionAfterRaceSelectionWhenNoClass_enable, true);
		config.addDefault(classes_cancleGUIExitWhenNoClassPresent_enable, true);
		config.addDefault(classes_takeClassWhenNoClass, "");
		
		config.addDefault(tutorials_enable, true);
		
		config.addDefault(language_used, "en");
		
		config.addDefault(worlds_disableOn, Arrays.asList(new String[]{"demoWorld", "demoWorld2"}));
		
		config.addDefault(general_copyDefaultTraitsOnStartup, true);
		config.addDefault(general_saving_savePlayerDataToDB, true);
		
		config.addDefault(races_gui_enable, true);
		config.addDefault(classes_gui_enable, true);
		
		config.addDefault(magic_wandId, Material.STICK.name());
		
		config.addDefault(level_mapExpPerLevelCalculationString, "{level} * {level} * {level} * 1000");
		config.addDefault(level_useRaCInbuildLevelSystem, true);
		
		
		config.options().copyDefaults(true);
	}
	
	
	/**
	 * reloads the Configuration of the plugin
	 */
	public GeneralConfig reload(){
		plugin.reloadConfig();
		FileConfiguration config = plugin.getConfig();

		config_channels_enable = config.getBoolean(chat_channel_enable, true);
		config_racechat_encrypt = config.getBoolean(chat_race_encryptForOthers, false);		
		
		config_whisper_enable = config.getBoolean(chat_whisper_enable, true);
		
		config_defaultHealth = config.getDouble(health_defaultHealth, 20d);
		
		config_enableDebugOutputs = config.getBoolean(debug_outputs_enable, true);
		config_enableErrorUpload = config.getBoolean(debug_outputs_errorUpload, true);
		
		config_classes_enable = config.getBoolean(classes_enable, true);
		config_metrics_enabled = config.getBoolean(metrics_enable, true);
		
		config_activate_reminder = config.getBoolean(races_remindDefaultRace_enable, true);
		config_reminder_interval = config.getInt(races_remindDefaultRace_interval, 10);
		config_adaptListName = config.getBoolean(races_display_adaptListName, true);
		
		config_enable_expDropBonus = config.getBoolean(races_drops_enable, true);
		
		config_tutorials_enable = config.getBoolean(tutorials_enable, true);
		
		config_usedLanguage = config.getString(language_used, "en");
		
		config_enable_healthbar_in_chat = config.getBoolean(health_bar_inChat_enable, true);
		
		config_usePermissionsForRaces = config.getBoolean(races_permissions_usePermissionsForEachRace, false);
		config_usePermissionsForClasses = config.getBoolean(classes_permissions_usePermissionsForEachClasses, false);
		
		config_copyDefaultTraitsOnStartup = config.getBoolean(general_copyDefaultTraitsOnStartup, true);
		
		config_useRaceClassSelectionMatrix = config.getBoolean(classes_useRaceClassSelectionMatrix, false);
		
		config_classChangeCommandUplink = config.getInt(classes_change_uplinkInSeconds, 0);
		
		config_raceChangeCommandUplink = config.getInt(races_change_uplinkInSeconds, 0);
		
		config_useRaceGUIToSelect = config.getBoolean(races_gui_enable, true);
		config_useClassGUIToSelect = config.getBoolean(classes_gui_enable, true);
		
		config_defaultRaceName = config.getString(races_defaultrace_name, "DefaultRace");
		config_defaultRaceTag = config.getString(races_defaultrace_tag, "[NoRace]");
		
		config_takeClassWhenNoClass = config.getString(classes_takeClassWhenNoClass, "");
		config_takeRaceWhenNoRace = config.getString(races_takeRaceWhenNoRace, "");
		
		if(config.isString(magic_wandId)){
			String itemName = config.getString(magic_wandId, "STICK");
			config_itemForMagic = Material.getMaterial(itemName.toUpperCase());
			if(config_itemForMagic == null){
				config_itemForMagic = Material.STICK;
			}
		}else{
			int itemId = config.getInt(magic_wandId, 280);
			config_itemForMagic = Material.getMaterial(itemId);
		}
		
		
		config_openClassSelectionAfterRaceSelectionWhenNoClass = config.getBoolean(classes_openClassSelectionAfterRaceSelectionWhenNoClass_enable, true);
		config_cancleGUIExitWhenNoClassPresent = config.getBoolean(classes_cancleGUIExitWhenNoClassPresent_enable, true);
		config_openRaceSelectionOnJoinWhenNoRace = config.getBoolean(races_openRaceSelectionOnJoinWhenNoRace_enable, true);
		config_cancleGUIExitWhenNoRacePresent = config.getBoolean(races_cancleGUIExitWhenNoRacePresent_enable, true);
		config_debugTimeAfterLoginOpening = config.getInt(races_openRaceSelectionOnJoinWhenNoRace_timeToOpenAfterLoginInSeconds, 2);
		
		config_savePlayerDataToDB = config.getBoolean(general_saving_savePlayerDataToDB, true);
		config_mapExpPerLevelCalculationString = config.getString(level_mapExpPerLevelCalculationString, "{level} * {level} * {level} * 1000");
		config_useRaCInbuildLevelSystem = config.getBoolean(level_useRaCInbuildLevelSystem, true);
		
		config_useAutoUpdater = config.getBoolean(updater_enableAutoUpdates, false);
		
		if(!LevelCalculator.verifyGeneratorStringWorks(config_mapExpPerLevelCalculationString)){
			plugin.log(" WARNING: The value for the Level Generation String could not be parsed! change: level.mapExpPerLevelCalculationString");
			config_mapExpPerLevelCalculationString = "{level} * {level} * {level} * 1000";
		}
		
		List<String> temp_config_worldsDisabled = config.getStringList("worlds_disableOn");
		
		
		//be sure to have lower case to not be case sensitive
		config_worldsDisabled = new LinkedList<String>();
		for(String tempName : temp_config_worldsDisabled){
			config_worldsDisabled.add(tempName.toLowerCase());
		}
		
		return this;
	}
	

	public boolean isConfig_racechat_encrypt() {
		return config_racechat_encrypt;
	}

	public double getConfig_defaultHealth() {
		return config_defaultHealth;
	}

	public boolean isConfig_adaptListName() {
		return config_adaptListName;
	}

	public boolean isConfig_whisper_enable() {
		return config_whisper_enable;
	}

	public boolean isConfig_enableDebugOutputs() {
		return config_enableDebugOutputs;
	}

	public boolean isConfig_enableErrorUpload() {
		return config_enableErrorUpload;
	}

	public boolean isConfig_classes_enable() {
		return config_classes_enable;
	}

	public boolean isConfig_channels_enable() {
		return config_channels_enable;
	}

	public boolean isConfig_metrics_enabled() {
		return config_metrics_enabled;
	}

	public boolean isConfig_activate_reminder() {
		return config_activate_reminder;
	}

	public int getConfig_reminder_interval() {
		return config_reminder_interval;
	}

	public boolean isConfig_enable_expDropBonus() {
		return config_enable_expDropBonus;
	}

	public boolean isConfig_enable_healthbar_in_chat() {
		return config_enable_healthbar_in_chat;
	}

	public boolean isConfig_tutorials_enable() {
		return config_tutorials_enable;
	}

	public boolean isConfig_usePermissionsForRaces() {
		return config_usePermissionsForRaces;
	}

	public boolean isConfig_usePermissionsForClasses() {
		return config_usePermissionsForClasses;
	}

	public boolean isConfig_copyDefaultTraitsOnStartup() {
		return config_copyDefaultTraitsOnStartup;
	}

	public boolean isConfig_useRaceClassSelectionMatrix() {
		return config_useRaceClassSelectionMatrix;
	}

	public String getConfig_usedLanguage() {
		return config_usedLanguage;
	}

	public List<String> getConfig_worldsDisabled() {
		return config_worldsDisabled;
	}

	public int getConfig_raceChangeCommandUplink() {
		return config_raceChangeCommandUplink;
	}

	public int getConfig_classChangeCommandUplink() {
		return config_classChangeCommandUplink;
	}

	/**
	 * @return the config_useClassGUIToSelect
	 */
	public boolean isConfig_useClassGUIToSelect() {
		return config_useClassGUIToSelect;
	}

	/**
	 * @return the config_useRaceGUIToSelect
	 */
	public boolean isConfig_useRaceGUIToSelect() {
		return config_useRaceGUIToSelect;
	}

	
	public String getConfig_defaultRaceName() {
		return config_defaultRaceName;
	}

	public String getConfig_defaultRaceTag() {
		return config_defaultRaceTag;
	}

	/**
	 * @return the config_itemForMagic
	 */
	public Material getConfig_itemForMagic() {
		return config_itemForMagic;
	}

	/**
	 * @return the config_mapExpPerLevelCalculationString
	 */
	public String getConfig_mapExpPerLevelCalculationString() {
		return config_mapExpPerLevelCalculationString;
	}

	/**
	 * @return the config_openRaceSelectionOnJoinWhenNoRace
	 */
	public boolean isConfig_openRaceSelectionOnJoinWhenNoRace() {
		return config_openRaceSelectionOnJoinWhenNoRace;
	}

	/**
	 * @return the config_openClassSelectionAfterRaceSelectionWhenNoClass
	 */
	public boolean isConfig_openClassSelectionAfterRaceSelectionWhenNoClass() {
		return config_openClassSelectionAfterRaceSelectionWhenNoClass;
	}

	/**
	 * @return the config_cancleGUIExitWhenNoRacePresent
	 */
	public boolean isConfig_cancleGUIExitWhenNoRacePresent() {
		return config_cancleGUIExitWhenNoRacePresent;
	}

	/**
	 * @return the config_cancleGUIExitWhenNoClassPresent
	 */
	public boolean isConfig_cancleGUIExitWhenNoClassPresent() {
		return config_cancleGUIExitWhenNoClassPresent;
	}

	public boolean isConfig_savePlayerDataToDB() {
		return config_savePlayerDataToDB;
	}

	public boolean isConfig_useRaCInbuildLevelSystem() {
		return config_useRaCInbuildLevelSystem;
	}

	public boolean isConfig_useAutoUpdater() {
		return config_useAutoUpdater;
	}

	public int getConfig_debugTimeAfterLoginOpening() {
		return config_debugTimeAfterLoginOpening;
	}

	public String getConfig_takeClassWhenNoClass() {
		return config_takeClassWhenNoClass;
	}

	public String getConfig_takeRaceWhenNoRace() {
		return config_takeRaceWhenNoRace;
	}

}
