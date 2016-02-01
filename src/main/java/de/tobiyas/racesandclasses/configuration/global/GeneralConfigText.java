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

import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.*;
import de.tobiyas.racesandclasses.util.consts.Consts;


public class GeneralConfigText {

	
	private static final String Header(){
		return ""
				+ "#ConfigTotal for RacesAndClasses\n"
				+ "#TemplateVersion " + Consts.configVersion 
				+ "\n\n"
				;
	}
	
	
	private static final String Chat(){
		return ""
			//Chat
			+ "\n"
			+ "######\n"
			+ "#CHAT#\n"
			+ "######\n"
			+ "\n"
			
			+ "# Tells if the Whispersystem is enabled. \n"
			+ "# boolean: true or false \n"
			+ "# default: true \n"
			+ chat_whisper_enable + ": true\n\n"
			
			+ "# Shows if Races should talk with encrypted messages. \n"
			+ "# boolean: true or false \n"
			+ "# default: true \n"
			+ "# NOTICE: Not implemented yet!\n"
			+ chat_race_encryptForOthers + ": false\n\n"
			
			+ "# Tells if the Channels System is enabled. \n"
			+ "# boolean: true or false \n"
			+ "# default: true \n"
			+ chat_channel_enable + ": true\n\n"
						
			+ "# Disables the channel join / leave messages. \n"
			+ "# boolean: true or false \n"
			+ "# default: false \n"
			+ chat_disable_channel_join_leave_messages + ": false\n\n"
			;
	}
	
	
	private static final String Health(){
		return ""
				//Health
				+ "\n"
				+ "########\n"
				+ "#HEALTH#\n"
				+ "########\n"
				+ "\n"
				
				+ "# The default Health a player has when he has no Race. \n"
				+ "# This can be a double (number with comma)\n"
				+ "# default: 20 \n"
				+ health_defaultHealth + ": 20\n\n"
				
				+ "# Tells to use a Chat message as health Bar. \n"
				+ "# boolean: true or false \n"
				+ "# default: true \n"
				+ health_bar_inChat_enable + ": true\n\n"
				
				+ "# Tells the plugin to NOT use health modifications. \n"
				+ "# boolean: true or false \n"
				+ "# default: false \n"
				+ disable_health_modifications + ": false\n\n"
				;
	}
	
	
	
