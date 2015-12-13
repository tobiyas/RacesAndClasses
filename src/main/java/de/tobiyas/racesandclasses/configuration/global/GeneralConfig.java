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
/*
 * Races - by tobiyas
 * http://
 *
 * powered by Kickstarter
 */
 
package de.tobiyas.racesandclasses.configuration.global;


import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.*;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.configuration.ConfigTemplate;
import de.tobiyas.racesandclasses.playermanagement.leveling.LevelCalculator;
import de.tobiyas.racesandclasses.playermanagement.leveling.LevelingSystem;
import de.tobiyas.racesandclasses.playermanagement.leveling.manager.McMMOLevelManager;
import de.tobiyas.racesandclasses.playermanagement.spellmanagement.mana.ManaManagerType;
import de.tobiyas.util.config.YAMLConfigExtended;
import de.tobiyas.util.formating.Pair;

 
 public class GeneralConfig{
	private final RacesAndClasses plugin;

	private boolean config_racechat_encrypt;
	
	private double config_defaultHealth;

	
	private boolean config_adaptListName;
	
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
	private boolean config_disableHealthMods;
	
	private boolean config_enableRaceTeams;
	private boolean config_useNewTraitBindSystem;
	
	
	private boolean config_usePermissionsForRaces;
	private boolean config_usePermissionsForClasses;
	
	private boolean config_copyDefaultTraitsOnStartup;
	
	private boolean config_useRaceClassSelectionMatrix;
	private boolean config_convertDatabaseOnStartup;
	private boolean config_food_enabled;

	//language to use
	private String config_usedLanguage;
	
	//disable on worlds
	private List<String> config_worldsDisabled;
	private boolean config_keep_max_hp_on_disabled_worlds;
	
	//Uplink for Race change command
	private int config_raceChangeCommandUplink;
	
	
	//Uplink for Class change command
	private int config_classChangeCommandUplink;
	
	private List<String> config_general_disable_commands;
	
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
	
	private double config_magic_sprintingManaCost;
	private int config_magic_sprintingManaDrainInterval;
	private boolean config_magic_manaRefillWhileSprinting;
	
	private Material config_itemForMagic;
	
	private String config_mapExpPerLevelCalculationString;
	private LevelingSystem config_useLevelSystem;
	
	private boolean config_savePlayerDataToDB;
	
	private int config_debugTimeAfterLoginOpening;
	private boolean config_useAutoUpdater;
	
	private boolean config_alsoUseLeftClickInGuis;
	private boolean config_disableAllScoreboardOutputs;
	
	private boolean config_enableRaces;
	
	private boolean config_disableArmorChecking;
	
	private boolean config_disableAllChatBars;
	private boolean config_disableChatJoinLeaveMessages;
	
	private boolean config_enableRaceSpawn;
	private boolean config_enableRaceSpawnOnDeath;
	private int config_raceSpawnCooldown;
	
	private int config_cooldown_on_bow_message;
	private int config_cooldown_on_wand_message;
	private int config_max_level;
	
	private String config_magic_manaShowPlace;

	private List<String> config_general_disable_aliases;
	private boolean config_gui_level_useMCLevelBar;
	
	private Material config_hotkeys_material;
	
	private List<String> config_race_commands_after_change;
	private List<String> config_class_commands_after_change;

	private int config_general_remove_old_data_days;
	private boolean config_general_remove_old_data_check_empty;
	private boolean config_useFoodManaBar;
	private ManaManagerType config_manaManagerType;
	private String config_gui_scoreboard_name;
	private boolean config_races_create_group_for_race;
	private boolean config_hotkeysEnabled;
	private boolean config_use_fireworks_on_level_up;
	private boolean config_use_levelup_message;
	private Pair<Double, Double> config_magic_outOfFightRegeneration = new Pair<Double,Double>(0d,100d);
	
	private final Set<String> config_disabled_regions = new HashSet<String>();
	private final Set<String> config_npc_select_race = new HashSet<String>();
	private final Set<String> config_npc_change_race = new HashSet<String>();
	private final Set<String> config_npc_select_class = new HashSet<String>();
	private final Set<String> config_npc_change_class = new HashSet<String>();
	private final Map<EntityType,Integer> config_custom_level_exp_gain = new HashMap<EntityType, Integer>();
	
	private final Set<Integer> config_disabledHotkeySlots = new HashSet<Integer>();
	
	private final Map<String,String> config_command_remaps = new HashMap<String,String>();
	
	/**
	 * Inits the ConfigTotal system.
	 * Also loads the config directly
	 */
	public GeneralConfig(){
		this.plugin = RacesAndClasses.getPlugin();

		checkRegenerate(false);
	}
	
	/**
	 * Checks if the ConfigTotal need Regeneration.
	 * 
	 * @param force if True, the config is Forced to regenerate.
	 */
	public void checkRegenerate(boolean force){
		setupConfiguration();
		reload();
		
		ConfigTemplate template = new ConfigTemplate();
		if(force || template.isOldConfigVersion()){
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
		config.addDefault(chat_disable_channel_join_leave_messages, false);
		
		config.addDefault(health_defaultHealth, 20);
		config.addDefault(health_bar_inChat_enable, true);
		
		config.addDefault(debug_outputs_enable, true);
		config.addDefault(debug_outputs_errorUpload, true);
		
		config.addDefault(classes_enable, true);
		
		config.addDefault(metrics_enable, true);
		
		config.addDefault(updater_enableAutoUpdates, false);
		
		config.addDefault(hotkeys_enabled, true);
		config.addDefault(hotkeys_material, Material.SHEARS.name());
		config.addDefault(use_new_traitbind_system, true);
		
		config.addDefault(races_remindDefaultRace_enable, true);
		config.addDefault(races_remindDefaultRace_interval, 10);
		config.addDefault(races_display_adaptListName, true);
		
		config.addDefault(races_drops_enable, true);
		config.addDefault(races_create_group_for_race, true);
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
		config.addDefault(gui_level_useMCLevelBar, false);
		config.addDefault(magic_useFoodManaBar, false);
		
		config.addDefault(worlds_disableOn, Arrays.asList(new String[]{"demoWorld", "demoWorld2"}));
		config.addDefault(keep_max_hp_on_disabled_worlds, true);
		
		config.addDefault(general_copyDefaultTraitsOnStartup, true);
		config.addDefault(general_saving_savePlayerDataToDB, false);
		config.addDefault(general_convert_database_on_startup, true);
		config.addDefault(general_disable_commands, new LinkedList<String>());
		config.addDefault(general_disable_aliases, new LinkedList<String>());
		config.addDefault(general_cooldown_on_wand_message, 10);
		config.addDefault(general_cooldown_on_bow_message, 10);
		config.addDefault(general_remove_old_data_days, 60);
		config.addDefault(general_remove_old_data_check_empty, true);
		
		config.addDefault(gui_scoreboard_disableAllOutputs, false);
		config.addDefault(gui_scoreboard_name, "&eRaC");
		config.addDefault(gui_disableAllChatBars, false);		
		config.addDefault(gui_also_use_leftclick_in_guis, true);
		
		config.addDefault(races_gui_enable, true);
		config.addDefault(classes_gui_enable, true);
		
		config.addDefault(magic_wandId, Material.STICK.name());
		config.addDefault(magic_manaManagerType, ManaManagerType.RaC.name());
		config.addDefault(magic_outOfFightRegeneration, "0#100");
		config.addDefault(magic_sprintingManaDrainInterval, 3);
		config.addDefault(magic_sprintingManaCost, 2);
		config.addDefault(magic_manaRefillWhileSprinting, true);
		config.addDefault(magic_manaShowPlace, "Chat");
		
		config.addDefault(level_mapExpPerLevelCalculationString, "{level} * {level} * {level} * 1000");
		config.addDefault(level_useLevelSystem, "RaC");
		config.addDefault(level_max_level, -1);
		config.addDefault(custom_level_exp_gain, "[ZOMBIE=0,SKELETON=0,CREEPER=0]");
		config.addDefault(use_fireworks_on_level_up, true);
		config.addDefault(use_levelup_message, true);
		
		config.addDefault(races_enable, true);
		config.addDefault(general_armor_disableArmorChecking, false);
		config.addDefault(disable_health_modifications, false);
		
		config.addDefault(race_spawn_when_dead, false);
		config.addDefault(race_spawn_cooldown, 300);
		config.addDefault(race_spawns_enabled, true);
		
		config.addDefault(food_enabled, true);

		
		config.options().copyDefaults(true);
	}
	
	
	/**
	 * reloads the Configuration of the plugin
	 */
	@SuppressWarnings("deprecation")
	public GeneralConfig reload(){
		plugin.reloadConfig();
		//TODO test if this works
		//plugin.getConfig();
		YAMLConfigExtended config = new YAMLConfigExtended(new File(plugin.getDataFolder(), "config.yml")).load();

		config_channels_enable = config.getBoolean(chat_channel_enable, true);
		config_racechat_encrypt = config.getBoolean(chat_race_encryptForOthers, false);		
		config_disableChatJoinLeaveMessages = config.getBoolean(chat_disable_channel_join_leave_messages, false);
		
		config_whisper_enable = config.getBoolean(chat_whisper_enable, true);
		
		config_defaultHealth = config.getDouble(health_defaultHealth, 20d);
		
		config_enableDebugOutputs = config.getBoolean(debug_outputs_enable, true);
		config_enableErrorUpload = config.getBoolean(debug_outputs_errorUpload, true);
		config_alsoUseLeftClickInGuis = config.getBoolean(gui_also_use_leftclick_in_guis, true);
		config_disableAllScoreboardOutputs = config.getBoolean(gui_scoreboard_disableAllOutputs, false);
		config_convertDatabaseOnStartup = config.getBoolean(general_convert_database_on_startup, true);
		
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
		config_disableArmorChecking = config.getBoolean(general_armor_disableArmorChecking, false);
		
		config_useRaceClassSelectionMatrix = config.getBoolean(classes_useRaceClassSelectionMatrix, false);
		
		config_classChangeCommandUplink = config.getInt(classes_change_uplinkInSeconds, 0);
		
		config_raceChangeCommandUplink = config.getInt(races_change_uplinkInSeconds, 0);
		
		config_useRaceGUIToSelect = config.getBoolean(races_gui_enable, true);
		config_useClassGUIToSelect = config.getBoolean(classes_gui_enable, true);
		
		config_defaultRaceName = config.getString(races_defaultrace_name, "DefaultRace");
		config_defaultRaceTag = config.getString(races_defaultrace_tag, "[NoRace]");
		
		config_takeClassWhenNoClass = config.getString(classes_takeClassWhenNoClass, "");
		config_takeRaceWhenNoRace = config.getString(races_takeRaceWhenNoRace, "");
		config_enableRaces = config.getBoolean(races_enable, true);
		config_gui_level_useMCLevelBar = config.getBoolean(gui_level_useMCLevelBar, false);
		config_useFoodManaBar = config.getBoolean(magic_useFoodManaBar, false);
		config_food_enabled = config.getBoolean(food_enabled, true);
		config_races_create_group_for_race = config.getBoolean(races_create_group_for_race, true);
		
		config_disabledHotkeySlots.clear();
		config_disabledHotkeySlots.addAll(config.getIntegerList(disabled_hotkey_slots));
		config_hotkeysEnabled = config.getBoolean(hotkeys_enabled);
		config_useNewTraitBindSystem = config.getBoolean(use_new_traitbind_system, true);
		
		config_magic_manaRefillWhileSprinting = config.getBoolean(magic_manaRefillWhileSprinting, false);
		config_magic_sprintingManaCost = config.getDouble(magic_sprintingManaCost, 1.5d);
		config_magic_sprintingManaDrainInterval = config.getInt(magic_sprintingManaDrainInterval, 3);
		config_max_level = config.getInt(level_max_level, -1);
		
		config_magic_manaShowPlace = config.getString(magic_manaShowPlace, "Chat");
		
		
		String config_magic_outOfFightRegeneration_tmp = config.getString(magic_outOfFightRegeneration, "0#100");
		try{
			String[] split = config_magic_outOfFightRegeneration_tmp.split(Pattern.quote("#"));
			double value = Double.parseDouble(split[0]);
			double time = Double.parseDouble(split[1]);
			
			config_magic_outOfFightRegeneration = new Pair<Double,Double>(value,time);
		}catch(Throwable exp){
			config_magic_outOfFightRegeneration = new Pair<Double,Double>(0d,100d);
		}
		
		List<String> list = config.getStringList(disabled_regions);
		config_disabled_regions.clear(); config_disabled_regions.addAll(list);
		
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
		
		config_savePlayerDataToDB = config.getBoolean(general_saving_savePlayerDataToDB, false);
		config_mapExpPerLevelCalculationString = config.getString(level_mapExpPerLevelCalculationString, "{level} * {level} * {level} * 1000");
		config_useLevelSystem = LevelingSystem.parse(config.getString(level_useLevelSystem, "RaC"));
		config_keep_max_hp_on_disabled_worlds = config.getBoolean(keep_max_hp_on_disabled_worlds, true);
		config_general_disable_commands = config.getStringList(general_disable_commands);
		
		config_disableHealthMods = config.getBoolean(disable_health_modifications, false);
		
		config_useAutoUpdater = config.getBoolean(updater_enableAutoUpdates, false);
		
		config_disableAllChatBars = config.getBoolean(gui_disableAllChatBars, false);
		config_enableRaceTeams = config.getBoolean(race_teams_enable, false);
		
		config_enableRaceSpawn = config.getBoolean(race_spawns_enabled, true);
		config_enableRaceSpawnOnDeath = config.getBoolean(race_spawn_when_dead, true);
		config_raceSpawnCooldown = config.getInt(race_spawn_cooldown, 300);

		config_general_remove_old_data_days = config.getInt(general_remove_old_data_days, 60);
		config_general_remove_old_data_check_empty = config.getBoolean(general_remove_old_data_days, true);
		
		config_cooldown_on_wand_message = config.getInt(general_cooldown_on_wand_message, 10);
		config_cooldown_on_bow_message = config.getInt(general_cooldown_on_bow_message, 10);
		
		config_general_disable_aliases = config.getStringList(general_disable_aliases);
		config_manaManagerType = ManaManagerType.resolve(config.getString(magic_manaManagerType, ManaManagerType.RaC.name()));
		
		config_gui_scoreboard_name = config.getString(gui_scoreboard_name, "&eRaC");
		config_use_fireworks_on_level_up = config.getBoolean(use_fireworks_on_level_up, true);
		config_use_levelup_message = config.getBoolean(use_levelup_message, true);
		
		config_race_commands_after_change = config.getStringList(races_command_after_change);
		config_class_commands_after_change = config.getStringList(classes_command_after_change);
		
		config_npc_select_race.clear();
		config_npc_change_race.clear();
		config_npc_select_class.clear();
		config_npc_change_class.clear();

		config_npc_select_race.addAll(config.getStringList(npc_select_race));
		config_npc_change_race.addAll(config.getStringList(npc_change_race));
		config_npc_select_class.addAll(config.getStringList(npc_select_class));
		config_npc_change_class.addAll(config.getStringList(npc_change_class));
		
		config_hotkeys_material = config.getMaterial(hotkeys_material, Material.SHEARS);
		
		if(config_useLevelSystem == LevelingSystem.RacesAndClasses 
				&& !LevelCalculator.verifyGeneratorStringWorks(config_mapExpPerLevelCalculationString)){
			plugin.log(" WARNING: The value for the Level Generation String could not be parsed! change: level.mapExpPerLevelCalculationString");
			config_mapExpPerLevelCalculationString = "{level} * {level} * {level} * 1000";
		}
		
		if(config_useLevelSystem == LevelingSystem.mcMMO
				&& !McMMOLevelManager.verifyGeneratorStringWorks(config_mapExpPerLevelCalculationString)){
			plugin.log(" WARNING: The value for the Level Generation String could not be parsed! change: level.mapExpPerLevelCalculationString");
			config_mapExpPerLevelCalculationString = "{axes} + {unarmed} + {archery} / 50";
		}
		
		List<String> temp_config_worldsDisabled = config.getStringList("worlds_disableOn");		
		//be sure to have lower case to not be case sensitive
		config_worldsDisabled = new LinkedList<String>();
		for(String tempName : temp_config_worldsDisabled){
			config_worldsDisabled.add(tempName.toLowerCase());
		}
		
		
		//Handle Remaps:
		List<String> remaps = config.getStringList(general_command_remaps);
		config_command_remaps.clear();
		for(String remap : remaps){
			if(!remap.contains("->")) continue;
			String[] split = remap.split(Pattern.quote("->"));
			String command = split[0];
			String replace = split[1];
			
			config_command_remaps.put(command, replace);
		}
		
		
		config_custom_level_exp_gain.clear();
		List<String> temp_config_custom_level_exp = config.getStringList(custom_level_exp_gain);
		for(String temp : temp_config_custom_level_exp){
			if(!temp.contains("=")) continue;
			
			String[] split = temp.split(Pattern.quote("="));
			if(split.length != 2) continue;
			
			try{
				EntityType type = EntityType.valueOf(split[0].toUpperCase());
				int amount = Integer.parseInt(split[1]);
				
				config_custom_level_exp_gain.put(type, amount);
			}catch(Throwable exp){}
		}
		
		return this;
	}
	
	

	//single purpose is to stop it from beeing displayed in the getter frame
	public RacesAndClasses getPlugin() {
		return plugin;
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

	public String getConfig_gui_scoreboard_name() {
		return config_gui_scoreboard_name;
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
		if(config_savePlayerDataToDB) {
			RacesAndClasses.getPlugin().logWarning("DataBase Support is disabled and will not be enabled in long time, maybe ever.");
			RacesAndClasses.getPlugin().logWarning("Use an older Version to Convert to YML.");
			RacesAndClasses.getPlugin().logWarning("For more details, look at the FAQ on the Bukkit dev page.");
			config_savePlayerDataToDB = false;
		}
		
		return config_savePlayerDataToDB;
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

	public boolean isConfig_alsoUseLeftClickInGuis() {
		return config_alsoUseLeftClickInGuis;
	}

	public boolean isConfig_disableAllScoreboardOutputs() {
		return config_disableAllScoreboardOutputs;
	}

	public boolean isConfig_enableRaces() {
		return config_enableRaces;
	}

	public boolean isConfig_disableArmorChecking() {
		return config_disableArmorChecking;
	}

	public boolean isConfig_keep_max_hp_on_disabled_worlds() {
		return config_keep_max_hp_on_disabled_worlds;
	}

	public boolean isConfig_disableHealthMods() {
		return config_disableHealthMods;
	}

	public boolean isConfig_disableAllChatBars() {
		return config_disableAllChatBars;
	}

	public List<String> getConfig_general_disable_commands() {
		return config_general_disable_commands;
	}

	public boolean isConfig_disableChatJoinLeaveMessages() {
		return config_disableChatJoinLeaveMessages;
	}

	public boolean isConfig_convertDatabaseOnStartup() {
		return config_convertDatabaseOnStartup;
	}

	public LevelingSystem getConfig_useLevelSystem() {
		return config_useLevelSystem;
	}

	public boolean isConfig_enableRaceSpawn() {
		return config_enableRaceSpawn;
	}

	public int getConfig_raceSpawnCooldown() {
		return config_raceSpawnCooldown;
	}

	public boolean isConfig_enableRaceSpawnOnDeath() {
		return config_enableRaceSpawnOnDeath;
	}

	public int getConfig_cooldown_on_bow_message() {
		return config_cooldown_on_bow_message;
	}

	public int getConfig_cooldown_on_wand_message() {
		return config_cooldown_on_wand_message;
	}

	public List<String> getConfig_general_disable_aliases() {
		return config_general_disable_aliases;
	}

	public boolean isConfig_gui_level_useMCLevelBar() {
		return config_gui_level_useMCLevelBar;
	}

	public int getConfig_general_remove_old_data_days() {
		return config_general_remove_old_data_days;
	}

	public boolean isConfig_general_remove_old_data_check_empty() {
		return config_general_remove_old_data_check_empty;
	}

	public boolean isConfig_useFoodManaBar() {
		return config_useFoodManaBar;
	}

	public ManaManagerType getConfig_manaManagerType() {
		return config_manaManagerType;
	}

	public boolean isConfig_food_enabled() {
		return config_food_enabled;
	}

	public Set<String> getConfig_disabledRegions() {
		return config_disabled_regions;
	}

	public boolean isConfig_races_create_group_for_race() {
		return config_races_create_group_for_race;
	}

	public Set<String> getConfig_npc_select_race() {
		return config_npc_select_race;
	}

	public Set<String> getConfig_npc_change_race() {
		return config_npc_change_race;
	}

	public Set<String> getConfig_npc_select_class() {
		return config_npc_select_class;
	}

	public Set<String> getConfig_npc_change_class() {
		return config_npc_change_class;
	}

	public boolean isConfig_hotkeysEnabled() {
		return config_hotkeysEnabled;
	}

	public Set<Integer> getConfig_disabledHotkeySlots() {
		return config_disabledHotkeySlots;
	}
	
	public boolean getConfig_enableRaceTeams(){
		return config_enableRaceTeams;
	}

	public boolean getConfig_useNewTraitBindSystem() {
		return config_useNewTraitBindSystem;
	}

	public Map<EntityType, Integer> getConfig_custom_level_exp_gain() {
		return config_custom_level_exp_gain;
	}

	public boolean isConfig_use_fireworks_on_level_up() {
		return config_use_fireworks_on_level_up;
	}

	public boolean isConfig_use_levelup_message() {
		return config_use_levelup_message;
	}
	
	public Pair<Double,Double> getConfig_magic_outOfFightRegeneration() {
		return config_magic_outOfFightRegeneration;
	}

	public double getConfig_magic_sprintingManaCost() {
		return config_magic_sprintingManaCost;
	}

	public int getConfig_magic_sprintingManaDrainInterval() {
		return config_magic_sprintingManaDrainInterval;
	}

	public boolean getConfig_magic_manaRefillWhileSprinting() {
		return config_magic_manaRefillWhileSprinting;
	}

	public Material getConfig_hotkeys_material() {
		return config_hotkeys_material;
	}
	
	public String getConfig_magic_manaShowPlace() {
		return config_magic_manaShowPlace;
	}

	public int getConfig_max_level() {
		return config_max_level;
	}
	
	public List<String> getConfig_race_commands_after_change() {
		return config_race_commands_after_change;
	}
	
	public List<String> getConfig_class_commands_after_change() {
		return config_class_commands_after_change;
	}

	public Map<String, String> getConfig_command_remaps() {
		return config_command_remaps;
	}
	
}
