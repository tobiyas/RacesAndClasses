package de.tobiyas.racesandclasses.configuration.global;

public class GeneralConfigFields {


	//Constants representing the Attribute Paths.
	//Chat
	public static final String chat_whisper_enable = "chat_whisper_enable";
	public static final String chat_race_encryptForOthers = "chat_race_encryptForOthers";
	public static final String chat_channel_enable = "chat_channel_enable";
		

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
	
	
	//General
	public static final String general_copyDefaultTraitsOnStartup = "general_copyDefaultTraitsOnStartup";
	public static final String general_saving_savePlayerDataToDB = "general_saving_savePlayerDataToDB";
	public static final String general_armor_disableArmorChecking = "general_armor_disableArmorChecking";
	
	//GUI
	public static final String gui_also_use_leftclick_in_guis = "gui_alsoUseLeftclickInGuis";
	public static final String gui_scoreboard_disableAllOutputs = "gui_scoreboard_disableAllOutputs";

	
	//Magic
	public static final String magic_wandId = "magic_wandId";
	
	
	//Leveling
	public static final String level_mapExpPerLevelCalculationString = "level_mapExpPerLevelCalculationString";
	public static final String level_useRaCInbuildLevelSystem = "level_useRaCInbuildLevelSystem";	
	
	
	//Races
	public static final String races_enable = "races_enable";
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