	private final static String Debug(){
		return ""
				//DEBUG
				+ "\n"
				+ "#######\n"
				+ "#DEBUG#\n"
				+ "#######\n"
				+ "\n"
				
				+ "# Tells the Plugin to enable Debug outputs. \n"
				+ "# If activating, beware of console spam. \n"
				+ "# boolean: true or false \n"
				+ "# default: true \n"
				+ debug_outputs_enable + ": true\n\n"
				
				+ "# Tells the Plugin to upload errors that happened to tobiyas's Error Server. \n"
				+ "# This server is located at: tobiyas.tk . \n"
				+ "# boolean: true or false \n"
				+ "# default: false \n"
				+ debug_outputs_errorUpload + ": false\n\n"
				
				+ "# Tells the Plugin to log all stuff to the console. \n"
				+ "# boolean: true or false \n"
				+ "# default: true \n"
				+ debug_outputs_writethrough + ": true\n\n"
				;
	}
		
	
	private final static String Metrics(){
		return ""
				//Metrics
				+ "\n"
				+ "#########\n"
				+ "#METRICS#\n"
				+ "#########\n"
				+ "\n"
				
				+ "# Tells the Plugin if The Server should participate on Metrics. \n"
				+ "# To get more infos on Metrics, see 'www.mcstats.org'\n"
				+ "# boolean: true or false \n"
				+ "# default: true \n"
				+ metrics_enable + ": true\n\n"
				;
	}
	
	
	private final static String Updater(){
		return ""
				//UPDATER
				+ "\n"
				+ "#########\n"
				+ "#UPDATER#\n"
				+ "#########\n"
				+ "\n"
				
				+ "# Tells the Plugin to auto-update when there is a new Release Version of the Plugin. \n"
				+ "# The Updater used is from h31ix.\n"
				+ "# link: 'https://github.com/h31ix/Updater/blob/master/src/net/h31ix/updater/Updater.java'\n"
				+ "# boolean: true or false \n"
				+ "# default: false \n"
				+ updater_enableAutoUpdates + ": false\n\n"
				;
	}
	
	
	private static final String Language(){
		return ""
				//LANGUAGE
				+ "\n"
				+ "##########\n"
				+ "#LANGUAGE#\n"
				+ "##########\n"
				+ "\n"
				
				+ "# Tells the Plugin which language file to use. \n"
				+ "# The language files are located at: 'plugins/RacesAndClasses/language/'.\n"
				+ "# String representing the language: en, de, es, it ...\n"
				+ "# default: en \n"
				+ "# NOTICE: Not implemented yet.\n"
				+ language_used + ": en\n\n"
				;
	}
	
	
	private static final String Multiworld(){
		return ""
				//MULTIWORLD
				+ "\n"
				+ "############\n"
				+ "#MULTIWORLD#\n"
				+ "############\n"
				+ "\n"
				
				+ "# All worlds to disable the Traits on. \n"
				+ "# String list: example: [world1337,nonexistingWorld,...]\n"
				+ "# default: [] \n"
				+ worlds_disableOn + ": []\n\n"
				
				+ "# All regions to disable the Traits on. \n"
				+ "# String list: example: [region12,tobiyas_region,...]\n"
				+ "# default: [] \n"
				+ disabled_regions + ": []\n\n"
				
				+ "# Tells the Plugin to reset the HP on change to disabled world. \n"
				+ "# boolean: true or false\n"
				+ "# default: true \n"
				+ keep_max_hp_on_disabled_worlds + ": true\n\n"
				;
	}
	
	
	
