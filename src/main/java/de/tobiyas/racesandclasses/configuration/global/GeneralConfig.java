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
import de.tobiyas.racesandclasses.playermanagement.leveling.LevelCalculator;

 
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

	private String config_defaultRaceName;
	private String config_defaultRaceTag;
	
	private int config_itemForMagic;
	
	private String config_mapExpPerLevelCalculationString;
	
	private boolean config_savePlayerDataToDB;
	
	
	/**
	 * Inits the Config system.
	 * Also loads the config directly
	 */
	public GeneralConfig(){
		this.plugin = RacesAndClasses.getPlugin();
		setupConfiguration();
	}

	/**
	 * Sets all default values.
	 * This is for first start + setting default values
	 * to have smart nulls.
	 */
	private void setupConfiguration(){
		FileConfiguration config = plugin.getConfig();

		config.addDefault("chat.whisper.enable", true);
		config.addDefault("chat.race.encryptForOthers", false);
		config.addDefault("chat.channel.enable", true);
		
		config.addDefault("health.defaultHealth", 20);
		config.addDefault("health.bar.inChat.enable", true);
		
		config.addDefault("debug.outputs.enable", true);
		config.addDefault("debug.outputs.errorUpload", true);
		
		config.addDefault("classes.enable", true);
		
		config.addDefault("metrics.enable", true);
		
		config.addDefault("races.remindDefaultRace.enable", true);
		config.addDefault("races.remindDefaultRace.interval", 10);
		config.addDefault("races.display.adaptListName", true);
		
		config.addDefault("races.drops.enable", true);
		config.addDefault("races.permissions.usePermissionsForEachRace", false);
		config.addDefault("races.change.uplinkInSeconds", 0);
		config.addDefault("races.defaultrace.name", "DefaultRace");
		config.addDefault("races.defaultrace.tag", "[NoRace]");
		config.addDefault("races.openRaceSelectionOnJoinWhenNoRace", true);
		config.addDefault("races.cancleGUIExitWhenNoRacePresent", true);
		
		
		config.addDefault("classes.permissions.usePermissionsForEachClasses", false);
		config.addDefault("classes.useRaceClassSelectionMatrix", false);
		config.addDefault("classes.change.uplinkInSeconds", 0);
		config.addDefault("classes.openClassSelectionAfterRaceSelectionWhenNoClass", true);
		config.addDefault("classes.cancleGUIExitWhenNoClassPresent", true);

		
		config.addDefault("tutorials.enable", true);
		
		config.addDefault("language.used", "en");
		
		config.addDefault("worlds.disableOn", Arrays.asList(new String[]{"demoWorld", "demoWorld2"}));
		
		config.addDefault("general.copyDefaultTraitsOnStartup", true);
		config.addDefault("general.saving.savePlayerDataToDB", true);
		
		config.addDefault("races.gui.enable", true);
		config.addDefault("classes.gui.enable", true);
		
		config.addDefault("magic.wandId", Material.STICK.getId());
		
		config.addDefault("leve.mapExpPerLevelCalculationString", "{level} * {level} * {level} * 1000");
		
		
		config.options().copyDefaults(true);
		plugin.saveConfig();

	}
	
	
	/**
	 * reloads the Configuration of the plugin
	 */
	public void reload(){
		plugin.reloadConfig();
		FileConfiguration config = plugin.getConfig();

		config_channels_enable = config.getBoolean("chat.channel.enable", true);
		config_racechat_encrypt = config.getBoolean("chat.race.encryptForOthers", false);		
		
		config_whisper_enable = config.getBoolean("chat.channelwhisper.enable", true);
		
		config_defaultHealth = config.getDouble("health.defaultHealth", 20d);
		
		config_enableDebugOutputs = config.getBoolean("debug.outputs.enable", true);
		config_enableErrorUpload = config.getBoolean("debug.outputs.errorUpload", true);
		
		config_classes_enable = config.getBoolean("classes.enable", true);
		config_metrics_enabled = config.getBoolean("metrics.enable", true);
		
		config_activate_reminder = config.getBoolean("races.remindDefaultRace.enable", true);
		config_reminder_interval = config.getInt("races.remindDefaultRace.interval", 10);
		config_adaptListName = config.getBoolean("races.display.adaptListName", true);
		
		config_enable_expDropBonus = config.getBoolean("races.drops.enable", true);
		
		config_tutorials_enable = config.getBoolean("tutorials.enable", true);
		
		config_usedLanguage = config.getString("language.used", "en");
		
		config_enable_healthbar_in_chat = config.getBoolean("health.bar.inChat.enable", true);
		
		config_usePermissionsForRaces = config.getBoolean("races.permissions.usePermissionsForEachRace", false);
		config_usePermissionsForClasses = config.getBoolean("classes.permissions.usePermissionsForEachClasses", false);
		
		config_copyDefaultTraitsOnStartup = config.getBoolean("general.copyDefaultTraitsOnStartup", true);
		
		config_useRaceClassSelectionMatrix = config.getBoolean("classes.useRaceClassSelectionMatrix", false);
		
		config_classChangeCommandUplink = config.getInt("classes.change.uplinkInSeconds", 0);
		
		config_raceChangeCommandUplink = config.getInt("races.change.uplinkInSeconds", 0);
		
		config_useRaceGUIToSelect = config.getBoolean("races.gui.enable", true);
		config_useClassGUIToSelect = config.getBoolean("classes.gui.enable", true);
		
		config_defaultRaceName = config.getString("races.defaultrace.name", "DefaultRace");
		config_defaultRaceTag = config.getString("races.defaultrace.tag", "[NoRace]");
		
		config_itemForMagic = config.getInt("magic.wandId", Material.STICK.getId());
		
		
		config_openClassSelectionAfterRaceSelectionWhenNoClass = config.getBoolean("classes.openClassSelectionAfterRaceSelectionWhenNoClass", true);
		config_cancleGUIExitWhenNoClassPresent = config.getBoolean("classes.cancleGUIExitWhenNoClassPresent", true);
		config_openRaceSelectionOnJoinWhenNoRace = config.getBoolean("races.openRaceSelectionOnJoinWhenNoRace", true);
		config_cancleGUIExitWhenNoRacePresent = config.getBoolean("races.cancleGUIExitWhenNoRacePresent", true);
		
		config_savePlayerDataToDB = config.getBoolean("general.saving.savePlayerDataToDB", true);
		config_mapExpPerLevelCalculationString = config.getString("level.mapExpPerLevelCalculationString", "{level} * {level} * {level} * 1000");
		if(!LevelCalculator.verifyGeneratorStringWorks(config_mapExpPerLevelCalculationString)){
			plugin.log(" WARNING: The value for the Level Generation String could not be parsed! change: level.mapExpPerLevelCalculationString");
			config_mapExpPerLevelCalculationString = "{level} * {level} * {level} * 1000";
		}
		
		List<String> temp_config_worldsDisabled = config.getStringList("worlds.disableOn");
		
		
		//be sure to have lower case to not be case sensitive
		config_worldsDisabled = new LinkedList<String>();
		for(String tempName : temp_config_worldsDisabled){
			config_worldsDisabled.add(tempName.toLowerCase());
		}
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
	public int getConfig_itemForMagic() {
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

}
