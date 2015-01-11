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
package de.tobiyas.racesandclasses.configuration.global;

public class GeneralConfigFields {


	//Constants representing the Attribute Paths.
	//Chat
	public static final String chat_whisper_enable = "chat_whisper_enable";
	public static final String chat_race_encryptForOthers = "chat_race_encryptForOthers";
	public static final String chat_channel_enable = "chat_channel_enable";
	public static final String chat_disable_channel_join_leave_messages = "chat_disable_channel_join_leave_messages";
		

	//Health
	public static final String health_defaultHealth = "health_defaultHealth";
	public static final String disable_health_modifications = "disable_health_modifications";
	public static final String health_bar_inChat_enable = "health_bar_inChat_enable";

	
	//Debug
	public static final String debug_outputs_enable = "debug_outputs_enable";
	public static final String debug_outputs_errorUpload = "debug_outputs_errorUpload";
	public static final String debug_outputs_writethrough = "debug_outputs_writethrough";
	
	
	//Tutorials
	public static final String tutorials_enable = "tutorials_enable";

	
	//Metrics
	public static final String metrics_enable = "metrics_enable";
	
	
	//Updater
	public static final String updater_enableAutoUpdates = "updater_enableAutoUpdates";

	
	//Language
	public static final String language_used = "language_used";
	
	
	//Multiworld
	public static final String worlds_disableOn = "worlds_disableOn";
	public static final String keep_max_hp_on_disabled_worlds = "keep_max_hp_on_disabled_worlds";
	public static final String disabled_regions = "disabled_regions";
	
	//HotKeys
	public static final String hotkeys_enabled = "hotkeys_enabled";
	public static final String use_new_traitbind_system = "use_new_traitbind_system";
	public static final String disabled_hotkey_slots = "disabled_hotkey_slots";
	
	//Race-Spawns
	public static final String race_spawns_enabled = "race_spawns_enabled";
	public static final String race_spawn_cooldown = "race_spawn_cooldown";
	public static final String race_spawn_when_dead = "race_spawn_when_dead";
	
	//Food
	public static final String food_enabled = "food_enabled";
	
	//General
	public static final String general_copyDefaultTraitsOnStartup = "general_copyDefaultTraitsOnStartup";
	public static final String general_saving_savePlayerDataToDB = "general_saving_savePlayerDataToDB";
	public static final String general_convert_database_on_startup = "general_convert_database_on_startup";
	public static final String general_armor_disableArmorChecking = "general_armor_disableArmorChecking";
	public static final String general_disable_commands = "general_disable_commands";
	public static final String general_disable_aliases = "general_disable_aliases";
	public static final String general_cooldown_on_wand_message = "general_cooldown_on_wand_message";
	public static final String general_cooldown_on_bow_message = "general_cooldown_on_bow_message";
	public static final String general_remove_old_data_days = "general_remove_old_data_days";
	public static final String general_remove_old_data_check_empty = "general_remove_old_data_check_empty";
	
	//GUI
	public static final String gui_also_use_leftclick_in_guis = "gui_alsoUseLeftclickInGuis";
	public static final String gui_disableAllChatBars = "gui_disableAllChatBars";
	public static final String gui_scoreboard_name = "gui_scoreboard_name";
	public static final String gui_scoreboard_disableAllOutputs = "gui_scoreboard_disableAllOutputs";
	public static final String gui_level_useMCLevelBar = "gui_level_useMCLevelBar";
	public static final String gui_useFoodManaBar = "gui_useFoodManaBar";
	public static final String gui_manaManagerType = "gui_manaManagerType";

	
	//Magic
	public static final String magic_wandId = "magic_wandId";
	
	
	//Leveling
	public static final String level_mapExpPerLevelCalculationString = "level_mapExpPerLevelCalculationString";
	public static final String level_useLevelSystem = "level_useLevelSystem";
	public static final String custom_level_exp_gain = "custom_level_exp_gain";
	public static final String use_fireworks_on_level_up = "use_fireworks_on_level_up";
	public static final String use_levelup_message = "use_levelup_message";
	
	
	//NPCs
	public static final String npc_select_race = "npc_select_race";
	public static final String npc_change_race = "npc_change_race";
	public static final String npc_select_class = "npc_select_class";
	public static final String npc_change_class = "npc_change_class";
	
	
	//Races
	public static final String races_enable = "races_enable";
	public static final String races_create_group_for_race = "races_create_group_for_race";
	public static final String races_remindDefaultRace_enable = "races_remindDefaultRace_enable";
	public static final String races_remindDefaultRace_interval = "races_remindDefaultRace_interval";
	public static final String races_display_adaptListName = "races_display_adaptListName";
	public static final String races_drops_enable = "races_drops_enable";
	public static final String races_permissions_usePermissionsForEachRace = "races_permissions_usePermissionsForEachRace";
	public static final String races_change_uplinkInSeconds = "races_change_uplinkInSeconds";
	public static final String races_defaultrace_name = "races_defaultrace_name";
	public static final String races_defaultrace_tag = "races_defaultrace_tag";
	public static final String races_takeRaceWhenNoRace = "races_takeRaceWhenNoRace";
	public static final String races_openRaceSelectionOnJoinWhenNoRace_enable = "races_openRaceSelectionOnJoinWhenNoRace_enable";
	public static final String races_cancleGUIExitWhenNoRacePresent_enable = "races_cancleGUIExitWhenNoRacePresent_enable";
	public static final String races_openRaceSelectionOnJoinWhenNoRace_timeToOpenAfterLoginInSeconds = "races_openRaceSelectionOnJoinWhenNoRace_timeToOpenAfterLoginInSeconds";
	public static final String races_gui_enable = "races_gui_enable";
	public static final String race_teams_enable = "race_teams_enable";
	
	
	//Classes
	public static final String classes_enable = "classes_enable";
	public static final String classes_permissions_usePermissionsForEachClasses = "classes_permissions_usePermissionsForEachClasses";
	public static final String classes_useRaceClassSelectionMatrix = "classes_useRaceClassSelectionMatrix";
	public static final String classes_takeClassWhenNoClass = "classes_takeClassWhenNoClass";
	public static final String classes_change_uplinkInSeconds = "classes_change_uplinkInSeconds";
	public static final String classes_openClassSelectionAfterRaceSelectionWhenNoClass_enable = "classes_openClassSelectionAfterRaceSelectionWhenNoClass_enable";
	public static final String classes_cancleGUIExitWhenNoClassPresent_enable = "classes_cancleGUIExitWhenNoClassPresent_enable";
	public static final String classes_gui_enable = "classes_gui_enable";
}