	private static final String Hotkeys(){
		return ""
				//Hotkeys
				+ "\n"
				+ "#########\n"
				+ "#HOTKEYS#\n"
				+ "#########\n"
				+ "\n"
				
				+ "# If hotkeys are enabled. \n"
				+ "# If true, /bindtrait will change your inventory to a Skill inventory.\n"
				+ "# default: true \n"
				+ hotkeys_enabled + ": true\n\n"
							
				+ "# If the new Hotkey System should be used. \n"
				+ "# This system has 2 different Hot-Bars. One for build, one for Fight.\n"
				+ "# default: true \n"
				+ use_new_traitbind_system + ": true\n\n"
				
				+ "# The Material to use for Hotkeys. \n"
				+ "# This is the Material to use for Hotkey Bar.\n"
				+ "# Materials are found here: https://github.com/Bukkit/Bukkit/blob/master/src/main/java/org/bukkit/Material.java.\n"
				+ "# default: SHEARS \n"
				+ hotkeys_material + ": SHEARS\n\n"
				
				+ "# The slots that are not bindable and are persistent throughout the Skill Bar (in the new Hotbar System). \n"
				+ "# The Slots may not be bound (0-8).\n"
				+ "# Example: [0,3,8].\n"
				+ "# default: [] \n"
				+ disabled_hotkey_slots + ": []\n\n"
				;
	}
	
	
	private static final String SkillSystem(){
		return ""
				+ "\n"
				+ "##############\n"
				+ "#Skill-System#\n"
				+ "##############\n"
				+ "\n"
				
				+ "# If the SkillSystem is enabled. \n"
				+ "# If true, the users may pick their skills.\n"
				+ "# default: false \n"
				+ skills_useSkillSystem + ": false\n\n"
				
				+ "# If the SkillSystem is enabled, the player get points. \n"
				+ "# This defines every how many levels the player get a point.\n"
				+ "# default: 5 \n"
				+ skills_skillpointEveryXLevel + ": 5\n\n"
				;
	}
	
	
	private static final String RaceSpawns(){
		return ""
				//Race-Spawns
				+ "\n"
				+ "#############\n"
				+ "#Race Spawns#\n"
				+ "#############\n"
				+ "\n"
				
				+ "# This tells if the Race-Spawning Feature is enabled or not.. \n"
				+ "# If it is enabled, players can get with '/racespawn' to their Race Spawns.\n"
				+ "# boolean: true or false \n"
				+ "# default: true \n"
				+ race_spawns_enabled + ": true\n\n"
				
				+ "# When a player dies, he will spawn on his Race Spawn.\n"
				+ "# If this is set to false, the player NOT spawn to his race spawn.\n"
				+ "# boolean: true or false \n"
				+ "# default: false \n"
				+ race_spawn_when_dead + ": false\n\n"
				
				+ "# Tells the cooldown for using /racespawn. \n"
				+ "# The cooldown is in seconds. 300 -> 6 Minutes. \n"
				+ "# integer: 0 - high number\n"
				+ "# default: 300 \n"
				+ race_spawn_cooldown + ": 300\n\n"
				;
	}
	
	
	private static final String Food(){
		return ""
				///FOOOOOD
				+ "\n"
				+ "######\n"
				+ "#Food#\n"
				+ "######\n"
				+ "\n"
				
				+ "# This tells to enable the Food addin.\n"
				+ "# If it is enabled, players can eat food that has the correct lores on them.\n"
				+ "# boolean: true or false \n"
				+ "# default: true \n"
				+ food_enabled + ": true\n\n"
				;
	}
	
	
	private static final String General(){
		return ""
				//GENERAL
				+ "\n"
				+ "#########\n"
				+ "#GENERAL#\n"
				+ "#########\n"
				+ "\n"
				
				+ "# Copys all Traits to the Trait folder on Plugin loading. \n"
				+ "# If you modify Traits used by the Plugin by code, turn this off.\n"
				+ "# boolean: true or false\n"
				+ "# default: true \n"
				+ general_copyDefaultTraitsOnStartup + ": true\n\n"

				//+ "# This tells the Plugin to use the Build in DB service to save data. \n"
				//+ "# If turned to false, a flat file (yml) will be used. THIS IS VERY SLOW!!!\n"
				//+ "# boolean: true or false\n"
				//+ "# default: true \n"
				//+ general_saving_savePlayerDataToDB + ": false\n\n"
				
				+ "# This tells the plugin to look at conversion at startup.. \n"
				+ "# If turned to false, no conversion of playerdata will be done on startup!\n"
				+ "# boolean: true or false\n"
				+ "# default: false \n"
				+ general_convert_database_on_startup + ": false\n\n"
				
				+ "# This tells the Plugin to NOT check the Armor if turned off. \n"
				+ "# If turned to true, the armor Checking of Races / Classes is Disabled.\n"
				+ "# boolean: true or false\n"
				+ "# default: false \n"
				+ general_armor_disableArmorChecking + ": false\n\n"
				
				+ "# This tells the plugin to NOT register the following commands. \n"
				+ "# This is a list of commands that are disabled for the plugin.\n"
				+ "# String list: [command1,command2,...]\n"
				+ "# default: [] \n"
				+ general_disable_commands + ": []\n\n"
				
				+ "# This tells the plugin to remap own commands. The first command is replaced by the second one. \n"
				+ "# This is to remap often used commands as /race or /playerinfo.\n"
				+ "# The replacesments work as following: command -> replacement. Example: racespawn->spawn.\n"
				+ "# String list: [command1->test12, command2->test123,...]\n"
				+ "# default: [] \n"
				+ general_command_remaps + ": []\n\n"
				
				+ "# This tells the plugin to NOT register the following aliases. \n"
				+ "# This is a list of aliases that are disabled for the plugin.\n"
				+ "# String list: [alias1,alias2,...]\n"
				+ "# default: [] \n"
				+ general_disable_aliases + ": []\n\n"
				
				+ "# This is the cooldown between 2 messages when switching to the bow. \n"
				+ "# number:  10  20   90   9001 ...."
				+ "# default: 10 \n"
				+ general_cooldown_on_bow_message + ": 10\n\n"
				
				+ "# This is the cooldown between 2 messages when switching to the wand. \n"
				+ "# number:  10  20   90   9001 ...."
				+ "# default: 10 \n"
				+ general_cooldown_on_wand_message + ": 10\n\n"

				+ "# When removing old data, the days to check. \n"
				+ "# If a player is not online for that long, his playerfile is removed.\n"
				+ "# The Value is in Days.\n"
				+ "# number:  10  20   90   9001 ...."
				+ "# default: 60 \n"
				+ general_remove_old_data_days + ": 60\n\n"
							
				+ "# When removing old data, if to check if the file is empty.\n"
				+ "# Empty means, that the player has no Race / Class entry.\n"
				+ "# boolean: true or false\n"
				+ "# default: true\n"
				+ general_remove_old_data_check_empty + ": true\n\n"
				;
	}
	
	
	
	private static final String Gui(){
		return ""
				//GUI
				+ "\n"
				+ "#####\n"
				+ "#GUI#\n"
				+ "#####\n"
				+ "\n"
				
				+ "# Enables to also use left clicks in Inventory GUIS.\n"
				+ "# boolean: true or false\n"
				+ "# default: true \n"
				+ gui_also_use_leftclick_in_guis + ": true\n\n"

				+ "# Setting this to true will disable all Outputs to scoreboards from the plugin.\n"
				+ "# boolean: true or false\n"
				+ "# default: false \n"
				+ gui_scoreboard_disableAllOutputs + ": false\n\n"
				
				+ "# This is the top line on the Scoreboard.\n"
				+ "# ColorCodes are replaced by &, eg: &e -> yellow.\n"
				+ "# String: name (&eHallo)\n"
				+ "# default: &eRaC \n"
				+ gui_scoreboard_name + ": '&eRaC'\n\n"
				
				+ "# If the permanent Scoreboard should be used.\n"
				+ "# If used, this will clash with other scoreboard plugins.\n"
				+ "# Go to next category by leftclicking 3 ticks with air in hand.\n"
				+ "# boolean: true or false\n"
				+ "# default: false \n"
				+ gui_enable_permanent_scoreboard + ": false\n\n"
				
				+ "# Setting this to true will disable all Outputs of bars to the chat of the players.\n"
				+ "# boolean: true or false\n"
				+ "# default: false \n"
				+ gui_disableAllChatBars + ": false\n\n"
				
				+ "# If this is activated, the level MC level bar is overwritten as Level Indicator.\n"
				+ "# boolean: true or false\n"
				+ "# default: false \n"
				+ gui_level_useMCLevelBar + ": false\n\n"
				;
	}
	
	
	private static final String Magic(){
		return ""
				//MAGIC
				+ "\n"
				+ "############\n"
				+ "#MAGIC/MANA#\n"
				+ "############\n"
				+ "\n"
				
				+ "# Tells the plugin which Item to use as wand (magic spell cast item). \n"
				+ "# int: itemID of the Item OR\n"
				+ "# String: The canonical Name of the Item. See: 'https://github.com/Bukkit/Bukkit/blob/master/src/main/java/org/bukkit/Material.java' \n"
				+ "# default: STICK \n"
				+ magic_wandId + ": STICK\n\n"
				
				+ "# If this is activated, the Mana will be displayed in the Food bar.\n"
				+ "# boolean: true or false\n"
				+ "# default: false \n"
				+ magic_useFoodManaBar + ": false\n\n"
								
				+ "# This is ment to be used with the 'use_foodManaBar'.\n"
				+ "# It will regenerate the player X Health every Y seconds.\n"
				+ "# Example: 2#30  will heal 2 health every 30 seconds out of fight.\n"
				+ "# To turn it off, just put 0 as X (value to heal).\n"
				+ "# String: X#Y\n"
				+ "# default: 0#100 \n"
				+ magic_outOfFightRegeneration + ": 0#100\n\n"
				
				+ "# The Mana Manager to use. This is the Mana System present and in use for this plugin.\n"
				+ "# This can be: RaC or MagicSpells\n"
				+ "# default: RaC \n"
				+ magic_manaManagerType + ": 'RaC'\n\n"
				
				+ "# Where the 'Mana-Bar' should be shown.\n"
				+ "# This can be: Chat, ActionBar(needs MC 1.8+), XPBar, None\n"
				+ "# default: Chat \n"
				+ magic_manaShowPlace + ": 'ActionBar'\n\n"
								
				+ "# If the Mana of a player refills while he sprints.\n"
				+ "# default: false \n"
				+ magic_manaRefillWhileSprinting + ": false\n\n"
				
				+ "# How much mana should be drained for Sprinting.\n"
				+ "# Can be 0 to disable this feature.\n"
				+ "# default: 1.5 \n"
				+ magic_sprintingManaCost + ": 1.5\n\n"
								
				+ "# When the Mana should be drained while running.\n"
				+ "# This is in seconds. This means every X seconds Y mana is drained."
				+ "# X is the Time here. Y is the mana in: 'magic_sprintingManaCost'."
				+ "# default: 3 \n"
				+ magic_sprintingManaDrainInterval + ": 3\n\n"
				;
	}
	
	
	private static final String Leveling(){
		return ""
				//LEVELING
				+ "\n"
				+ "##########\n"
				+ "#LEVELING#\n"
				+ "##########\n"
				+ "\n"
				
				+ "# This is the Generator Polynome of the Level calculation. \n"
				+ "# String: a string representing a polynome \n"
				+ "# {level} will be replaced by the level of calculation.\n"
				+ "# If mcmmo is used as Level System, the Calculation will use McMMO skills.\n"
				+ "# The Skill name has to be written in {} eg. {mining}.\n"
				+ "# A valid calculator would be: '{mining} + {excavation} \\ 20'\n"
				+ "# default: '{level} * {level} * {level} * 1000' \n"
				+ "# By default, the the next level needs (level^3) * 1000 exp.\n"
				+ level_mapExpPerLevelCalculationString + ": '{level} * {level} * {level} * 1000'\n\n"

				+ "# If a Firework should be fired on an Level Up. \n"
				+ "# Boolean: true or false\n"
				+ "# default: true \n"
				+ use_fireworks_on_level_up + ": true\n\n"
				
				+ "# The max level, no one can get over.\n"
				+ "# If set to something below 0, there will not be any restriction.\n"
				+ "# Number: 0 to as much as you like\n"
				+ "# default: true \n"
				+ level_max_level + ": -1\n\n"
				
				+ "# A level Up message is sent when the level is increased. \n"
				+ "# Boolean: true or false\n"
				+ "# default: true \n"
				+ use_levelup_message + ": true\n\n"
				
				+ "# This tells the Plugin which level system to use. \n"
				+ "# It can use one of the following:.\n"
				+ "# - RaC: Races and Classes leveling System.\n"
				+ "# - MC: Minecraft Levels.\n"
				+ "# - SkillAPI: Leveling System from SkillAPI.\n"
				+ "# - mcMMO: Leveling System from McMMO. also check for the calculation String above.\n"
				+ "# String: RaC or MC or SkillAPI or MCMMO\n"
				+ "# default: RaC \n"
				+ level_useLevelSystem + ": RaC\n\n"
				
				+ "# This tells the Plugin how much Custom EXP to give the player on an Entity kill. \n"
				+ "# It is a list of Strings, which means they have to be seperated by a comma or be written below each other.\n"
				+ "# Each element in this list has two parts: EntityType=ExpAmount.\n"
				+ "# EntityType can be found here: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html\n"
				+ "# The amount of EXP is an Integer value (0,1,2,3,4, ...).\n"
				+ "# List of String: [EntityType=AmountEXP,...]\n"
				+ "# default: [ZOMBIE=0,SKELETON=0,CREEPER=0,BLAZE=0,WITCH=0] \n"
				+ custom_level_exp_gain + ": [ZOMBIE=0,SKELETON=0,CREEPER=0,BLAZE=0,WITCH=0]\n\n"
				;
	}
	
	private static final String NpcIntegration(){
		return ""
				//NPC integration
				+ "# The NPCs to open the GUI for Race Selection. \n"
				+ "# NPC names have to be the real names of the NPCs.\n"
				+ "# The Gui will open even if the GUI is disabled in Race Secion.\n"
				+ "# String List: a list of NPC names.\n"
				+ "# default: [] \n"
				+ npc_select_race + ": []\n\n"

				+ "# The NPCs to open the GUI for Race Change Selection. \n"
				+ "# NPC names have to be the real names of the NPCs.\n"
				+ "# The Gui will open even if the GUI is disabled in Race Secion.\n"
				+ "# String List: a list of NPC names.\n"
				+ "# default: [] \n"
				+ npc_change_race + ": []\n\n"

				+ "# The NPCs to open the GUI for Class Selection. \n"
				+ "# NPC names have to be the real names of the NPCs.\n"
				+ "# The Gui will open even if the GUI is disabled in Class Secion.\n"
				+ "# String List: a list of NPC names.\n"
				+ "# default: [] \n"
				+ npc_select_class + ": []\n\n"
				;
	}
	
	
	private static final String Races(){
		return ""
				+ "\n"
				+ "#######\n"
				+ "#RACES#\n"
				+ "#######\n"
				+ "\n"
				
				+ "# Tells the plugin to use the Race System. \n"
				+ "# boolean: true or false \n"
				+ "# default: true \n"
				+ races_enable + ": true\n\n"
				
				+ "# If the Race-Teams should be enabled. \n"
				+ "# RacesTeams are defined in the raceTeams.yml \n"
				+ "# Races in the same team can not deal damage to each other.\n"
				+ "# boolean: true or false \n"
				+ "# default: false \n"
				+ race_teams_enable + ": false\n\n"
				
				+ "# Tells the plugin to remember everyone not having a Race to get one. \n"
				+ "# boolean: true or false \n"
				+ "# default: true \n"
				+ races_remindDefaultRace_enable + ": true\n\n"
				
				+ "# Tells the plugin to create a Permissions Group for every Race. \n"
				+ "# boolean: true or false \n"
				+ "# default: true \n"
				+ races_create_group_for_race + ": true\n\n"
				
				+ "# This is the interval the plugin remembers the user to get a race. \n"
				+ "# The Value is in Minutes \n"
				+ "# integer: value without comma \n"
				+ "# default: 10 \n"
				+ races_remindDefaultRace_interval + ": 10\n\n"
				
				+ "# Tells the plugin to replace the Name of the Player with a prefix of the race in the 'TAB' player list. \n"
				+ "# boolean: true or false \n"
				+ "# default: true \n"
				+ races_display_adaptListName + ": true\n\n"
				
				+ "# Tells the Plugin to use it's own Drop list for Mobs. \n"
				+ "# This list is located at:  'plugins/RacesAndClasses/TraitConfig/DropRates.yml'\n"
				+ "# boolean: true or false \n"
				+ "# default: true \n"
				+ races_drops_enable + ": true\n\n"
				
				+ "# Tells the plugin to check for Permission for Each Race. \n"
				+ "# The Permission is: 'RaC.races.RACENAME' \n"
				+ "# boolean: true or false \n"
				+ "# default: false \n"
				+ races_permissions_usePermissionsForEachRace + ": false\n\n"
				
				+ "# Tells the plugin to give the '/race change' Command an Uplink. \n"
				+ "# Values <= 0 say to give NO uplink \n"
				+ "# int: value without comma \n"
				+ "# default: 3600 \n"
				+ races_change_uplinkInSeconds + ": 3600\n\n"
				
				//Default Race stuff
				+ "# The name the Default Race should have. \n"
				+ "# String: a text. Preferable a name \n"
				+ "# default: DefaultRace \n"
				+ races_defaultrace_name + ": DefaultRace\n\n"
				
				+ "# The tag the Default Race should have. \n"
				+ "# String: a text. Preferable a name \n"
				+ "# default: '[NoRace]' \n"
				+ races_defaultrace_tag + ": '[NoRace]'\n\n"
				
				+ "# When this is set (not ''), whenever a player has no race,  \n"
				+ "# he will be pushed into the Race mentioned. \n"
				+ "# Example: races_takeRaceWhenNoRace: 'Elf' would push the player to the Elf Race if he has no Race. \n"
				+ "# HINT: leaving this empty (use ''), the default race will be selected \n"
				+ "# String: a text. Preferable a race name or an empty String \n"
				+ "# default: '' \n"
				+ races_takeRaceWhenNoRace + ": ''\n\n"
				
				+ "# When a player logs in and does not have a Race, the Race selection screen is shown. \n"
				+ "# To disable this, set this option to: false \n"
				+ "# boolean: true or false \n"
				+ "# default: true \n"
				+ races_openRaceSelectionOnJoinWhenNoRace_enable + ": true\n\n"

				+ "# When a player logs in and does not have a Race, the Race selection screen is shown. \n"
				+ "# When this option is true, the screen is reopened as long as he hasn't selected a Race. \n"
				+ "# boolean: true or false \n"
				+ "# default: true \n"
				+ races_cancleGUIExitWhenNoRacePresent_enable + ": true\n\n"
				
				+ "# The time between login and showing of the selection screen in secods. \n"
				+ "# int: number without comma \n"
				+ "# default: 2 \n"
				+ races_openRaceSelectionOnJoinWhenNoRace_timeToOpenAfterLoginInSeconds + ": 2\n\n"
				
				+ "# This indicates if the GUI selection via Inventory menu should be used or the command selection. \n"
				+ "# true = gui, false = command\n"
				+ "# boolean: true or false \n"
				+ "# default: true \n"
				+ races_gui_enable + ": true\n\n"
				
				+ "# This command will be executed when the player changes his race.\n"
				+ "# The Key %RACE% will be replaced with the race name. \n"
				+ "# The Key %PLAYER% will be replaced with the player name. \n"
				+ "# The Key %DISPLAY% will be replaced with the player's display name. \n"
				+ "# If %CONSOLE% is contained in this text, the console will run the Command. \n"
				+ "# Hint: If empty, no command will be run.\n"
				+ "# Hint2: This can also be a list of commands.\n"
				+ "# Example: '%CONSOLE%broadcast %PLAYER% is now a %RACE%.' \n"
				+ "# String: a text / command.\n"
				+ "# default: '' \n"
				+ races_command_after_change + ": ''\n\n"
				;
	}
	
	
	private static final String Classes(){
		return ""
				+ "\n"
				+ "#########\n"
				+ "#CLASSES#\n"
				+ "#########\n"
				+ "\n"
				
				+ "# Tells the plugin to enable classes. \n"
				+ "# Setting this to false will deactivate the complete Class system.\n"
				+ "# boolean: true or false \n"
				+ "# default: true \n"
				+ classes_enable + ": true\n\n"
				
				+ "# Tells the plugin to check for Permission for Each Class. \n"
				+ "# The Permission is: 'RaC.classes.CLASSNAME' \n"
				+ "# boolean: true or false \n"
				+ "# default: false \n"
				+ classes_permissions_usePermissionsForEachClasses + ": false\n\n"
				
				+ "# Tells the plugin to check every class selection against a Race / Class matrix. \n"
				+ "# The matrix can be found in: 'plugins/RacesAndClasses/racesClassesSelectionMatrix.yml'\n"
				+ "# Adding an entry looks like this: \n"
				+ "# RACENAME: [CLASS1, CLASS2, CLASS3]\n"
				+ "# This means the race RACENAME can only select CLASS1, ClASS2 or CLASS3.\n"
				+ "# boolean: true or false \n"
				+ "# default: false \n"
				+ classes_useRaceClassSelectionMatrix + ": false\n\n"
				
				+ "# When this is set (not ''), whenever a player has no class,  \n"
				+ "# he will be pushed into the class mentioned. \n"
				+ "# Example: classes_takeClassWhenNoClass: 'Warrior' would push the player to the Warrior Class if he has no Class. \n"
				+ "# HINT: leaving this empty (use ''), the default class will be selected \n"
				+ "# String: a text. Preferable a Class name or an empty String \n"
				+ "# default: '' \n"
				+ classes_takeClassWhenNoClass + ": ''\n\n"
				
				+ "# Tells the plugin to give the '/class change' Command an Uplink. \n"
				+ "# Values <= 0 say to give NO uplink \n"
				+ "# int: value without comma \n"
				+ "# default: 3600 \n"
				+ classes_change_uplinkInSeconds + ": 3600\n\n"
				
				+ "# When a player logs in and does not have a Class, the Class selection screen is shown. \n"
				+ "# To disable this, set this option to: false \n"
				+ "# boolean: true or false \n"
				+ "# default: true \n"
				+ classes_openClassSelectionAfterRaceSelectionWhenNoClass_enable + ": true\n\n"

				+ "# When a player logs in and does not have a Class, the Class selection screen is shown. \n"
				+ "# When this option is true, the screen is reopened as long as he hasn't selected a Class. \n"
				+ "# boolean: true or false \n"
				+ "# default: true \n"
				+ classes_cancleGUIExitWhenNoClassPresent_enable + ": true\n\n"
				
				+ "# This indicates if the GUI selection via Inventory menu should be used or the command selection. \n"
				+ "# true = gui, false = command\n"
				+ "# boolean: true or false \n"
				+ "# default: true \n"
				+ classes_gui_enable + ": true\n\n"
				
				+ "# This command will be executed when the player changes his class.\n"
				+ "# The Key %CLASS% will be replaced with the class name. \n"
				+ "# The Key %PLAYER% will be replaced with the player name. \n"
				+ "# The Key %DISPLAY% will be replaced with the player's display name. \n"
				+ "# If %CONSOLE% is contained in this text, the console will run the Command. \n"
				+ "# Hint: If empty, no command will be run.\n"
				+ "# Hint2: This can also be a list of commands.\n"
				+ "# Example: '%CONSOLE%broadcast %PLAYER% is now a %CLASS%.' \n"
				+ "# String: a text / command.\n"
				+ "# default: '' \n"
				+ classes_command_after_change + ": ''\n\n"
				;
	}

	
	
	/**
	 * Returns the Generated Config Text.
	 * 
	 * @return generated Config Text.
	 */
	public static String GlobalConfigText() {
		return Header()
			+ Chat()
			+ Health()
			+ Debug()
			+ Metrics()
			+ Updater()
			+ Language()
			+ Multiworld()
			+ Hotkeys()
			+ SkillSystem()
			+ RaceSpawns()
			+ Food()
			+ General()
			+ Gui()
			+ Magic()
			+ Leveling()
			+ NpcIntegration()
			+ Races()
			+ Classes();
	}

}
